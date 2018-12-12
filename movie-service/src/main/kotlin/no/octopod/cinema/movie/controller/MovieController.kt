package no.octopod.cinema.movie.controller

import no.octopod.cinema.common.dto.MovieDto
import no.octopod.cinema.common.dto.WrappedResponse
import no.octopod.cinema.common.hateos.Format
import no.octopod.cinema.common.hateos.HalLink
import no.octopod.cinema.common.hateos.HalPage
import no.octopod.cinema.movie.converter.MovieConverter
import no.octopod.cinema.movie.repository.MovieRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@RequestMapping(
        path = ["/movies"],
        produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@CrossOrigin
class MovieController {

    @Autowired
    lateinit var repo: MovieRepository

    @PostMapping
    fun createMovie(
            @RequestBody movieDto: MovieDto
    ): ResponseEntity<Void> {
        if (movieDto.title.isNullOrEmpty() || movieDto.release_date == null) {
            return ResponseEntity.status(400).build()
        }

        val entity = MovieConverter.transform(movieDto)
        entity.releaseDate = entity.releaseDate

        val saved = repo.save(entity)
        return ResponseEntity.created(URI.create("/movies/${saved.id}")).build()
    }

    @GetMapping(path = ["/{movieId}"])
    fun getById(
            @PathVariable("movieId")
            movieId: String
    ): ResponseEntity<WrappedResponse<MovieDto>> {
        val id = movieId.toLongOrNull() ?: return ResponseEntity.status(404).build()
        val movie = repo.findById(id).orElse(null) ?: return ResponseEntity.status(404).build()

        val dto = MovieConverter.transform(movie)
        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = dto
                ).validated()
        )
    }

    @GetMapping(produces = [Format.HAL_V1])
    fun getAll(
            @RequestParam("page", defaultValue = "1")
            page: Int,
            @RequestParam("limit", defaultValue = "10")
            limit: Int,
            @RequestParam("featuredOnly", defaultValue = "false")
            featured: Boolean
    ): ResponseEntity<WrappedResponse<HalPage<MovieDto>>> {
        if (page < 1 || limit < 1) {
            return ResponseEntity.status(400).body(
                    WrappedResponse<HalPage<MovieDto>>(
                            code = 400,
                            message = "Malformed page or limit supplied"
                    ).validated()
            )
        }

        val entityList = if (featured) {
            repo.findByFeaturedTrue()
        } else {
            repo.findAll().toList()
        }

        val dto = MovieConverter.transform(entityList, page, limit)

        val uriBuilder = UriComponentsBuilder.fromPath("/movies")
        dto._self = HalLink(uriBuilder.cloneBuilder()
                .queryParam("featuredOnly", featured)
                .queryParam("page", page)
                .queryParam("limit", limit)
                .build().toString())

        if (entityList.isNotEmpty() && page > 1) {
            dto.previous = HalLink(uriBuilder.cloneBuilder()
                    .queryParam("featuredOnly", featured)
                    .queryParam("page", (page - 1))
                    .queryParam("limit", limit)
                    .build().toString())
        }

        if ((page * limit) < entityList.size) {
            dto.next = HalLink(uriBuilder.cloneBuilder()
                    .queryParam("featuredOnly", featured)
                    .queryParam("page", (page + 1))
                    .queryParam("limit", limit)
                    .build().toString())
        }

        return ResponseEntity.ok(
                WrappedResponse(
                        code = 200,
                        data = dto
                ).validated()
        )
    }
}