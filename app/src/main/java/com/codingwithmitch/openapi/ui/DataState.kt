package com.codingwithmitch.openapi.ui

/**
* A generic class that holds a value with its [Data], [Loading] and [StateError] status.
 */
data class DataState<T>(
    var loading: Loading = Loading(false),
    var error: Event<StateError>? = null,
    var data: Data<T>? = null
) {
    /**
     * This helper methods help to quickly build resource objects according
     * the corresponding data, error and loading status.
     */
    companion object {
        /**
         * Represents error state, which provides an error [Response].
         */
        fun <T> error(response: Response): DataState<T> {
            return DataState(
                error = Event(StateError(response))
            )
        }

        /**
         * Represents loading state, which optionally returns the cached data.
         */
        fun <T> loading(isLoading: Boolean, cachedData: T? = null): DataState<T> {
            return DataState(
                loading = Loading(isLoading),
                data = Data(
                    Event.dataEvent(cachedData),
                    null
                )
            )
        }

        /**
         * Represents data state, which optionally returns the [Data] from the request
         * and the [Response].
         */
        fun <T> data(data: T? = null, response: Response? = null): DataState<T> {
            return DataState(
                data = Data(
                    Event.dataEvent(data),
                    Event.responseEvent(response))
            )
        }
    }
}