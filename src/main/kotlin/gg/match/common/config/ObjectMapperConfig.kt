package gg.match.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.addSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Configuration
class ObjectMapperConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        val javaTimeModule = JavaTimeModule()
        val objectMapper= ObjectMapper()
        val localDateTimeSerializer = LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.of("Asia/Seoul")))
        javaTimeModule.addSerializer(LocalDateTime::class, localDateTimeSerializer)
        objectMapper.registerModule(javaTimeModule)
        return objectMapper
    }
}