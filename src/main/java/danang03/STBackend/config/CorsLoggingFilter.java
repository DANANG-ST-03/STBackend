package danang03.STBackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CorsLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(CorsLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Origin & Referer 확인
        String origin = request.getHeader("Origin"); // 요청한 클라이언트의 출처 (CORS 관련)
        String referer = request.getHeader("Referer"); // 이전 페이지 정보

        log.info("📢 요청 URL: {} | Method: {} | Origin: {} | Referer: {}",
                request.getRequestURI(), request.getMethod(), origin, referer);

        filterChain.doFilter(request, response);
    }
}