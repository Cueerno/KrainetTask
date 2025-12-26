package com.radiuk.auth_service.repository;

import com.radiuk.auth_service.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void before() {
        user = User.builder()
                .email("test@gmail.com")
                .username("test")
                .password("test")
                .firstname("test")
                .lastname("test")
                .role(User.Role.USER)
                .build();

        userRepository.save(user);
    }

    @Test
    void findByEmail_shouldReturnUser_whenExists() {
        Optional<User> result = userRepository.findByEmail("test@gmail.com");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("test");
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenNotExists() {
        Optional<User> result = userRepository.findByEmail("not.test@example.com");

        assertThat(result).isEmpty();
    }
}
