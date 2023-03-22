package gg.match.common.config

import gg.match.common.jwt.filter.JwtAuthenticationFilter
import gg.match.common.jwt.util.JwtResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.CorsUtils
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtResolver: JwtResolver
) {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer? {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring().antMatchers("/api/user/signin", "/api/user/signup", "/api/user/refresh",
            "/css/**", "/js/**", "/images/**", "/api/lol/**")
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http!!
            .httpBasic().disable()
            .csrf().disable()
            .exceptionHandling()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .antMatchers("/api/user/signup", "/api/user/signin", "/api/user/refresh", "/api/lol/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .cors()
            .and()
            .addFilterBefore(JwtAuthenticationFilter(jwtResolver), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
//    에러 해결 Bean 등록이나 없어도 잘 동작하는것 확인, 캐시에러가 아닐 경우 재 추가를 위한 주석처리
//
//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource? {
//        val configuration = CorsConfiguration()
//        configuration.addAllowedOrigin("http://localhost:3000")
//        configuration.addAllowedMethod("*")
//        configuration.addAllowedHeader("*")
//        configuration.allowCredentials = true
//        configuration.maxAge = 3600L
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", configuration)
//        return source
//    }

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()
}