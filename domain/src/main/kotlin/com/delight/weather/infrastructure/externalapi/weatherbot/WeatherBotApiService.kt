package com.delight.weather.infrastructure.externalapi.weatherbot

import com.delight.weather.infrastructure.externalapi.weatherbot.dto.CurrentExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.ForecastHourlyExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.HistoricalHourlyExternalApiResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherBotApiService {

    @GET("/current")
    suspend fun current(
        @Query("api_key") apiKey: String,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
    ): Response<CurrentExternalApiResponseDto>

    @GET("/forecast/hourly")
    suspend fun forecastHourly(
        @Query("api_key") apiKey: String,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("hour_offset") hourOffset: Int,
    ): Response<ForecastHourlyExternalApiResponseDto>

    @GET("/historical/hourly")
    suspend fun historicalHourly(
        @Query("api_key") apiKey: String,
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("hour_offset") hourOffset: Int,
    ): Response<HistoricalHourlyExternalApiResponseDto>

}
