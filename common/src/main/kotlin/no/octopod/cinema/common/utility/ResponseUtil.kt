package no.octopod.cinema.common.utility

import no.octopod.cinema.common.dto.WrappedResponse
import org.springframework.http.ResponseEntity

object ResponseUtil {
    fun <T>getWrappedResponse(rawStatusCode: Int, data: T? = null, message: String? = null): ResponseEntity<WrappedResponse<T>> {
        return ResponseEntity.status(rawStatusCode).body(
                WrappedResponse(
                        code = rawStatusCode,
                        data = data,
                        message = message
                ).validated()
        )
    }
}