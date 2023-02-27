package br.com.alura.school.enrollment;

import com.fasterxml.jackson.annotation.JsonProperty;

class UserEnrollmentRequest {

	@JsonProperty
	private String username;
	
	
	public UserEnrollmentRequest() {
	}

	public UserEnrollmentRequest(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
