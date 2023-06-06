package com.kodilla.CarRentalFrontend.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BackendConfiguration {

    @Value("${car.api.endpoint}")
    private String carEndpoint;

    @Value("${driver.api.endpoint}")
    private String driverEndpoint;

    @Value("${client.api.endpoint}")
    private String clientEndpoint;

    @Value("${reservation.api.endpoint}")
    private String reservationEndpoint;

    @Value("${rentalOrder.api.endpoint}")
    private String rentalOrderEndpoint;

    @Value("${damage.api.endpoint}")
    private String damageEndpoint;

}
