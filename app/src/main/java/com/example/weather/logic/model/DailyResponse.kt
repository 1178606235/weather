package com.example.weather.logic.model

import androidx.work.Data
import com.google.gson.annotations.SerializedName
/*
* 将所有的数据模型类都定义在DailyResponse的内部，
* 防止出现和其他接口的数据模型类有同名冲突的情况
* */
data class DailyResponse(val status:String,val result:Result) {
    data class Result(val daily:Daily)
    data class Daily(val temperature:List<Temperature>, val skycon:List<Skycon>,
                     @SerializedName("life_index") val lifeIndex:LifeIndex)
    data class Temperature(val max:Float,val min:Float)
    data class Skycon(val value:String,val data:Data)
    data class LifeIndex(val coldRisk:List<LifeDescription>, val carWashing:List<LifeDescription>,
                         val ultraviolet:List<LifeDescription>, val dressing:List<LifeDescription>)
    data class LifeDescription(val desc:String)
}