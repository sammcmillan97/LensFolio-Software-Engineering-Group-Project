package nz.ac.canterbury.seng302.portfolio.authentication;

import io.grpc.StatusRuntimeException;
import nz.ac.canterbury.seng302.portfolio.service.AuthenticateClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private AuthenticateClientService authenticateClientService;

    private AuthenticateClientService getAuthenticateClientService(HttpServletRequest request) {
        if(authenticateClientService == null){
            ServletContext servletContext = request.getServletContext();
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            authenticateClientService = webApplicationContext.getBean(AuthenticateClientService.class);
        }
        return authenticateClientService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        PreAuthenticatedAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        if(!authentication.isAuthenticated()) {
            CookieUtil.clear(res, "lens-session-token");
        }

        chain.doFilter(req, res);
    }

    /**
     * Check with the IdP whether the user making this request is authenticated, and set the authState returned
     * as our authentication principal. This allows us to access the authState (including name, roles, id, etc.)
     * in any of our controllers just by adding an @AuthenticationPrincipal parameter.
     *
     * @param request HTTP request sent by client
     * @return PreAuth token with the authState of user, and whether they are authenticated
     */
    private PreAuthenticatedAuthenticationToken getAuthentication(HttpServletRequest request) {
        // Create an auth token for an unauthenticated user
        PreAuthenticatedAuthenticationToken authToken = new PreAuthenticatedAuthenticationToken(null, null);
        authToken.setAuthenticated(false);

        String lensSessionCookieJwtString = CookieUtil.getValue(request, "lens-session-token");
        if (!StringUtils.hasText(lensSessionCookieJwtString)) {
            // No cookie with jwt session token found, return unauthenticated token
            return authToken;
        }

        AuthState authState;
        try {
            authState = getAuthenticateClientService(request).checkAuthState();
        } catch (StatusRuntimeException e) {
            // This exception is thrown if the IdP encounters some error, or if the IdP can not be reached
            // Also may be thrown if some error connecting to IdP, either way, return unauthenticated token
            return authToken;
        }

        // If we get here, then the IdP has returned 'some' auth state, so we configure our auth token with whatever
        // the IdP has said about the authentication status of the user that provided this token
        authToken = new PreAuthenticatedAuthenticationToken(authState, lensSessionCookieJwtString);
        authToken.setAuthenticated(authState.getIsAuthenticated());
        return authToken;
    }
}