package com.delight.weather.application.adapter

import com.delight.weather.domain.weatherbot.WeatherService
import com.delight.weather.domain.weatherbot.dto.CurrentDto
import com.delight.weather.domain.weatherbot.dto.ForecastHourlyDto
import com.delight.weather.domain.weatherbot.dto.HistoricalHourlyDto
import com.delight.weather.domain.weatherbot.dto.WeatherSummaryDto
import com.delight.weather.infrastructure.exceptions.ExternalApiException
import com.delight.weather.infrastructure.externalapi.weatherbot.WeatherBotApiService
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.CurrentExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.ForecastHourlyExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.HistoricalHourlyExternalApiResponseDto
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import retrofit2.Response
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@Service
class WeatherServiceAdapter(
    private val weatherBotApiService: WeatherBotApiService
) : WeatherService {

    private val objectMapper = jacksonObjectMapper()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    private val logger = LoggerFactory.getLogger(this::class.java)

    @OptIn(ExperimentalTime::class)
    override fun getSummary(lat: Float, lon: Float): WeatherSummaryDto {
        var current: CurrentDto? = null
        val forecastList: MutableList<ForecastHourlyDto> = mutableListOf()
        val historicalList: MutableList<HistoricalHourlyDto> = mutableListOf()

        val elapsed = measureTime {
            val responseList = runBlocking {
                getData(lat, lon)
            }

            responseList.forEach {
                when (it.isSuccessful) {
                    false -> {
                        throw ExternalApiException(
                            HttpStatus.valueOf(it.code()),
                            it.errorBody()!!.string()
                        )
                    }
                    true -> when (val data = it.body()!!) {
                        is CurrentExternalApiResponseDto -> current = CurrentDto.of(data)
                        is ForecastHourlyExternalApiResponseDto -> forecastList.add(ForecastHourlyDto.of(data))
                        is HistoricalHourlyExternalApiResponseDto -> historicalList.add(HistoricalHourlyDto.of(data))
                    }
                }
            }
        }

        logger.info("total time $elapsed")

        logger.info("current - $current")
        logger.info("forecastList - $forecastList")
        logger.info("historicalList - $historicalList")

        return WeatherSummaryDto("a", "b", "c")
    }

    private suspend fun getData(lat: Float, lon: Float) = runBlocking {
        val current = async(Dispatchers.IO) {
            weatherBotApiService.current(apiKey, lat, lon)
        }

        val forecastList = (6..48 step 6).map { time ->
            async(Dispatchers.IO) {
                weatherBotApiService.forecastHourly(apiKey, lat, lon, time)
            }
        }

        val historicalList = (-24..-6 step 6).map { time ->
            async(Dispatchers.IO) {
                weatherBotApiService.historicalHourly(apiKey, lat, lon, time)
            }
        }

        mutableListOf<Deferred<Response<*>>>(current).apply {
            this.addAll(forecastList)
            this.addAll(historicalList)
        }.awaitAll()
    }

    private suspend fun getCurrent(lat: Float, lon: Float): CurrentDto = runBlocking {
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

    private suspend fun getForecastByHourlyInParallel(lat: Float, lon: Float): List<ForecastHourlyDto> =
        coroutineScope {
            val result = (6..48 step 6).map { time ->
                async(Dispatchers.IO) {
                    weatherBotApiService.forecastHourly(apiKey, lat, lon, time)
                }
            }.awaitAll()

            result.map {
                when (it.isSuccessful) {
                    false -> {
                        throw ExternalApiException(
                            HttpStatus.valueOf(it.code()),
                            it.errorBody()!!.string()
                        )
                    }
                    true -> ForecastHourlyDto.of(it.body()!!)
                }
            }
        }

    private suspend fun getHistoricalByHourlyInParallel(lat: Float, lon: Float): List<HistoricalHourlyDto> =
        coroutineScope {
            val result = (-24..-6 step 6).map { time ->
                async(Dispatchers.IO) {
                    weatherBotApiService.historicalHourly(apiKey, lat, lon, time)
                }
            }.awaitAll()

            result.map {
                when (it.isSuccessful) {
                    false -> {
                        throw ExternalApiException(
                            HttpStatus.valueOf(it.code()),
                            it.errorBody()!!.string()
                        )
                    }
                    true -> HistoricalHourlyDto.of(it.body()!!)
                }
            }
        }

    companion object {
        const val apiKey = "CMRJW4WT7V3QA5AOIGPBC"
    }
}
