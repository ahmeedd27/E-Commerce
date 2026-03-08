package com.ahmed.E_CommerceApp.Config;

import com.ahmed.E_CommerceApp.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request
            , @NonNull HttpServletResponse response
            , @NonNull FilterChain filterChain)
            throws ServletException, IOException {
      final String auth=request.getHeader("Authorization");
      String username=null;
      String jwt=null;
        if (auth != null && auth.startsWith("Bearer ")) {
            jwt = auth.substring(7);
            try {
                username = jwtService.extractUserName(jwt);
            } catch (JwtException e) {
                //  Bad token — just continue without setting authentication
                // Spring Security will return 401 automatically
                filterChain.doFilter(request, response);
                return;
            }
        }
      if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
          UserDetails userDetails= userDetailsService.loadUserByUsername(username);
          if(jwtService.validateToken(jwt , userDetails)){
              UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(
                      userDetails , null , userDetails.getAuthorities()
              );
              authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
              SecurityContextHolder.getContext().setAuthentication(authToken);
          }
      }
      filterChain.doFilter(request,response);

    }
}
