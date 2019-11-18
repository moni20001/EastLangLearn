package com.eastlanglearn.east.config;

import com.eastlanglearn.east.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class ApplicationSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final UserService userService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;  //for password encoding

    @Autowired
    public ApplicationSecurityConfiguration(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                //  .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .authorizeRequests()
                .antMatchers("/login", "/register", "/").anonymous()
                .antMatchers("/demote/**", "/promote/**", "/delete/**").hasAuthority("ADMIN")
                .antMatchers("/admin").hasAnyAuthority("ADMIN", "MODERATOR")

                .antMatchers("/css/**", "/js/**", "/img/**", "/"
                        , "/details/**", "/profile/**", "/search/**"
                        , "/confirmEmail/**", "/diff/*", "/story/**", "/stories", "/leaderboard", "/search/**", "/help/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .and()
                .rememberMe()
                .rememberMeParameter("rememberMe")
                .key("MASQWE")
                .userDetailsService(this.userService)
                .rememberMeCookieName("REME")
                //1 week validity
                .tokenValiditySeconds(10080)
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll();
    }
}
