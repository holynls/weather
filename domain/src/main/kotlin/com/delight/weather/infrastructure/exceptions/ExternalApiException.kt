package com.delight.weather.infrastructure.exceptions

import org.springframework.http.HttpStatus

data class ExternalApiException(
    val status: HttpStatus,
    override val message: String?,
) : Exception(message)
