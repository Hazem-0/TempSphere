package com.darkzoom.tempsphere.utils

import com.darkzoom.tempsphere.data.local.entity.CurrentWeatherEntity
import com.darkzoom.tempsphere.data.local.entity.ForecastItemEntity
import com.darkzoom.tempsphere.data.remote.model.*


fun CurrentWeatherResponse.toEntity(units: String, lang: String): CurrentWeatherEntity {
    var mainWeatherType = ""
    var weatherDesc = ""
    var weatherIco = ""

    if (this.weather.isNotEmpty()) {
        val firstWeatherItem = this.weather[0]
        mainWeatherType = firstWeatherItem.main
        weatherDesc = firstWeatherItem.description
        weatherIco = firstWeatherItem.icon
    }

    return CurrentWeatherEntity(
        id = this.id,
        lat = this.coord.lat,
        lon = this.coord.lon,
        cityName = this.name,
        country = this.sys.country,
        temp = this.main.temp,
        feelsLike = this.main.feelsLike,
        tempMin = this.main.tempMin,
        tempMax = this.main.tempMax,
        pressure = this.main.pressure,
        humidity = this.main.humidity,
        visibility = this.visibility,
        windSpeed = this.wind.speed,
        windDeg = this.wind.deg,
        windGust = this.wind.gust,
        cloudsAll = this.clouds.all,
        weatherMain = mainWeatherType,
        weatherDescription = weatherDesc,
        weatherIcon = weatherIco,
        sunrise = this.sys.sunrise,
        sunset = this.sys.sunset,
        dt = this.dt,
        timezone = this.timezone,
        units = units,
        lang = lang
    )
}



fun ForecastResponse.toEntities(units: String, lang: String): List<ForecastItemEntity> {
    val entityList = mutableListOf<ForecastItemEntity>()
    for (item in this.list) {

        var mainWeatherType = ""
        var weatherDesc = ""
        var weatherIco = ""

        if (item.weather.isNotEmpty()) {
            val firstWeatherItem = item.weather[0]
            mainWeatherType = firstWeatherItem.main
            weatherDesc = firstWeatherItem.description
            weatherIco = firstWeatherItem.icon
        }

        val entity = ForecastItemEntity(
            dt = item.dt,
            cityId = this.city.id,
            cityName = this.city.name,
            country = this.city.country,
            cityLat = this.city.coord.lat,
            cityLon = this.city.coord.lon,
            cityTimezone = this.city.timezone,
            citySunrise = this.city.sunrise,
            citySunset = this.city.sunset,
            temp = item.main.temp,
            feelsLike = item.main.feelsLike,
            tempMin = item.main.tempMin,
            tempMax = item.main.tempMax,
            pressure = item.main.pressure,
            humidity = item.main.humidity,
            visibility = item.visibility,
            windSpeed = item.wind.speed,
            windDeg = item.wind.deg,
            windGust = item.wind.gust,
            cloudsAll = item.clouds.all,
            weatherMain = mainWeatherType,
            weatherDescription = weatherDesc,
            weatherIcon = weatherIco,
            pop = item.pop,
            dtTxt = item.dtTxt,
            units = units,
            lang = lang
        )

        entityList.add(entity)
    }

    return entityList
}

