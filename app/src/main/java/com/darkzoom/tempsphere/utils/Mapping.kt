package com.darkzoom.tempsphere.utils


import androidx.compose.ui.graphics.Color
import com.darkzoom.tempsphere.data.local.model.*
import com.darkzoom.tempsphere.data.local.model.entity.AlertEntity
import com.darkzoom.tempsphere.data.local.model.entity.CurrentWeatherEntity
import com.darkzoom.tempsphere.data.local.model.entity.FavLocationEntity
import com.darkzoom.tempsphere.data.local.model.entity.ForecastItemEntity
import com.darkzoom.tempsphere.data.remote.model.*
import com.darkzoom.tempsphere.ui.home.HomeUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun CurrentWeatherResponse.toEntity(units: String, lang: String): CurrentWeatherEntity {
    val firstWeather    = weather.firstOrNull()
    return CurrentWeatherEntity(
        id                  = id,
        lat                 = coord.lat,
        lon                 = coord.lon,
        cityName            = name,
        country             = sys.country,
        temp                = main.temp,
        feelsLike           = main.feelsLike,
        tempMin             = main.tempMin,
        tempMax             = main.tempMax,
        pressure            = main.pressure,
        humidity            = main.humidity,
        visibility          = visibility,
        windSpeed           = wind.speed,
        windDeg             = wind.deg,
        windGust            = wind.gust,
        cloudsAll           = clouds.all,
        weatherMain         = firstWeather?.main         ?: "",
        weatherDescription  = firstWeather?.description  ?: "",
        weatherIcon         = firstWeather?.icon         ?: "",
        sunrise             = sys.sunrise,
        sunset              = sys.sunset,
        dt                  = dt,
        timezone            = timezone,
        units               = units,
        lang                = lang,

    )
}

fun ForecastResponse.toEntities(units: String, lang: String): List<ForecastItemEntity> =
    list.map { item ->
        val firstWeather = item.weather.firstOrNull()
        ForecastItemEntity(
            dt = item.dt,
            cityId = city.id,
            cityName = city.name,
            country = city.country,
            cityLat = city.coord.lat,
            cityLon = city.coord.lon,
            cityTimezone = city.timezone,
            citySunrise = city.sunrise,
            citySunset = city.sunset,
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
            weatherMain = firstWeather?.main ?: "",
            weatherDescription = firstWeather?.description ?: "",
            weatherIcon = firstWeather?.icon ?: "",
            pop = item.pop,
            dtTxt = item.dtTxt,
            units = units,
            lang = lang
        )
    }

fun CurrentWeatherEntity.toSuccess(forecast: List<ForecastItemEntity>) =
    HomeUiState.Success(
        city          = cityName,
        temp          = temp.toInt(),
        feelsLike     = feelsLike.toInt(),
        high          = tempMax.toInt(),
        low           = tempMin.toInt(),
        description   = weatherDescription.replaceFirstChar { it.uppercase() },
        weatherType   = weatherIcon.toWeatherType(),
        humidity      = humidity,
        windMs        = windSpeed.toFloat(),
        pressureHpa   = pressure,
        cloudinessPct = cloudsAll,
        dateLabel     = dt.toDateLabel(),
        hourly        = forecast.take(10).map { it.toHourly() },
        daily         = forecast.groupBy { it.dt.toDayLabel() }.values.take(7).map { it.toDaily() },
        isRefreshing  = false
    )

fun ForecastItemEntity.toHourly() = HourlyWeather(
    time      = dt.toHourLabel(),
    tempF     = temp.toInt(),
    type      = weatherIcon.toWeatherType(),
    precipPct = (pop * 100).toInt()
)

fun List<ForecastItemEntity>.toDaily() = DailyWeather(
    day       = first().dt.toDayLabel(),
    high      = maxOf { it.tempMax }.toInt(),
    low       = minOf { it.tempMin }.toInt(),
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
    val isDay = endsWith('d')
    return when (take(2)) {
        "01"       -> if (isDay) WeatherType.SUNNY         else WeatherType.NIGHT
        "02"       -> if (isDay) WeatherType.PARTLY_CLOUDY else WeatherType.NIGHT
        "03", "04" -> WeatherType.CLOUDY
        "09", "10" -> WeatherType.RAINY
        "11"       -> WeatherType.THUNDER
        "13"       -> WeatherType.SNOWY
        "50"       -> WeatherType.FOGGY
        else       -> WeatherType.CLOUDY
    }
}

fun WeatherType.toGradientColors(): List<Color> = when (this) {
    WeatherType.SUNNY         -> listOf(Color(0xFF1E6F3A), Color(0xFF124A24))
    WeatherType.PARTLY_CLOUDY -> listOf(Color(0xFF2E4195), Color(0xFF1B296A))
    WeatherType.CLOUDY        -> listOf(Color(0xFF1D6B87), Color(0xFF0F4458))
    WeatherType.RAINY         -> listOf(Color(0xFF5D24A7), Color(0xFF3B1573))
    WeatherType.SNOWY         -> listOf(Color(0xFF2C5F7A), Color(0xFF163040))
    WeatherType.FOGGY         -> listOf(Color(0xFF3A3A5C), Color(0xFF1E1E38))
    else                      -> listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
}

fun Long.toDateLabel(): String =
    SimpleDateFormat("EEE, MMM d · h:mm a", Locale.getDefault())
        .format(Date(this * 1000))

fun Long.toHourLabel(): String =
    SimpleDateFormat("h a", Locale.getDefault())
        .format(Date(this * 1000))

fun Long.toDayLabel(): String =
    SimpleDateFormat("EEE", Locale.getDefault())
        .format(Date(this * 1000))



fun Double.toDisplayWindSpeed(windUnit: String): String = when (windUnit) {
    "km/h" -> "%.1f km/h".format(this * 3.6)
    "mph"  -> "%.1f mph".format(this * 2.237)
    else   -> "%.1f m/s".format(this)
}

fun CurrentWeatherResponse.toSavedLocation(entity: FavLocationEntity): SavedLocation {
    val type = (weather.firstOrNull()?.icon ?: "").toWeatherType()
    return SavedLocation(
        id             = entity.id,
        city           = entity.city,
        country        = entity.country,
        latitude       = entity.latitude,
        longitude      = entity.longitude,
        temp           = main.temp.toInt(),
        feelsLike      = main.feelsLike.toInt(),
        high           = main.tempMax.toInt(),
        low            = main.tempMin.toInt(),
        description    = weather.firstOrNull()?.description
            ?.replaceFirstChar { it.uppercase() } ?: "",
        time           = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
        type           = type,
        gradientColors = type.toGradientColors()
    )
}

fun FavLocationEntity.toCachedSavedLocation(): SavedLocation {
    val type = cachedWeatherTypeString?.let { WeatherType.valueOf(it) } ?: WeatherType.SUNNY
    return SavedLocation(
        id             = id,
        city           = city,
        country        = country,
        latitude       = latitude,
        longitude      = longitude,
        temp           = cachedTemp      ?: 0,
        feelsLike      = cachedFeelsLike ?: 0,
        high           = cachedHigh      ?: 0,
        low            = cachedLow       ?: 0,
        description    = cachedDescription
            ?: if (cachedTemp == null) "Loading…" else "Offline",
        time           = lastUpdated?.toHourLabel() ?: "--:--",
        type           = type,
        gradientColors = type.toGradientColors()
    )
}

fun FavLocationEntity.updateWith(response: CurrentWeatherResponse): FavLocationEntity {
    val type = (response.weather.firstOrNull()?.icon ?: "").toWeatherType()
    return copy(
        cachedTemp               = response.main.temp.toInt(),
        cachedFeelsLike          = response.main.feelsLike.toInt(),
        cachedHigh               = response.main.tempMax.toInt(),
        cachedLow                = response.main.tempMin.toInt(),
        cachedDescription        = response.weather.firstOrNull()
            ?.description?.replaceFirstChar { it.uppercase() },
        cachedWeatherTypeString  = type.name,
        lastUpdated              = System.currentTimeMillis() / 1000
    )
}

fun FavLocationEntity.toSkeleton() = SavedLocation(
    id             = id,
    city           = city,
    country        = country,
    latitude       = latitude,
    longitude      = longitude,
    temp           = 0,
    feelsLike      = 0,
    high           = 0,
    low            = 0,
    description    = "Loading…",
    time           = "--:--",
    type           = WeatherType.SUNNY,
    gradientColors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81))
)

fun CurrentWeatherResponse.toPlaceDetailData(
    city    : String,
    country : String,
    forecast: ForecastResponse
): PlaceDetailData {
    val weatherIcon = weather.firstOrNull()?.icon ?: ""

    val hourlyItems = forecast.list.take(8).map { slot ->
        HourlyWeather(
            time      = slot.dt.toHourLabel(),
            tempF     = slot.main.temp.toInt(),
            type      = slot.weather.firstOrNull()?.icon?.toWeatherType() ?: WeatherType.SUNNY,
            precipPct = (slot.pop * 100).toInt()
        )
    }

    val dailyItems = forecast.list
        .groupBy { it.dt.toDayLabel() }
        .entries
        .take(5)
        .map { (dayLabel, slots) ->
            DailyWeather(
                day       = dayLabel,
                high      = slots.maxOf { it.main.tempMax.toInt() },
                low       = slots.minOf { it.main.tempMin.toInt() },
                type      = slots[slots.size / 2].weather.firstOrNull()
                    ?.icon?.toWeatherType() ?: WeatherType.SUNNY,
                precipPct = (slots.maxOf { it.pop } * 100).toInt()
            )
        }

    return PlaceDetailData(
        city          = city,
        country       = country,
        temp          = main.temp.toInt(),
        feelsLike     = main.feelsLike.toInt(),
        high          = main.tempMax.toInt(),
        low           = main.tempMin.toInt(),
        description   = weather.firstOrNull()?.description
            ?.replaceFirstChar { it.uppercase() } ?: "",
        humidity      = main.humidity,
        windMs        = wind.speed,
        pressureHpa   = main.pressure,
        cloudinessPct = clouds.all,
        weatherType   = weatherIcon.toWeatherType(),
        hourly        = hourlyItems,
        daily         = dailyItems,
        dateLabel     = dt.toDateLabel(),
        isRefreshing  = false
    )
}

 fun String.toApiUnits() = when (this) {
    "Celsius"    -> "metric"
    "Fahrenheit" -> "imperial"
    else         -> "standard"
}

 fun String.toApiLang() = when (this) {
    "Arabic" -> "ar"
    else     -> "en"
}

 fun String.toUnitSymbol() = when (this) {
    "Celsius"    -> "°C"
    "Fahrenheit" -> "°F"
    else         -> "K"
}