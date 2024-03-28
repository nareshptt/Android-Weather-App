package com.example.weatherapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        Searchcity()
         fetchWeatherData("Goa")


    }

    private fun Searchcity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :android.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return  true
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(Apiinterface::class.java)

        val response = retrofit.gateWeatherData(cityName,"68f7bc5cb3c0b960f16b1fa90a0e0611", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responsebody = response.body()
                if (response.isSuccessful && responsebody != null){
                    val temp = responsebody.main.temp.toString()
                    val humidity = responsebody.main.humidity
                    val windspeed = responsebody.wind.speed
                    val sunrise = responsebody.sys.sunrise.toLong()
                    val sunset = responsebody.sys.sunset.toLong()
                    val seaLevel = responsebody.main.pressure
                    val condition = responsebody.weather.firstOrNull()?.main?: "Unknown"
                    val maxTemp = responsebody.main.temp_max
                    val minTemp = responsebody.main.temp_min


                    binding.temp.text = "$temp°C"
                    binding.max.text = "Max Temp: $maxTemp°C"
                    binding.min.text = "Min Temp: $minTemp°C"
                    binding.texthumidity.text = "$humidity %"
                    binding.textwind.text = "$windspeed m/s"
                    binding.textsunrise.text = "${daytime(sunrise)}"
                    binding.textsunset.text = "${daytime(sunset)}"
                    binding.sealevel.text = "$seaLevel hpa"
                    binding.textconditions.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text = data()
                        binding.city.text = "$cityName"

                    changeimagesacordingToweather(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeimagesacordingToweather(conditions: String) {
        when(conditions){
            "Clear Sky","Sunny","Clear", -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
            }
            "Light Rain","Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
            }
        }
    }

    private fun data(): String {
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun daytime(timestamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }


}