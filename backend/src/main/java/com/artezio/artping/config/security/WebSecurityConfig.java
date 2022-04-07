package com.artezio.artping.config.security;

import com.artezio.artping.config.security.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Qualifier("artUserDetailService")
    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // We don't need CSRF for this example
        httpSecurity
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // dont authenticate this particular request
                .authorizeRequests()
                .antMatchers("/api/authenticate", "/authenticate", "/api/users/register",
                        "/version", "/api/version", "/recovery/**",
                        "/actuator/**",
                        // swagger
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/api-docs",
                        "/configuration/ui",
                        "/configuration/**").permitAll()
                .antMatchers(HttpMethod.POST, "/calendars").hasAuthority("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/calendars/**").hasAuthority("ADMIN")
                .antMatchers("/calendars").hasAnyAuthority("ADMIN", "HR")
                .antMatchers("/projects/**").hasAnyAuthority("ADMIN", "PM")
                .antMatchers(HttpMethod.GET, "/offices/**").hasAnyAuthority("ADMIN", "DIRECTOR", "HR")
                .antMatchers("/offices/**").hasAuthority("ADMIN")
                .antMatchers("/employee/all").hasAnyAuthority("ADMIN", "HR", "PM", "DIRECTOR")
                .antMatchers("/employee/**").hasAnyAuthority("ADMIN", "HR")
                .antMatchers("/employee-checks").hasAnyAuthority("ADMIN", "HR", "PM", "DIRECTOR")
                .antMatchers("/import").hasAnyAuthority("ADMIN", "HR")
                .antMatchers("/export").hasAnyAuthority("ADMIN", "HR", "PM", "DIRECTOR")
                // all other requests need to be authenticated
                .anyRequest().authenticated()
        ;

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
