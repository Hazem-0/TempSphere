package com.darkzoom.tempsphere.utils


import CurrentWeatherEntity
import ForecastItemEntity
import com.darkzoom.tempsphere.data.local.model.entity.AlertEntity
import com.darkzoom.tempsphere.data.local.model.AlertModel
import com.darkzoom.tempsphere.data.remote.model.*
import com.darkzoom.tempsphere.data.local.model.DailyWeather
import com.darkzoom.tempsphere.data.local.model.HomeUiState
import com.darkzoom.tempsphere.data.local.model.HourlyWeather
import com.darkzoom.tempsphere.data.local.model.RepeatMode
import com.darkzoom.tempsphere.data.local.model.WeatherType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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


 fun CurrentWeatherEntity.toSuccess(forecast: List<ForecastItemEntity>) =
    HomeUiState.Success(
        city          = cityName,
        temp         = temp.toInt(),
        feelsLike    = feelsLike.toInt(),
        high         = tempMax.toInt(),
        low          = tempMin.toInt(),
        description   = weatherDescription.replaceFirstChar { it.uppercase() },
        weatherType   = weatherIcon.toWeatherType(),
        humidity      = humidity,
        windMs        = windSpeed.toFloat(),
        pressureHpa   = pressure,
        cloudinessPct = cloudsAll,
        dateLabel     = dt.toDateLabel(),
        hourly        = forecast.take(10).map { it.toHourly() },
        daily         = forecast.groupBy { it.dt.toDayLabel() }.values.take(7).map { it.toDaily() }
    )

 fun ForecastItemEntity.toHourly() = HourlyWeather(
    time      = dt.toHourLabel(),
    tempF     = temp.toInt(),
    type      = weatherIcon.toWeatherType(),
    precipPct = (pop * 100).toInt()
)

 fun List<ForecastItemEntity>.toDaily() = DailyWeather(
    day       = first().dt.toDayLabel(),
    high     = maxOf { it.tempMax }.toInt(),
    low      = minOf { it.tempMin }.toInt(),
    type      = first().weatherIcon.toWeatherType(),
    precipPct = (maxOf { it.pop } * 100).toInt()
)


fun AlertEntity.toDomain(): AlertModel = AlertModel(
    id         = id,
    timeText   = timeText,
    alertType  = alertType,
    isEnabled  = isEnabled,
    hour       = hour,
    minute     = minute,
    repeatMode = RepeatMode.fromString(repeatMode)
)

fun AlertModel.toEntity(): AlertEntity = AlertEntity(
    id         = id,
    timeText   = timeText,
    alertType  = alertType,
    isEnabled  = isEnabled,
    hour       = hour,
    minute     = minute,
    repeatMode = repeatMode.toStorageString()
)

 fun String.toWeatherType(): WeatherType {
    val isDay = this.endsWith('d')
    return when (this.take(2)) {
        "01" -> if (isDay) WeatherType.SUNNY else WeatherType.NIGHT
        "02" -> if (isDay) WeatherType.PARTLY_CLOUDY else WeatherType.NIGHT
        "03", "04" -> WeatherType.CLOUDY
        "09", "10" -> WeatherType.RAINY
        "11" -> WeatherType.THUNDER
        "13" -> WeatherType.SNOWY
        "50" -> WeatherType.FOGGY
        else -> WeatherType.CLOUDY
    }
}

 fun Long.toDateLabel() =
    SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault()).format(Date(this * 1000))

 fun Long.toHourLabel() =
    SimpleDateFormat("h a", Locale.getDefault()).format(Date(this * 1000))

 fun Long.toDayLabel() =
    SimpleDateFormat("EEE", Locale.getDefault()).format(Date(this * 1000))