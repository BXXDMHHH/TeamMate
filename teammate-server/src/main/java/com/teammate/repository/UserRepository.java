package com.teammate.repository;

import com.teammate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Repository 是“Java 代码访问数据库”的入口。
// 继承 JpaRepository 后，Spring Data JPA 会自动提供增删改查能力。
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA 会根据方法名自动生成 SQL：
    // SELECT ... FROM users WHERE email = ?
    Optional<User> findByEmail(String email);
}
