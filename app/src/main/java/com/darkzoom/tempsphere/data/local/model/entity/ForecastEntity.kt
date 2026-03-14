import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_items")
data class ForecastItemEntity(
    @PrimaryKey
    val dt: Long,
    val cityId: Int,
    val cityName: String,
    val country: String,
    val cityLat: Double,
    val cityLon: Double,
    val cityTimezone: Int,
    val citySunrise: Long,
    val citySunset: Long,
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
    val pop: Double,
    val dtTxt: String,
    val units: String,
    val lang: String,
    val cachedAt: Long = System.currentTimeMillis()
)