
package com.transittrack.dto;

import com.transittrack.entity.Registration;
import java.time.LocalDateTime;

public class RegistrationResponse {

    private Long registrationId;
    private String studentName;
    private String studentEmail;
    private Long routeId;
    private String routeName;
    private String busNumber;
    private String stopName;
    private String status;
    private LocalDateTime requestedAt;

    public RegistrationResponse(Registration registration) {
        this.registrationId = registration.getRegistrationId();
        this.studentName = registration.getUser().getName();
        this.studentEmail = registration.getUser().getEmail();
        this.routeId = registration.getRoute().getRouteId();
        this.routeName = registration.getRoute().getRouteName();
        this.busNumber = registration.getRoute().getBus().getBusNumber();
        this.stopName = registration.getStopName();
        this.status = registration.getStatus();
        this.requestedAt = registration.getRequestedAt();
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public Long getRouteId() {
        return routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public String getStopName() {
        return stopName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
}
