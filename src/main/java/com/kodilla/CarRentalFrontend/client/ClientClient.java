package com.kodilla.CarRentalFrontend.client;

import com.kodilla.CarRentalFrontend.config.BackendConfiguration;
import com.kodilla.CarRentalFrontend.domain.ClientDto;
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
public class ClientClient {

    private final RestTemplate restTemplate;
    private final BackendConfiguration backendConfiguration;

    @Autowired
    public ClientClient(RestTemplate restTemplate, BackendConfiguration backendConfiguration) {
        this.restTemplate = restTemplate;
        this.backendConfiguration = backendConfiguration;
    }

    public List<ClientDto> getClients() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getClientEndpoint()).build().encode().toUri();
            ClientDto[] response = restTemplate.getForObject(url, ClientDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new ClientDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public ClientDto getClient(Long clientId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getClientEndpoint() + "/" + clientId).build().encode().toUri();
            return restTemplate.getForObject(url, ClientDto.class);
        } catch (RestClientException e) {
            return new ClientDto();
        }
    }

    public List<ClientDto> getVipStatusClients() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getClientEndpoint() + "/vipClients").build().encode().toUri();
            ClientDto[] response = restTemplate.getForObject(url, ClientDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new ClientDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public void createClient(ClientDto clientDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getClientEndpoint()).build().encode().toUri();
        restTemplate.postForObject(url, clientDto, ClientDto.class);
    }

    public void updateClientVipStatus(Long clientId) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getClientEndpoint() + "/updateVipStatus/" + clientId).build().encode().toUri();
        restTemplate.put(url, null);

    }

    public void updateClient(Long clientId, ClientDto clientDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getClientEndpoint() + "/" + clientId).build().encode().toUri();
        restTemplate.put(url, clientDto);
    }

    public void deleteClient(Long clientId) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getClientEndpoint() + "/" + clientId).build().encode().toUri();
        restTemplate.delete(url);

    }
}
