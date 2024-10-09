package com.cms.user.resource;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.cms.user.constants.DatabaseConstant.UserStatus;
import com.cms.user.dto.CommonApiResponse;
import com.cms.user.dto.UserDetailResponseDto;
import com.cms.user.dto.UserList;
import com.cms.user.dto.UserListResponseDto;
import com.cms.user.dto.UserLoginRequest;
import com.cms.user.dto.UserLoginResponse;
import com.cms.user.dto.UserRegisterDto;
import com.cms.user.entity.User;
import com.cms.user.exception.UserNotFoundException;
import com.cms.user.service.CustomUserDetailsService;
import com.cms.user.service.UserService;
import com.cms.user.utility.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UserResource {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	ObjectMapper objectMapper = new ObjectMapper();
	
	public ResponseEntity<CommonApiResponse> registerAdmin(UserRegisterDto userRegisterDto) {

		CommonApiResponse response = new CommonApiResponse();

		if (userRegisterDto == null) {
		throw new IllegalArgumentException("request is empty");}

		User existingUser = this.userService.getUserByEmailAndRole(userRegisterDto.getEmailId(),
				userRegisterDto.getRole());

		if (existingUser != null) {
		throw new RuntimeException("Admin with this email id already registered");}

		User user = UserRegisterDto.toEntity(userRegisterDto);
        user.setEmailId(userRegisterDto.getEmailId());
		user.setStatus(UserStatus.ACTIVE.value());
		
		String encodedPassword = passwordEncoder.encode(userRegisterDto.getPassword());

		user.setPassword(encodedPassword);

		existingUser = this.userService.registerUser(user);

		if (existingUser == null) {
		throw new RuntimeException("Failed to register admin");}

		response.setResponseMessage("Admin registered Successfully");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> registerUser(UserRegisterDto registerRequest) {

		CommonApiResponse response = new CommonApiResponse();

		if (registerRequest == null) {
			throw new IllegalArgumentException("user is null");}

		User existingUser = this.userService.getUserByEmailAndRole(registerRequest.getEmailId(),
				registerRequest.getRole());

		if (existingUser != null) {
		throw new RuntimeException("User already registered");}

		User user = UserRegisterDto.toEntity(registerRequest);

		user.setStatus(UserStatus.ACTIVE.value());
		user.setRole(registerRequest.getRole());

		String encodedPassword = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPassword);

		existingUser = this.userService.registerUser(user);

		if (existingUser == null) {
		throw new RuntimeException("Failed to register the user");}

		response.setResponseMessage("User registered Successfully");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserLoginResponse> login(UserLoginRequest loginRequest) {

		UserLoginResponse response = new UserLoginResponse();

		if (loginRequest == null) {
		throw new IllegalArgumentException("Missing Input");}

		String jwtToken = null;
		User user = null;
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmailId(), loginRequest.getPassword()));
		} catch (Exception ex) {
		throw new RuntimeException("Invalid password or email");}

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmailId());

		user = userService.getUserByEmail(loginRequest.getEmailId());

		if (user.getStatus() != UserStatus.ACTIVE.value()) {
	throw new RuntimeException("Failed to login, user not active");	}

		for (GrantedAuthority grantedAuthory : userDetails.getAuthorities()) {
			if (grantedAuthory.getAuthority().equals(loginRequest.getRole())) {
				jwtToken = jwtUtil.generateToken(userDetails.getUsername());
			}
		}

		// user is authenticated
		if (jwtToken != null) {
			response.setUser(user);

			response.setResponseMessage("Logged in sucessful");
			response.setSuccess(true);
			response.setJwtToken(jwtToken);
			return new ResponseEntity(response, HttpStatus.OK);

		}

		else {

		throw new RuntimeException("failed to login");}

	}

	public ResponseEntity<CommonApiResponse> deleteUser(int userId) {

		CommonApiResponse response = new CommonApiResponse();

		if (userId == 0) {
		throw new IllegalArgumentException("User id is 0");}

		User user = this.userService.getUserById(userId);

		user.setStatus(com.cms.user.constants.DatabaseConstant.UserStatus.NOT_ACTIVE.value());

		User deletedUser = this.userService.updateUser(user);

		if (deletedUser == null) {
		throw new UserNotFoundException("\"Failed to delete the user\"");	}

		response.setResponseMessage("User deleted Successfully");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateUser(UserRegisterDto registerRequest) throws IllegalAccessException {

		CommonApiResponse response = new CommonApiResponse();

		if (registerRequest == null) {
		throw new IllegalAccessException("Missing input");	}

		User userDetailToUpdate = new User();

		User existingUser = this.userService.getUserById(registerRequest.getId());

		if (existingUser == null) {
	throw new UserNotFoundException("User not found");	}

		userDetailToUpdate.setId(existingUser.getId());
		userDetailToUpdate.setPassword(existingUser.getPassword());
		userDetailToUpdate.setEmailId(existingUser.getEmailId());
		userDetailToUpdate.setRole(existingUser.getRole());
		userDetailToUpdate.setStatus(existingUser.getStatus());
		userDetailToUpdate.setAge(registerRequest.getAge());
		userDetailToUpdate.setCity(registerRequest.getCity());
		userDetailToUpdate.setContact(registerRequest.getContact());
		userDetailToUpdate.setFirstName(registerRequest.getFirstName());
		userDetailToUpdate.setGender(registerRequest.getGender());
		userDetailToUpdate.setLastName(registerRequest.getLastName());
		userDetailToUpdate.setPincode(registerRequest.getPincode());
		userDetailToUpdate.setStreet(registerRequest.getStreet());

		User updatedUser = this.userService.updateUser(userDetailToUpdate);

		if (updatedUser == null) {
		throw new UserNotFoundException("Failed to update the user, maybe user not found");}

		response.setResponseMessage("User updated Successfully");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserDetailResponseDto> getUserDetailsById(int userId) {

		UserDetailResponseDto response = new UserDetailResponseDto();

		if (userId == 0) {
			throw new IllegalArgumentException("User id is 0");}


		User user = this.userService.getUserById(userId);

		if (user == null) {
		throw new UserNotFoundException("User not found");}

		response.setUser(user);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserDetailResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserListResponseDto> getAllUsers(String role) {

		UserListResponseDto response = new UserListResponseDto();

		List<UserList> users = new ArrayList<>();

		List<User> listOfUser = this.userService.getUsersByRoleAndStatus(role, UserStatus.ACTIVE.value());
		
		if(CollectionUtils.isEmpty(listOfUser)) {
	throw new UserNotFoundException("No Users found ..!!");	}

		for (User user : listOfUser) {

			UserList userList = new UserList();
			userList.setUser(user);
			users.add(userList);
		}

		response.setUsers(users);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserListResponseDto>(response, HttpStatus.OK);
	}

}
