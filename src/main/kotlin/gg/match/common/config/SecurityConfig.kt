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
import org.springframework.web.cors.CorsUtils

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtResolver: JwtResolver
) {
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer? {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring().antMatchers(
                //user
                "/api/user/signin", "/api/user/signup", "/api/user/refresh", "/api/user/health",
                "/api/user/play/**",
                //setting
                "/css/**", "/js/**", "/images/**",
                //lol
                "/api/lol/user/**", "/api/lol/user/exist/**","/api/lol/boards/**", "/api/lol/summoner/**/**",
                //pubg
                "/api/pubg/user/**/**", "/api/pubg/user/exist/**/**","/api/pubg/boards/**", "/api/pubg/player/**/**/**",
                //overwatch
                "/api/overwatch/user/**", "/api/overwatch/user/exist/**","/api/overwatch/boards/**", "/api/overwatch/player/**/**",
                //valorant
                "/api/valorant/user/exist",
                //admin
                "/api/admin/login"
            )
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic().disable()
            .csrf().disable()
            .exceptionHandling()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .anyRequest().authenticated()
            .and()
            .cors()
            .and()
            .addFilterBefore(JwtAuthenticationFilter(jwtResolver), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()
}