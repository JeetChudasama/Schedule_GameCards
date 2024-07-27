package com.example.listandgamecards.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.listandgamecards.models.Schedule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun filterScheduleByStatus(schedule: List<Schedule>, status: Int): List<Schedule> {
    return schedule.filter { it.st.toInt() == status }
}
@RequiresApi(Build.VERSION_CODES.O)
fun getLatestPastGame(schedule: List<Schedule>): Schedule? {
    val userZoneId = ZoneId.systemDefault() // User's local time zone

    return schedule
        .filter { it.gametime.isNotEmpty() }
        .maxByOrNull {
            try {
                val zonedDateTime = LocalDateTime.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
                    .atZone(ZoneId.of("UTC")) // Assume input time is in UTC
                    .withZoneSameInstant(userZoneId) // Convert to user's local time zone
                zonedDateTime.toLocalDate() // Get local date
            } catch (e: Exception) {
                // Handle parse exception if necessary, e.g., logging
                LocalDate.MIN // Use a default date in case of parse error
            }
        }
}