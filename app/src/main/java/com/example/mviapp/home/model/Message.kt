package com.example.mviapp.home.model

data class Message(val id: Int, val text: String, val isSentByMe: Boolean)
sealed class ChatState {
    object Loading : ChatState()
    data class Success(val data: List<Message>) : ChatState()
    data class Error(val message: String) : ChatState()
}