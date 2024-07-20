package com.example.listandgamecards.models

interface ScheduleRepository {
    suspend fun getSchedule(): List<Schedule>
    suspend fun getGames(): Entry
    suspend fun getTeams(): List<Team>
}
