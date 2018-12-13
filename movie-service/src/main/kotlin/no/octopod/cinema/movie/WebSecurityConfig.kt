package no.octopod.cinema.movie

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy


@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .httpBasic()
            .and()
            .cors()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/movies/**").permitAll()
            .antMatchers(HttpMethod.POST, "/movies/**").permitAll()
            .antMatchers(
                    "/v2/api-docs",
                    "/swagger-resources/**",
                    "/swagger-ui.html",
                    "/webjars/**").hasRole("ADMIN")
            .anyRequest().denyAll()
            .and()
            .csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }
}

