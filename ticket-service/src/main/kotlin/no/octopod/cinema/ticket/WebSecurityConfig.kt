package no.octopod.cinema.ticket

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {

    @Bean
    fun userSecurity() : UserSecurity {
        return UserSecurity()
    }

    override fun configure(http: HttpSecurity) {

        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/tickets").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/tickets/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/tickets/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/tickets/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/tickets/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/tickets").permitAll()
                //
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .authorizeRequests().antMatchers("/**").permitAll()
    }

}

class UserSecurity{

    fun checkId(authentication: Authentication, id: String) : Boolean{

        val current = (authentication.principal as UserDetails).username

        return current == id
    }
}