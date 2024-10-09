package com.cms.claim.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CoverageDetails {

	private int id;

	private String type;

	private String description;

	private BigDecimal amount;

	private Policy policy;

}
