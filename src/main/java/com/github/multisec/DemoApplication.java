package com.github.multisec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }


    @Component
    class StickySessionFilter extends GenericFilterBean {

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

            System.out.println("...................");
            System.out.println("...FILTER       ...");
            System.out.println("...FILTER       ...");
            System.out.println("...FILTER       ...");
            System.out.println("...FILTER       ...");
            System.out.println("...................");

            ((HttpServletResponse)servletResponse).addCookie(
                    new Cookie("sticky", InetAddress.getLocalHost().getHostAddress())
            );

            filterChain.doFilter(servletRequest, servletResponse);
        }
    }


    @RestController
    public class WebController {

        @GetMapping("/")
        public Map<String, Object> hello(HttpSession session) throws UnknownHostException {

            Map<String, Object> map = new HashMap<>();

            map.put("hostname", InetAddress.getLocalHost().getHostName());
            map.put("address", InetAddress.getLocalHost().getHostAddress());
            map.put("JSESSIONID", session.getId());
            return map;
        }

    }



}
