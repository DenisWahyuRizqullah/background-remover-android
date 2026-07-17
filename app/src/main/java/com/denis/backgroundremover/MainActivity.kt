package com.denis.backgroundremover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.denis.backgroundremover.data.BackgroundRemoverClient
import com.denis.backgroundremover.ui.AppScreen
import com.denis.backgroundremover.ui.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Manual Factory untuk menyuntikkan BackgroundRemoverClient ke ViewModel
                    val client = BackgroundRemoverClient(applicationContext)
                    val mainViewModel: MainViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return MainViewModel(client) as T
                            }
                        }
                    )
                    
                    AppScreen(viewModel = mainViewModel)
                }
            }
        }
    }
}
