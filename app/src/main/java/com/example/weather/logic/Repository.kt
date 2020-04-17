package com.example.weather.logic

import android.util.Log
import androidx.lifecycle.liveData
import com.example.weather.logic.dao.PlaceDao
import com.example.weather.logic.model.Place
import com.example.weather.logic.model.Weather
import com.example.weather.logic.network.WeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

/*
* 创建单例类作为仓库层的统一封装入口
* 将线程参数设置为Dispatchers.IO，这样代码块中的内容会在子线程中运行
* */
object Repository {
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        //能自动构建并返回一个liveData对象    可以在liveData的代码块中调用任意的挂起函数
        val placeResponse = WeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("请求状态是：${placeResponse.status}"))
        }
    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        //创建协程域调用async
        coroutineScope {
            val deferredRealtime = async {
                WeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                WeatherNetwork.getDailyWeather(lng, lat)
            }
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            Log.e("realtimeResponse", ":${realtimeResponse.status}")
            Log.e("dailyResponse", ":${dailyResponse.status}")
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather =
                    Weather(
                        realtimeResponse.result.realtime,
                        dailyResponse.result.daily
                    )
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime 请求状态是 ${realtimeResponse.status}" +
                                "daily 请求状态是 ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlacedSaved() = PlaceDao.isPlaceSaved()
}

//内部先调用liveData函数然后统一进行try catch处理 然后获取lambda的执行结果调用emit发射出去
private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
    liveData(context)
    {
        val result = try {
            block()
        } catch (e: Exception) {
            Result.failure<T>(e)
        }
        emit(result)
    }