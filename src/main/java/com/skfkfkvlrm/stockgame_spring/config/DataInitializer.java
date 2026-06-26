package com.skfkfkvlrm.stockgame_spring.config;

import com.skfkfkvlrm.stockgame_spring.domain.AppUser;
import com.skfkfkvlrm.stockgame_spring.domain.Role;
import com.skfkfkvlrm.stockgame_spring.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 초기 관리자 계정을 자동 생성합니다.
 * 이미 존재하는 계정은 덮어쓰지 않습니다.
 *
 * 운영 환경에서는 초기 비밀번호를 application.yaml 환경변수로 분리해야 합니다.
 * 예: ${ADMIN_PASSWORD:admin1234}
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        createDefaultAdminIfAbsent();
        createDefaultManagerIfAbsent();
    }

    private void createDefaultAdminIfAbsent() {
        if (!appUserRepository.existsByUsername("admin")) {
            AppUser admin = AppUser.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin1234")) // 운영 시 환경변수로 교체
                    .role(Role.ROLE_ADMIN)
                    .build();
            appUserRepository.save(admin);
            log.info("[DataInitializer] ADMIN 초기 계정 생성 완료 (username: admin)");
        } else {
            log.debug("[DataInitializer] ADMIN 계정이 이미 존재합니다.");
        }
    }

    private void createDefaultManagerIfAbsent() {
        if (!appUserRepository.existsByUsername("manager")) {
            AppUser manager = AppUser.builder()
                    .username("manager")
                    .password(passwordEncoder.encode("manager1234")) // 운영 시 환경변수로 교체
                    .role(Role.ROLE_MANAGER)
                    .build();
            appUserRepository.save(manager);
            log.info("[DataInitializer] MANAGER 초기 계정 생성 완료 (username: manager)");
        } else {
            log.debug("[DataInitializer] MANAGER 계정이 이미 존재합니다.");
        }
    }
}
