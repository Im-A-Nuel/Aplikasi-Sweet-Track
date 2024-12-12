package com.capstone.sweettrack.view.ui.result.resultocr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.sweettrack.data.Repository
import com.capstone.sweettrack.data.remote.response.EditCalorieResponse
import com.capstone.sweettrack.data.remote.response.OcrResponse
import kotlinx.coroutines.launch

class ResultOcrViewModel(private val repository: Repository) : ViewModel() {


    private val _updateResult = MutableLiveData<EditCalorieResponse>()
    val updateResult: LiveData<EditCalorieResponse> get() = _updateResult


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun addResultToHistory(uri: String, data: OcrResponse) {
        viewModelScope.launch {
            println("data : $data}")
            repository.addResultOcrToHistoryScan(uri, data)

        }
    }

    fun updateUserCalorieDay(
        calorie: Double
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.updateUserCalorieDay(calorie)
                _updateResult.postValue(response)
            } catch (e: Exception) {
                _updateResult.postValue(
                    EditCalorieResponse(
                        statusCode = 500,
                        error = true,
                        message = "Error",
                        describe = "jika anda memakan makanan ini maka melebihi kalori harian anda"
                    )
                )
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}