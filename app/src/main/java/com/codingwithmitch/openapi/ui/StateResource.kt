package com.codingwithmitch.openapi.ui

/**
 * Observe loading constantly. It is not an event driving thing.
 */
data class Loading(val isLoading: Boolean)

/**
 * Data class for errors.
 */
data class StateError(val response: Response)
/**
 * Contains data and the [Response] return from request.
 * It is wrapped in an [Event] class so that the user can see the data only once.
 */
data class Data<T>(val data: Event<T>?, val response: Event<Response>?)

/**
 * If there is a response message (for example about success operation or failure)
 * declares the message and the way how should be it shown via the [ResponseType].
 */
data class Response(val message: String?, val responseType: ResponseType)

/**
 * Handles how to show response message.
 */
sealed class ResponseType {
    /** In this case, we show response message with a toast. */
    object Toast : ResponseType()

    /** In this case, we show response message with a dialog. */
    object Dialog : ResponseType()

    /** In this case, we not show any response message. */
    object None : ResponseType()
}

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content

    override fun toString(): String {
        return "Event(content=$content, hasBeenHandled=$hasBeenHandled)"
    }

    companion object{

        // we don't want an event if the data is null
        fun <T> dataEvent(data: T?): Event<T>?{
            data?.let {
                return Event(it)
            }
            return null
        }

        // we don't want an event if the response is null
        fun responseEvent(response: Response?): Event<Response>?{
            response?.let{
                return Event(response)
            }
            return null
        }
    }


}