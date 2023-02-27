package br.com.alura.school.enrollment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.alura.school.course.Course;
import br.com.alura.school.user.User;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	Optional<Enrollment> findByCourse(Course Course);

	@Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.user = :user AND e.course = :course")
	boolean existsByUserAndCourse(@Param("user") User user, @Param("course") Course course);

	@Query("SELECT COUNT(e) FROM Enrollment e WHERE e.user = :user")
    Long countByUser(@Param("user") User user);
}
