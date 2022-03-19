package covid19.statistics.reports.filter;

import covid19.statistics.reports.service.MyUserDetailsService;
import covid19.statistics.reports.dto.UserDetail;
import covid19.statistics.reports.util.Constants;
import covid19.statistics.reports.util.CustomUtil;
import covid19.statistics.reports.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    public Logger LOG = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String username = null;
        String jwt = null;

        try {

            final String authorizationHeader = request.getHeader(Constants.AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith(Constants.BEARER)) {
                jwt = authorizationHeader.substring(Constants.INT_SEVEN);

                username = CustomUtil.getUserFromJwtToken(jwt);
                //username = jwtUtil.extractUsername(jwt);

                // Getting the dto details from the active list
                UserDetail userDetail = CustomUtil.activeUsersList.get(username);

                if(Objects.isNull(userDetail))
                    throw new Exception("User not found in the list");

                if(!userDetail.getJwtToken().equals(jwt) || !CustomUtil.isjwtTokenValid(userDetail.getJwtCreationTime()))
                    throw new Exception("User token has been expired or invalidated");
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

            chain.doFilter(request, response);

        } catch (ExpiredJwtException eje) {
            LOG.error("Security exception for dto {} - {}", eje.getClaims().getSubject(), eje.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(Constants.UNAUTHORIZED_ERROR_MSG);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (Exception e) {
            LOG.error("Invalid user: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

    private static Optional<String> getJwtFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader(Constants.AUTHORIZATION);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.BEARER))
            return Optional.of(bearerToken.substring(7));

        return Optional.empty();
    }

    private void setSecurityContext(WebAuthenticationDetails authDetails, String token) {

        final String username = jwtUtil.extractUsername(token);
        final List<String> roles = jwtUtil.getRoles(token);
        final UserDetails userDetails = new User(username, "", roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        authentication.setDetails(authDetails);
        // After setting the Authentication in the context, we specify
        // that the current dto is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
