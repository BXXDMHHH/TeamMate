package com.teammate.repository;

import com.teammate.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 负责访问 project_members 表。
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    // 根据用户 id 找到这个用户加入的所有项目。
    // Spring Data JPA 会根据方法名自动生成查询。
    List<ProjectMember> findByUserId(Long userId);

    // 判断用户是否已经加入某个项目。
    // 这个方法主要用于权限检查。
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
}
