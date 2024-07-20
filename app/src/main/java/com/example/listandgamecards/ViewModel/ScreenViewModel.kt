package com.example.listandgamecards.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listandgamecards.models.Entry
import com.example.listandgamecards.models.Schedule
import com.example.listandgamecards.models.ScheduleRepository
import com.example.listandgamecards.models.Team
import kotlinx.coroutines.launch

class ScreenViewModel(private val repository: ScheduleRepository) : ViewModel() {
    private val _schedule = MutableLiveData<List<Schedule>>()
    val schedule: LiveData<List<Schedule>> = _schedule

    private val _games = MutableLiveData<Entry>()
    val games: LiveData<Entry> = _games

    private val _teams = MutableLiveData<List<Team>>()
    val teams: LiveData<List<Team>> = _teams

    init {
        viewModelScope.launch {
            _schedule.value = repository.getSchedule()
            _games.value = repository.getGames()
            _teams.value = repository.getTeams()
        }
    }
}



