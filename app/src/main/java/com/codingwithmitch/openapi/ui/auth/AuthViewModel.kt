package com.codingwithmitch.openapi.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.codingwithmitch.openapi.api.auth.request.LoginRequest
import com.codingwithmitch.openapi.api.auth.request.RegistrationRequest
import com.codingwithmitch.openapi.api.auth.responses.LoginResponse
import com.codingwithmitch.openapi.api.auth.responses.RegistrationResponse
import com.codingwithmitch.openapi.models.AuthToken
import com.codingwithmitch.openapi.repository.auth.AuthRepository
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent
import com.codingwithmitch.openapi.ui.auth.state.AuthStateEvent.*
import com.codingwithmitch.openapi.ui.auth.state.AuthViewState
import com.codingwithmitch.openapi.ui.auth.state.LoginFields
import com.codingwithmitch.openapi.ui.auth.state.RegistrationFields
import com.codingwithmitch.openapi.util.AbsentLiveData
import com.codingwithmitch.openapi.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        return when(stateEvent) {

            is LoginAttemptEvent -> {
                authRepository.login(
                    LoginRequest(
                    stateEvent.email,
                    stateEvent.password
                )
                )
            }

            is RegistrationAttempEvent -> {
                authRepository.registration(RegistrationRequest(
                    stateEvent.email,
                    stateEvent.userName,
                    stateEvent.password,
                    stateEvent.passwordConfirmation
                ))
            }

            is CheckPreviousAuthEvent -> {
                return authRepository.checkPrevAuthUser()
            }

            is None -> {
                return object: LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(
                            data = null,
                            response = null
                        )
                    }
                }
            }
        }
    }

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentViewState()
        if (update.registrationFields == registrationFields) {
            return
        }

        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewState()
        if (update.loginFields == loginFields) {
            return
        }

        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewState()
        if (update.authToken == authToken) {
            return
        }

        update.authToken = authToken
        _viewState.value = update
    }

    fun cancelActiveJobs() {
        handlePendingData()
        authRepository.cancelActiveJobs()
    }

    fun handlePendingData() {
       setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        authRepository.cancelActiveJobs()
    }
}