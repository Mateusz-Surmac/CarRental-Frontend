package com.kodilla.CarRentalFrontend.client;

import com.kodilla.CarRentalFrontend.config.BackendConfiguration;
import com.kodilla.CarRentalFrontend.domain.OrderStatus;
import com.kodilla.CarRentalFrontend.domain.RentalOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
public class RentalOrderClient {

    private final RestTemplate restTemplate;
    private final BackendConfiguration backendConfiguration;

    @Autowired
    public RentalOrderClient(RestTemplate restTemplate, BackendConfiguration backendConfiguration) {
        this.restTemplate = restTemplate;
        this.backendConfiguration = backendConfiguration;
    }

    public List<RentalOrderDto> getRentalOrders() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint()).build().encode().toUri();
            RentalOrderDto[] response = restTemplate.getForObject(url, RentalOrderDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new RentalOrderDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public RentalOrderDto getRentalOrder(Long rentalOrderId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint() + "/" + rentalOrderId).build().encode().toUri();
            return restTemplate.getForObject(url, RentalOrderDto.class);
        } catch (RestClientException e) {
            return new RentalOrderDto();
        }
    }

    public List<RentalOrderDto> getRentalOrdersByClientId(Long clientId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint() + "/client/" + clientId).build().encode().toUri();
            RentalOrderDto[] response = restTemplate.getForObject(url, RentalOrderDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new RentalOrderDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public List<RentalOrderDto> getRentalOrdersByCarId(Long carId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint() + "/car/" + carId).build().encode().toUri();
            RentalOrderDto[] response = restTemplate.getForObject(url, RentalOrderDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new RentalOrderDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public List<RentalOrderDto> getRentalOrdersByOrderStatus(OrderStatus orderStatus) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint() + "/status/" + orderStatus).build().encode().toUri();
            RentalOrderDto[] response = restTemplate.getForObject(url, RentalOrderDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new RentalOrderDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public double calculateAmountDueForClient(Long clientId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint() + "/client/" + clientId + "/amountDue").build().encode().toUri();
            Double response = restTemplate.getForObject(url, Double.class);
            if (response != null) {
                return response;
            } else {
                return 0;
            }
        } catch (RestClientException e) {
            return -1;
        }
    }

    public void createRentalOrder(RentalOrderDto rentalOrderDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint()).build().encode().toUri();
        restTemplate.postForObject(url, rentalOrderDto, RentalOrderDto.class);
    }

    public void updateRentalOrder(Long rentalOrderId, RentalOrderDto rentalOrderDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint() + "/" + rentalOrderId).build().encode().toUri();
        restTemplate.put(url, rentalOrderDto);
    }

    public double updateCostPaid(Long rentalOrderId, double amount) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint() + "/" + rentalOrderId + "/costPaid/" + amount).build().encode().toUri();
            ResponseEntity<Double> response = restTemplate.exchange(url, HttpMethod.PUT, null, Double.class);
            if (response.getBody() != null) {
                return response.getBody();
            } else {
                return 0;
            }
        } catch (RestClientException e) {
            return -1;
        }
    }

    public void deleteRentalOrder(Long rentalOrderId) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getRentalOrderEndpoint() + "/" + rentalOrderId).build().encode().toUri();
        restTemplate.delete(url);
    }
}

