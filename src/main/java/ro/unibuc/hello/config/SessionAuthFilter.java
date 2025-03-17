package ro.unibuc.hello.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.unibuc.hello.service.UserService;
import ro.unibuc.hello.data.UserEntity;

import java.io.IOException;
import java.util.Optional;

@Component
public class SessionAuthFilter implements Filter {

    @Autowired
    private UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
            String sessionId = null;

            if (httpRequest.getCookies() != null) {
                for (Cookie cookie : httpRequest.getCookies()) {
                    if ("sessionId".equals(cookie.getName())) {
                        sessionId = cookie.getValue();
                        break;
                    }
                }
            }

            if (sessionId != null) {
                Optional<UserEntity> user = userService.getUserBySessionId(sessionId);
            } else {
                UserEntity newUser = userService.createUser();
                sessionId = newUser.getSessionId();

                Cookie newCookie = new Cookie("sessionId", sessionId);
                newCookie.setHttpOnly(true);
                newCookie.setSecure(false);
                newCookie.setPath("/");
                newCookie.setMaxAge(10 * 365 * 24 * 60 * 60);
                httpResponse.addCookie(newCookie);
            }
        }

        chain.doFilter(request, response);
    }
}
