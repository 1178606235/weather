package com.example.weather.logic.model

import com.google.gson.annotations.SerializedName

/*
* 定义数据模型
* 使用serializedName注解的方式，来让JSON和Kotlin字段之间建立映射关系
* */
data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(
    val name: String,
    val location: Location, @SerializedName("formatted_address") val address: String
)

data class Location(val lng: String, val lat: String)