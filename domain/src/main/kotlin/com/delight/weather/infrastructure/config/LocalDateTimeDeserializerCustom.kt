package com.delight.weather.infrastructure.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LocalDateTimeDeserializerCustom : LocalDateTimeDeserializer() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): LocalDateTime {
        return runCatching {
            LocalDateTime.ofInstant(Instant.ofEpochSecond(p.text.toLong()), ZoneId.systemDefault())
        }.getOrElse { throw IllegalArgumentException("타임스탬프만 사용 가능합니다.") }
    }
}
