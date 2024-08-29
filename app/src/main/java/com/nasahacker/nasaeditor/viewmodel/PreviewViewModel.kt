package com.nasahacker.nasaeditor.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PreviewViewModel : ViewModel() {

    private val _fileUri = MutableLiveData<Uri?>()
    val fileUri: LiveData<Uri?> get() = _fileUri

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun setFileUri(uriString: String?) {
        if (uriString != null) {
            try {
                _fileUri.value = Uri.parse(uriString)
                _errorMessage.value = null
            } catch (e: Exception) {
                _fileUri.value = null
                _errorMessage.value = "Error loading file: ${e.message}"
            }
        } else {
            _fileUri.value = null
            _errorMessage.value = "File URI is missing"
        }
    }
}
