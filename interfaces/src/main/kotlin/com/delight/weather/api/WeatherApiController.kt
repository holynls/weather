package com.delight.weather.api

import com.delight.weather.domain.weather.WeatherService
import com.delight.weather.domain.weather.dto.WeatherSummaryDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/summary")
class WeatherApiController(
    private val weatherService: WeatherService
) {

    @Operation(summary = "get weather summary")
    @ApiResponse(
        responseCode = "200",
        description = "Success",
        content = [
            Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = Schema(implementation = WeatherSummaryDto::class)
            )
        ]
    )
    @GetMapping
    fun getSummary(
        @RequestParam lat: Float,
        @RequestParam lon: Float
    ): ResponseEntity<WeatherSummaryDto> =
        when {
            lat < -90 || lat >= 90 || lon < -180 || lon >= 180 -> ResponseEntity.badRequest().build()
            else -> ResponseEntity.ok(weatherService.getSummary(lat, lon))
        }

}
