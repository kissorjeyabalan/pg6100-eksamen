package no.octopod.cinema.kino

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

//Copied from acruri82 testing and development, websecurityconfig

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {
        http
            .httpBasic()
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/shows/**").permitAll()
            .antMatchers(HttpMethod.DELETE, "/shows/**").permitAll()
            .antMatchers(HttpMethod.GET, "/theaters/**").permitAll()
            .antMatchers(HttpMethod.GET, "/shows/**").permitAll()
            .antMatchers("/theaters/**").hasRole("ADMIN")
            .antMatchers("/shows/**").hasRole("ADMIN")
            .antMatchers("/v2/api-docs", "/swagger-resources/configuration/ui",
                    "/swagger-resources", "/swagger-resources/configuration/security",
                    "/swagger-ui.html", "/webjars/**").hasRole("ADMIN")
            .anyRequest().denyAll()
            .and()
            .csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }
}

