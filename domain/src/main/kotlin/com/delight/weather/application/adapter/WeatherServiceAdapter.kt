package com.delight.weather.application.adapter

import com.delight.weather.domain.weatherbot.WeatherService
import com.delight.weather.domain.weatherbot.dto.CurrentDto
import com.delight.weather.domain.weatherbot.dto.ForecastHourlyDto
import com.delight.weather.domain.weatherbot.dto.HistoricalHourlyDto
import com.delight.weather.domain.weatherbot.dto.WeatherSummaryDto
import com.delight.weather.infrastructure.exceptions.ExternalApiException
import com.delight.weather.infrastructure.externalapi.weatherbot.WeatherBotApiService
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class WeatherServiceAdapter(
    private val weatherBotApiService: WeatherBotApiService
): WeatherService {

    private val objectMapper = jacksonObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    @Value("\${api.weather-bot.api-key}")
    lateinit var apiKey: String

    override fun getSummary(lat: Float, lon: Float): WeatherSummaryDto {
        TODO("Not yet implemented")
    }

    private fun getCurrent(lat: Float, lon: Float): CurrentDto = runBlocking {
        run {
            val response = weatherBotApiService.current(apiKey, lat, lon)

            when (response.isSuccessful) {
                false -> {
                    throw ExternalApiException(
                        HttpStatus.valueOf(response.code()),
                        response.errorBody()!!.string()
                    )
                }
                true -> response.body()!!.let { CurrentDto.of(it) }
            }
        }
    }

    private fun getForecastByHourly(lat: Float, lon: Float, hourOffset: Int): ForecastHourlyDto = runBlocking {
        run {
            val response = weatherBotApiService.forecastHourly(apiKey, lat, lon, hourOffset)

            when (response.isSuccessful) {
                false -> {
                    throw ExternalApiException(
                        HttpStatus.valueOf(response.code()),
                        response.errorBody()!!.string()
                    )
                }
                true -> response.body()!!.let { ForecastHourlyDto.of(it) }
            }
        }
    }

    private fun getHistoricalByHourly(lat: Float, lon: Float, hourOffset: Int): HistoricalHourlyDto = runBlocking {
        run {
            val response = weatherBotApiService.historicalHourly(apiKey, lat, lon, hourOffset)

            when (response.isSuccessful) {
                false -> {
                    throw ExternalApiException(
                        HttpStatus.valueOf(response.code()),
                        response.errorBody()!!.string()
                    )
                }
                true -> response.body()!!.let { HistoricalHourlyDto.of(it) }
            }
        }
    }

}
