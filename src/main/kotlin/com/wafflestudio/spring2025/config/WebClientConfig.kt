package com.wafflestudio.spring2025.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig(
    @Value("\${snu.api.base-url}")
    private val snuApiBaseUrl: String
) {
    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        // 10MB로 버퍼 사이즈 늘리기
        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) // 10MB
            }
            .build()

        return builder
            .baseUrl(snuApiBaseUrl)
            .exchangeStrategies(exchangeStrategies)
            .build()
    }
}