package com.delight.weather.infrastructure.externalapi.weatherbot.dto

import java.time.LocalDateTime

data class ForecastHourlyExternalApiResponseDto(
    val timestamp: LocalDateTime,
    val code: Int,
    val min_temp: Float,
    val max_temp: Float,
    val rain: Int
)
