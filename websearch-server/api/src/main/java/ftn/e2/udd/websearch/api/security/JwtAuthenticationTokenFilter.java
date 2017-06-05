package ftn.e2.udd.websearch.api.security;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;



/**
 * @author Aleksandar on 6/5/2017.
 */

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("In authentication filter...");

        String tokenHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        String token = JwtTokenUtil.getTokenFromHttpHeader(tokenHeader);

        String usernameFromToken = JwtTokenUtil.getUsernameFromToken(token);

        logger.info("Checking authentication for user: " + usernameFromToken);

        boolean shouldAuthenticate = usernameFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null;

        if (shouldAuthenticate) {
            // Instead of loading user details from DB, the information can be stored in and read from token
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(usernameFromToken);

            if (JwtTokenUtil.isValidToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("Authenticated user " + usernameFromToken + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }

        filterChain.doFilter(request, response);
    }

}
