package com.denis.backgroundremover.ui

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.backgroundremover.data.BackgroundRemoverClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

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

    fun saveImage(contentResolver: ContentResolver) {
        val bytes = resultImageBytes ?: return

        viewModelScope.launch {
            isLoading = true
            val success = withContext(Dispatchers.IO) {
                try {
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    val filename = "BG_Remover_${System.currentTimeMillis()}.png"
                    
                    val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/BackgroundRemover")
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }

                    val imageUri = contentResolver.insert(imageCollection, contentValues)
                    
                    imageUri?.let { uri ->
                        contentResolver.openOutputStream(uri)?.use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentValues.clear()
                            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                            contentResolver.update(uri, contentValues, null, null)
                        }
                        true
                    } ?: false
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
            
            isLoading = false
            toastMessage = if (success) "Gambar berhasil disimpan ke Galeri!" else "Gagal menyimpan gambar."
        }
    }
}
