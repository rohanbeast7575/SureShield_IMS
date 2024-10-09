package com.cms.policy.resource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.cms.policy.constants.DatabaseConstant.ActiveStatus;
import com.cms.policy.constants.DatabaseConstant.CoverageType;
import com.cms.policy.constants.DatabaseConstant.PolicyApplicationStatus;
import com.cms.policy.constants.DatabaseConstant.PolicyPlan;
import com.cms.policy.dto.AddPolicyApplicationRequest;
import com.cms.policy.dto.AddPolicyRequestDto;
import com.cms.policy.dto.CommonApiResponse;
import com.cms.policy.dto.CoverageDetailDto;
import com.cms.policy.dto.PolicyApplicationResponseDto;
import com.cms.policy.dto.PolicyResponseDto;
import com.cms.policy.dto.UpdatePolicyApplicationStatusRequest;
import com.cms.policy.dto.User;
import com.cms.policy.dto.UserDetailResponseDto;
import com.cms.policy.entity.CoverageDetails;
import com.cms.policy.entity.Policy;
import com.cms.policy.entity.PolicyApplication;
import com.cms.policy.exception.PolicyNotFoundException;
import com.cms.policy.exception.UserNotFoundException;
import com.cms.policy.external.UserService;
import com.cms.policy.service.CoverageDetailsService;
import com.cms.policy.service.PolicyApplicationService;
import com.cms.policy.service.PolicyService;
import com.cms.policy.service.RedisImplmt;
import com.cms.policy.utility.Helper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PolicyResource {

	@Autowired
	private PolicyService policyService;

	@Autowired
	private CoverageDetailsService coverageDetailsService;

	@Autowired
	private UserService userService;

	@Autowired
	private PolicyApplicationService policyApplicationService;

	@Autowired
	private RedisImplmt redisImplmt;

	@Transactional
	public ResponseEntity<CommonApiResponse> addPolicy(AddPolicyRequestDto request) {

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getName() == null || request.getDescription() == null
				|| request.getPremiumAmount() == null) {
			// response.setResponseMessage("bad request- missing or invalid request");
			throw new IllegalArgumentException("bad request- missing or invalid request");
		}

		if (request.getCoverageDetails() == null) {
			// response.setResponseMessage("bad request- coverge details not found");
			throw new IllegalArgumentException("bad request- coverge details not found");
		}

		Policy policy = new Policy();
		policy.setName(request.getName());
		policy.setDescription(request.getDescription());
		policy.setPlan(request.getPlan());
		policy.setPolicyId(Helper.generatePolicyId(10));
		policy.setPremiumAmount(request.getPremiumAmount());
		policy.setStatus(ActiveStatus.ACTIVE.value());

		Policy addedPolicy = this.policyService.addPolicy(policy);

		if (addedPolicy == null)
			throw new RuntimeException("Failed to add the policy!!!");

		List<CoverageDetailDto> coverageDetails = request.getCoverageDetails();

		for (CoverageDetailDto coverage : coverageDetails) {
			CoverageDetails detail = new CoverageDetails();
			detail.setAmount(coverage.getAmount());
			detail.setDescription(coverage.getDescription());
			detail.setPolicy(addedPolicy);
			detail.setType(coverage.getType());

			coverageDetailsService.addCoverageDetail(detail);
		}

		response.setResponseMessage("Policy added successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<PolicyResponseDto> fetchAllPolicies() {

		// List<Policy> cachedList =null;
		List<Policy> cachedList = redisImplmt.getPolicies("key_policies", Policy.class);
		PolicyResponseDto response = new PolicyResponseDto();

		if (cachedList == null) {

			List<Policy> policies = this.policyService.getAllByStatus(ActiveStatus.ACTIVE.value());

			if (CollectionUtils.isEmpty(policies)) {
				response.setResponseMessage("No Active Policies found!!!");
				response.setSuccess(false);
				return new ResponseEntity<PolicyResponseDto>(response, HttpStatus.OK);
			}

			response.setPolicies(policies);
			response.setResponseMessage("Policies fetched successful!!!");
			response.setSuccess(true);
			redisImplmt.setPolicies("key_policies", policies, 360L);
			return new ResponseEntity<PolicyResponseDto>(response, HttpStatus.OK);

		} else {
			response.setPolicies(cachedList);
			response.setResponseMessage("Policies cached successful!!!");
			response.setSuccess(true);
			return new ResponseEntity<PolicyResponseDto>(response, HttpStatus.OK);
		}

	}

	public ResponseEntity<PolicyResponseDto> fetchPolicy(Integer policyId) {

		PolicyResponseDto response = new PolicyResponseDto();

		if (policyId == null) {
			throw new IllegalArgumentException("Policy ID missing");
		}

		Policy policy = this.policyService.getById(policyId);

		if (policy == null) {
			throw new PolicyNotFoundException("Policy not found");
		}

		response.setPolicies(Arrays.asList(policy));
		response.setResponseMessage("Policy fetched successful!!!");
		response.setSuccess(true);
		return new ResponseEntity<PolicyResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deletePolicy(Integer policyId) {

		PolicyResponseDto response = new PolicyResponseDto();

		if (policyId == null) {
			throw new IllegalArgumentException("Policy ID missing");
		}

		Policy policy = this.policyService.getById(policyId);

		if (policy == null) {
			throw new PolicyNotFoundException("Policy not found");
		}

		policy.setStatus(ActiveStatus.IN_ACTIVE.value());

		Policy updatedPolicy = this.policyService.updatePolicy(policy);

		if (updatedPolicy == null) {
			// response.setResponseMessage();
			throw new RuntimeException("Failed to delete the Policy!!!");
		}

		response.setResponseMessage("Policy Deleted successful!!!");
		response.setSuccess(true);
		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> addPolicyApplication(AddPolicyApplicationRequest request) {

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getCustomerId() == null || request.getPolicyId() == null) {
			// response.setResponseMessage();
			throw new IllegalArgumentException("bad request- missing or invalid request");
		}

		Policy policy = this.policyService.getById(request.getPolicyId());

		if (policy == null)
			throw new PolicyNotFoundException("Policy not found");

		if (!policy.getStatus().equals(ActiveStatus.ACTIVE.value())) {
			response.setResponseMessage("Selected Policy is not active currently!!!s");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		UserDetailResponseDto userresponse = this.userService.getUserById(request.getCustomerId());

		User user = null;

		if (userresponse != null) {
			user = userresponse.getUser();
		}

		if (user == null) {
			throw new UserNotFoundException("User Not found");
		}
		PolicyApplication exitingApplication = this.policyApplicationService.getByPolicyAndCustomerId(policy,
				request.getCustomerId());

		if (exitingApplication != null)
			throw new RuntimeException("You have already applied for this Application");

		PolicyApplication policyApplication = new PolicyApplication();
		policyApplication.setApplicationDate(
				String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		policyApplication.setCustomerId(request.getCustomerId());
		policyApplication.setPolicy(policy);
		policyApplication.setStatus(PolicyApplicationStatus.PENDING.value());

		PolicyApplication addedApplication = policyApplicationService.addApplication(policyApplication);

		if (addedApplication == null)
			throw new RuntimeException("Failed to apply for Policy");

		response.setResponseMessage("Policy added successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<PolicyApplicationResponseDto> fetchPolicyApplications() {

		PolicyApplicationResponseDto response = new PolicyApplicationResponseDto();

		List<PolicyApplication> applications = this.policyApplicationService.getAllApplication();

		if (CollectionUtils.isEmpty(applications)) {
			response.setResponseMessage("No Applications founds!!!");
			response.setSuccess(false);

			return new ResponseEntity<PolicyApplicationResponseDto>(response, HttpStatus.OK);
		}

		for (PolicyApplication application : applications) {
			UserDetailResponseDto userresponse = this.userService.getUserById(application.getCustomerId());

			User user = null;

			if (userresponse != null) {
				user = userresponse.getUser();
			}
			application.setUser(user);

		}

		response.setApplications(applications);
		response.setResponseMessage("Applications fetched successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<PolicyApplicationResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<PolicyApplicationResponseDto> fetchPolicyApplication(Integer applicationId) {

		PolicyApplicationResponseDto response = new PolicyApplicationResponseDto();

		PolicyApplication application = this.policyApplicationService.getById(applicationId);

		if (applicationId == null) 
			throw new IllegalArgumentException("ApplicationID not found,bad request- missing or invalid request");
		
		if (application == null) 
			throw new IllegalArgumentException("Application not found,bad request- missing or invalid request");

		UserDetailResponseDto userresponse = this.userService.getUserById(application.getCustomerId());

		User user = null;

		if (userresponse != null) {
			user = userresponse.getUser();
		}
		application.setUser(user);

		response.setApplications(Arrays.asList(application));
		response.setResponseMessage("Application fetched successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<PolicyApplicationResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<PolicyApplicationResponseDto> fetchPolicyApplicationByCustomerId(Integer customerId) {

		PolicyApplicationResponseDto response = new PolicyApplicationResponseDto();

		if (customerId == null) 
			throw new IllegalArgumentException("bad request- missing or invalid request");


		List<PolicyApplication> applications = this.policyApplicationService.getByCustomerId(customerId);

		if (CollectionUtils.isEmpty(applications)) 
			throw new RuntimeCryptoException("No Application ID found");

		for (PolicyApplication application : applications) {
			UserDetailResponseDto userresponse = this.userService.getUserById(application.getCustomerId());

			User user = null;

			if (userresponse != null) {
				user = userresponse.getUser();
			}
			application.setUser(user);

		}

		response.setApplications(applications);
		response.setResponseMessage("Applications fetched successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<PolicyApplicationResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateApplicationStatus(UpdatePolicyApplicationStatusRequest request) {

		CommonApiResponse response = new CommonApiResponse();

		if (request == null || request.getPolicyApplicationId() == null || request.getStatus() == null)
			throw new IllegalArgumentException("bad request- missing or invalid request");


	

		if (request.getStatus().equals(PolicyApplicationStatus.APPROVED.value())
				&& (request.getStartDate() == null || request.getEndDate() == null)) {
			response.setResponseMessage("Please select Policy start date & end date");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		PolicyApplication application = this.policyApplicationService.getById(request.getPolicyApplicationId());

		if (application == null) 
			throw new PolicyNotFoundException("Policy Application Not found");

		application.setStatus(request.getStatus());

		if (request.getStatus().equals(PolicyApplicationStatus.APPROVED.value())) {
			application.setStartDate(request.getStartDate());
			application.setEndDate(request.getEndDate());
		}

		PolicyApplication updatedApplication = this.policyApplicationService.updateApplication(application);

		if (updatedApplication == null) 
			throw new RuntimeException("Failed to update the application status!!!");
			
		response.setResponseMessage("Policy Application updated successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<List<String>> fetchPolicyCoverage() {

		List<String> covaregeTypes = new ArrayList<>();

		for (CoverageType type : CoverageType.values()) {
			covaregeTypes.add(type.value());
		}

		return new ResponseEntity<List<String>>(covaregeTypes, HttpStatus.OK);
	}

	public ResponseEntity<List<String>> fetchApplicationstatus() {
		List<String> statuses = new ArrayList<>();

		for (PolicyApplicationStatus status : PolicyApplicationStatus.values()) {
			statuses.add(status.value());
		}

		return new ResponseEntity<List<String>>(statuses, HttpStatus.OK);
	}

	public ResponseEntity<List<String>> fetchPolicyPlans() {
		List<String> plans = new ArrayList<>();

		for (PolicyPlan plan : PolicyPlan.values()) {
			plans.add(plan.value());
		}

		return new ResponseEntity<List<String>>(plans, HttpStatus.OK);
	}

}
