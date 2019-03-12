Spring Security Pre-Authentication scenario with Multiple Docker containers
===

Simple Dockerized Spring Boot application protected with Spring Security.
Intended as an illustration of several non-trivial concepts.
- Apache proxy forwarding requests to two different instances of the same Spring Boot application
- Pre-Authentication based on request headers
- Logging out user
- Session registry

#### Pre-authentication

Authentication is configured as in [Pre-Authentication Scenarios](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#preauth).

There is an Apache proxy which injects different `User` and `Password` headers depending on whether the query stirng contains `?user1` or `?user2`.

In the real application, there will be an external authentication mechanism (Siteminder, for example). But we are interested in the Spring Security setup here.

Request headers injected by the proxy will be recuperated by the Spring's `RequestHeaderAuthenticationFilter` which is added to HTTP security configuration.

This is how application work:

- http://localhost/app1
    * Exception is thrown since there are no credentials in the request
- http://localhost/app1/?user1
    * Logs in user `user1` to `app1`
    * Any subsequent requests to http://localhost/app1 _in the same session_ do not need authentication. It is recovered from HTTP session store.
    * Shows container ID, `JSESSIONID`, `username` from security context and contents of session registry for the current principal.
- http://localhost/app1/?user2
    * Logs in user `user2` to `app1`
- http://localhost/app1/logout
    * Logs out user from the current session on `app1`

The same works for the application `/app2`.

#### Logout

A simple logout is configured wihch logs the user from _the current_ HTTP session and for _one application instance_ only.

**TODO**: Investigate how one can logout user from _all_ active sessions on _all_ applications.

#### Session tracking

HTTP security is configured to keep track of users' sessions using the defaulf implementation of `SessionRegistry`.

#### Running

Build and run as a usual Docker compose services stack, see `docker-compose.yml` file.

#### Links and acknowledgments

Code based on examples from
- https://hub.docker.com/r/diouxx/apache-proxy
- https://www.baeldung.com/spring-security-custom-filter
- https://www.baeldung.com/spring-security-session
- https://stackoverflow.com/a/40273578
