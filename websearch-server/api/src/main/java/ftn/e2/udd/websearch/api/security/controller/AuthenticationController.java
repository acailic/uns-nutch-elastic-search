package ftn.e2.udd.websearch.api.security.controller;

import ftn.e2.udd.websearch.api.security.JwtTokenUtil;
import ftn.e2.udd.websearch.api.security.model.AuthenticationRequest;
import ftn.e2.udd.websearch.api.security.model.JwtAuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
	
	private UserDetailsService userDetailsService;
	
	private AuthenticationManager authenticationManager;

	@Autowired
	public AuthenticationController(UserDetailsService userDetailsService,
			AuthenticationManager authenticationManager) {
		this.userDetailsService = userDetailsService;
		this.authenticationManager = authenticationManager;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						authenticationRequest.username,
						authenticationRequest.password
						)
				);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username);
		String token = JwtTokenUtil.generateToken(userDetails);
		
		return ResponseEntity.ok(new JwtAuthenticationResponse(token));
	}
	
}
