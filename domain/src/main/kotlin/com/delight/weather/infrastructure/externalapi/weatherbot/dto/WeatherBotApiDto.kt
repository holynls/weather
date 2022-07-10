package com.delight.weather.infrastructure.externalapi.weatherbot.dto

import java.time.LocalDateTime

sealed class WeatherBotApiDto(
    timestamp: LocalDateTime
)

class CurrentExternalApiResponseDto(
    val timestamp: LocalDateTime,
    val code: Int,
    val temp: Float,
    val rain1h: Int
): WeatherBotApiDto(timestamp)

class ForecastHourlyExternalApiResponseDto(
    val timestamp: LocalDateTime,
    val code: Int,
    val min_temp: Float,
    val max_temp: Float,
    val rain: Int
): WeatherBotApiDto(timestamp)

class HistoricalHourlyExternalApiResponseDto(
    val timestamp: LocalDateTime,
    val code: Int,
    val temp: Float,
    val rain1h: Int
): WeatherBotApiDto(timestamp)
