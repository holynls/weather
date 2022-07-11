package com.delight.weather.domain.weather

import com.delight.weather.domain.weather.dto.WeatherSummaryDto

interface WeatherService {

    fun getSummary(
        lat: Float,
        lon: Float,
    ): WeatherSummaryDto
}
