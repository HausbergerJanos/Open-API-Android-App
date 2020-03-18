package com.codingwithmitch.openapi.ui.auth.state

sealed class AuthStateEvent {

    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ) : AuthStateEvent()

    data class RegistrationAttemptEvent(
        val email: String,
        val userName: String,
        val password: String,
        val passwordConfirmation: String
    ) : AuthStateEvent()

    class CheckPreviousAuthEvent : AuthStateEvent()

    class None : AuthStateEvent()
}