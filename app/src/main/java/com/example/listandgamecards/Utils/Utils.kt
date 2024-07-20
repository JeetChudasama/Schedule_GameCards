package com.example.listandgamecards.Utils

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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