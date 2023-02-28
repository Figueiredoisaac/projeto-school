package br.com.alura.school.enrollment;

import static java.lang.String.format;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.alura.school.course.Course;
import br.com.alura.school.course.CourseRepository;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;

@RestController
public class EnrollmentController {

	@Autowired
	private final EnrollmentRepository enrollmentRepository;

	@Autowired
	private final CourseRepository courseRepository;

	@Autowired
	private final UserRepository userRepository;

	public EnrollmentController(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository,
			UserRepository userRepository) {
		this.enrollmentRepository = enrollmentRepository;
		this.courseRepository = courseRepository;
		this.userRepository = userRepository;
	}

	@PostMapping("/courses/{courseCode}/enroll")
	public ResponseEntity<EnrollmentReportResponse> enroll(@PathVariable("courseCode") String code,
			@RequestBody UserEnrollmentRequest userEnrollRequest) {
		Optional<Course> courseReq = courseRepository.findByCode(code);
		Optional<User> userReq = userRepository.findByUsername(userEnrollRequest.getUsername());

		if (courseReq.isPresent() && userReq.isPresent()) {
			Course courseEnroll = courseReq.get();
			User userEnroll = userReq.get();
			Boolean enrollValidation = enrollmentRepository.existsByUserAndCourse(userEnroll, courseEnroll);
			if (enrollValidation) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user is already enrolled");
			}
			Enrollment enrollment = new Enrollment(userEnroll, courseEnroll);
			enrollmentRepository.save(enrollment);
			URI location = URI.create(format("/courses/%s/enroll/%s", code, enrollment.getEnrollmentNumber()));
			return ResponseEntity.created(location).build();
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No course or user found");
		}
	}

	@GetMapping("/courses/enroll/report")
	public ResponseEntity<List<EnrollmentReportResponse>> report() {
		List<User> allUsers = userRepository.findAll();
		if (allUsers.isEmpty() == false) {
			List<EnrollmentReportResponse> response = new ArrayList<EnrollmentReportResponse>();
			allUsers.forEach(user -> {
				String email = user.getEmail();
				Long countEnrollment = enrollmentRepository.countByUser(user);
				if (countEnrollment > 0) {
					EnrollmentReportResponse newReport = new EnrollmentReportResponse(email, countEnrollment);
					response.add(newReport);
				}
			});
			if (response.size() == 0) {
				throw new ResponseStatusException(HttpStatus.NO_CONTENT, format("No users found"));
			}
			return ResponseEntity.ok(response);
		} else {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, format("No users found"));
		}
	}

}
