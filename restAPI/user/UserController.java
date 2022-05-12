package twins.restAPI.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import twins.logic.UsersService;
import twins.logic.user.NewUserDetailsBoundary;
import twins.logic.user.UserBoundary;
import twins.logic.user.UserId;

@RestController
@CrossOrigin("*")
public class UserController {
	private UsersService usersService;
	private String space;
	
	@Autowired
	public UserController(UsersService usersService) {
		super();
		this.usersService = usersService;
	}
	
	// read this value from Spring configuration
	@Value("${spring.application.name:space}")
	public void setSpace(String space) {
		this.space = space;
	}

	@RequestMapping(
			path = "/twins/users/login/{userSpace}/{userEmail}", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary getUserDetails(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email) {
		return this.usersService.login(space, email);
	}
	
	@RequestMapping(
			path = "/twins/users", 
			method = RequestMethod.POST, 
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createNewUser(@RequestBody NewUserDetailsBoundary newUserDetails){
		System.err.println(newUserDetails.getEmail());
		UserBoundary boundary = new UserBoundary(
				new UserId(this.space, newUserDetails.getEmail()),
				newUserDetails.getRole(),
				newUserDetails.getUsername(),
				newUserDetails.getAvatar());
		return usersService.createUser(boundary);
	}
	
	@RequestMapping(
			path = "/twins/users/{userSpace}/{userEmail}", 
			method = RequestMethod.PUT, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateUserDetails(
			@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email,
			@RequestBody UserBoundary update) {
		this.usersService.updateUser(space, email, update);
	}

}
