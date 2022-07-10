package com.delight.weather.infrastructure.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.LocalDateTime
import java.time.ZoneId

class LocalDateTimeSerializerCustom : JsonSerializer<LocalDateTime>() {

    override fun serialize(value: LocalDateTime, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeNumber(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    }
}
