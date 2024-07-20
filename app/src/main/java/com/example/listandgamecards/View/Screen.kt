package com.example.listandgamecards.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.request.Parameters
import com.example.listandgamecards.models.Entry
import com.example.listandgamecards.models.FutureGame
import com.example.listandgamecards.models.PastGameCard
import com.example.listandgamecards.models.Schedule
import com.example.listandgamecards.models.ScreenViewModel
import com.example.listandgamecards.models.Team
import com.example.listandgamecards.models.UpcomingGame
import com.example.listandgamecards.models.formatDateToCustomFormat
import com.example.listandgamecards.models.formatDateToMonthYear
import com.example.listandgamecards.models.formatTime
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ScreenViewModel) {
    val schedule by viewModel.schedule.observeAsState(listOf())
    val games by viewModel.games.observeAsState(Parameters.Entry(0, ""))
    val teams by viewModel.teams.observeAsState(listOf())

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Team", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(top = it.calculateTopPadding())
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Black),
                containerColor = Color.Black,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value]),
                        color = Color(0xFFFFC107),
                        height = 3.dp
                    )
                }
            ) {
                HomeTabs.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.Gray,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentTab.ordinal)
                            }
                        },
                        text = { Text(text = currentTab.text) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (HomeTabs.entries[page]) {
                    HomeTabs.Schedule -> ScheduleTab(
                        schedule = schedule,
                        game = games,
                        team = teams
                    )

                    HomeTabs.Games -> GamesTab(
                            games = games as Entry,
                            schedule = schedule,
                            team = teams
                    )
                }
            }
        }
    }
}

enum class HomeTabs(
    val text: String
) {
    Schedule(
        text = "Schedule"
    ),
    Games(
        text = "Games"
    )
}

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
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PastGameCardImage(pastGameCard: PastGameCard, schedule: List<Schedule>, team: List<Team>) {
    // Filter past games and sort by date
    val pastGames = schedule.filter { it.st.toInt() == 3 }
    val latestPastGame = pastGames.maxByOrNull {
        LocalDate.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
    }

    // Check if there's a past game to display
    latestPastGame?.let { scheduleItem ->
        val homeTeam = team.find { it.tid == scheduleItem.h.tid }
        val visitorTeam = team.find { it.tid == scheduleItem.v.tid }
        val tid =
            team.find { it.tid == scheduleItem.h.tid || it.tid == scheduleItem.v.tid }?.tid
        val displayVS = if (tid == scheduleItem.h.tid) "VS" else "@"
        val gameStatus = scheduleItem.st

        Card(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp),
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
                    // Row to display team logos and game details
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(visitorTeam?.logo),
                            contentDescription = "visitor team",
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(8.dp))

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

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = displayVS,
                            color = Color.White
                        ) // If the app team is the Home Team, display "VS" or If the app team is the Visitor Team, display "@"
                        Spacer(modifier = Modifier.width(8.dp))

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

                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            painter = rememberAsyncImagePainter(homeTeam?.logo),
                            contentDescription = "home team",
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }

                    // Card for "GAME RECAP"
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
                        colors = CardDefaults.cardColors(Color(0xFFFFC107)),
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
                                fontWeight = FontWeight.Bold
                            ))
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpcomingGameImage(upcomingGame: UpcomingGame, schedule: List<Schedule>, team: List<Team>) {
    // Filter and sort the schedule for upcoming games (gameStatus = 1)
    val upcomingGames = schedule.filter { it.st.toInt() == 1 }.sortedBy {
        LocalDate.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
    }

    // Get the first upcoming game
    val nextUpcomingGame = upcomingGames.firstOrNull()

    nextUpcomingGame?.let { scheduleItem ->
        val homeTeam = team.find { it.tid == scheduleItem.h.tid }
        val visitorTeam = team.find { it.tid == scheduleItem.v.tid }
        val tid = team.find { it.tid == scheduleItem.h.tid || it.tid == scheduleItem.v.tid }?.tid
        val displayVS = if (tid == scheduleItem.h.tid) "VS" else "@"
        val gameStatus = scheduleItem.st
        val displayHA = if (tid == scheduleItem.h.tid) "HOME" else "AWAY"

        Card(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp),
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
                            .padding(bottom = 8.dp)
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
                        homeTeam?.ta?.let {
                            Text(
                                text = it,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = displayVS,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        visitorTeam?.ta?.let {
                            Text(
                                text = it,
                                color = Color.Black
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
                                fontSize = 7.sp
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
                                fontSize = 7.sp
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
                            fontSize = 7.sp
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
                            .fillMaxWidth(),
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
                                    .size(30.dp)
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
    // Filter and sort the schedule for future games (gameStatus = 1)
    val futureGames = schedule.filter { it.st.toInt() == 1 }.sortedBy {
        LocalDate.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
    }

    // Get the second future game
    val secondFutureGame = futureGames.getOrNull(1)

    secondFutureGame?.let { scheduleItem ->
        val homeTeam = team.find { it.tid == scheduleItem.h.tid }
        val visitorTeam = team.find { it.tid == scheduleItem.v.tid }
        val tid = team.find { it.tid == scheduleItem.h.tid || it.tid == scheduleItem.v.tid }?.tid
        val displayVS = if (tid == scheduleItem.h.tid) "VS" else "@"
        val gameStatus = scheduleItem.st
        val displayHA = if (tid == scheduleItem.h.tid) "HOME" else "AWAY"

        Card(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp),
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
                            .padding(bottom = 8.dp)
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
                        homeTeam?.ta?.let {
                            Text(
                                text = it,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = displayVS,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        visitorTeam?.ta?.let {
                            Text(
                                text = it,
                                color = Color.White
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
                                fontSize = 7.sp
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
                                fontSize = 7.sp
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
                            fontSize = 7.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {

                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalCoilApi::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleTab(schedule: List<Schedule>, game: Any, team: List<Team>) {
    val sortedSchedule = schedule.sortedBy {
        LocalDate.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
    }
    val groupedSchedule = sortedSchedule.groupBy { formatDateToMonthYear(it.gametime) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Find the index of the next upcoming game
    val today = LocalDate.now()
    val nextGameIndex = remember(sortedSchedule) {
        sortedSchedule.indexOfFirst {
            try {
                val gameDate = LocalDate.parse(it.gametime, DateTimeFormatter.ISO_DATE_TIME)
                gameDate.isAfter(today)
            } catch (e: Exception) {
                false
            }
        }
    }

    LaunchedEffect(nextGameIndex) {
        if (nextGameIndex != -1) {
            listState.scrollToItem(nextGameIndex)
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
                    Text(
                        text = month,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            itemsIndexed(schedules) { index, scheduleItem ->
                val homeTeam = team.find { it.tid == scheduleItem.h.tid }
                val visitorTeam = team.find { it.tid == scheduleItem.v.tid }
                val tid =
                    team.find { it.tid == scheduleItem.h.tid || it.tid == scheduleItem.v.tid }?.tid
                val displayVS = if (tid == scheduleItem.h.tid) "VS" else "@"
                val displayHA = if (tid == scheduleItem.h.tid) "HOME" else "AWAY"
                val gameStatus = scheduleItem.st
                val gameQuarter = scheduleItem.stt // Game quarter
                val gameClock = scheduleItem.cl
//                var gameClock by remember { mutableStateOf(scheduleItem.cl) }
//                LaunchedEffect(Unit) {
//                    while (true) {
//                        delay(10000) // Delay for 10 seconds
//
//                        // Update the game clock
//                        gameClock = updateGameClock(gameClock)
//                    }
//                }

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
                                    2 -> scheduleItem.stt + "   |   " + gameClock
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
                                2 -> scheduleItem.v.s
                                3 -> scheduleItem.v.s
                                else -> "N/A"
                            }?.let {
                                Text(
                                    text = it,
                                    color = Color.White
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
                                2 -> scheduleItem.h.s
                                3 -> scheduleItem.h.s
                                else -> "N/A"
                            }?.let {
                                Text(
                                    text = it,
                                    color = Color.White
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
                                visitorTeam?.ta?.let { Text(text = it, color = Color.White) }
                            }
                            Spacer(modifier = Modifier.width(96.dp))
                            if(gameStatus.toInt() == 2 || gameStatus.toInt() == 3){
                                homeTeam?.ta?.let { Text(text = it, color = Color.White) }
                            }
                        }
                        if (gameStatus.toInt() == 1) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
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
                                    Text(text = "ticketmaster", fontFamily = FontFamily.Cursive)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}