package com.delight.weather.infrastructure.config

import com.delight.weather.infrastructure.externalapi.weatherbot.WeatherBotApiService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class WeatherBotApiConfig {

    @Value("\${api.weather-bot.url}")
    lateinit var baseUrl: String

    @Bean
    fun weatherBotApiService(): WeatherBotApiService =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
            .create(WeatherBotApiService::class.java)

}
