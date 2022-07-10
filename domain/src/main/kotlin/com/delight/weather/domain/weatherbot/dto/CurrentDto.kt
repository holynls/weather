package com.delight.weather.domain.weatherbot.dto

import com.delight.weather.infrastructure.externalapi.weatherbot.dto.CurrentExternalApiResponseDto
import java.time.LocalDateTime

data class CurrentDto(
    val timestamp: LocalDateTime,
    val code: Int,
    val temp: Float,
    val rain1h: Int
) {
    companion object {
        fun of(currentExternalApiResponseDto: CurrentExternalApiResponseDto): CurrentDto = CurrentDto(
            timestamp = currentExternalApiResponseDto.timestamp,
            code = currentExternalApiResponseDto.code,
            temp = currentExternalApiResponseDto.temp,
            rain1h = currentExternalApiResponseDto.rain1h
        )
    }
}
