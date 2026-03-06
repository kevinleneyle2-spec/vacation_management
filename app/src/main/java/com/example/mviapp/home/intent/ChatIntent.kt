package com.example.mviapp.home.intent

sealed class ChatIntent {
    object LoadData : ChatIntent()
    data class SendMessage(val text: String) : ChatIntent()
}