package danang03.STBackend.domain.projects;

import danang03.STBackend.domain.projects.dto.ProjectAddRequest;
import danang03.STBackend.domain.projects.dto.ProjectResponse;
import danang03.STBackend.domain.projects.dto.ProjectUpdateRequest;
import danang03.STBackend.domain.projects.dto.ProjectUpdateResponse;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Long addProject(ProjectAddRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus()).build();
         projectRepository.save(project);

         return project.getId();
    }


    public Page<ProjectResponse> getProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(project -> new ProjectResponse(
                        project.getId(),
                        project.getName(),
                        project.getDescription(),
                        project.getStartDate(),
                        project.getEndDate(),
                        project.getStatus()
                ));
    }


    @Transactional
    public void updateProject(Long id, ProjectUpdateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project with id " + id + "not found"));

        // 업데이트 메서드 호출
        project.update(
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus()
        );
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Project with id " + id + " not found");
        }
        projectRepository.deleteById(id);
    }
}
