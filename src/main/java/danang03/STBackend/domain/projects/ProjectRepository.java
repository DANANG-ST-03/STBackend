package danang03.STBackend.domain.projects;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p.name FROM Project p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :input, '%'))")
    List<String> findNamesStartingWith(@Param("input") String input, Pageable pageable);


    // for search
    @Query("SELECT e FROM Project e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) ")
    Page<Project> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);


    // for dashboard
    @Query("SELECT COUNT(p) FROM Project p WHERE DATE(p.createdAt) <= :lastDay AND p.startDate IS NULL")
    Integer countPendingProjectsBefore(@Param("lastDay") LocalDate lastDay);

    @Query("SELECT COUNT(p) FROM Project p " +
            "WHERE p.startDate IS NOT NULL " +
            "AND p.startDate <= :lastDay " +
            "AND (p.endDate IS NULL OR p.endDate > :lastDay)")
    Integer countOngoingProjectsBefore(@Param("lastDay") LocalDate lastDay);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.endDate IS NOT NULL AND p.endDate <= :lastDay")
    Integer countCompletedProjectsNumBefore(@Param("lastDay") LocalDate lastDay);

    @Query("SELECT p.category, COUNT(p) FROM Project p GROUP BY p.category")
    List<Object[]> countProjectCategories();
}
