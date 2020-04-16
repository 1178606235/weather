package com.example.weather.logic.network

import com.example.weather.WeatherApplication
import com.example.weather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
/*
* @GET注解配置中的地址
* 搜索城市数据的API中用注解Query的方式来实现
* searchPlaces方法的返回值声明成Call<PlaceResponse>这样就会将服务器返回的Json数据自动解析成PlaceResponse对象
* */
interface PlaceService {
    @GET("v2/place?token=${WeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
}