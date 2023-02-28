package br.com.alura.school.enrollment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.school.course.Course;
import br.com.alura.school.course.CourseRepository;
import br.com.alura.school.user.User;
import br.com.alura.school.user.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EnrollmentControllerTest {
	
	 	private final ObjectMapper jsonMapper = new ObjectMapper();

	    @Autowired
	    private MockMvc mockMvc;

	    @Autowired
	    private EnrollmentRepository enrollmentRepository;

	    @Autowired
	    private CourseRepository courseRepository;

	    @Autowired
	    private UserRepository userRepository;

	    @Test
	    void should_enroll_user_in_course() throws Exception {
	        Course courseTest = new Course("java-test","Java Teste","Um curso de teste Java");
	        courseRepository.save(courseTest);
	        User userTest = new User("isaac","isaac@email.com");
	        userRepository.save(userTest);
	        String username = userTest.getUsername();

	        mockMvc.perform(post("/courses/java-test/enroll")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(jsonMapper.writeValueAsString(new UserEnrollmentRequest(username))))
	          .andExpect(status().isCreated())
	          .andExpect(header().string("Location", "/courses/java-test/enroll/1"));
	    }

	    @Test
	    void should_not_enroll_user_already_enrolled() throws Exception {
	    	Course courseTest = new Course("java-test","Java Teste","Um curso de teste Java");
	        courseRepository.save(courseTest);
	        User userTest = new User("isaac","isaac@email.com");
	        userRepository.save(userTest);
	        String username = userTest.getUsername();
	        Enrollment enrollmentTest = new Enrollment(userTest, courseTest);
	        enrollmentRepository.save(enrollmentTest);

	        mockMvc.perform(post("/courses/java-test/enroll")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(jsonMapper.writeValueAsString(new UserEnrollmentRequest(username))))
	          .andExpect(status().isBadRequest());
	    }


	    @Test
	    void should_not_enroll_unknown_user() throws Exception {
	    	Course courseTest = new Course("java-test","Java Teste","Um curso de teste Java");
	        courseRepository.save(courseTest);
	        String username = "unknown-user";

	        mockMvc.perform(post("/courses/java-test/enroll")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(jsonMapper.writeValueAsString(new UserEnrollmentRequest(username))))
	          .andExpect(status().isBadRequest());
	    }


	    @Test
	    void should_not_enroll_unknown_course() throws Exception {
	        String unknowCourseCode = "unknown-course";
	        User userTest = new User("isaac","isaac@email.com");
	        userRepository.save(userTest);
	        String username = userTest.getUsername();

	        mockMvc.perform(post("/courses/"+unknowCourseCode+"/enroll")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(jsonMapper.writeValueAsString(new UserEnrollmentRequest(username))))
	          .andExpect(status().isBadRequest());
	    }
	    
	    @Test
	    void should_return_no_cotent() throws Exception {
	    	mockMvc.perform(get("/courses/enroll/report")
	    			.accept(MediaType.APPLICATION_JSON))
	    			.andExpect(status().isNoContent());
	    }

	    @Test
	    void should_return_one_user_enrolled() throws Exception {
	    	Course courseTest = new Course("java-test","Java Teste","Um curso de teste Java");
	        courseRepository.save(courseTest);
	        User userTest = new User("isaac","isaac@email.com");
	        userRepository.save(userTest);
	        Enrollment enrollmentTest = new Enrollment(userTest, courseTest);
	        enrollmentRepository.save(enrollmentTest);
	        
	    	mockMvc.perform(get("/courses/enroll/report")
	    			.accept(MediaType.APPLICATION_JSON))
	    			.andExpect(status().isOk())
			    	.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			    	.andExpect(jsonPath("$.length()", is(1)))
		            .andExpect(jsonPath("$[0].email", is("isaac@email.com")))
	    			.andExpect(jsonPath("$[0].quantidade_matriculas", is(1)));
	    }
	    
	    @Test
	    void should_return_multiple_users_enrolled() throws Exception {
	    	Course courseTest = new Course("java-test","Java Teste","Um curso de teste Java");
	        courseRepository.save(courseTest);
	        User userTestIsaac = new User("isaac","isaac@email.com");
	        userRepository.save(userTestIsaac);
	        User userTestAlex = new User("alex","alex@email.com");
	        userRepository.save(userTestAlex);
	        Enrollment enrollmentTestIsaac = new Enrollment(userTestIsaac, courseTest);
	        enrollmentRepository.save(enrollmentTestIsaac);
	        Enrollment enrollmentTestAlex = new Enrollment(userTestAlex, courseTest);
	        enrollmentRepository.save(enrollmentTestAlex);
	        
	    	mockMvc.perform(get("/courses/enroll/report")
	    			.accept(MediaType.APPLICATION_JSON))
	    			.andExpect(status().isOk())
			    	.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			    	.andExpect(jsonPath("$.length()", is(2)))
		            .andExpect(jsonPath("$[0].email", is("isaac@email.com")))
	    			.andExpect(jsonPath("$[0].quantidade_matriculas", is(1)))
	    			.andExpect(jsonPath("$[1].email", is("alex@email.com")))
	    			.andExpect(jsonPath("$[1].quantidade_matriculas", is(1)));
	    }
	    
    }
