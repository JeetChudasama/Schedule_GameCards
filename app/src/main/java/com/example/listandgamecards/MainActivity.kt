package com.example.listandgamecards

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listandgamecards.View.MainScreen
import com.example.listandgamecards.models.Entry
import com.example.listandgamecards.models.GameRoot
import com.example.listandgamecards.models.MainViewModelFactory
import com.example.listandgamecards.models.RepositoryImpl
import com.example.listandgamecards.models.Schedule
import com.example.listandgamecards.models.ScheduleRoot
import com.example.listandgamecards.models.ScreenViewModel
import com.example.listandgamecards.models.ShduleData
import com.example.listandgamecards.models.Team
import com.example.listandgamecards.models.TeamRoot
import com.example.listandgamecards.models.getJsonDataFromGame
import com.example.listandgamecards.models.getJsonDataFromSchedule
import com.example.listandgamecards.models.getJsonDataFromTeams
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: ScreenViewModel by viewModels {
        MainViewModelFactory(RepositoryImpl(applicationContext))
    }

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