package com.example.listandgamecards.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.listandgamecards.Utils.formatDateToMonthYear
import com.example.listandgamecards.models.Schedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ScheduleBusinessLogic.kt
@RequiresApi(Build.VERSION_CODES.O)
fun getSortedSchedule(schedule: List<Schedule>): List<Schedule> {
    return schedule.sortedBy {
        LocalDate.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
    }
}

fun getGroupedSchedule(sortedSchedule: List<Schedule>): Map<String, List<Schedule>> {
    return sortedSchedule.groupBy { formatDateToMonthYear(it.gametime) }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getNextGameIndex(sortedSchedule: List<Schedule>): Int {
    val today = LocalDate.now()
    return sortedSchedule.indexOfFirst {
        try {
            val gameDate = LocalDate.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
            gameDate.isAfter(today)
        } catch (e: Exception) {
            false
        }
    }
}