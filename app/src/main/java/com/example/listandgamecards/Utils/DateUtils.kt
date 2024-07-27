package com.example.listandgamecards.Utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun formatDateToMonthYear(dateString: String, inputFormat: String = "yyyy-MM-dd"): String {
    val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
    val date: Date? = inputDateFormat.parse(dateString)
    val outputDateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return date?.let { outputDateFormat.format(it).uppercase(Locale.getDefault()) } ?: ""
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateToCustomFormat(dateString: String, timeZone: String = "UTC"): String {
    val dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
    val zonedDateTime = dateTime.atZone(ZoneId.of(timeZone)) // Original time zone
    val localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime() // Convert to local time
    return localDateTime.format(DateTimeFormatter.ofPattern("EEE MMM dd", Locale.ENGLISH))
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(dateTimeString: String, timeZone: String = "UTC"): String {
    val dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    val zonedDateTime = dateTime.atZone(ZoneId.of(timeZone)) // Original time zone
    val localDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime() // Convert to local time
    return localDateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH))
}