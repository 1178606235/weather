package com.example.weather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.weather.logic.Repository
import com.example.weather.logic.model.Location
/*
*编写天气ui信息
* */
class WeatherViewModel : ViewModel() {
    private val locationLiveData = MutableLiveData<Location>()
    var placeName = ""
    var locationLng = ""
    var locationLat = ""
    val weatherLiveData = Transformations.switchMap(locationLiveData) {
        Repository.refreshWeather(it.lng,it.lat)
    }

    fun refreshWeather(lng: String, lat: String) {
        locationLiveData.value = Location(lng, lat)
    }
}