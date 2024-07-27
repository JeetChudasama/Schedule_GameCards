package com.example.listandgamecards.usecases

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.listandgamecards.models.Schedule
import java.time.Duration
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

@RequiresApi(Build.VERSION_CODES.O)
fun calculateDuration(gametime: String): Duration {
    val gameDateTime = LocalDateTime.parse(gametime, DateTimeFormatter.ISO_DATE_TIME)
        .atZone(ZoneId.of("UTC"))
        .withZoneSameInstant(ZoneId.systemDefault())

    val now = LocalDateTime.now().atZone(ZoneId.systemDefault())
    return Duration.between(now, gameDateTime)
}

// Function to format the time components as two-digit strings
@RequiresApi(Build.VERSION_CODES.O)
fun formatTimeComponents(duration: Duration): String {
    val days = duration.toDays()
    val hours = duration.toHours() % 24
    val minutes = duration.toMinutes() % 60

    val daysFormatted = String.format("%02d", days)
    val hoursFormatted = String.format("%02d", hours)
    val minutesFormatted = String.format("%02d", minutes)

    return "$daysFormatted | $hoursFormatted | $minutesFormatted"
}