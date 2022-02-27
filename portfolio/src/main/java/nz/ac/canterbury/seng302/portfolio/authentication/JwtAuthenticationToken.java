package nz.ac.canterbury.seng302.portfolio.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    User principal;
    String token;

    public JwtAuthenticationToken(String token, User principal, Collection<? extends GrantedAuthority> authorities ) {
        super(authorities);
        this.token = token;
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public void setToken( String token ) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}