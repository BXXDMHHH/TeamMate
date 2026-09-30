package com.teammate.repository;

import com.teammate.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 负责访问 messages 表。
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 根据项目 id 查询聊天记录，并按照创建时间从旧到新排列。
    // 最终效果类似：SELECT ... WHERE project_id = ? ORDER BY created_at ASC
    List<Message> findByProjectIdOrderByCreatedAtAsc(Long projectId);
}
