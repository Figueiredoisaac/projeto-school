package br.com.alura.school.enrollment;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.alura.school.course.Course;
import br.com.alura.school.user.User;

@Entity
@Table(name = "enrollment", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "course_id" }) })
public class Enrollment {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long enrollmentNumber;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;

	@Column(nullable = false)
	private LocalDateTime date;
	
	public Enrollment() {
	}
	
	public Enrollment(User user, Course course) {
		this.user = user;
		this.course = course;
		this.date = LocalDateTime.now();
	}

	public Long getEnrollmentNumber() {
		return enrollmentNumber;
	}

	public User getUser() {
		return user;
	}

	public Course getCourse() {
		return course;
	}

	public LocalDateTime getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "Matricula " + enrollmentNumber + " do usuario " + user.getUsername() + " no curso "
				+ course.getName();
	}
}
