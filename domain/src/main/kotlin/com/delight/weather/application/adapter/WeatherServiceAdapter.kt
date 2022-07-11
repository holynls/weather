package com.delight.weather.application.adapter

import com.delight.weather.domain.weather.WeatherService
import com.delight.weather.domain.weather.dto.CurrentDto
import com.delight.weather.domain.weather.dto.ForecastHourlyDto
import com.delight.weather.domain.weather.dto.HistoricalHourlyDto
import com.delight.weather.domain.weather.dto.WeatherSummaryDto
import com.delight.weather.infrastructure.exceptions.ExternalApiException
import com.delight.weather.infrastructure.externalapi.weatherbot.WeatherBotApiService
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.CurrentExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.ForecastHourlyExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.HistoricalHourlyExternalApiResponseDto
import kotlinx.coroutines.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import retrofit2.Response
import kotlin.math.abs

@Service
class WeatherServiceAdapter(
    private val weatherBotApiService: WeatherBotApiService
) : WeatherService {

    override fun getSummary(lat: Float, lon: Float): WeatherSummaryDto {
        var current: CurrentDto? = null
        val forecastList: MutableList<ForecastHourlyDto> = mutableListOf()
        val historicalList: MutableList<HistoricalHourlyDto> = mutableListOf()

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

        if (current == null || forecastList.isEmpty() || historicalList.isEmpty())
            throw IllegalStateException("날씨 정보를 받아오지 못했습니다.")

        return WeatherSummaryDto(
            decideGreeting(current!!),
            decideTemperature(current!!, historicalList),
            decideHeadsUp(forecastList)
        )
    }

    private fun decideGreeting(current: CurrentDto): String =
        when {
            current.code == SNOWY && current.rain1h >= 100 -> "폭설이 내리고 있어요."
            current.code == SNOWY -> "눈이 포슬포슬 내립니다."
            current.code == RAINY && current.rain1h >= 100 -> "폭우가 내리고 있어요."
            current.code == RAINY -> "비가 오고 있습니다."
            current.code == CLOUDY -> "날씨가 약간은 칙칙해요."
            current.code == CLEAR && current.temp >= 30 -> "따사로운 햇살을 맞으세요."
            current.temp <= 0 -> "날이 참 춥네요."
            else -> "날씨가 참 맑습니다."
        }

    private fun decideTemperature(current: CurrentDto, histories: List<HistoricalHourlyDto>): String {
        val lastDateTemp = histories.minByOrNull { it.timestamp }?.temp
            ?: throw IllegalStateException("날씨 정보를 받아오지 못했습니다.")
        val temperatureDiff = abs(current.temp - lastDateTemp)

        val temperatureDifference = when {
            current.temp >= 15 && current.temp < lastDateTemp -> "어제보다 ${temperatureDiff}도 덜 덥습니다."
            current.temp < 15 && current.temp < lastDateTemp -> "어제보다 ${temperatureDiff}도 더 춥습니다."
            current.temp >= 15 && current.temp > lastDateTemp -> "어제보다 ${temperatureDiff}도 더 덥습니다."
            current.temp < 15 && current.temp > lastDateTemp -> "어제보다 ${temperatureDiff}도 덜 춥습니다."
            current.temp >= 15 && current.temp == lastDateTemp -> "어제와 비슷하게 덥습니다."
            current.temp < 15 && current.temp == lastDateTemp -> "어제와 비슷하게 춥습니다."
            else -> "날씨 정보를 알 수 없습니다."
        }

        val temperatureList = listOf(current.temp) + histories.map { it.temp }

        return "$temperatureDifference 최고기온은 ${temperatureList.maxByOrNull { it }}도, 최저기온은 ${temperatureList.minByOrNull { it }}도 입니다."
    }

    private fun decideHeadsUp(forecasts: List<ForecastHourlyDto>): String {
        val listOf48HoursIn = forecasts.sortedBy { it.timestamp }
        val listOf24HoursIn = listOf48HoursIn.take(4)

        return when {
            listOf24HoursIn.count { it.code == SNOWY } >= 2 -> "내일 폭설이 내릴 수도 있으니 외출 시 주의하세요."
            listOf48HoursIn.count { it.code == SNOWY } >= 2 -> "눈이 내릴 예정이니 외출 시 주의하세요."
            listOf24HoursIn.count { it.code == RAINY } >= 2 -> "폭우가 내릴 예정이에요. 우산을 미리 챙겨두세요."
            listOf48HoursIn.count { it.code == RAINY } >= 2 -> "며칠동안 비 소식이 있어요."
            else -> "날씨는 대체로 평온할 예정이에요."
        }
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

    companion object {
        const val apiKey = "CMRJW4WT7V3QA5AOIGPBC"
        const val SNOWY = 3
        const val RAINY = 2
        const val CLOUDY = 1
        const val CLEAR = 0
    }
}
