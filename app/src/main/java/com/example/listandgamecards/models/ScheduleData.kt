package com.example.listandgamecards.models

import com.google.gson.annotations.SerializedName

data class ScheduleRoot(
    val data: ShduleData,
)

data class ShduleData(
    val schedules: List<Schedule>,
)

data class Schedule(
    val uid: String,
    val year: Long,
    @SerializedName("league_id")
    val leagueId: String,
    @SerializedName("season_id")
    val seasonId: String,
    val gid: String,
    val gcode: String,
    val seri: String,
    @SerializedName("is_game_necessary")
    val isGameNecessary: String,
    val gametime: String,
    val cl: String,
    val arena_name: String,
    @SerializedName("arena_city")
    val arenaCity: String,
    @SerializedName("arena_state")
    val arenaState: String,
    val st: Long,
    val stt: String,
    val ppdst: String,
    @SerializedName("buy_ticket")
    val buyTicket: String?,
    @SerializedName("buy_ticket_url")
    val buyTicketUrl: String?,
    @SerializedName("logo_url")
    val logoUrl: Any?,
    val hide: Boolean,
    @SerializedName("game_state")
    val gameState: String,
    @SerializedName("game_subtype")
    val gameSubtype: String?,
    val h: H,
    val v: V,
)

data class H(
    val tid: String,
    val re: String,
    val ta: String,
    val tn: String,
    val tc: String,
    val s: String?,
    @SerializedName("ist_group")
    val istGroup: String?,
)

data class V(
    val tid: String,
    val re: String,
    val ta: String,
    val tn: String,
    val tc: String,
    val s: String?,
    @SerializedName("ist_group")
    val istGroup: String?,
)


