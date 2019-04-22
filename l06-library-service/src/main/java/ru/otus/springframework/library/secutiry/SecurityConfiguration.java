package ru.otus.springframework.library.secutiry;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;

@Profile({"rest", "mvc"})
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String BOOK_ROLE = "BOOK";
    private static final String AUTHOR_ROLE = "AUTHOR";
    private static final String GENRE_ROLE = "GENRE";

    @Autowired
    private final UserDetailsService userDetailsService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()

                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                    .authorizeRequests()

                    .antMatchers("/login*").permitAll()

                    .antMatchers("/css/*").permitAll()
                    .antMatchers("/js/*").permitAll()

                    .antMatchers("/book/**/*").hasRole(BOOK_ROLE)
                    .antMatchers(POST, "/api/v2/book").hasRole(BOOK_ROLE)
                    .antMatchers(DELETE, "/api/v2/book").hasRole(BOOK_ROLE)
                    .antMatchers("/api/v2/book/**/*").hasRole(BOOK_ROLE)

                    .antMatchers("/author/**/*").hasRole(AUTHOR_ROLE)
                    .antMatchers(POST, "/api/v2/author").hasRole(AUTHOR_ROLE)
                    .antMatchers(DELETE, "/api/v2/author").hasRole(AUTHOR_ROLE)
                    .antMatchers("/api/v2/author/**/*").hasRole(AUTHOR_ROLE)

                    .antMatchers("/genre/**/*").hasRole(GENRE_ROLE)
                    .antMatchers(POST, "/api/v2/genre").hasRole(GENRE_ROLE)
                    .antMatchers(DELETE, "/api/v2/genre").hasRole(GENRE_ROLE)
                    .antMatchers("/api/v2/genre/**/*").hasRole(GENRE_ROLE)

                    .anyRequest().authenticated()

                .and()
                    .formLogin()
                    .loginPage("/login.html")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/")

                .and()
                    .rememberMe()
                    .key("library-security-token")
                    .rememberMeCookieName("library-token")
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(userDetailsService);
    }

}