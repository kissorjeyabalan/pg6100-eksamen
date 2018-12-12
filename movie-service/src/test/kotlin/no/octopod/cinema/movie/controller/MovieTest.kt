package no.octopod.cinema.movie.controller

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.octopod.cinema.common.dto.MovieDto
import no.octopod.cinema.movie.repository.MovieRepository
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.ZonedDateTime

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class MovieTest {
    @LocalServerPort private var port = 0
    @Autowired lateinit var movieRepo: MovieRepository

    @Before
    fun before() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        movieRepo.deleteAll()
        given().get("/movies")
                .then()
                .statusCode(200)
                .body("data.data.size()", equalTo(0))
    }

    @Test
    fun testCreateAndGetMovie() {
        val movie = MovieDto(
                title = "Movie 1",
                description = "Description of movie",
                image_path = "url",
                release_date = ZonedDateTime.now().withFixedOffsetZone().withNano(0),
                featured = true
        )

        val moviePath = given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(movie)
                .post("/movies")
                .then()
                .statusCode(201)
                .extract().header("Location")

        given().get(moviePath)
                .then()
                .statusCode(200)
                .body("data.title", equalTo(movie.title))
    }

    @Test
    fun testCreateAndGetAllMovies() {
        val movie = MovieDto(
                title = "Movie 1",
                description = "Description of movie",
                image_path = "url",
                release_date = ZonedDateTime.now().withFixedOffsetZone().withNano(0),
                featured = true
        )

        val altMovie = MovieDto(
                title = "Movie 2",
                description = "Description of movie",
                image_path = "url",
                release_date = ZonedDateTime.now().withFixedOffsetZone().withNano(0),
                featured = false
        )

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(movie)
                .post("/movies")
                .then()
                .statusCode(201)

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(altMovie)
                .post("/movies")
                .then()
                .statusCode(201)

        given().get("/movies")
                .then()
                .statusCode(200)
                .body("data.count", equalTo(2))
                .body("data.data.title", hasItems(movie.title, altMovie.title))

        given().get("/movies?featuredOnly=true")
                .then()
                .statusCode(200)
                .body("data.count", equalTo(1))
                .body("data.data.title", hasItem(movie.title))
                .body("data.data.title", not(hasItem(altMovie.title)))
    }

    @Test
    fun testCreateInvalidMovie() {
        val movie = MovieDto(
                title = null,
                description = "Description of movie",
                image_path = "url",
                release_date = null,
                featured = true
        )
        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(movie)
                .post("/movies")
                .then()
                .statusCode(400)
    }

    @Test
    fun testGetInvalidMovieId() {
        given().get("/movies/one")
                .then()
                .statusCode(404)

        given().get("/movies/1")
                .then()
                .statusCode(404)
    }

    @Test
    fun testInvalidPageLimit() {
        given().get("/movies?page=0&limit=0")
                .then()
                .statusCode(400)
    }

    @Test
    fun testPaginationForAllMovies() {
        val movie = MovieDto(
                title = "Movie 1",
                description = "Description of movie",
                image_path = "url",
                release_date = ZonedDateTime.now().withFixedOffsetZone().withNano(0),
                featured = true
        )

        val altMovie = MovieDto(
                title = "Movie 2",
                description = "Description of movie",
                image_path = "url",
                release_date = ZonedDateTime.now().withFixedOffsetZone().withNano(0),
                featured = false
        )

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(movie)
                .post("/movies")
                .then()
                .statusCode(201)

        given().auth()
                .basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(altMovie)
                .post("/movies")
                .then()
                .statusCode(201)

        val nextPage = given()
                .get("/movies?limit=1")
                .then()
                .statusCode(200)
                .body("data.pages", equalTo(2))
                .body("data.count", equalTo(2))
                .body("data._links.next.href", notNullValue())
                .body("data._links.previous", nullValue())
                .extract().body().jsonPath().getString("data._links.next.href")

        given().get(nextPage)
                .then()
                .statusCode(200)
                .body("data.pages", equalTo(2))
                .body("data.count", equalTo(2))
                .body("data._links.next", nullValue())
                .body("data._links.previous.href", notNullValue())
    }
}