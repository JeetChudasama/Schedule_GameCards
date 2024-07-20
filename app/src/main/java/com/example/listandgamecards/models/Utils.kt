package com.example.listandgamecards.models

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale

fun getJsonDataFromSchedule(context: Context, fileName: String): String? {
    val schedulejsonString: String
    try {
        schedulejsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return schedulejsonString
}

fun getJsonDataFromGame(context: Context, fileName: String): String? {
    val gamejsonString: String
    try {
        gamejsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return gamejsonString
}

fun getJsonDataFromTeams(context: Context, fileName: String): String? {
    val teamjsonString: String
    try {
        teamjsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        ioException.printStackTrace()
        return null
    }
    return teamjsonString
}

fun formatDateToMonthYear(dateString: String, inputFormat: String = "yyyy-MM-dd"): String {
    // Define the input date format
    val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())

    // Parse the input date string to a Date object
    val date: Date? = inputDateFormat.parse(dateString)

    // Define the output date format
    val outputDateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    // Format the date object to the desired output format
    return date?.let { outputDateFormat.format(it).uppercase(Locale.getDefault()) } ?: ""
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateToCustomFormat(dateString: String): String {
    // Parse the date string
    val date = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)

    // Format the date
    return date.format(DateTimeFormatter.ofPattern("EEE MMM dd", Locale.ENGLISH))
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(dateTimeString: String): String {
    val dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME)
    val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
    return dateTime.format(formatter)
}

//fun updateGameClock(clock: String): String {
//    val currentTime = clock.split(":").let { parts ->
//        val minutes = parts[0].toInt()
//        val seconds = parts[1].split(".").let { secondsParts ->
//            secondsParts[0].toInt() + secondsParts[1].toInt() / 10
//        }
//        val newTime = minutes * 60 + seconds + 10
//        val newMinutes = newTime / 60
//        val newSeconds = newTime % 60
//        "${String.format("%02d", newMinutes)}:${String.format("%02d.%1d", newSeconds / 10, newSeconds % 10)}"
//    }
//    return currentTime
//}