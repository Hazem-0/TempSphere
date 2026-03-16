package com.darkzoom.tempsphere.data.datasource

import com.darkzoom.tempsphere.data.contract.WeatherRemoteDatasource
import com.darkzoom.tempsphere.data.remote.model.City
import com.darkzoom.tempsphere.data.remote.model.Clouds
import com.darkzoom.tempsphere.data.remote.model.Coord
import com.darkzoom.tempsphere.data.remote.model.CurrentWeatherResponse
import com.darkzoom.tempsphere.data.remote.model.ForecastResponse
import com.darkzoom.tempsphere.data.remote.model.MainWeather
import com.darkzoom.tempsphere.data.remote.model.SysCurrent
import com.darkzoom.tempsphere.data.remote.model.Wind

class FakeWeatherRemoteDatasource(
    var currentWeatherResponse: CurrentWeatherResponse = CurrentWeatherResponse(
        coord = Coord(
            0.0,
            0.0
        ),
        weather = emptyList(),
        base = "stations",
        main = MainWeather(0.0, 0.0, 0.0, 0.0, 0, 0 , 0, 0, 0.0),
        visibility = 10000,
        wind = Wind(0.0, 0 ,0.0),
        clouds = Clouds(0),
        dt = 1618317040,
        sys = SysCurrent(1, 1, "EG", 1618310000, 1618350000),
        timezone = 7200,
        id = 360630,
        name = "Cairo",
        cod = 200
    ),
    var forecastResponse: ForecastResponse = ForecastResponse(
        code = "200",
        message = 0,
        cnt = 0,
        list = emptyList(),
        city = City(0, "Cairo", Coord(0.0, 0.0), "EG", 0, 0, 0, 0)
    ),
    var shouldThrow: Boolean = false
) : WeatherRemoteDatasource {

    override suspend fun getCurrentWeather(
        lat: Double, lon: Double, apiKey: String, units: String, lang: String
    ): CurrentWeatherResponse {
        if (shouldThrow) throw Exception("Network error")
        return currentWeatherResponse
    }

    override suspend fun getForecast(
        lat: Double, lon: Double, apiKey: String, units: String, lang: String
    ): ForecastResponse {
        if (shouldThrow) throw Exception("Network error")
        return forecastResponse
    }
}