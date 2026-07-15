package com.denis.backgroundremover

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.denis.backgroundremover.data.BackgroundRemoverClient
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen()
                }
            }
        }
    }
}

@Composable
fun AppScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State untuk menyimpan URI gambar yang dipilih dari galeri
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // State untuk menyimpan hasil byte array gambar dari API
    var resultImageBytes by remember { mutableStateOf<ByteArray?>(null) }

    // State Loading
    var isLoading by remember { mutableStateOf(false) }

    // Client Network
    val client = remember { BackgroundRemoverClient(context) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        resultImageBytes = null // Reset hasil lama jika memilih foto baru
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (selectedImageUri != null && resultImageBytes == null) {
            // Tampilkan foto mentah sebelum diproses
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Original Image",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        } else if (resultImageBytes != null) {
            // Tampilkan foto hasil setelah background dihapus
            val bitmap = remember(resultImageBytes) {
                BitmapFactory.decodeByteArray(resultImageBytes, 0, resultImageBytes!!.size)
            }
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "No Background Image",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada gambar yang dipilih")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text("Pilih Foto")
                }

                Button(
                    onClick = {
                        selectedImageUri?.let { uri ->
                            isLoading = true
                            coroutineScope.launch {
                                val result = client.removeBackground(uri)
                                isLoading = false
                                if (result != null) {
                                    resultImageBytes = result
                                    Toast.makeText(context, "Berhasil menghapus background!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Gagal memproses gambar.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    enabled = selectedImageUri != null
                ) {
                    Text("Hapus Background")
                }
            }
        }
    }
}