package ru.otus.springframework.library.secutiry;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Profile({"rest", "mvc"})
@ConditionalOnProperty(name = "library.dao.provider", havingValue = "spring-mongodb-jpa")
@Service
@RequiredArgsConstructor
class MongodbUserDetailsService implements UserDetailsService {

    private final MongodbUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUserName(username)
                .map(u -> User.builder()
                        .username(u.getUserName())
                        .password(u.getPassword())
                        .roles(u.getRoleString().split(","))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
