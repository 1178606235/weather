package com.example.weather.ui.weather

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.logic.model.Weather
import com.example.weather.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    val viewModel by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            WeatherViewModel::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        * 调用getWindow().getDecorView()拿到当前Activity的视图
        * 在调用getSystemUiVisibility来改变系统UI的显示
        * 传入的这两个表示Activity的布局会显示在状态栏的上面
        * 最后再调用setStatusBarColor将状态栏设置成透明色
        * */
        setContentView(R.layout.activity_weather)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        if (viewModel.placeName.isEmpty()) viewModel.placeName =
            intent.getStringExtra("place_name") ?: ""
        if (viewModel.locationLat.isEmpty()) viewModel.locationLat =
            intent.getStringExtra("location_lat") ?: ""
        if (viewModel.locationLng.isEmpty()) viewModel.locationLng =
            intent.getStringExtra("location_lng") ?: ""
        viewModel.weatherLiveData.observe(this, Observer {
            val weather = it.getOrNull()
            Log.e("weather", ":${weather.toString()}")
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
        })
        //实现下拉刷新功能
        swipeRefresh.setColorSchemeColors(getColor(R.color.colorPrimary))
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        /*
        * 实现点击切换城市按钮 打开滑动菜单，
        * 监听DrawerLayout的状态，当滑动菜单被隐藏的时候，同时也要隐藏输入法
        * */
        navBtn.setOnClickListener {
            drawLayout.openDrawer(GravityCompat.START)
        }
        drawLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        })
    }

    private fun showWeatherInfo(weather: Weather) {
        //获取viewModel中的数据显示到控件上
        placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml布局里的数据
        val currentTempText = "${realtime.temperature.toInt()}℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast.xml里的内容
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view =
                LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dataInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            Log.e("aaaaaaadataInfo", "dddddddddddddddddddddddddddd")
            val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            Log.e("ccccccccccccccccdataInfo", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
            if (skycon.data != null) {
                dataInfo.text = simpleDataFormat.format(skycon.data)
            }
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
            Log.e("aaaadataInfo", dataInfo.text.toString())
            Log.e("aaaascyIcon", skyIcon.toString())
            Log.e("aaaaskyInfo", skyInfo.text.toString())
            Log.e("aaaatempInfo", temperatureInfo.text.toString())
        }
        //填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }

    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }
}
