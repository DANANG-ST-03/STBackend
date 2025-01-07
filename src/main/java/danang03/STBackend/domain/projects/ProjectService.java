package danang03.STBackend.domain.projects;

import danang03.STBackend.domain.projects.dto.ProjectAddRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
