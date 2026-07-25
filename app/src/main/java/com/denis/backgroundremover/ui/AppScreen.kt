package com.denis.backgroundremover.ui

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        viewModel.onImageSelected(uri)
    }

    // Handle Toast messages from ViewModel
    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hapus Background") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            AppScreenContent(
                isLoading = viewModel.isLoading,
                selectedImageUri = viewModel.selectedImageUri,
                resultImageBytes = viewModel.resultImageBytes,
                onPickImage = { launcher.launch("image/*") },
                onRemoveBg = { viewModel.removeBackground() },
                onSaveImage = { viewModel.saveImage(context.contentResolver) }
            )
        }
    }
}

@Composable
fun AppScreenContent(
    isLoading: Boolean,
    selectedImageUri: Uri?,
    resultImageBytes: ByteArray?,
    onPickImage: () -> Unit,
    onRemoveBg: () -> Unit,
    onSaveImage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (selectedImageUri != null && resultImageBytes == null) {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = "Original Image",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        } else if (resultImageBytes != null) {
            val bitmap = remember(resultImageBytes) {
                BitmapFactory.decodeByteArray(resultImageBytes, 0, resultImageBytes.size)
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
                Button(onClick = onPickImage) {
                    Text("Pilih Foto")
                }

                Button(
                    onClick = onRemoveBg,
                    enabled = selectedImageUri != null
                ) {
                    Text("Hapus Background")
                }
            }

            if (resultImageBytes != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onSaveImage,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Simpan ke Galeri")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppScreenPreview() {
    MaterialTheme {
        AppScreenContent(
            isLoading = false,
            selectedImageUri = null,
            resultImageBytes = null,
            onPickImage = {},
            onRemoveBg = {},
            onSaveImage = {}
        )
    }
}
