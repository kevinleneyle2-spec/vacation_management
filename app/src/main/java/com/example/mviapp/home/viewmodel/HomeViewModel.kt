package com.example.mviapp.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mviapp.home.intent.ChatIntent
import com.example.mviapp.home.model.ChatState
import com.example.mviapp.home.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<ChatState>(ChatState.Loading)
    val state: StateFlow<ChatState> = _state.asStateFlow()
    private val _intentFlow = MutableSharedFlow<ChatIntent>()

    val test = listOf<String>("fraise", "banane", "cerise", "pomme")


    init {
        processIntents()
    }

    fun sendIntent(intent: ChatIntent) {
        viewModelScope.launch { _intentFlow.emit(intent) }
    }

    private fun processIntents() {
        viewModelScope.launch {
            _intentFlow.collect { intent ->
                when (intent) {
                    is ChatIntent.LoadData -> loadData()
                    is ChatIntent.SendMessage -> sendMessage(intent.text)
                }
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = try {
                val data = listOf(
                    Message(id = 1, text = "hello", isSentByMe = true)
                )
                ChatState.Success(data)
            } catch (e: Exception) {
                ChatState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun sendMessage(text: String) {
        val newMessage = Message(
            id = 1,
            text = text,
            isSentByMe = true
        )
    }
}