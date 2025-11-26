package gobuzov.myweather.data.remote

data class ForecastModel(
    val location: Location,
    val current: Current,
    val forecast: ForecastContainer? = null
)

data class ForecastContainer(
    val forecastday: List<ForecastDay>? = null
)

data class ForecastDay(
    val date: String,
    val day: Day,
    val hour: List<Hour>? = null
)

data class Day(
    val avgtemp_c: String,
    val maxtemp_c: String,
    val mintemp_c: String,
    val condition: Condition
)
data class Hour(
    val temp_c: String,
    val time: String,
    val condition: Condition
)