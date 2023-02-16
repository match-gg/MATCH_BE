package gg.match.domain.user.oauth

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import gg.match.domain.user.dto.Oauth2UserDTO
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

class OAuth2ServiceImpl(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) : OAuth2Service {
    private val KAKAO_MY_INFO_API: String = "https://kapi.kakao.com/v2/user/me"

    override fun getOAuth2User(oAuth2AccessToken: String): Oauth2UserDTO {
        val userJson = getProfileInfoFromProvider(oAuth2AccessToken)
        return buildOAuth2User(userJson)
    }

    private fun getProfileInfoFromProvider(oAuth2AccessToken: String): JsonNode {
        val response: ResponseEntity<String> = try {
            restTemplate.postForEntity(
                KAKAO_MY_INFO_API,
                buildRequest(oAuth2AccessToken),
                String::class.java
            )
        } catch (e: HttpClientErrorException){
            throw Exception()
        }

        return try {
            objectMapper.readTree(response.body)
        } catch (e: JsonProcessingException) {
            throw Exception("반환 에러")
        }
    }

    private fun buildRequest(oAuth2AccessToken: String): HttpEntity<*>{
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.setBearerAuth(oAuth2AccessToken)
        return HttpEntity(null, headers)
    }

    private fun buildOAuth2User(jsonNode: JsonNode): Oauth2UserDTO{
        val kakao_account = jsonNode.get("kakao_account")
        val oAuth2Id = jsonNode.get("id").asText()
        val nickname = kakao_account
            .get("profile")
            .get("nickname")
            .asText()
        val email = kakao_account.get("email").asText()

        return Oauth2UserDTO(
            oauth2Id = "kakao$oAuth2Id",
            nickname = nickname,
            email = email
        )
    }
}