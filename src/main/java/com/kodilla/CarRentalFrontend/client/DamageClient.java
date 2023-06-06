package com.kodilla.CarRentalFrontend.client;

import com.kodilla.CarRentalFrontend.config.BackendConfiguration;
import com.kodilla.CarRentalFrontend.domain.DamageDto;
import lombok.RequiredArgsConstructor;
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
public class DamageClient {

    private final RestTemplate restTemplate;
    private final BackendConfiguration backendConfiguration;

    @Autowired
    public DamageClient(RestTemplate restTemplate, BackendConfiguration backendConfiguration) {
        this.restTemplate = restTemplate;
        this.backendConfiguration = backendConfiguration;
    }

    public List<DamageDto> getDamageList() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDamageEndpoint()).build().encode().toUri();
            DamageDto[] response = restTemplate.getForObject(url, DamageDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new DamageDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public DamageDto getDamage(Long damageId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDamageEndpoint() + "/" + damageId).build().encode().toUri();
            return restTemplate.getForObject(url, DamageDto.class);
        } catch (RestClientException e) {
            return new DamageDto();
        }
    }

    public void saveDamage(DamageDto damageDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDamageEndpoint()).build().encode().toUri();
        restTemplate.postForObject(url, damageDto, DamageDto.class);

    }

    public void updateDamage(Long damageId, DamageDto damageDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDamageEndpoint() + "/" + damageId).build().encode().toUri();
        restTemplate.put(url, damageDto);
    }

    public void deleteDamage(Long damageId) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getDamageEndpoint() + "/" + damageId).build().encode().toUri();
        restTemplate.delete(url);
    }
}
