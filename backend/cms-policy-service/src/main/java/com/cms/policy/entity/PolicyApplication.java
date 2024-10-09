package com.cms.policy.entity;

import com.cms.policy.dto.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
public class PolicyApplication {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "policy_id")
	private Policy policy;

	private int customerId;

	private String applicationDate;  // epoch time

	private String startDate; // set by insurance company

	private String endDate; // set by insurance company

	private String status; // insurance company will update the status

	@Transient
	private User user;
	
}
