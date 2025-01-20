package danang03.STBackend.domain.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import danang03.STBackend.domain.employee.Skill;
import danang03.STBackend.domain.projects.ProjectCategory;
import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DashboardResponse {
    // 직원, 프젝 수
    private final Integer totalEmployeesNum;
    private final Double totalEmployeesChangePercent;   // 전월 대비 직원 수 증감 비율 (%)

    private Integer pendingProjectsNum;
    private Double pendingProjectsChangePercent;    // 전월 대비 프로젝트 수 증감 비율 (%)

    private Integer ongoingProjectsNum;
    private Double ongoingProjectsChangePercent;  // 전월 대비 진행 중인 프로젝트 증감 비율 (%)

    private Integer completedProjectsNum;
    private Double completedProjectsChangePercent;// 전월 대비 완료된 프로젝트 증감 비율 (%)

    // 그래프
    private Map<String, Integer> employeesPerMonth;
    private Map<String, Integer> prevEmployeesPerMonth;
    private Map<String, Integer> ongoingProjectsPerMonth;
    private Map<String, Integer> prevOngoingProjectsPerMonth;

    private Map<String, Double> employeeSkillRatio;       // 직원 스킬 비율 (5개 이후는 기타)
    private Map<String, Double> projectCategoryRatio;     // 프로젝트 카테고리 비율 (8개 이후는 기타)
}
