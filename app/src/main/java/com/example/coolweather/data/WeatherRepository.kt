package com.example.coolweather.data

import com.example.coolweather.data.db.WeatherDao
import com.example.coolweather.data.model.weather.Weather
import com.example.coolweather.data.network.CoolWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherRepository private constructor(
    private val weatherDao: WeatherDao,
    private val network: CoolWeatherNetwork
) {
    suspend fun getWeather(weatherId: String): Weather {
        var weather = weatherDao.getCacheWeatherInfo()
        if (weather == null) weather = requestWeather(weatherId)

        return weather
    }

    suspend fun refreshWeather(weatherId: String) = requestWeather(weatherId)

    suspend fun getBingPic(): String {
        var url = weatherDao.getCacheBingPic()
        if (url == null) url = requestBingPic()
        return url
    }

    suspend fun refreshBingPic() = requestBingPic()

    fun isWeatherCached() = weatherDao.getCacheWeatherInfo() != null

    fun getCachedWeather() = weatherDao.getCacheWeatherInfo()!!

    private suspend fun requestWeather(weatherId: String) = withContext(Dispatchers.IO) {
        val heWeather = network.fetchWeather(weatherId)
        val weather = heWeather.weather!![0]
        weatherDao.cacheWeatherInfo(weather)
        weather
    }

    private suspend fun requestBingPic() = withContext(Dispatchers.IO) {
        val url = network.fetBingPic()
        weatherDao.cacheBingPic(url)
        url
    }

    companion object {
        private lateinit var instance: WeatherRepository
        fun getInstance(weatherDao: WeatherDao, network: CoolWeatherNetwork): WeatherRepository {
            if (!::instance.isInitialized) {
                synchronized(WeatherRepository::class.java) {
                    if (!::instance.isInitialized) {
                        instance = WeatherRepository(weatherDao, network)
                    }
                }
            }
            return instance
        }
    }
}