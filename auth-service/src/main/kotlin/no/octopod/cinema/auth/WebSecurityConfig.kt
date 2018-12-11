package no.octopod.cinema.auth

import no.octopod.cinema.auth.service.AuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
        private val authService: AuthenticationService
) : WebSecurityConfigurerAdapter()  {

    @Autowired lateinit var dataSource: DataSource
    @Autowired lateinit var passwordEncoder: PasswordEncoder

    @Bean
    override fun userDetailsServiceBean(): UserDetailsService {
        return super.userDetailsServiceBean()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(http: HttpSecurity) {
        http
                .httpBasic()
                .and()
                .logout()
                .and()
                //
                .authorizeRequests()
                .antMatchers("/user").authenticated()
                .antMatchers("/login").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/logout").permitAll()
                .anyRequest().denyAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {

        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("""
                     SELECT username, password, enabled
                     FROM users
                     WHERE username=?
                     """)
                .authoritiesByUsernameQuery("""
                     SELECT x.username, y.roles
                     FROM users x, authentication_entity_roles y
                     WHERE x.username=? and y.authentication_entity_username=x.username
                     """)
                .passwordEncoder(passwordEncoder)

        authService.initializeDefaultUsers()
    }
}