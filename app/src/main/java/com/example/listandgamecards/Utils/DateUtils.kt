package com.example.listandgamecards.Utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
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
fun formatDateToCustomFormat(dateString: String): String {
    val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
    return date.format(DateTimeFormatter.ofPattern("EEE MMM dd", Locale.ENGLISH))
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(dateTimeString: String): String {
    val dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
    return dateTime.format(formatter)
}