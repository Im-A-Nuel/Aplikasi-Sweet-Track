package com.capstone.sweettrack.view.ui.chatbot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatHistory = MutableLiveData<ArrayList<Message>>()
    val chatHistory: LiveData<ArrayList<Message>> = _chatHistory

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    // Inisialisasi model GenerativeAI
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-pro-latest",
        apiKey = "api_key"
    )

    // Fungsi untuk mengirimkan pesan ke Gemini AI dan mendapatkan respons
    fun sendMessage(input: String) {
        // Tambahkan pesan pengguna ke chat history
        val userMessage = Message(input, isLocalUser = true, timestamp = System.currentTimeMillis())
        addMessage(userMessage)

        // Kirim pesan ke API Gemini AI dalam coroutine
        viewModelScope.launch {
            try {
                val inputContent = content {
                    text("$input Jawab dalam bahasa Indonesia dan beri jawaban singkat.")
                }

                // Mengirim prompt ke Gemini AI
                val response = generativeModel.generateContent(inputContent)

                val botMessage = Message(response.text, isLocalUser = false, timestamp = System.currentTimeMillis())
                addMessage(botMessage)

            } catch (e: Exception) {
                _errorMessage.value = "Failed to connect to Gemini AI: ${e.message}"
            }
        }
    }


    // Fungsi untuk menambah pesan ke dalam history chat
    private fun addMessage(message: Message) {
        val list = _chatHistory.value ?: ArrayList()
        list.add(message)
        _chatHistory.value = list
    }
}