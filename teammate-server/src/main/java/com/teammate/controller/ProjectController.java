package com.teammate.controller;

import com.teammate.dto.ProjectResponse;
import com.teammate.service.ProjectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> getProjects(@RequestParam Long userId) {
        return projectService.getProjectsForUser(userId);
    }

    @GetMapping("/{projectId}")
    public ProjectResponse getProject(@PathVariable Long projectId, @RequestParam Long userId) {
        return ProjectResponse.from(projectService.getProjectForMember(projectId, userId));
    }
}
