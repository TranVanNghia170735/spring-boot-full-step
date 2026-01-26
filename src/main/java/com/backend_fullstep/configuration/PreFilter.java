package com.backend_fullstep.configuration;

import com.backend_fullstep.common.TokenType;
import com.backend_fullstep.exception.ErrorResponse;
import com.backend_fullstep.service.JwtService;
import com.backend_fullstep.service.UserServiceDetail;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceDetail userServiceDetail;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("PreFilter {} {}", request.getMethod(), request.getRequestURI());

        final String authorization = request.getHeader("Authorization");
        ObjectMapper mapper = new ObjectMapper();

        try {
            if (StringUtils.isNotEmpty(authorization) && authorization.startsWith("Bearer ")) {
                String token = authorization.substring(7);
                String username = "";
                try {
                    username = jwtService.extractUsername(token, TokenType.ACCESS_TOKEN);
                } catch (AccessDeniedException e) {
                    log.info("Extract username form token", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(mapper.writeValueAsString(ErrorResponse.builder().path(request.getRequestURI()).message(e.getMessage()).build()));
                    return;
                }


                if (StringUtils.isNotEmpty(username) && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = userServiceDetail.userDetailsService().loadUserByUsername(username);

                    if (jwtService.isValid(token, TokenType.ACCESS_TOKEN, userDetails)) {
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(authToken);
                        SecurityContextHolder.setContext(context);

                    }
                }

                filterChain.doFilter(request, response); // direct sang api
            }
        } finally {
            filterChain.doFilter(request, response);
        }

    }
}
