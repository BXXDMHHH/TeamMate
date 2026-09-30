package com.teammate.config;

import com.teammate.entity.Message;
import com.teammate.entity.MessageType;
import com.teammate.entity.Project;
import com.teammate.entity.ProjectMember;
import com.teammate.entity.User;
import com.teammate.repository.MessageRepository;
import com.teammate.repository.ProjectMemberRepository;
import com.teammate.repository.ProjectRepository;
import com.teammate.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            MessageRepository messageRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            User alice = userRepository.save(
                    new User("Alice", "alice@test.com", passwordEncoder.encode("123456")));
            User bob = userRepository.save(
                    new User("Bob", "bob@test.com", passwordEncoder.encode("123456")));
            User charlie = userRepository.save(
                    new User("Charlie", "charlie@test.com", passwordEncoder.encode("123456")));

            Project alpha = projectRepository.save(
                    new Project("Project Alpha", "TeamMate Android application"));

            projectMemberRepository.save(new ProjectMember(alpha, alice, "MEMBER"));
            projectMemberRepository.save(new ProjectMember(alpha, bob, "MEMBER"));
            projectMemberRepository.save(new ProjectMember(alpha, charlie, "MEMBER"));

            messageRepository.save(new Message(
                    alpha, alice, "登录接口今天能完成吗？", MessageType.USER));
            messageRepository.save(new Message(
                    alpha, bob, "可以，我今晚提交。", MessageType.USER));
            messageRepository.save(new Message(
                    alpha, alice, "那明天开始前后端联调。", MessageType.USER));
        };
    }
}
