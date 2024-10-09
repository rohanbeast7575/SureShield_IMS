package com.cms.claim.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Claim {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String claimId;
	
	private int policyId;
	
	private int policyApplicationId;
	
	private int customerId;
	
	private int surveyorId;
	
	private BigDecimal claimAmount;
	
	private BigDecimal amtApprovedBySurveyor;
	
	private String claimApplicationDate;
	
	private String claimStatus; // open, close
	
	private String actionStatus; // pending ,assigned to surveyor, claim accepted , claim rejected
	
//	private BigDecimal surveyorFees;
	
	private String dateOfAccident;
	
	private String customerClaimResponse;  // accept or withdraw claim
	
}
