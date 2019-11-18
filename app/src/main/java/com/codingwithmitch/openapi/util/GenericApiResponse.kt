package com.codingwithmitch.openapi.util

import retrofit2.Response

/**
 * Generic class which handles api response based on different situations. Checks the response code and that the
 * response was valid, or not. In most projects we always need a success, error and empty response.
 *
 * Copied from Architecture components google sample:
 * https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/api/ApiResponse.kt
 */
@Suppress("unused") // T is used in extending classes
sealed class GenericApiResponse<T> {

    companion object {

        // Builder for handling error. If there is a throwable, creates an ApiErrorResponse.
        // Invoked when a network exception occurred talking to the server or when an unexpected
        // exception occurred creating the request or processing the response. It will return
        // the error message.
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error")
        }

        // Invoked for a received HTTP response.
        // Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
        fun <T> create(response: Response<T>): GenericApiResponse<T> {

            if(response.isSuccessful){
                // Response is successful. Everything is fine.
                // Handle response according to HTTP codes.
                val body = response.body()
                return if (body == null || response.code() == 204) {
                    ApiEmptyResponse()
                } else if(response.code() == 401){
                    ApiErrorResponse("401 Unauthorized. Token may be invalid.")
                } else {
                    ApiSuccessResponse(body = body)
                }
            } else{
                // Response is not successful, so we have to provide an error response.
                val msg = response.errorBody()?.string()
                val errorMsg = if (msg.isNullOrEmpty()) {
                    response.message()
                } else {
                    msg
                }
                return ApiErrorResponse(errorMsg ?: "unknown error")
            }
        }
    }
}

/**
 * API success response. Return the body of the response.
 */
data class ApiSuccessResponse<T>(val body: T) : GenericApiResponse<T>()

/**
 * API error response. Notify listeners with an error message.
 */
data class ApiErrorResponse<T>(val errorMessage: String) : GenericApiResponse<T>()

/**
 * Separate class for HTTP 204 responses so that we can make ApiSuccessResponse's body non-null.
 */
class ApiEmptyResponse<T> : GenericApiResponse<T>()
