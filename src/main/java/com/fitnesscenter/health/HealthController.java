package com.fitnesscenter.health;

import com.fitnesscenter.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

//Actuator is your application's health and monitoring system
//    Spring Boot Actuator is used to monitor and manage your application while it is running.
//    it does the following: Application health monitoring, Database health monitoring, Deployment monitoring, Troubleshooting, Monitoring systems such as Prometheus/Grafana later, Detecting application failures and Kubernetes/container health checks later
//   e.g:::::::::::
//   you can run http://localhost:8080/actuator/health which prints status: up i.e. Spring Boot considers the application healthy. so the actuator its asking if your backend if alive and healthy
//   if you open http://localhost:8080/swagger-ui.html and http://localhost:8080/v3/api-docs you will see the health endpoint in swagger

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> health() {

        Map<String, String> data = Map.of(
                "status", "UP",
                "service", "fitness-management-system"
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Fitness Management System is running",
                        data
                )
        );
    }
}