package com.buspass.bus_pass_tracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ApprovalRequest {

    @NotNull(message = "Validity days is required")
    @Min(value = 1, message = "Validity days must be at least 1")
    private Integer validityDays;

    private String adminRemark;

    public ApprovalRequest() {
    }

    public Integer getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(Integer validityDays) {
        this.validityDays = validityDays;
    }

    public String getAdminRemark() {
        return adminRemark;
    }

    public void setAdminRemark(String adminRemark) {
        this.adminRemark = adminRemark;
    }
}