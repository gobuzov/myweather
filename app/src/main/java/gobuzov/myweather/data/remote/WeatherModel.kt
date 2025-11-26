package gobuzov.myweather.data.remote

data class WeatherModel(
    val current: Current,
    val location: Location,
    val forecast: ForecastContainer
)