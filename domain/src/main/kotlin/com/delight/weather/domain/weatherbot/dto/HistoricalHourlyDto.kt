package com.delight.weather.domain.weatherbot.dto

import com.delight.weather.infrastructure.externalapi.weatherbot.dto.HistoricalHourlyExternalApiResponseDto
import java.time.LocalDateTime

data class HistoricalHourlyDto(
    val timestamp: LocalDateTime,
    val code: Int,
    val temp: Float,
    val rain1h: Int
) {
    companion object {
        fun of(historicalHourlyExternalApiResponseDto: HistoricalHourlyExternalApiResponseDto): HistoricalHourlyDto =
            HistoricalHourlyDto(
                timestamp = historicalHourlyExternalApiResponseDto.timestamp,
                code = historicalHourlyExternalApiResponseDto.code,
                temp = historicalHourlyExternalApiResponseDto.temp,
                rain1h = historicalHourlyExternalApiResponseDto.rain1h
            )
    }
}
