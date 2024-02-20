package com.study.signuplogin.repository;


import com.study.signuplogin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Optional<클래스명> :  값이 존재할 수도 있고 존재하지 않을 수도 있는 컨테이너 객체
}
