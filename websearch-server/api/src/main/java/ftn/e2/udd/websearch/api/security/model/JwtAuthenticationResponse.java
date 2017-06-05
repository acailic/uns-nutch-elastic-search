package ftn.e2.udd.websearch.api.security.model;

public class JwtAuthenticationResponse {

	private String token;
	
	public JwtAuthenticationResponse(String token) {
		this.setToken(token);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
