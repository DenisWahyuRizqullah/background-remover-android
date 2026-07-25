package com.denis.backgroundremover

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.denis.backgroundremover.data.BackgroundRemoverClient
import com.denis.backgroundremover.ui.AppScreen
import com.denis.backgroundremover.ui.LobbyScreen
import com.denis.backgroundremover.ui.MainViewModel

enum class Screen {
    Lobby,
    BackgroundRemover
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf(Screen.Lobby) }
                    
                    val client = BackgroundRemoverClient(applicationContext)
                    val mainViewModel: MainViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return MainViewModel(client) as T
                            }
                        }
                    )
                    
                    BackHandler(enabled = currentScreen != Screen.Lobby) {
                        currentScreen = Screen.Lobby
                    }

                    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                        when (screen) {
                            Screen.Lobby -> {
                                LobbyScreen(
                                    onNavigateToBackgroundRemover = {
                                        currentScreen = Screen.BackgroundRemover
                                    }
                                )
                            }
                            Screen.BackgroundRemover -> {
                                AppScreen(
                                    viewModel = mainViewModel,
                                    onBack = { currentScreen = Screen.Lobby }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
