package gobuzov.myweather.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/v1/forecast.json")
    suspend fun getForecastWeather(
        @Query("key") apiKey: String = "fa8b3df74d4042b9aa7135114252304",
        @Query("q") city: String = "55.7569,37.6151",
        @Query("days") days: Int = 3
    ): retrofit2.Response<ForecastModel>

}