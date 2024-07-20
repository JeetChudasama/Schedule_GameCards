package com.example.listandgamecards

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.listandgamecards.View.MainScreen
import com.example.listandgamecards.ViewModel.MainViewModelFactory
import com.example.listandgamecards.models.RepositoryImpl
import com.example.listandgamecards.ViewModel.ScreenViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ScreenViewModel by viewModels {
        MainViewModelFactory(RepositoryImpl(applicationContext))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize().background(Color.Black)
            ) {
                MainScreen(viewModel)
            }
        }

    }
}