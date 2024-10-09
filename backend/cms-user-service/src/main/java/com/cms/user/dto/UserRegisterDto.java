package com.cms.user.dto;

import org.springframework.beans.BeanUtils;

import com.cms.user.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDto {
	
	  
	    private int id;

	    @NotBlank(message = "First name is mandatory")
	    @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
	    private String firstName;

	    @NotBlank(message = "Last name is mandatory")
	    @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
	    private String lastName;

	    @Min(value = 0, message = "Age must be greater than or equal to 0")
	    @NotNull(message = "Age  is mandatory")
	    @Max(value = 120, message = "Age must be less than or equal to 120")
	    private int age;

	    @NotBlank(message = "Gender is mandatory")
	    @Pattern(regexp = "Male|Female|Other", message = "Gender must be 'Male', 'Female', or 'Other'")
	    private String gender;

	    @NotBlank(message = "Email is mandatory")
	    @Email(message = "Email should be valid")
	    private String emailId;

	    @NotBlank(message = "Contact number is mandatory")
	    @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be a 10-digit number")
	    private String contact;

	    @NotBlank(message = "Street is mandatory")
	    private String street;

	    @NotBlank(message = "City is mandatory")
	    private String city;

	    @NotBlank(message = "Pincode is mandatory")
	    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be a 6-digit number")
	    private String pincode;

	    @NotBlank(message = "Password is mandatory")
	    @Size(min = 8, message = "Password must be at least 8 characters long")
	    private String password;

	    @NotBlank(message = "Role is mandatory")
	    private String role;
	
	public static User toEntity(UserRegisterDto userRegisterDto) {
		User user = new User();
		BeanUtils.copyProperties(userRegisterDto, user);
		return user;
	}

}
