package com.teammate.repository;

import com.teammate.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

// 项目数据库访问层。
// JpaRepository<Project, Long> 中：
// Project = 操作哪张实体表
// Long = 主键 id 的类型
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // 目前直接使用 JpaRepository 自带的 findById、save 等方法。
}
