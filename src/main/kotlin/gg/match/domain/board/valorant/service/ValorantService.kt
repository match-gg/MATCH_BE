package gg.match.domain.board.valorant.service

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import gg.match.controller.error.BusinessException
import gg.match.controller.error.ErrorCode
import gg.match.domain.board.valorant.dto.ValorantUserTokenDTO
import gg.match.domain.board.valorant.entity.Agent
import gg.match.domain.board.valorant.repository.AgentRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
@Transactional(readOnly = true)
class ValorantService (
    @Value("\${valorant.client-id}") private val valorantClientId: String,
    @Value("\${valorant.client-secret}") private val valorantClientSecret: String,
    @Value("\${valorant.callback-uri}") private val valorantCallbackUri: String,
    private val agentRepository: AgentRepository,
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
){
    private val tokenUrl: String = "https://auth.riotgames.com/token"
    private val infoUrl: String = "https://asia.api.riotgames.com/riot/account/v1/accounts/me"

    fun getValorantUser(code: String): JsonNode{
        val rsoReturnJson = requestRiotAccessToken(code) ?: throw BusinessException(ErrorCode.BAD_REQUEST)
        return getValorantUserData(rsoReturnJson)
    }

    private fun requestRiotAccessToken(code: String): JsonNode? {
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val params = LinkedMultiValueMap<String, String>()
        params.add("grant_type", "authorization_code")
        params.add("code", code)
        params.add("redirect_uri", valorantCallbackUri)
        params.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
        params.add("client_assertion", valorantClientSecret)

        val request = HttpEntity(params, httpHeaders)
        val response: ResponseEntity<String> = try{
            restTemplate.postForEntity(tokenUrl, request, String::class.java)
        } catch(e: HttpClientErrorException){
            e.printStackTrace()
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return try {
            val agent: Agent = objectMapper.readValue(response.body.toString(), ValorantUserTokenDTO::class.java).toEntity()
            agentRepository.save(agent)
            objectMapper.readTree(response.body)
        } catch (e: JsonProcessingException) {
            throw Exception("반환 에러")
        }
    }

    private fun getValorantUserData(rsoReturnJson: JsonNode): JsonNode{
        val idToken = rsoReturnJson.get("id_token")
        val accessToken = rsoReturnJson.get("access_token").asText()
        val refreshToken = rsoReturnJson.get("refresh_token")

        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_FORM_URLENCODED
        httpHeaders.set("Authorization", "Bearer $accessToken")

        val request = HttpEntity(null, httpHeaders)
        val response: ResponseEntity<String> = try{
            restTemplate.exchange(infoUrl, HttpMethod.GET, request, String::class.java)
        } catch(e: HttpClientErrorException){
            e.printStackTrace()
            if(e.statusCode == HttpStatus.NOT_FOUND){
                throw BusinessException(ErrorCode.USER_NOT_FOUND)
            }
            throw BusinessException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
        return try {
            objectMapper.readTree(response.body)
        } catch (e: JsonProcessingException) {
            throw Exception("반환 에러")
        }
    }
}