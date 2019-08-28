package com.example.coolweather.data.db

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.coolweather.CoolWeatherApplication
import com.example.coolweather.data.model.weather.Weather
import com.google.gson.Gson


class WeatherDao {

    fun cacheWeatherInfo(weather: Weather?) {
        if (weather == null) return
        PreferenceManager.getDefaultSharedPreferences(CoolWeatherApplication.context).edit {
            val weatherInfo = Gson().toJson(weather)
            putString("weather", weatherInfo)
        }
    }

    fun getCacheWeatherInfo(): Weather? {
        val weatherInfo =
            PreferenceManager.getDefaultSharedPreferences(CoolWeatherApplication.context)
                .getString("weather", null)
        if (weatherInfo != null) {
            return Gson().fromJson(weatherInfo, Weather::class.java)
        }
        return null
    }

    fun cacheBingPic(bingPic: String?) {
        if (bingPic == null) return
        PreferenceManager.getDefaultSharedPreferences(CoolWeatherApplication.context).edit {
            putString("bing_pic", bingPic)
        }
    }

    fun getCacheBingPic(): String? =
        PreferenceManager.getDefaultSharedPreferences(CoolWeatherApplication.context).getString(
            "bing_pic",
            null
        )

    private fun SharedPreferences.edit(action: SharedPreferences.Editor.() -> Unit) {
        val editor = edit()
        action(editor)
        editor.apply()
    }
}