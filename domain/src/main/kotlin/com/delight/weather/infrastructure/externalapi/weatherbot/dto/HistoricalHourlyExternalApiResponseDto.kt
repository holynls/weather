package com.delight.weather.infrastructure.externalapi.weatherbot.dto

import java.time.LocalDateTime

data class HistoricalHourlyExternalApiResponseDto(
    val timestamp: LocalDateTime,
    val code: Int,
    val temp: Float,
    val rain1h: Int
)
