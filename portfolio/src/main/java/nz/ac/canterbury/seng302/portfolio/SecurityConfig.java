package nz.ac.canterbury.seng302.portfolio;

import nz.ac.canterbury.seng302.portfolio.authentication.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        // Force authentication for all endpoints except /login
        security
            .addFilterBefore(new JwtAuthenticationFilter(), BasicAuthenticationFilter.class)
                .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/login")
                    .permitAll()
                    .and()
                .authorizeRequests()
                    .anyRequest()
                    .authenticated();

        security.cors();
        security.csrf().disable();
        security.logout()
                .permitAll()
                .invalidateHttpSession(true)
                .deleteCookies("lens-session-token")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));

        // Disable basic http security and the spring security login form
        security
            .httpBasic().disable()
            .formLogin().disable();

        // let the H2 console embed itself in a frame
        security.headers().frameOptions().sameOrigin();
    }

    @Override
    public void configure(WebSecurity web) throws Exception
    {
        web.ignoring().antMatchers("/login");
    }
}