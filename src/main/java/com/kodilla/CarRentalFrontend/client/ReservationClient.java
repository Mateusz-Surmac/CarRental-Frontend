package com.kodilla.CarRentalFrontend.client;

import com.kodilla.CarRentalFrontend.config.BackendConfiguration;
import com.kodilla.CarRentalFrontend.domain.ReservationDto;
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
public class ReservationClient {

    private final RestTemplate restTemplate;
    private final BackendConfiguration backendConfiguration;

    @Autowired
    public ReservationClient(RestTemplate restTemplate, BackendConfiguration backendConfiguration) {
        this.restTemplate = restTemplate;
        this.backendConfiguration = backendConfiguration;
    }

    public List<ReservationDto> getReservations() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getReservationEndpoint()).build().encode().toUri();
            ReservationDto[] response = restTemplate.getForObject(url, ReservationDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new ReservationDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public ReservationDto getReservation(Long reservationId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getReservationEndpoint() + "/" + reservationId).build().encode().toUri();
            return restTemplate.getForObject(url, ReservationDto.class);
        } catch (RestClientException e) {
            return null;
        }
    }

    public List<ReservationDto> getReservationsByClientId(Long clientId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getReservationEndpoint() + "/client/" + clientId).build().encode().toUri();
            ReservationDto[] response = restTemplate.getForObject(url, ReservationDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new ReservationDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public List<ReservationDto> getReservationsByCarId(Long carId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getReservationEndpoint() + "/car/" + carId).build().encode().toUri();
            ReservationDto[] response = restTemplate.getForObject(url, ReservationDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new ReservationDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public void createReservation(ReservationDto reservationDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getReservationEndpoint()).build().encode().toUri();
        restTemplate.postForObject(url, reservationDto, ReservationDto.class);
    }

    public void updateReservation(Long reservationId, ReservationDto reservationDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getReservationEndpoint() + "/" + reservationId).build().encode().toUri();
        restTemplate.put(url, reservationDto);
    }

    public void deleteReservation(Long reservationId) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getReservationEndpoint() + "/" + reservationId).build().encode().toUri();
        restTemplate.delete(url);
    }
}
