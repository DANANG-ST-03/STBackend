package danang03.STBackend.domain.relation;

import danang03.STBackend.domain.employee.Employee;
import danang03.STBackend.domain.employee.EmployeeRepository;
import danang03.STBackend.domain.member.MemberRepository;
import danang03.STBackend.domain.projects.Project;
import danang03.STBackend.domain.projects.ProjectRepository;
import danang03.STBackend.domain.relation.EmployeeProject.Role;
import danang03.STBackend.domain.relation.dto.EmployeeProjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeProjectService {
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeProjectRepository employeeProjectRepository;

    @Autowired
    public EmployeeProjectService(EmployeeRepository employeeRepository, ProjectRepository projectRepository, EmployeeProjectRepository employeeProjectRepository) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.employeeProjectRepository = employeeProjectRepository;
    }

    public void assignEmployeeToProject(EmployeeProjectRequest request) {
        // 직원과 프로젝트를 데이터베이스에서 조회
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Employee_Project 엔티티 생성 및 저장
        EmployeeProject employeeProject = EmployeeProject.builder()
                .employee(employee)
                .project(project)
                .role(Role.valueOf(request.getRole()))
                .contribution(request.getContribution())
                .build();

        employeeProjectRepository.save(employeeProject);
    }
}
