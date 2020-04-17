package com.example.weather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.weather.WeatherApplication
import com.example.weather.logic.model.Place
import com.google.gson.Gson
/*
* 封装存储和读取数据的接口
* 先通过GSON将place转换成一个JSON字符串，然后用字符串存储的方式来进行保存
* 然后再通过GSON将JSON字符串解析成Place对象并返回
* */
object PlaceDao {
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }
    fun getSavedPlace():Place{
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }
    fun isPlaceSaved() = sharedPreferences().contains("place")
}

private fun sharedPreferences() = WeatherApplication.context
    .getSharedPreferences("weather", Context.MODE_PRIVATE)