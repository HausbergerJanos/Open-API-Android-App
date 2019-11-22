package com.codingwithmitch.openapi.ui.auth.state

import com.codingwithmitch.openapi.models.AuthToken

data class AuthViewState(
    var registrationFields: RegistrationFields? = RegistrationFields(),
    var loginFields: LoginFields? = LoginFields(),
    var authToken: AuthToken? = null
)

data class RegistrationFields(
    var registrationEmail: String? = null,
    var registrationUserName: String? = null,
    var registrationPassword: String? = null,
    var registrationConfirmPassword: String? = null
) {

    class RegistrationError {
        companion object {

            fun mustFillAllFields(): String {
                return "All fields are required."
            }

            fun passwordsDoNotMatch(): String {
                return "Password must match."
            }

            fun none(): String {
                return "None."
            }
        }
    }

    fun isValidForRegistration(): String {
        if (registrationEmail.isNullOrEmpty() ||
                registrationUserName.isNullOrEmpty() ||
                registrationPassword.isNullOrEmpty() ||
                registrationConfirmPassword.isNullOrEmpty()) {
            return RegistrationError.mustFillAllFields()
        }

        if (!registrationPassword.equals(registrationConfirmPassword)) {
            return RegistrationError.passwordsDoNotMatch()
        }

        return RegistrationError.none()
    }
}

data class LoginFields(
    var loginEmail: String? = null,
    var loginPassword: String? = null
) {
    class LoginError {
        companion object{

            fun mustFillAllFields(): String{
                return "You can't login without an email and password."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValidForLogin(): String{

        if(loginEmail.isNullOrEmpty() || loginPassword.isNullOrEmpty()) {
            return LoginError.mustFillAllFields()
        }

        return LoginError.none()
    }

    override fun toString(): String {
        return "LoginState(email=$loginEmail, password=$loginPassword)"
    }
}