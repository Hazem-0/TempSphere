import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey
    val id: Int,
    val lat: Double,
    val lon: Double,
    val cityName: String,
    val country: String,
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val visibility: Int,
    val windSpeed: Double,
    val windDeg: Int,
    val windGust: Double?,
    val cloudsAll: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val sunrise: Long,
    val sunset: Long,
    val dt: Long,
    val timezone: Int,
    val units: String,
    val lang: String,
    val cachedAt: Long = System.currentTimeMillis()
)