package com.cms.policy.entity;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;  // Add this import

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String policyId;
    
    private String name;

    private String description;

    private BigDecimal premiumAmount;

    private String plan; // monthly - yearly

  //  @JsonIgnore  // Prevent lazy-loaded collection from being serialized
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CoverageDetails> coverageDetailsList;

    private String status;
}
