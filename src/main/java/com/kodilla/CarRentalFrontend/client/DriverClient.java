package com.kodilla.CarRentalFrontend.client;

import com.kodilla.CarRentalFrontend.config.BackendConfiguration;
import com.kodilla.CarRentalFrontend.domain.DriverDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Optional.ofNullable;

@Component
public class DriverClient {

    private final RestTemplate restTemplate;
    private final BackendConfiguration backendConfiguration;

    @Autowired
    public DriverClient(RestTemplate restTemplate, BackendConfiguration backendConfiguration) {
        this.restTemplate = restTemplate;
        this.backendConfiguration = backendConfiguration;
    }

    public List<DriverDto> getDrivers() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDriverEndpoint()).build().encode().toUri();
            DriverDto[] response = restTemplate.getForObject(url, DriverDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new DriverDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public DriverDto getDriver(Long driverId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDriverEndpoint() + "/" + driverId).build().encode().toUri();
            return restTemplate.getForObject(url, DriverDto.class);
        } catch (RestClientException e) {
            return new DriverDto();
        }
    }

    public List<DriverDto> getEmployeeDriverList() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDriverEndpoint() + "/employeeDriverList").build().encode().toUri();
            DriverDto[] response = restTemplate.getForObject(url, DriverDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new DriverDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public void createDriver(DriverDto driverDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDriverEndpoint()).build().encode().toUri();
        restTemplate.postForObject(url, driverDto, DriverDto.class);
    }

    public void updateDriver(Long driverId, DriverDto driverDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDriverEndpoint() + "/" + driverId).build().encode().toUri();
        restTemplate.put(url, driverDto);
    }
}

