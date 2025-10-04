package com.dvpgiftcenter.dto.admin;

import jakarta.validation.constraints.NotBlank;

public class UpdateOrderStatusRequest {
    @NotBlank
    private String status;
    private String trackingNumber; // optional

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
}
