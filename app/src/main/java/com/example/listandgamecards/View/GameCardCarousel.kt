package com.example.listandgamecards.View

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.listandgamecards.models.Entry
import com.example.listandgamecards.models.FutureGame
import com.example.listandgamecards.models.PastGameCard
import com.example.listandgamecards.models.Schedule
import com.example.listandgamecards.models.Team
import com.example.listandgamecards.models.UpcomingGame
import com.example.listandgamecards.Utils.formatDateToCustomFormat
import com.example.listandgamecards.Utils.formatTime
import com.example.listandgamecards.models.PromotionCard
import com.example.listandgamecards.usecases.calculateDuration
import com.example.listandgamecards.usecases.filterAndSortSchedule
import com.example.listandgamecards.usecases.filterScheduleByStatus
import com.example.listandgamecards.usecases.formatTimeComponents
import com.example.listandgamecards.usecases.getLatestPastGame
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GamesTab(games: Entry, schedule: List<Schedule>, team: List<Team>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PastGameCardImage(games.pastGameCard, schedule = schedule, team = team)
            }
            item {
                UpcomingGameImage(games.upcomingGame, schedule = schedule, team = team)
            }
            item {
                FutureGameImage(games.futureGame, schedule = schedule, team = team)
            }
            item {
                PromotionGameCard(games.promotionCards, schedule = schedule, team = team)
            }
        }
    }
}

@Composable
fun PromotionGameCard(promotionCards: List<PromotionCard>, schedule: List<Schedule>, team: List<Team>) {
    val context = LocalContext.current
    val promotionCard = promotionCards.firstOrNull { it.position.toInt() == 2 }

    when(promotionCard != null){
        true ->
            Card(
                modifier = Modifier
                    .width(220.dp)
                    .height(220.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                shape = RoundedCornerShape(16.dp)
            ){
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    promotionCard.card.firstOrNull()?.let { card ->
                        Image(
                            painter = rememberAsyncImagePainter(card.backgroundImage.url),
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ){

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.Start
                            ){
                                Text(text = card.title, color = Color.White,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.Start
                            ){
                                Text(text = "2023/24", color = Color.White,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(card.button.ctaLink)
                                        )
                                        context.startActivity(intent)
                                    },
                                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                                colors = CardDefaults.cardColors(Color(0xFFFFAB00)),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(25.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = card.button.ctaText, color = Color.Black,
                                        style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                }
                            }
                        }

                    }
                }
            }

        false -> TODO()
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PastGameCardImage(pastGameCard: PastGameCard, schedule: List<Schedule>, team: List<Team>) {
    val context = LocalContext.current
    val pastGames = filterScheduleByStatus(schedule, 3)
    val latestPastGame = getLatestPastGame(pastGames)

    latestPastGame?.let { scheduleItem ->
        val homeTeam = team.find { it.tid == scheduleItem.h.tid }
        val visitorTeam = team.find { it.tid == scheduleItem.v.tid }
        val tid =
            team.find { it.tid == scheduleItem.h.tid || it.tid == scheduleItem.v.tid }?.tid
        val displayVS = if (tid == scheduleItem.h.tid) "VS" else "@"
        val gameStatus = scheduleItem.st

        Card(
            modifier = Modifier
                .width(220.dp)
                .height(220.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ){
                Image(
                    painter = rememberAsyncImagePainter(pastGameCard.backgroundImage.url),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(visitorTeam?.logo),
                            contentDescription = "visitor team",
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterVertically)
                        )
//                        Spacer(modifier = Modifier.width(8.dp))

                        when (gameStatus.toInt()) {
                            1 -> visitorTeam?.ta
                            2 -> scheduleItem.v.s
                            3 -> scheduleItem.v.s
                            else -> "N/A"
                        }?.let {
                            Text(
                                text = it,
                                color = Color.White
                            )
                        }

//                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = displayVS,
                            color = Color.White
                        ) // If the app team is the Home Team, display "VS" or If the app team is the Visitor Team, display "@"
//                        Spacer(modifier = Modifier.width(8.dp))

                        when (gameStatus.toInt()) {
                            1 -> homeTeam?.ta
                            2 -> scheduleItem.h.s
                            3 -> scheduleItem.h.s
                            else -> "N/A"
                        }?.let {
                            Text(
                                text = it,
                                color = Color.White
                            )
                        }

//                        Spacer(modifier = Modifier.width(8.dp))
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
                        Spacer(modifier = Modifier.width(110.dp))
                        if(gameStatus.toInt() == 2 || gameStatus.toInt() == 3){
                            homeTeam?.ta?.let { Text(text = it, color = Color.White, style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold) }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(pastGameCard.button.ctaLink)
                                )
                                context.startActivity(intent)
                                },
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                        colors = CardDefaults.cardColors(Color(0xFFFFAB00)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(25.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "GAME RECAP", color = Color.Black, style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpcomingGameImage(upcomingGame: UpcomingGame, schedule: List<Schedule>, team: List<Team>) {
    val context = LocalContext.current
    val upcomingGames = filterAndSortSchedule(schedule)

    val nextUpcomingGame = upcomingGames.firstOrNull()

    nextUpcomingGame?.let { scheduleItem ->
        val homeTeam = team.find { it.tid == scheduleItem.h.tid }
        val visitorTeam = team.find { it.tid == scheduleItem.v.tid }
        val tid = team.find { it.tid == scheduleItem.h.tid || it.tid == scheduleItem.v.tid }?.tid
        val displayVS = if (tid == scheduleItem.h.tid) "VS" else "@"
        val gameStatus = scheduleItem.st
        val displayHA = if (tid == scheduleItem.h.tid) "HOME" else "AWAY"

        var duration by remember { mutableStateOf(calculateDuration(scheduleItem.gametime)) }
        LaunchedEffect(scheduleItem.gametime) {
            while (true) {
                delay(60_000L) // 1 minute in milliseconds
                duration = calculateDuration(scheduleItem.gametime)
            }
        }
        val formattedTime = formatTimeComponents(duration)

        Card(
            modifier = Modifier
                .width(220.dp)
                .height(220.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(upcomingGame.backgroundImage.url),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp, top = 8.dp)
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ){
                        Image(
                            painter = rememberAsyncImagePainter(visitorTeam?.logo),
                            contentDescription = "visitor team",
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterVertically)
                        )
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
                            .padding(bottom = 1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        visitorTeam?.ta?.let {
                            Text(
                                text = it,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = displayVS,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(5.dp))

                        homeTeam?.ta?.let {
                            Text(
                                text = it,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        if (gameStatus.toInt() == 1) {
                            Text(
                                text = displayHA,
                                color = Color.DarkGray,
                                fontSize = 9.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Divider(
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(1.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }

                        if (gameStatus.toInt() == 1) {
                            Text(
                                text = formatDateToCustomFormat(scheduleItem.gametime),
                                color = Color.DarkGray,
                                fontSize = 9.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Divider(
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(1.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        Text(
                            text = formatTime(scheduleItem.gametime),
                            color = Color.DarkGray,
                            fontSize = 9.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ){
                    Card(
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(Color(0xFF000000).copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(5.dp)
                    ){
                        Row (
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text(
                                    text = formattedTime.split(" | ")[0],
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "DAYS",
                                    color = Color.LightGray,
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text(
                                    text = formattedTime.split(" | ")[1],
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "HRS",
                                    color = Color.LightGray,
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text(
                                    text = formattedTime.split(" | ")[2],
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "MIN",
                                    color = Color.LightGray,
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(upcomingGame.button.ctaLink)
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
                                .height(25.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BUY TICKETS ON", color = Color.Black, style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = rememberAsyncImagePainter(upcomingGame.button.icons.trailingIcon.url),
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FutureGameImage(futureGame: FutureGame, schedule: List<Schedule>, team: List<Team>) {
    val futureGames = filterAndSortSchedule(schedule)
    val secondFutureGame = futureGames.getOrNull(1)

    secondFutureGame?.let { scheduleItem ->
        val homeTeam = team.find { it.tid == scheduleItem.h.tid }
        val visitorTeam = team.find { it.tid == scheduleItem.v.tid }
        val tid = team.find { it.tid == scheduleItem.h.tid || it.tid == scheduleItem.v.tid }?.tid
        val displayVS = if (tid == scheduleItem.h.tid) "VS" else "@"
        val gameStatus = scheduleItem.st
        val displayHA = if (tid == scheduleItem.h.tid) "HOME" else "AWAY"
        var duration by remember { mutableStateOf(calculateDuration(scheduleItem.gametime)) }
        LaunchedEffect(scheduleItem.gametime) {
            while (true) {
                delay(60_000L)
                duration = calculateDuration(scheduleItem.gametime)
            }
        }
        val formattedTime = formatTimeComponents(duration)

        Card(
            modifier = Modifier
                .width(220.dp)
                .height(220.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = rememberAsyncImagePainter(futureGame.backgroundImage.url),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp, top = 8.dp)
                ){
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ){
                        Image(
                            painter = rememberAsyncImagePainter(visitorTeam?.logo),
                            contentDescription = "visitor team",
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterVertically)
                        )
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
                            .padding(bottom = 1.dp)
                    ){
                        visitorTeam?.ta?.let {
                            Text(
                                text = it,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = displayVS,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        homeTeam?.ta?.let {
                            Text(
                                text = it,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        if (gameStatus.toInt() == 1) {
                            Text(
                                text = displayHA,
                                color = Color.White,
                                fontSize = 9.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Divider(
                                color = Color.White,
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(1.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }

                        if (gameStatus.toInt() == 1) {
                            Text(
                                text = formatDateToCustomFormat(scheduleItem.gametime),
                                color = Color.White,
                                fontSize = 9.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Divider(
                                color = Color.White,
                                modifier = Modifier
                                    .height(6.dp)
                                    .width(1.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        Text(
                            text = formatTime(scheduleItem.gametime),
                            color = Color.White,
                            fontSize = 9.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .padding(bottom = 10.dp, start = 10.dp),
                        colors = CardDefaults.cardColors(Color(0xFF000000).copy(alpha = 0.7f)),
                        shape = RoundedCornerShape(5.dp)
                    ){
                        Row (
                            modifier = Modifier
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text(
                                    text = formattedTime.split(" | ")[0],
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "DAYS",
                                    color = Color.LightGray,
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text(
                                    text = formattedTime.split(" | ")[1],
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "HRS",
                                    color = Color.LightGray,
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Divider(
                                color = Color.Gray,
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(1.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Text(
                                    text = formattedTime.split(" | ")[2],
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Text(
                                    text = "MIN",
                                    color = Color.LightGray,
                                    style = TextStyle(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
