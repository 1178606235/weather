package com.example.weather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
* 定义一个统一的网络数据访问入口，对所有网络请求的API进行封装
* */
object WeatherNetwork {
    private val placeService =
        ServiceCreator.create(PlaceService::class.java)//使用ServiceCreator创建一个PlaceService接口的动态管理对象

    suspend fun searchPlaces(query: String) =
        placeService.searchPlaces(query).await()//调用刚在PlaceService接口定义的searchPlaces方法来发起搜索城市数据请求

    private suspend fun <T> Call<T>.await(): T {            //将await()定义成Call<T>的扩展函数
        return suspendCoroutine {
            //使用suspendCoroutine函数 当前协程被立刻挂起
            enqueue(object : Callback<T> {                          //回调返回网络请求结果
                override fun onFailure(call: Call<T>, t: Throwable) {//请求失败 调用failure 返回失败原因
                    it.resumeWithException(t)
                }

                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>
                ) {//请求成功 调用resume()恢复被挂起的协程
                    val body = response.body()
                    if (body != null) it.resume(body)
                    else it.resumeWithException(RuntimeException("response body is null!"))
                }
            })
        }
    }
}