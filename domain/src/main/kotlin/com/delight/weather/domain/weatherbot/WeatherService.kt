package com.delight.weather.domain.weatherbot

import com.delight.weather.domain.weatherbot.dto.WeatherSummaryDto

interface WeatherService {

    fun getSummary(
        lat: Float,
        lon: Float,
    ): WeatherSummaryDto
}
