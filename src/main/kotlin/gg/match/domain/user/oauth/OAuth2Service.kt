package gg.match.domain.user.oauth

import gg.match.domain.user.dto.Oauth2UserDTO
import org.springframework.stereotype.Service

@Service
interface OAuth2Service {
    fun getOAuth2User(oAuth2AccessToken: String) : Oauth2UserDTO
}