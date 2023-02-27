package br.com.alura.school.enrollment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnrollmentReportResponse {

	@JsonProperty
	private String email;
	@JsonProperty
	private Long quantidade_matriculas;
	
	public EnrollmentReportResponse(String email, Long quantidade_matriculas) {
		this.email = email;
		this.quantidade_matriculas = quantidade_matriculas;
	}

}
