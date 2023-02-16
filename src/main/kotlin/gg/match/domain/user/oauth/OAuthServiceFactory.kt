package gg.match.domain.user.oauth

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class OAuthServiceFactory(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {
    fun getOAuthService(): OAuth2Service{
        return OAuth2ServiceImpl(restTemplate, objectMapper)
    }
}