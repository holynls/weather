package com.delight.weather.infrastructure.config

import com.delight.weather.infrastructure.externalapi.weatherbot.WeatherBotApiService
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.time.LocalDateTime

@Configuration
class WeatherBotApiConfig {

    @Bean
    fun weatherBotApiService(): WeatherBotApiService {
        val objectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .apply {
                val module = SimpleModule()
                module.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializerCustom())
                module.addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializerCustom())
                this.registerModule(module)
                this.registerModule(KotlinModule.Builder().build())
                this.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()
            .create(WeatherBotApiService::class.java)
    }

    companion object {
        const val baseUrl = "https://thirdparty-weather-api-v2.droom.workers.dev"
    }
}
