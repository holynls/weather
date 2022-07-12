package com.delight.weather.application.adapter

import com.delight.weather.infrastructure.exceptions.ExternalApiException
import com.delight.weather.infrastructure.externalapi.weatherbot.WeatherBotApiService
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.CurrentExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.ForecastHourlyExternalApiResponseDto
import com.delight.weather.infrastructure.externalapi.weatherbot.dto.HistoricalHourlyExternalApiResponseDto
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockk
import okhttp3.ResponseBody
import org.amshove.kluent.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import retrofit2.Response

class WeatherServiceAdapterTest {

    private val weatherBotApiService: WeatherBotApiService = mockk(relaxed = true)

    @InjectMockKs
    private lateinit var service: WeatherServiceAdapter

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
        MockKAnnotations.init(this)
    }

    @Test
    fun `외부 api 호출 에러시 ExternalApiException throw`() {
        val current = currentExternalApiResponseDto { }

        coEvery { weatherBotApiService.current(any(), any(), any()) } returns Response.success(current)
        coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), any()) } returns
                Response.error(401, ResponseBody.create(null, "api 키가 다름"))

        assertThrows<ExternalApiException> { service.getSummary(any(), any()) }
    }

    @Nested
    inner class GreetingMessageTest {
        private val forecastHourlyExternalApiResponseDto: ForecastHourlyExternalApiResponseDto =
            forecastHourlyExternalApiResponseDto { }
        private val historicalHourlyExternalApiResponseDto: HistoricalHourlyExternalApiResponseDto =
            historicalHourlyExternalApiResponseDto { }

        @BeforeEach
        fun beforeEach() {
            clearAllMocks()
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), any()) } returns
                    Response.success(forecastHourlyExternalApiResponseDto)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), any()) } returns
                    Response.success(historicalHourlyExternalApiResponseDto)
        }

        @Test
        fun `폭설 조건에 맞을 경우 폭설 메시지를 생성해준다`() {
            val current = currentExternalApiResponseDto {
                code = 3
                rain1h = 150
            }

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns Response.success(current)

            val result = service.getSummary(any(), any())

            result.greeting shouldBe "폭설이 내리고 있어요."
        }

        @Test
        fun `눈 조건에 맞을 경우 눈 메시지를 생성해준다`() {
            val current = currentExternalApiResponseDto {
                code = 3
                rain1h = 50
            }

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns Response.success(current)

            val result = service.getSummary(any(), any())

            result.greeting shouldBe "눈이 포슬포슬 내립니다."
        }

        @Test
        fun `폭우 조건에 맞을 경우 폭우 메시지를 생성해준다`() {
            val current = currentExternalApiResponseDto {
                code = 2
                rain1h = 150
            }

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns Response.success(current)

            val result = service.getSummary(any(), any())

            result.greeting shouldBe "폭우가 내리고 있어요."
        }

        @Test
        fun `비 조건에 맞을 경우 비 메시지를 생성해준다`() {
            val current = currentExternalApiResponseDto {
                code = 2
                rain1h = 50
            }

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns Response.success(current)

            val result = service.getSummary(any(), any())

            result.greeting shouldBe "비가 오고 있습니다."
        }

        @Test
        fun `흐림 조건에 맞을 경우 흐림 메시지를 생성해준다`() {
            val current = currentExternalApiResponseDto {
                code = 1
                rain1h = 20
            }

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns Response.success(current)

            val result = service.getSummary(any(), any())

            result.greeting shouldBe "날씨가 약간은 칙칙해요."
        }

        @Test
        fun `햇살 조건에 맞을 경우 햇살 메시지를 생성해준다`() {
            val current = currentExternalApiResponseDto {
                code = 0
                temp = 31f
                rain1h = 150
            }

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns Response.success(current)

            val result = service.getSummary(any(), any())

            result.greeting shouldBe "따사로운 햇살을 맞으세요."
        }

        @Test
        fun `추위 조건에 맞을 경우 추위 메시지를 생성해준다`() {
            val current = currentExternalApiResponseDto {
                code = 0
                temp = -1f
                rain1h = 150
            }

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns Response.success(current)

            val result = service.getSummary(any(), any())

            result.greeting shouldBe "날이 참 춥네요."
        }

        @Test
        fun `모두 해당하지 않을 경우 날씨 맑음 메시지를 표시해준다`() {
            val current = currentExternalApiResponseDto {
                code = 0
                temp = 15f
                rain1h = 150
            }

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns Response.success(current)

            val result = service.getSummary(any(), any())

            result.greeting shouldBe "날씨가 참 맑습니다."
        }
    }


    @Nested
    inner class TemperatureMessageTest {
        private val forecastHourlyExternalApiResponseDto: ForecastHourlyExternalApiResponseDto =
            forecastHourlyExternalApiResponseDto { }

        @BeforeEach
        fun beforeEach() {
            clearAllMocks()
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), any()) } returns
                    Response.success(forecastHourlyExternalApiResponseDto)
        }

        @Test
        fun `어제보다 덜 더울 때`() {
            val current = currentExternalApiResponseDto {
                temp = 16f
            }

            val historyAt24HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -24
                    temp = 18f
                }
            val historyAt18HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -18
                    temp = 18f
                }
            val historyAt12HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -12
                    temp = 18f
                }
            val historyAt6HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -6
                    temp = 18f
                }

            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -24) } returns
                    Response.success(historyAt24HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -18) } returns
                    Response.success(historyAt18HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -12) } returns
                    Response.success(historyAt12HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -6) } returns
                    Response.success(historyAt6HoursBefore)

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns
                    Response.success(current)

            val result = service.getSummary(any(), any())

            result.temperature.contains("덜 덥습니다") shouldBe true
        }

        @Test
        fun `어제보다 더 추울 때`() {
            val current = currentExternalApiResponseDto {
                temp = 14f
            }

            val historyAt24HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -24
                    temp = 18f
                }
            val historyAt18HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -18
                    temp = 18f
                }
            val historyAt12HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -12
                    temp = 18f
                }
            val historyAt6HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -6
                    temp = 18f
                }

            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -24) } returns
                    Response.success(historyAt24HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -18) } returns
                    Response.success(historyAt18HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -12) } returns
                    Response.success(historyAt12HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -6) } returns
                    Response.success(historyAt6HoursBefore)

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns
                    Response.success(current)

            val result = service.getSummary(any(), any())

            result.temperature.contains("더 춥습니다") shouldBe true
        }

        @Test
        fun `어제보다 더 더울 때`() {
            val current = currentExternalApiResponseDto {
                temp = 16f
            }

            val historyAt24HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -24
                    temp = 10f
                }
            val historyAt18HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -18
                    temp = 18f
                }
            val historyAt12HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -12
                    temp = 18f
                }
            val historyAt6HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -6
                    temp = 18f
                }

            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -24) } returns
                    Response.success(historyAt24HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -18) } returns
                    Response.success(historyAt18HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -12) } returns
                    Response.success(historyAt12HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -6) } returns
                    Response.success(historyAt6HoursBefore)

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns
                    Response.success(current)

            val result = service.getSummary(any(), any())

            result.temperature.contains("더 덥습니다") shouldBe true
        }

        @Test
        fun `어제보다 덜 추울 때`() {
            val current = currentExternalApiResponseDto {
                temp = 14f
            }

            val historyAt24HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -24
                    temp = 6f
                }
            val historyAt18HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -18
                    temp = 18f
                }
            val historyAt12HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -12
                    temp = 18f
                }
            val historyAt6HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -6
                    temp = 18f
                }

            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -24) } returns
                    Response.success(historyAt24HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -18) } returns
                    Response.success(historyAt18HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -12) } returns
                    Response.success(historyAt12HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -6) } returns
                    Response.success(historyAt6HoursBefore)

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns
                    Response.success(current)

            val result = service.getSummary(any(), any())

            result.temperature.contains("덜 춥습니다") shouldBe true
        }

        @Test
        fun `어제와 비슷하게 더울 때`() {
            val current = currentExternalApiResponseDto {
                temp = 16f
            }

            val historyAt24HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -24
                    temp = 16f
                }
            val historyAt18HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -18
                    temp = 18f
                }
            val historyAt12HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -12
                    temp = 18f
                }
            val historyAt6HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -6
                    temp = 18f
                }

            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -24) } returns
                    Response.success(historyAt24HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -18) } returns
                    Response.success(historyAt18HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -12) } returns
                    Response.success(historyAt12HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -6) } returns
                    Response.success(historyAt6HoursBefore)

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns
                    Response.success(current)

            val result = service.getSummary(any(), any())

            result.temperature.contains("비슷하게 덥습니다") shouldBe true
        }

        @Test
        fun `어제와 비슷하게 추울 때`() {
            val current = currentExternalApiResponseDto {
                temp = 14f
            }

            val historyAt24HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -24
                    temp = 14f
                }
            val historyAt18HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -18
                    temp = 18f
                }
            val historyAt12HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -12
                    temp = 18f
                }
            val historyAt6HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -6
                    temp = 18f
                }

            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -24) } returns
                    Response.success(historyAt24HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -18) } returns
                    Response.success(historyAt18HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -12) } returns
                    Response.success(historyAt12HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -6) } returns
                    Response.success(historyAt6HoursBefore)

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns
                    Response.success(current)

            val result = service.getSummary(any(), any())

            result.temperature.contains("비슷하게 춥습니다") shouldBe true
        }

        @Test
        fun `최고 기온과 최저 기온 판별`() {
            val current = currentExternalApiResponseDto {
                temp = 10f
            }

            val historyAt24HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -24
                    temp = 14f
                }
            val historyAt18HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -18
                    temp = 40f
                }
            val historyAt12HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -12
                    temp = -10f
                }
            val historyAt6HoursBefore =
                historicalHourlyExternalApiResponseDto {
                    hoursDiff = -6
                    temp = 18f
                }

            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -24) } returns
                    Response.success(historyAt24HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -18) } returns
                    Response.success(historyAt18HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -12) } returns
                    Response.success(historyAt12HoursBefore)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), -6) } returns
                    Response.success(historyAt6HoursBefore)

            coEvery { weatherBotApiService.current(any(), any(), any()) } returns
                    Response.success(current)

            val result = service.getSummary(any(), any())

            result.temperature.contains("최고기온은 40") shouldBe true
            result.temperature.contains("최저기온은 -10") shouldBe true
        }
    }

    @Nested
    inner class HeadsUpMessageTest {
        private val currentExternalApiResponseDto: CurrentExternalApiResponseDto =
            currentExternalApiResponseDto { }
        private val historicalHourlyExternalApiResponseDto: HistoricalHourlyExternalApiResponseDto =
            historicalHourlyExternalApiResponseDto { }

        @BeforeEach
        fun beforeEach() {
            clearAllMocks()
            coEvery { weatherBotApiService.current(any(), any(), any()) } returns
                    Response.success(currentExternalApiResponseDto)
            coEvery { weatherBotApiService.historicalHourly(any(), any(), any(), any()) } returns
                    Response.success(historicalHourlyExternalApiResponseDto)
        }

        @Test
        fun `폭설 예정 케이스`() {

            val forecastAt6HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 6
                    code = 3
                }
            val forecastAt12HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 12
                    code = 2
                }
            val forecastAt18HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 18
                    code = 3
                }
            val forecastAt24HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 24
                    code = 2
                }
            val forecastAt30HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 30
                    code = 2
                }
            val forecastAt36HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 36
                    code = 2
                }
            val forecastAt42HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 42
                    code = 3
                }
            val forecastAt48HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 48
                    code = 3
                }

            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 6) } returns
                    Response.success(forecastAt6HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 12) } returns
                    Response.success(forecastAt12HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 18) } returns
                    Response.success(forecastAt18HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 24) } returns
                    Response.success(forecastAt24HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 30) } returns
                    Response.success(forecastAt30HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 36) } returns
                    Response.success(forecastAt36HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 42) } returns
                    Response.success(forecastAt42HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 48) } returns
                    Response.success(forecastAt48HoursAfter)

            val result = service.getSummary(any(), any())

            result.headsUp.contains("폭설") shouldBe true
        }

        @Test
        fun `눈 예정 케이스`() {

            val forecastAt6HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 6
                    code = 2
                }
            val forecastAt12HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 12
                    code = 2
                }
            val forecastAt18HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 18
                    code = 2
                }
            val forecastAt24HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 24
                    code = 2
                }
            val forecastAt30HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 30
                    code = 2
                }
            val forecastAt36HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 36
                    code = 3
                }
            val forecastAt42HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 42
                    code = 3
                }
            val forecastAt48HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 48
                    code = 2
                }

            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 6) } returns
                    Response.success(forecastAt6HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 12) } returns
                    Response.success(forecastAt12HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 18) } returns
                    Response.success(forecastAt18HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 24) } returns
                    Response.success(forecastAt24HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 30) } returns
                    Response.success(forecastAt30HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 36) } returns
                    Response.success(forecastAt36HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 42) } returns
                    Response.success(forecastAt42HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 48) } returns
                    Response.success(forecastAt48HoursAfter)

            val result = service.getSummary(any(), any())

            result.headsUp.contains("눈") shouldBe true
        }

        @Test
        fun `폭우 예정 케이스`() {

            val forecastAt6HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 6
                    code = 2
                }
            val forecastAt12HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 12
                    code = 1
                }
            val forecastAt18HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 18
                    code = 2
                }
            val forecastAt24HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 24
                    code = 2
                }
            val forecastAt30HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 30
                    code = 2
                }
            val forecastAt36HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 36
                    code = 3
                }
            val forecastAt42HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 42
                    code = 2
                }
            val forecastAt48HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 48
                    code = 1
                }

            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 6) } returns
                    Response.success(forecastAt6HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 12) } returns
                    Response.success(forecastAt12HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 18) } returns
                    Response.success(forecastAt18HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 24) } returns
                    Response.success(forecastAt24HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 30) } returns
                    Response.success(forecastAt30HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 36) } returns
                    Response.success(forecastAt36HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 42) } returns
                    Response.success(forecastAt42HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 48) } returns
                    Response.success(forecastAt48HoursAfter)

            val result = service.getSummary(any(), any())

            result.headsUp.contains("폭우") shouldBe true
        }

        @Test
        fun `비 소식 케이스`() {

            val forecastAt6HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 6
                    code = 1
                }
            val forecastAt12HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 12
                    code = 3
                }
            val forecastAt18HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 18
                    code = 1
                }
            val forecastAt24HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 24
                    code = 2
                }
            val forecastAt30HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 30
                    code = 1
                }
            val forecastAt36HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 36
                    code = 2
                }
            val forecastAt42HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 42
                    code = 0
                }
            val forecastAt48HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 48
                    code = 1
                }

            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 6) } returns
                    Response.success(forecastAt6HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 12) } returns
                    Response.success(forecastAt12HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 18) } returns
                    Response.success(forecastAt18HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 24) } returns
                    Response.success(forecastAt24HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 30) } returns
                    Response.success(forecastAt30HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 36) } returns
                    Response.success(forecastAt36HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 42) } returns
                    Response.success(forecastAt42HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 48) } returns
                    Response.success(forecastAt48HoursAfter)

            val result = service.getSummary(any(), any())

            result.headsUp.contains("비") shouldBe true
        }

        @Test
        fun `그외 케이스`() {

            val forecastAt6HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 6
                    code = 1
                }
            val forecastAt12HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 12
                    code = 2
                }
            val forecastAt18HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 18
                    code = 3
                }
            val forecastAt24HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 24
                    code = 0
                }
            val forecastAt30HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 30
                    code = 1
                }
            val forecastAt36HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 36
                    code = 1
                }
            val forecastAt42HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 42
                    code = 1
                }
            val forecastAt48HoursAfter =
                forecastHourlyExternalApiResponseDto {
                    hoursDiff = 48
                    code = 0
                }

            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 6) } returns
                    Response.success(forecastAt6HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 12) } returns
                    Response.success(forecastAt12HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 18) } returns
                    Response.success(forecastAt18HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 24) } returns
                    Response.success(forecastAt24HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 30) } returns
                    Response.success(forecastAt30HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 36) } returns
                    Response.success(forecastAt36HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 42) } returns
                    Response.success(forecastAt42HoursAfter)
            coEvery { weatherBotApiService.forecastHourly(any(), any(), any(), 48) } returns
                    Response.success(forecastAt48HoursAfter)

            val result = service.getSummary(any(), any())

            result.headsUp.contains("평온") shouldBe true
        }
    }
}
