package com.example.listandgamecards.View

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import com.example.listandgamecards.models.Schedule
import com.example.listandgamecards.models.Team
import com.example.listandgamecards.Utils.formatDateToCustomFormat
import com.example.listandgamecards.Utils.formatDateToMonthYear
import com.example.listandgamecards.Utils.formatTime
import com.example.listandgamecards.models.Entry
import com.example.listandgamecards.usecases.getNextGameIndex
import com.example.listandgamecards.usecases.getSortedSchedule
import com.example.listandgamecards.usecases.updateGameClockAndQuarter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalCoilApi::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleTab(schedule: List<Schedule>, game: Entry, team: List<Team>) {
    val context = LocalContext.current
    val sortedSchedule = getSortedSchedule(schedule)
    val groupedSchedule = sortedSchedule.groupBy { formatDateToMonthYear(it.gametime) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val today = LocalDate.now()
    val nextGameIndex = getNextGameIndex(sortedSchedule)

    val months = groupedSchedule.keys.toList()
    var currentMonthIndex by remember { mutableStateOf(months.indexOf(formatDateToMonthYear(today.toString()))) }

    val firstVisibleItemMonth by remember {
        derivedStateOf {
            val firstVisibleItem = listState.firstVisibleItemIndex
            groupedSchedule.entries.find { (_, schedules) ->
                schedules.any { schedule -> sortedSchedule.indexOf(schedule) == firstVisibleItem }
            }?.key
        }
    }

    LaunchedEffect(firstVisibleItemMonth) {
        firstVisibleItemMonth?.let { month ->
            currentMonthIndex = months.indexOf(month)
        }
    }

    LaunchedEffect(nextGameIndex) {
        if (nextGameIndex != -1) {
            coroutineScope.launch {
                listState.scrollToItem(nextGameIndex)
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        groupedSchedule.forEach { (month, schedules) ->
            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.DarkGray)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        IconButton(onClick = {
                            if (currentMonthIndex > 0) {
                                currentMonthIndex -= 1
                                val targetMonth = months[currentMonthIndex]
                                val targetIndex = groupedSchedule.values.flatten().indexOfFirst {
                                    formatDateToMonthYear(it.gametime) == targetMonth
                                }
                                if (targetIndex != -1) {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(targetIndex)
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous Month", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = month,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (currentMonthIndex < months.size - 1) {
                                currentMonthIndex += 1
                                val targetMonth = months[currentMonthIndex]
                                val targetIndex = groupedSchedule.values.flatten().indexOfFirst {
                                    formatDateToMonthYear(it.gametime) == targetMonth
                                }
                                if (targetIndex != -1) {
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(targetIndex)
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next Month", tint = Color.White)
                        }
                    }
                }
            }
            itemsIndexed(schedules) { index, scheduleItem ->
                val homeTeam = team.find { it.tid == scheduleItem.h.tid }
                val visitorTeam = team.find { it.tid == scheduleItem.v.tid }
                val tid = team.find { it.tid == scheduleItem.h.tid || it.tid == scheduleItem.v.tid }?.tid
                val displayVS = if (tid == scheduleItem.h.tid) "VS" else "@"
                val displayHA = if (tid == scheduleItem.h.tid) "HOME" else "AWAY"
                val gameStatus = scheduleItem.st
                var gameQuarter by remember { mutableStateOf(scheduleItem.stt) }
                val initialGameClock = scheduleItem.cl
                var gameClock by remember { mutableStateOf(initialGameClock) }
                var homeTeamScore by remember { mutableStateOf(scheduleItem.h.s) }
                var visitorTeamScore by remember { mutableStateOf(scheduleItem.v.s) }

//                fun updateGameClockAndQuarter(clock: String): String {
//                    val clockParts = clock.split(":")
//                    val minutes = clockParts.getOrNull(0)?.toIntOrNull() ?: 0
//                    val seconds = clockParts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
//
//                    if (gameQuarter.contains("QTR")) {
//                        if (minutes >= 10) {
//                            gameQuarter = when (gameQuarter) {
//                                "QTR 1" -> "Halftime"
//                                "QTR 2" -> "Halftime"
//                                "QTR 3" -> "Halftime"
//                                "QTR 4" -> "FINAL"
//                                else -> gameQuarter
//                            }
//                            return "00:00.0"
//                        }
//                    } else if (gameQuarter == "Halftime") {
//                        if (minutes >= 15) {
//                            gameQuarter = when (gameQuarter) {
//                                "Halftime" -> "QTR 2"
//                                "QTR 2 Halftime" -> "QTR 3"
//                                "QTR 3 Halftime" -> "QTR 4"
//                                else -> "FINAL"
//                            }
//                            return "00:00.0"
//                        }
//                    }
//
//                    val updatedSeconds = seconds + 10
//                    val updatedMinutes = minutes + (updatedSeconds / 60).toInt()
//                    val newSeconds = updatedSeconds % 60
//                    return "%02d:%04.1f".format(updatedMinutes, newSeconds)
//                }

                // Update game clock every 10 seconds
                LaunchedEffect(gameStatus) {
                    while (gameStatus.toInt() == 2) {
                        delay(10_000L)
                        val (newClock, newQuarter) = updateGameClockAndQuarter(gameClock, gameQuarter)
                        gameClock = newClock
                        gameQuarter = newQuarter
                        if (!newQuarter.contains("Halftime")) {
                            if ((0..1).random() == 0) {
                                homeTeamScore = (homeTeamScore?.toIntOrNull()?.plus(1) ?: 0).toString()
                            } else {
                                visitorTeamScore = (visitorTeamScore?.toIntOrNull()?.plus(1) ?: 0).toString()
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                    colors = CardDefaults.cardColors(Color(0xFF37474F)),
                    shape = RoundedCornerShape(20)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if(gameStatus.toInt() == 1 || gameStatus.toInt() == 3){
                                Text(
                                    text = displayHA,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Divider(
                                    color = Color.Black,
                                    modifier = Modifier
                                        .height(16.dp)
                                        .width(1.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            if(gameStatus.toInt() == 1 || gameStatus.toInt() == 3){
                                Text(
                                    text = formatDateToCustomFormat(scheduleItem.gametime),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Divider(
                                    color = Color.Black,
                                    modifier = Modifier
                                        .height(16.dp)
                                        .width(1.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Text(
                                text = when (gameStatus.toInt()) {
                                    1 -> formatTime(scheduleItem.gametime) // Adjust based on actual requirements
                                    2 -> "$gameQuarter   |   $gameClock"
                                    3 -> "FINAL"
                                    else -> "N/A"
                                },
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        if (gameStatus.toInt() == 2){
                            Card(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(20.dp)
                                    .align(Alignment.CenterHorizontally),
                                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                                colors = CardDefaults.cardColors(Color.DarkGray),
                                shape = RoundedCornerShape(30)
                            ){
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Live", color = Color.LightGray, fontSize = 12.sp)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(visitorTeam?.logo),
                                contentDescription = "visiter team",
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.CenterVertically)
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            when (gameStatus.toInt()) {
                                1 -> visitorTeam?.ta
                                2 -> visitorTeamScore
                                3 -> scheduleItem.v.s
                                else -> "N/A"
                            }?.let {
                                Text(
                                    text = it,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = displayVS,
                                color = Color.White
                            )    //If the app team is the Home Team, display "VS" or If the app team is the Visitor Team, display "@"
                            Spacer(modifier = Modifier.width(8.dp))

                            when (gameStatus.toInt()) {
                                1 -> homeTeam?.ta
                                2 -> homeTeamScore
                                3 -> scheduleItem.h.s
                                else -> "N/A"
                            }?.let {
                                Text(
                                    text = it,
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(homeTeam?.logo),
                                contentDescription = "home team",
                                modifier = Modifier
                                    .size(30.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if(gameStatus.toInt() == 2 || gameStatus.toInt() == 3){
                                visitorTeam?.ta?.let { Text(text = it, color = Color.White, style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.ExtraBold) }
                            }
                            Spacer(modifier = Modifier.width(96.dp))
                            if(gameStatus.toInt() == 2 || gameStatus.toInt() == 3){
                                homeTeam?.ta?.let { Text(text = it, color = Color.White, style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.ExtraBold) }
                            }
                        }
                        if (gameStatus.toInt() == 1) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(game.upcomingGame.button.ctaLink)
                                        )
                                        context.startActivity(intent)
                                    },
                                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                                colors = CardDefaults.cardColors(Color.White),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "BUY TICKETS ON")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Image(
                                        painter = rememberAsyncImagePainter(game.upcomingGame.button.icons.trailingIcon.url),
                                        contentDescription = "ticketmaster logo",
                                        modifier = Modifier
                                            .size(40.dp)
                                            .align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}