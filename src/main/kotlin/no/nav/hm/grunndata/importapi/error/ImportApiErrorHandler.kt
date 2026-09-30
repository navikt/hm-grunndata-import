package no.nav.hm.grunndata.importapi.error

import io.micronaut.context.annotation.Replaces
import io.micronaut.core.convert.exceptions.ConversionErrorException
import io.micronaut.http.*
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ConversionErrorHandler
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.http.server.exceptions.JsonExceptionHandler
import org.slf4j.LoggerFactory
import java.util.*
import jakarta.inject.Singleton
import tools.jackson.core.JacksonException
import tools.jackson.core.exc.StreamReadException
import tools.jackson.databind.exc.InvalidFormatException
import tools.jackson.databind.exc.ValueInstantiationException
import tools.jackson.module.kotlin.KotlinInvalidNullException

@Produces
@Singleton
class ImportApiErrorHandler : ExceptionHandler<ImportApiError, HttpResponse<ErrorMessage>> {

    override fun handle(request: HttpRequest<*>, error: ImportApiError): HttpResponse<ErrorMessage> {
        val response =  when (error.type) {
            ErrorType.NOT_FOUND -> HttpResponse.notFound(createMessage(error))
            ErrorType.MISSING_PARAMETER, ErrorType.INVALID_VALUE, ErrorType.PARSE_ERROR -> HttpResponse.badRequest(createMessage(error))
            ErrorType.CONFLICT -> HttpResponseFactory.INSTANCE.status<ErrorMessage>(HttpStatus.CONFLICT).body(createMessage(error))
            ErrorType.UNKNOWN -> HttpResponse.serverError(createMessage(error))
        }
        if (error.type != ErrorType.NOT_FOUND) { LOG.error(response.body().toString()) }
        return response
    }
    private fun createMessage(error: ImportApiError) = ErrorMessage(message = error.message!!, errorType = error.type)
}

@Produces
@Singleton
@Replaces(ConversionErrorHandler::class)
class ConversionExceptionHandler : ExceptionHandler<ConversionErrorException, HttpResponse<ErrorMessage>> {

    override fun handle(
        request: HttpRequest<*>,
        exception: ConversionErrorException
    ): HttpResponse<ErrorMessage> {
        val response = when (exception.cause) {
            is JacksonException -> handleJacksonException(exception.cause as JacksonException)
            else -> HttpResponse.serverError(ErrorMessage(exception.message, ErrorType.UNKNOWN))
        }
        LOG.error(response.body().toString())
        return response
    }
}

@Produces
@Singleton
@Replaces(JsonExceptionHandler::class)
class ApiJsonErrorHandler : ExceptionHandler<JacksonException, HttpResponse<ErrorMessage>> {


    override fun handle(
        request: HttpRequest<*>,
        exception: JacksonException
    ): HttpResponse<ErrorMessage> {
        val response = handleJacksonException(exception)
        LOG.error(response.body().toString())
        return response
    }

}

private fun handleJacksonException(error: JacksonException): HttpResponse<ErrorMessage> {
    return when (error) {
        is StreamReadException -> HttpResponse
                .badRequest(ErrorMessage("Parse error: at ${error.location}", ErrorType.PARSE_ERROR))
        is KotlinInvalidNullException -> HttpResponse
                .badRequest(ErrorMessage("Missing parameter: ${error.propertyName}", ErrorType.MISSING_PARAMETER))
        is InvalidFormatException -> HttpResponse
                .badRequest(ErrorMessage("Invalid value: ${error.value} at ${error.pathReference}",
                    ErrorType.INVALID_VALUE
                ))
        is ValueInstantiationException -> HttpResponse.badRequest(ErrorMessage("Wrong value: ${error.message} at ${error.pathReference}",
            ErrorType.INVALID_VALUE
        ))
        else -> HttpResponse.badRequest(ErrorMessage("Bad Json: ${error.localizedMessage}", ErrorType.UNKNOWN))
    }
}

enum class ErrorType {
    PARSE_ERROR, MISSING_PARAMETER, INVALID_VALUE, CONFLICT, NOT_FOUND, UNKNOWN
}

// Global error logger for errorhandler
private val LOG = LoggerFactory.getLogger("HttpRequestErrorHandler")

class ImportApiError(message: String, val type: ErrorType) : Exception(message)

data class ErrorMessage (val message : String, val errorType: ErrorType, val errorRef: UUID = UUID.randomUUID())

