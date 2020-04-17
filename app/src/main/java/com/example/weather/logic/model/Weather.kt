package com.example.weather.logic.model
/*
* 将realtime和daily封装起来
* */
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)