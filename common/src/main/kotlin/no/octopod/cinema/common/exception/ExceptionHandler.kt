package no.octopod.cinema.common.exception

import no.octopod.cinema.common.dto.WrappedResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.RuntimeException

@ControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {
    companion object {
        const val INTERNAL_SERVER_ERROR = "An internal server error occured"
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedExceptions(ex: Exception, req: WebRequest): ResponseEntity<Any> {
        return handleExceptionInternal(
                RuntimeException(INTERNAL_SERVER_ERROR),
                null,
                HttpHeaders(),
                HttpStatus.valueOf(500),
                req
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleNumberFormatException(ex: Exception, req: WebRequest): ResponseEntity<Any> {
        return handleExceptionInternal(
                RuntimeException("Wrong argument type supplied"),
                null,
                HttpHeaders(),
                HttpStatus.valueOf(400),
                req
        )
    }

    override fun handleExceptionInternal(
            ex: Exception,
            body: Any?,
            headers: HttpHeaders,
            status: HttpStatus,
            request: WebRequest
    ) : ResponseEntity<Any> {
        val dto = WrappedResponse<Any>(
                code = status.value(),
                message = ex.message
        ).validated()

        return ResponseEntity(dto, headers, status)
    }
}