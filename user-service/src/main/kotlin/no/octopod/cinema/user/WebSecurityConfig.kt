package no.octopod.cinema.user

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter()  {

    override fun configure(http: HttpSecurity) {
        http
                .httpBasic()
                .and()
                .logout()
                .and()
                //
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }
}