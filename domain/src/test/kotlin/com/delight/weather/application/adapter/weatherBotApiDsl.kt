package com.delight.weather.application.adapter

import com.delight.weather.faker
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.CurrentExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.ForecastHourlyExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.HistoricalHourlyExternalApiResponseDto
import java.time.LocalDateTime

@DslMarker
annotation class CurrentExternalApiResponseDtoDsl
@DslMarker
annotation class ForecastHourlyExternalApiResponseDtoDsl
@DslMarker
annotation class HistoricalHourlyExternalApiResponseDtoDsl

fun currentExternalApiResponseDto(init: CurrentExternalApiResponseDtoBuilder.() -> Unit): CurrentExternalApiResponseDto {
    val builder: CurrentExternalApiResponseDtoBuilder = faker.randomProvider.randomClassInstance()
    init(builder)
    return builder.build()
}

fun forecastHourlyExternalApiResponseDto(
    init: ForecastHourlyExternalApiResponseDtoBuilder.() -> Unit
): ForecastHourlyExternalApiResponseDto {
    val builder: ForecastHourlyExternalApiResponseDtoBuilder = faker.randomProvider.randomClassInstance()
    init(builder)
    return builder.build()
}

fun historicalHourlyExternalApiResponseDto(
    init: HistoricalHourlyExternalApiResponseDtoBuilder.() -> Unit
): HistoricalHourlyExternalApiResponseDto {
    val builder: HistoricalHourlyExternalApiResponseDtoBuilder = faker.randomProvider.randomClassInstance()
    init(builder)
    return builder.build()
}

@CurrentExternalApiResponseDtoDsl
data class CurrentExternalApiResponseDtoBuilder(
    var code: Int,
    var temp: Float,
    var rain1h: Int
) {
    fun build() = CurrentExternalApiResponseDto(LocalDateTime.now(), code, temp, rain1h)
}

@ForecastHourlyExternalApiResponseDtoDsl
data class ForecastHourlyExternalApiResponseDtoBuilder(
    var hoursDiff: Long,
    var code: Int,
    var min_temp: Float,
    var max_temp: Float,
    var rain: Int
) {
    fun build(): ForecastHourlyExternalApiResponseDto {
        val date = when {
            hoursDiff > 72 -> LocalDateTime.now().plusHours(72)
            hoursDiff >= 0 -> LocalDateTime.now().plusHours(hoursDiff)
            hoursDiff < -12 -> LocalDateTime.now().plusHours(-12)
            else -> LocalDateTime.now().plusHours(hoursDiff)
        }

        return ForecastHourlyExternalApiResponseDto(date, code, min_temp, max_temp, rain)
    }
}

@HistoricalHourlyExternalApiResponseDtoDsl
data class HistoricalHourlyExternalApiResponseDtoBuilder(
    var hoursDiff: Long,
    var code: Int,
    var temp: Float,
    var rain1h: Int
) {
    fun build(): HistoricalHourlyExternalApiResponseDto {
        val date = when {
            hoursDiff > 72 -> LocalDateTime.now().plusHours(72)
            hoursDiff >= 0 -> LocalDateTime.now().plusHours(hoursDiff)
            hoursDiff < -24 -> LocalDateTime.now().plusHours(-25)
            else -> LocalDateTime.now().plusHours(hoursDiff)
        }

        return HistoricalHourlyExternalApiResponseDto(date, code, temp, rain1h)
    }
}
