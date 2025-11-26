package gobuzov.myweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import gobuzov.myweather.ui.theme.MyWeatherTheme
///
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
///
import gobuzov.myweather.data.remote.ForecastModel
import gobuzov.myweather.data.remote.RetrofitInstance
import kotlinx.coroutines.*
import gobuzov.myweather.MainActivity.Companion.STATE_SPLASH
import gobuzov.myweather.MainActivity.Companion.STATE_ERROR
import gobuzov.myweather.MainActivity.Companion.STATE_GOOD
import gobuzov.myweather.MainActivity.Companion.model
import gobuzov.myweather.MainActivity.Companion.state
import gobuzov.myweather.data.remote.Day
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {
    companion object {
        const val STATE_SPLASH: Int = 0
        const val STATE_ERROR: Int = 1
        const val STATE_GOOD: Int = 2
        val state = mutableStateOf(STATE_SPLASH);
        lateinit var model : ForecastModel
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyWeatherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MakeUI()
                }
            }
        }
    }
}
suspend fun getData() : ForecastModel?{
    var model : ForecastModel? = null
    try {
        val response = RetrofitInstance.weatherApi.getForecastWeather()
        if (response.isSuccessful) {
            model = response.body()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return model
}
@Composable
fun MakeUI() {
    if (STATE_SPLASH==state.value) {
        SplashScreen()
    }else if (STATE_ERROR==state.value){
        ErrorScreen()
    }else if (STATE_GOOD==state.value) {
        WeatherScreen()
    }
}
@Composable
fun WeatherScreen() {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = model.location.name, fontSize = 30.sp, color = Color.Black)
        Text(text = model.location.country, fontSize = 16.sp, color = Color.Gray)
        //Spacer(modifier = Modifier.height(25.dp))
        Text(text = model.location.localtime, fontSize = 20.sp, color = Color.Black)
        Text(
            text = model.current.temp_c + "°C",
            fontSize = 80.sp, color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Почасовой прогноз на сегодня:", fontSize = 20.sp, color = Color.Black)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            LazyRow{ // показываем только текущего дня
                val hours = model.forecast?.forecastday?.get(0)?.hour;
                hours?.size?.let {
                    items(it){ index ->
                        val item = hours[index]
                        HourItem(
                            //icon = "https:${item.condition.icon}",
                            temp = item.temp_c+"°C",
                            hour = item.time.substring(11, 16)
                        )
                        Spacer(modifier = Modifier.width(35.dp))
                    }
                }
            }
        }
        Column (){
            val forecastdays = model.forecast?.forecastday
            DayItem(dayname = "Сегодня", daydate = forecastdays!!.get(0).date, day = forecastdays!!.get(0).day)
            DayItem(dayname = "Завтра", daydate = forecastdays!!.get(1).date, day = forecastdays!!.get(1).day)
            DayItem(dayname = "Послезавтра", daydate = forecastdays!!.get(2).date, day = forecastdays!!.get(2).day)
        }
    }
}
@Composable
fun DayItem(
    dayname: String,
    daydate: String,
    day: Day
) {
    Box(
        modifier = Modifier
            .height(150.dp)
    ){
        Row (
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize(),
        ) {
            Column(modifier = Modifier.width(160.dp)) {
                Text(text = dayname, fontSize = 20.sp, color = Color.Black )
                Text(text = daydate, fontSize = 20.sp, color = Color.Black )
            }

            /*  AsyncImage(
                  modifier = Modifier
                      .size(65.dp)
                      .weight(2f),
                  model = icon,
                  contentDescription = "Icon"
              )*/
            Row() {
                Text(text = day.mintemp_c, fontSize = 40.sp, color = Color.Blue)
                Text(text = "/", fontSize = 40.sp, color = Color.Black)
                Text(text = day.maxtemp_c, fontSize = 40.sp, color = Color.Red)
            }
        }
    }
}

@Composable
fun HourItem(
    //icon: Any?,
    temp: String,
    hour: String
){
    Box(
        modifier = Modifier.height(120.dp).width(80.dp)
    ){
        Column(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = hour,
                fontSize = 20.sp,
                color = Color.Black
            )

          /*  AsyncImage(
                modifier = Modifier
                    .size(65.dp)
                    .weight(2f),
                model = icon,
                contentDescription = "Icon"
            )*/
            Text(
                text = temp,
                fontSize = 20.sp,
                color = Color.Blue
            )
        }
    }
}
@Composable
fun SplashScreen() {
    LaunchedEffect(Unit) {
        GlobalScope.launch { // Launches a new coroutine
            var result: ForecastModel? = getData()
            if (null != result) {
                model = result
                state.value = STATE_GOOD
            } else {
                state.value = STATE_ERROR
            }
            delay(2000) // Keep splash visible for 2 seconds
        }
    }
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.pawer),
                contentDescription = "App logo",
                modifier = Modifier.size(280.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator()
            Text(
                text = "Пауэр Интэрнэшнл",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}
@Composable
fun ErrorScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ошибка соединения",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Button(
                onClick = {
                    state.value = STATE_SPLASH
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp) // Adds padding around the button
            ) {
                Text("Повторить запрос")
            }
            Button(
                onClick = {
                    exitProcess(0)
                },
                modifier = Modifier
                    .fillMaxWidth() // Makes the button fill the available width
                    .padding(16.dp) // Adds padding around the button
            ) {
                Text("Выйти из программы")
            }
        }
    }
}