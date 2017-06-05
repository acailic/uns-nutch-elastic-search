package ftn.e2.udd.websearch.api.security.controller;


import ftn.e2.udd.websearch.api.security.JwtTokenUtil;
import ftn.e2.udd.websearch.api.security.model.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/user")
public class UserController {
	
	private UserDetailsService userDetailsService;
	
	@Autowired
	public UserController(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public JwtUser getAuthenticatedUser(HttpServletRequest request) {
		String tokenHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
		String token = JwtTokenUtil.getTokenFromHttpHeader(tokenHeader);
		String usernameFromToken = JwtTokenUtil.getUsernameFromToken(token);
		
		JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(usernameFromToken);
		
		return user;
	}
}
