package com.example.listandgamecards.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.listandgamecards.Utils.formatDateToMonthYear
import com.example.listandgamecards.models.Schedule
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun getSortedSchedule(schedule: List<Schedule>): List<Schedule> {
    val userZoneId = ZoneId.systemDefault()
    return schedule.sortedBy {
        try {
            val zonedDateTime = LocalDateTime.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .withZoneSameInstant(userZoneId)
            zonedDateTime.toLocalDate()
        } catch (e: Exception) {
            LocalDate.MAX
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//fun getGroupedSchedule(sortedSchedule: List<Schedule>): Map<String, List<Schedule>> {
//    val userZoneId = ZoneId.systemDefault()
//    return sortedSchedule.groupBy {
//        try {
//            val zonedDateTime = LocalDateTime.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
//                .atZone(ZoneId.of("Asia/Kolkata"))
//                .withZoneSameInstant(userZoneId)
//            formatDateToMonthYear(zonedDateTime.toLocalDate().toString())
//        } catch (e: Exception) {
//            ""
//        }
//    }
//}

@RequiresApi(Build.VERSION_CODES.O)
fun getNextGameIndex(sortedSchedule: List<Schedule>): Int {
    val today = LocalDate.now(ZoneId.systemDefault())
    return sortedSchedule.indexOfFirst {
        try {
            val zonedDateTime = LocalDateTime.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
                .atZone(ZoneId.of("Asia/Kolkata"))
                .withZoneSameInstant(ZoneId.systemDefault())
            zonedDateTime.toLocalDate().isAfter(today)
        } catch (e: Exception) {
            false
        }
    }
}
fun updateGameClockAndQuarter(clock: String, gameQuarter: String): Pair<String, String> {
    val clockParts = clock.split(":")
    val minutes = clockParts.getOrNull(0)?.toIntOrNull() ?: 0
    val seconds = clockParts.getOrNull(1)?.toDoubleOrNull() ?: 0.0

    var updatedGameQuarter = gameQuarter

    if (updatedGameQuarter.contains("QTR")) {
        if (minutes >= 10) {
            updatedGameQuarter = when (updatedGameQuarter) {
                "QTR 1" -> "Halftime 1"
                "QTR 2" -> "Halftime 2"
                "QTR 3" -> "Halftime 3"
                "QTR 4" -> "FINAL"
                else -> updatedGameQuarter
            }
            return Pair("00:00.0", updatedGameQuarter)
        }
    } else if (updatedGameQuarter.contains("Halftime")) {
        if (minutes >= 15) {
            updatedGameQuarter = when (updatedGameQuarter) {
                "Halftime" -> "QTR 2"
                "Halftime 2" -> "QTR 3"
                "Halftime 3" -> "QTR 4"
                else -> "FINAL"
            }
            return Pair("00:00.0", updatedGameQuarter)
        }
    }

    val updatedSeconds = seconds + 10
    val updatedMinutes = minutes + (updatedSeconds / 60).toInt()
    val newSeconds = updatedSeconds % 60
    val newClock = "%02d:%04.1f".format(updatedMinutes, newSeconds)

    return Pair(newClock, updatedGameQuarter)
}





