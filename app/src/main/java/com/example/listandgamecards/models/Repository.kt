package com.example.listandgamecards.models

import android.content.Context
import com.google.gson.Gson

class RepositoryImpl(private val applicationContext: Context) : ScheduleRepository {
    override suspend fun getSchedule(): List<Schedule> {
        val jsonFileString = getJsonDataFromSchedule(applicationContext, "Schedule.json")
        val gson = Gson()
        val scheduleWrapper: ScheduleRoot = gson.fromJson(jsonFileString, ScheduleRoot::class.java)
        return scheduleWrapper.data.schedules
    }

    override suspend fun getGames(): Entry {
        val jsonFileString = getJsonDataFromGame(applicationContext, "GameCardData.json")
        val gson = Gson()
        val gameWrapper: GameRoot = gson.fromJson(jsonFileString, GameRoot::class.java)
        return gameWrapper.entry
    }

    override suspend fun getTeams(): List<Team> {
        val jsonFileString = getJsonDataFromTeams(applicationContext, "teams.json")
        val gson = Gson()
        val teamsWrapper: TeamRoot = gson.fromJson(jsonFileString, TeamRoot::class.java)
        return teamsWrapper.data.teams
    }
}

