package com.codingwithmitch.openapi.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 * Generic [ViewModel] class which should be used as a parent for other view models.
 *
 * [StateEvent] fires up to trigger events, which should be handled in child classes.
 * (For example fetch some data from repository.)
 *
 * [ViewState] represents data which is inside the view.
 * (For example data fields on the actual screen.)
 */
abstract class BaseViewModel<StateEvent, ViewState> : ViewModel() {

    protected val _stateEvent: MutableLiveData<StateEvent> = MutableLiveData()
    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    /**
     * Returns [ViewState] as a [LiveData] to observe it any change
     */
    val viewState: LiveData<ViewState>
        get() = _viewState

    /**
     * Returns a [LiveData] object which wraps [ViewState] in a [DataState]
     */
    val dataState: LiveData<DataState<ViewState>> =
        Transformations
            .switchMap(_stateEvent) { stateEvent ->
                // If there is a different StateEvent this function
                // will be triggered
                stateEvent?.let {
                    handleStateEvent(stateEvent)
                }
            }


    /**
     * Set different [StateEvent] which triggers the [handleStateEvent] function
     * in the child view model.
     */
    fun setStateEvent(stateEvent: StateEvent) {
        _stateEvent.value = stateEvent
    }

    /**
     * Set a new [ViewState].
     */
    fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }

    /**
     * Returns the corresponding [ViewState] variable. If it is null,
     * creates a new instance variable with calling the [initNewViewState] function.
     */
    fun getCurrentViewState(): ViewState {
        return viewState.value ?: initNewViewState()
    }

    /**
     * Returns a new [ViewState] instance variable.
     */
    abstract fun initNewViewState(): ViewState

    /**
     * Handle [StateEvent] change in child view models.
     * Returns a [LiveData] object which contains a [ViewState] wrapped in a [DataState]
     */
    abstract fun handleStateEvent(stateEvent: StateEvent): LiveData<DataState<ViewState>>
}