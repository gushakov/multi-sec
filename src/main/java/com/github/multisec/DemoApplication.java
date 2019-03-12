package com.github.multisec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @RestController
    public class WebController {

        @Autowired
        private SessionRegistry sessionRegistry;

        @GetMapping("/")
        public Map<String, Object> hello(HttpSession session) throws UnknownHostException {

            UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Map<String, Object> map = new HashMap<>();

            map.put("hostname", InetAddress.getLocalHost().getHostName());
            map.put("username", principal.getUsername());
            map.put("JSESSIONID", session.getId());
            map.put("sessions", sessionRegistry.getAllSessions(principal, true));
            return map;
        }

    }

    @Configuration
    public class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Bean
        public AuthenticationDetailsSource<HttpServletRequest,
                PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource(){
            return context -> new PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails(context, Collections.emptyList());
        }

        @Bean
        public PreAuthenticatedGrantedAuthoritiesUserDetailsService preAuthenticatedGrantedAuthoritiesUserDetailsService(){
            return new PreAuthenticatedGrantedAuthoritiesUserDetailsService();
        }

        @Bean
        public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider(){
            PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(preAuthenticatedGrantedAuthoritiesUserDetailsService());
            return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager() {
            return new ProviderManager(Collections.singletonList(preAuthenticatedAuthenticationProvider()));
        }

        @Bean
        public RequestHeaderAuthenticationFilter requestHeaderAuthenticationFilter() {
            RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
            filter.setAuthenticationDetailsSource(authenticationDetailsSource());
            filter.setAuthenticationManager(authenticationManager());
            filter.setPrincipalRequestHeader("User");
            filter.setCredentialsRequestHeader("Password");
            return filter;
        }

        @Bean
        public SessionRegistry sessionRegistry(){
            return new SessionRegistryImpl();
        }

        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher(){
            return new HttpSessionEventPublisher();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.sessionManagement()
                    .maximumSessions(-1)
                    .sessionRegistry(sessionRegistry())
                    .and()
                 .and()
                    .logout()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("http://localhost")
                 .and()
                    .addFilterBefore(requestHeaderAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        }

    }

}
