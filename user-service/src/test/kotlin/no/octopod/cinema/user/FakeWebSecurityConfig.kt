package no.octopod.cinema.user

import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
@Order(1)
class FakeWebSecurityConfig: WebSecurityConfig() {
    override fun configure(http: HttpSecurity) {
        super.configure(http)
        http.httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        print("CONfiguring authaewtaonweg")
        auth.inMemoryAuthentication()
                .withUser("12345678").password("{noop}123").roles("USER").and()
                .withUser("87654321").password("{noop}123").roles("USER").and()
                .withUser("admin").password("{noop}admin").roles("ADMIN", "USER")
    }
}