package com.kodilla.CarRentalFrontend.client;

import com.kodilla.CarRentalFrontend.config.BackendConfiguration;
import com.kodilla.CarRentalFrontend.domain.CarClass;
import com.kodilla.CarRentalFrontend.domain.CarDto;
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
public class CarClient {

    private final RestTemplate restTemplate;
    private final BackendConfiguration backendConfiguration;

    @Autowired

    public CarClient(RestTemplate restTemplate, BackendConfiguration backendConfiguration) {
        this.restTemplate = restTemplate;
        this.backendConfiguration = backendConfiguration;
    }

    public List<CarDto> getCars() {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint()).build().encode().toUri();
            CarDto[] response = restTemplate.getForObject(url, CarDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new CarDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }
    public CarDto getCarById(Long carId) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint() + "/" + carId).build().encode().toUri();
            return restTemplate.getForObject(url, CarDto.class);
        } catch (RestClientException e) {
            return new CarDto();
        }
    }

    public List<CarDto> getCarsByModel(String carModel) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint() + "/model")
                    .queryParam("carModel", carModel)
                    .build().encode().toUri();
            CarDto[] response = restTemplate.getForObject(url, CarDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new CarDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public List<CarDto> getCarsByClass(CarClass carClass) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint() + "/class")
                    .queryParam("carClass", carClass.name())
                    .build().encode().toUri();
            CarDto[] response = restTemplate.getForObject(url, CarDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new CarDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public List<CarDto> getCarsBySeatsNumber(int seatsNumber) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint() + "/seats")
                    .queryParam("seatsNumber", seatsNumber)
                    .build().encode().toUri();
            CarDto[] response = restTemplate.getForObject(url, CarDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new CarDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public List<CarDto> getCarsByGearbox(boolean manualGearbox) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint() + "/gearbox")
                    .queryParam("manualGearbox", manualGearbox)
                    .build().encode().toUri();
            CarDto[] response = restTemplate.getForObject(url, CarDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new CarDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public List<CarDto> getCarsByProductionYear(int productionYear) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint() + "/year")
                    .queryParam("productionYear", productionYear)
                    .build().encode().toUri();
            CarDto[] response = restTemplate.getForObject(url, CarDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new CarDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public List<CarDto> getCarsByMileage(Long mileage) {
        try {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint() + "/mileage")
                    .queryParam("mileage", mileage)
                    .build().encode().toUri();
            CarDto[] response = restTemplate.getForObject(url, CarDto[].class);
            return Arrays.asList(ofNullable(response).orElse(new CarDto[0]));
        } catch (RestClientException e) {
            return new ArrayList<>();
        }
    }

    public void saveCar(CarDto carDto) {
            URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint()).build().encode().toUri();
            restTemplate.postForObject(url, carDto, CarDto.class);
    }

    public void updateCar(Long carId, CarDto carDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint() + "/" + carId).build().encode().toUri();
        restTemplate.put(url, carDto);
    }

    public void deleteCar(Long carId) {
        URI url = UriComponentsBuilder.fromHttpUrl(backendConfiguration.getCarEndpoint() + "/" + carId).build().encode().toUri();
        restTemplate.delete(url);
    }

    public List<CarDto> getCarsByFilters(String modelFilter, CarClass classFilter, int  seatsNumberFilter, boolean filterByGearbox, boolean gearboxFilter, int productionYearFilter, long mileageFilter) {
        List<CarDto> filteredCars = new ArrayList<>();
        if (!modelFilter.equals("")) {
           filteredCars =  getCarsByModel(modelFilter);
        }
        if (classFilter != null) {
            filteredCars = getCarsByClass(classFilter);
        }
        if (seatsNumberFilter != 0) {
            filteredCars = getCarsBySeatsNumber(seatsNumberFilter);
        }
        if (filterByGearbox){
            filteredCars = getCarsByGearbox(gearboxFilter);
        }
        if (productionYearFilter != 0) {
            filteredCars = getCarsByProductionYear(productionYearFilter);
        }
        if (mileageFilter != 0) {
            filteredCars = getCarsByMileage(mileageFilter);
        }
        return filteredCars;
    }


}
