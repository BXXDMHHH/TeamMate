package com.teammate.service;

import com.teammate.dto.LoginRequest;
import com.teammate.dto.LoginResponse;
import com.teammate.entity.User;
import com.teammate.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// Service 层负责“业务逻辑”。
// Controller 收到请求后，一般交给 Service 处理真正的业务。
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring 会自动把 UserRepository 和 PasswordEncoder 注入进来。
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 登录业务：
    // 1. 根据邮箱找用户
    // 2. 检查密码
    // 3. 登录成功后返回用户基本信息
    public LoginResponse login(LoginRequest request) {
        // 找不到用户时，直接返回 401 Unauthorized。
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        // 数据库保存的是 BCrypt 密码哈希。
        // matches 会把用户输入的明文密码与数据库里的哈希进行比较。
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        // 登录成功，只把需要给 Android 的信息返回出去。
        // 不把 passwordHash 返回给客户端。
        return new LoginResponse(user.getId(), user.getUsername(), user.getEmail());
    }
}
