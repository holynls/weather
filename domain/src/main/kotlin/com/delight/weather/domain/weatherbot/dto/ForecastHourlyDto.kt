package com.delight.weather.domain.weatherbot.dto

import com.delight.weather.infrastructure.externalapi.weatherbot.dto.ForecastHourlyExternalApiResponseDto
import java.time.LocalDateTime

data class ForecastHourlyDto(
    val timestamp: LocalDateTime,
    val code: Int,
    val minTemp: Float,
    val maxTemp: Float,
    val rain: Int
) {
    companion object {
        fun of(forecastHourlyExternalApiResponseDto: ForecastHourlyExternalApiResponseDto): ForecastHourlyDto =
            ForecastHourlyDto(
                timestamp = forecastHourlyExternalApiResponseDto.timestamp,
                code = forecastHourlyExternalApiResponseDto.code,
                minTemp = forecastHourlyExternalApiResponseDto.min_temp,
                maxTemp = forecastHourlyExternalApiResponseDto.max_temp,
                rain = forecastHourlyExternalApiResponseDto.rain
            )
    }
}
