package danang03.STBackend.domain.dashboard;

import danang03.STBackend.domain.dashboard.dto.DashboardResponse;
import danang03.STBackend.domain.employee.EmployeeRepository;
import danang03.STBackend.domain.projects.ProjectRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DashboardService {
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    public DashboardService(EmployeeRepository employeeRepository, ProjectRepository projectRepository) {
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
    }


    public DashboardResponse getDashboard() {
        log.info("get dashboard service");

        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);
        YearMonth previousYear = currentMonth.minusYears(1);
        log.info("Current month: " + currentMonth);

        // 직원, 프로젝트 수 with 전월대비 증감
        Integer totalEmployeesNum = countEmployeesBeforeMonth(currentMonth);
        Integer prevTotalEmployeesNum = countEmployeesBeforeMonth(previousMonth);
        Double totalEmployeesChangePercent = calculateChangePercent(totalEmployeesNum, prevTotalEmployeesNum);
        log.info("Total employees: " + totalEmployeesNum);

        Integer pendingProjectsNum = countPendingProjects(currentMonth);
        log.info("Pending projects: " + pendingProjectsNum);
        Integer prevPendingProjectsNum = countPendingProjects(previousMonth);
        log.info("Previous projects: " + prevPendingProjectsNum);
        Double totalProjectsChangePercent = calculateChangePercent(totalEmployeesNum, prevPendingProjectsNum);
        log.info("Total projects change: " + totalProjectsChangePercent);

        Integer ongoingProjectsNum = countOngoingProjects(currentMonth);
        Integer prevOngoingProjectsNum = countOngoingProjects(previousMonth);
        Double ongoingProjectsChangePercent = calculateChangePercent(ongoingProjectsNum, prevOngoingProjectsNum);
        log.info("Ongoing projects change: " + ongoingProjectsChangePercent);

        Integer completedProjectsNum = countCompletedProjects(currentMonth);
        Integer prevCompletedProjectsNum = countCompletedProjects(previousMonth);
        Double completedProjectsChangePercent = calculateChangePercent(completedProjectsNum, prevCompletedProjectsNum);
        log.info("Completed projects change: " + completedProjectsChangePercent);


        // 직원 수 및 진행 중인 프로젝트 수 (최근 6개월 데이터)
        Map<String, Integer> employeesPerMonth = countEmployeesForLast6Months(currentMonth);
        Map<String, Integer> prevEmployeesPerMonth = countEmployeesForLast6Months(previousYear);
        Map<String, Integer> ongoingProjectsPerMonth = countOngoingProjectsForLast6Months(currentMonth);
        Map<String, Integer> prevOngoingProjectsPerMonth = countOngoingProjectsForLast6Months(previousYear);
        log.info("Total employees: " + totalEmployeesNum);


        Map<String, Double> employeeSkillRatio = getEmployeeSkillRatio();
        Map<String, Double> projectCategoryRatio = getProjectCategoryRatio();
        log.info("Total employees: " + totalEmployeesNum);

        return DashboardResponse.builder()
                .totalEmployeesNum(totalEmployeesNum)
                .totalEmployeesChangePercent(totalEmployeesChangePercent)
                .pendingProjectsNum(pendingProjectsNum)
                .pendingProjectsChangePercent(totalProjectsChangePercent)
                .ongoingProjectsNum(ongoingProjectsNum)
                .ongoingProjectsChangePercent(ongoingProjectsChangePercent)
                .completedProjectsNum(completedProjectsNum)
                .completedProjectsChangePercent(completedProjectsChangePercent)
                .employeesPerMonth(employeesPerMonth)
                .prevEmployeesPerMonth(prevEmployeesPerMonth)
                .ongoingProjectsPerMonth(ongoingProjectsPerMonth)
                .prevOngoingProjectsPerMonth(prevOngoingProjectsPerMonth)
                .employeeSkillRatio(employeeSkillRatio)
                .projectCategoryRatio(projectCategoryRatio)
                .build();
    }

    private Integer countEmployeesBeforeMonth(YearMonth targetMonth) {
        LocalDate lastDayOfMonth = targetMonth.atEndOfMonth();
        return employeeRepository.countEmployeesJoinedBefore(lastDayOfMonth);
    }

    private Integer countPendingProjects(YearMonth targetMonth) {
        LocalDate lastDayOfMonth = targetMonth.atEndOfMonth();
        return projectRepository.countPendingProjectsBefore(lastDayOfMonth);
    }

    private Integer countOngoingProjects(YearMonth targetMonth) {
        LocalDate lastDayOfMonth = targetMonth.atEndOfMonth();
        return projectRepository.countOngoingProjectsBefore(lastDayOfMonth);
    }

    private Integer countCompletedProjects(YearMonth targetMonth) {
        LocalDate lastDayOfMonth = targetMonth.atEndOfMonth();
        return projectRepository.countCompletedProjectsNumBefore(lastDayOfMonth);
    }


    private Double calculateChangePercent(Integer current, Integer previous) {
        if (previous == 0) return 0.0; // 전월이 0이면 증감률 0%
        double result = ((current - previous) / (double) previous) * 100;
        return Math.round(result * 100.0) / 100.0;
    }



    private Map<String, Integer> countEmployeesForLast6Months(YearMonth targetMonth) {
        Map<String, Integer> employeesPerMonth = new LinkedHashMap<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = targetMonth.minusMonths(i);
            Integer employeeCount = employeeRepository.countEmployeesJoinedBefore(month.atEndOfMonth());
            employeesPerMonth.put(month.toString(), employeeCount);
        }
        return employeesPerMonth;
    }


    private Map<String, Integer> countOngoingProjectsForLast6Months(YearMonth targetMonth) {
        Map<String, Integer> projectsPerMonth = new LinkedHashMap<>();

        for (int i = 5; i >= 0; i--) {
            YearMonth month = targetMonth.minusMonths(i);
            Integer proejctsCount = projectRepository.countOngoingProjectsBefore(month.atEndOfMonth());
            projectsPerMonth.put(month.toString(), proejctsCount);
        }
        return projectsPerMonth;
    }




    // 직원 스킬 비율 계산 (상위 5개, 나머지는 "기타" 그룹)
    private Map<String, Double> getEmployeeSkillRatio() {
        List<Object[]> rawData = employeeRepository.countSkills();
        return processCategoryRatio(rawData, 5);
    }

    // 프로젝트 카테고리 비율 계산 (상위 8개, 나머지는 "기타" 그룹)
    private Map<String, Double> getProjectCategoryRatio() {
        List<Object[]> rawData = projectRepository.countProjectCategories();
        return processCategoryRatio(rawData, 8);
    }

    // 공통 비율 계산 로직 (상위 N개, 나머지는 "기타")
    private Map<String, Double> processCategoryRatio(List<Object[]> rawData, int topN) {
        Map<String, Integer> countMap = new HashMap<>();
        int total = 0;

        // 원본 데이터에서 개수 계산
        // Object[] => ["Python", 13]
        for (Object[] row : rawData) {
            String category = row[0].toString();
            int count = ((Number) row[1]).intValue();
            countMap.put(category, count);
            total += count;
        }

        // 비율 계산
        Map<String, Double> ratioMap = new LinkedHashMap<>();
        int finalTotal = total;
        countMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())) // 내림차순 정렬
                .limit(topN)
                .forEach(entry -> {
                    double percentage = (entry.getValue() / (double) finalTotal) * 100;
                    ratioMap.put(entry.getKey(), Math.round(percentage * 100.0) / 100.0);
                });

        // 기타 그룹 추가
        int otherCount = countMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .skip(topN)
                .mapToInt(Map.Entry::getValue)
                .sum();

        if (otherCount > 0) {
            double percentage = (otherCount / (double) total) * 100;
            ratioMap.put("others", Math.round(percentage * 100.0) / 100.0);
        }

        return ratioMap;
    }
}
