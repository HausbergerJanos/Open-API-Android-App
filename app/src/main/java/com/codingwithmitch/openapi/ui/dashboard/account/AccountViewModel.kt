package com.codingwithmitch.openapi.ui.dashboard.account

import androidx.lifecycle.LiveData
import com.codingwithmitch.openapi.models.AccountProperties
import com.codingwithmitch.openapi.repository.dashboard.AccountRepository
import com.codingwithmitch.openapi.session.SessionManager
import com.codingwithmitch.openapi.ui.BaseViewModel
import com.codingwithmitch.openapi.ui.DataState
import com.codingwithmitch.openapi.ui.dashboard.account.state.AccountStateEvent
import com.codingwithmitch.openapi.ui.dashboard.account.state.AccountStateEvent.*
import com.codingwithmitch.openapi.ui.dashboard.account.state.AccountViewState
import com.codingwithmitch.openapi.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
) : BaseViewModel<AccountStateEvent, AccountViewState>() {

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when (stateEvent) {

            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                }?: AbsentLiveData.create()
            }

            is UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.saveAccountProperties(
                        authToken,
                        AccountProperties(
                            authToken.account_pk,
                            stateEvent.email,
                            stateEvent.userName
                        )
                    )
                }?: AbsentLiveData.create()
            }

            is ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmNewPassword)
                }?: AbsentLiveData.create()
            }

            is None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentViewState()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logOut() {
        sessionManager.logout()
    }

    fun cancelActiveJobs() {
        handlePendingData()
        accountRepository.cancelActiveJobs()
    }

    fun handlePendingData() {
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        accountRepository.cancelActiveJobs()
    }
}