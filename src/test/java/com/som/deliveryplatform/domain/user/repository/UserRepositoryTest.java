package com.som.deliveryplatform.domain.user.repository;

import com.som.deliveryplatform.domain.user.entity.User;
import com.som.deliveryplatform.domain.user.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest    // JPA 기능만 검증하기 위한 어노테아션 (기본적으로 @Transactional 포함)
class UserRepositoryTest {
    
    @Autowired
    UserRepository userRepository;
    
    @Test
    @DisplayName("이메일로 사용자 조회 가능해야 한다")
    void findByEmail_success() {
        // given
        String email = "test@test.com";
        userRepository.save(User.builder().email(email)
                .role(Role.USER)
                .build());

        // when
        Optional<User> result = userRepository.findByEmail(email);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("존재하지 않는 이메일 조회시 빈 Optional이 변환되어야 한다")
    void findByEmail_notFound() {
        // when
        String email = "test@test.com";
        Optional<User> result = userRepository.findByEmail(email);

        // then
        assertThat(result).isEmpty();
    }
}