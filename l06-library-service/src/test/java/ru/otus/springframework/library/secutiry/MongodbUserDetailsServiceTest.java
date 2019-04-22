package ru.otus.springframework.library.secutiry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongodbUserDetailsServiceTest {

    @Mock
    private MongodbUserRepository userRepository;

    @InjectMocks
    private MongodbUserDetailsService mongodbUserDetailsService;

    @Test
    void loadUserByUsernameSuccess() {
        var userName = "user";
        var password = "123";
        var role = "TEST";
        var user = mock(MongodbUser.class);

        when(user.getUserName()).thenReturn(userName);
        when(user.getPassword()).thenReturn(password);
        when(user.getRoleString()).thenReturn(role);
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(user));

        var secUser = mongodbUserDetailsService.loadUserByUsername(userName);
        assertThat(secUser.getUsername(), equalTo(userName));
        assertThat(secUser.getPassword(), equalTo(password));
        assertThat(secUser.getAuthorities(), contains(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    @Test
    void loadUserByUsernameFail() {
        var userName = "user";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> mongodbUserDetailsService.loadUserByUsername(userName));
    }
}