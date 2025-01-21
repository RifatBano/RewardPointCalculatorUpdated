package com.infy.RewardPointCalculator.tokenConfig;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.infy.RewardPointCalculator.service.CustomerService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

  private JwtTokenProvider jwtTokenProvider;
  private CustomerService userDetailsService;
  
  @Autowired
  public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, CustomerService userDetailsService) {
	super();
	this.jwtTokenProvider = jwtTokenProvider;
	this.userDetailsService = userDetailsService;
}

@Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException {
      String token = jwtTokenProvider.resolveToken(request);

      if (token != null && jwtTokenProvider.validateToken(token)) {
          UserDetails userDetails = userDetailsService.loadUserByUsername(jwtTokenProvider.getUsername(token));

          UsernamePasswordAuthenticationToken authentication = 
              new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
          
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
      }

      filterChain.doFilter(request, response);
  }

}