package com.denis.backgroundremover.ui

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.backgroundremover.data.BackgroundRemoverClient
import kotlinx.coroutines.launch

class MainViewModel(private val client: BackgroundRemoverClient) : ViewModel() {

    // State untuk UI
    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    var resultImageBytes by mutableStateOf<ByteArray?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var toastMessage by mutableStateOf<String?>(null)
        private set

    fun onImageSelected(uri: Uri?) {
        selectedImageUri = uri
        resultImageBytes = null
    }

    fun removeBackground() {
        val uri = selectedImageUri ?: return
        
        isLoading = true
        viewModelScope.launch {
            val result = client.removeBackground(uri)
            isLoading = false
            if (result != null) {
                resultImageBytes = result
                toastMessage = "Berhasil menghapus background!"
            } else {
                toastMessage = "Gagal memproses gambar."
            }
        }
    }

    fun clearToastMessage() {
        toastMessage = null
    }
}
