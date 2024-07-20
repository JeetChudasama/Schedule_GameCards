package com.example.listandgamecards.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.listandgamecards.models.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun filterScheduleByStatus(schedule: List<Schedule>, status: Int): List<Schedule> {
    return schedule.filter { it.st.toInt() == status }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getLatestPastGame(schedule: List<Schedule>): Schedule? {
    return schedule.maxByOrNull { LocalDate.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME) }
}