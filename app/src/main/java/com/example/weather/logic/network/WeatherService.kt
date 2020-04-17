package com.example.weather.logic.network

import com.example.weather.WeatherApplication
import com.example.weather.logic.model.DailyResponse
import com.example.weather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/*
* 定义一个用于访问天气信息API的Retrofit接口
* 用@Path注解请求接口中动态传入的经纬度坐标
* 这两个方法的放回置被声明成了 Call<RealtimeResponse>和Call<DailyResponse>
对应了刚刚定义好的两个数据模型类
* */
interface WeatherService {
    @GET("v2.5/${WeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<RealtimeResponse>

    @GET("v2.5/${WeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String, @Path("lat") lat: String): Call<DailyResponse>
}