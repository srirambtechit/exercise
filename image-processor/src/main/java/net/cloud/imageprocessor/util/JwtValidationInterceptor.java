package net.cloud.imageprocessor.util;

import lombok.extern.log4j.Log4j2;
import net.cloud.imageprocessor.exception.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Log4j2
@Component
public class JwtValidationInterceptor implements HandlerInterceptor {

    private final JwtHelper jwtHelper;

    public JwtValidationInterceptor(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwtString = request.getHeader("X-Jwt-Token");
        log.info("VHI: " + jwtString);
        if (Objects.isNull(jwtString))
            throw new UnauthorizedException("Jwt token is required");

        return jwtHelper.verify(jwtString);
    }
}
