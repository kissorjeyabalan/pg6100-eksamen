package com.octopod.cinema.user

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
@Order(1)
@Profile("disable-auth")
class FakeWebSecurityConfig: WebSecurityConfig() {
    override fun configure(http: HttpSecurity) {
        super.configure(http)

        http.httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}user").roles("USER").and()
                .withUser("admin").password("{noop}admin").roles("ADMIN")
    }
}