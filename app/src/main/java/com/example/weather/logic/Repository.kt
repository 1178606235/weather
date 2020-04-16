package com.example.weather.logic

import androidx.lifecycle.liveData
import com.example.weather.logic.model.Place
import com.example.weather.logic.network.WeatherNetwork
import kotlinx.coroutines.Dispatchers

/*
* 创建单例类作为仓库层的统一封装入口
* 将线程参数设置为Dispatchers.IO，这样代码块中的内容会在子线程中运行
* */
object Repository {
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        //能自动构建并返回一个liveData对象    可以在liveData的代码块中调用任意的挂起函数
        val result = try {
            val placeResponse = WeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("请求状态是：${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }
}