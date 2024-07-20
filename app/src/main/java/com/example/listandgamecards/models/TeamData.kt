package com.example.listandgamecards.models

import com.google.gson.annotations.SerializedName

data class TeamRoot(
    val data: TmData,
)

data class TmData(
    val teams: List<Team>,
)

data class Team(
    val uid: String,
    val year: Long,
    @SerializedName("league_id")
    val leagueId: String,
    @SerializedName("season_id")
    val seasonId: String,
    @SerializedName("ist_group")
    val istGroup: String,
    val tid: String,
    val tn: String,
    val ta: String,
    val tc: String,
    val di: String,
    val co: String,
    val sta: String,
    val logo: String?,
    val color: String,
)


