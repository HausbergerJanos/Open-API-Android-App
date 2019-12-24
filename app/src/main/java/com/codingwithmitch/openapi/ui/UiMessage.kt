package com.codingwithmitch.openapi.ui

data class UiMessage(
    val message: String,
    val messageType: UIMessageType
)

sealed class UIMessageType {

    object Toast : UIMessageType()

    object Dialog : UIMessageType()

    class AreYouSureDialog(
        val callback: AreYouSureCallback
    ): UIMessageType()

    object None : UIMessageType()
}