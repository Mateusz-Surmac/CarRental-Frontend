package com.kodilla.CarRentalFrontend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DamageDto {
    private Long id;
    private String description;
    private LocalDate date;
    private double cost;
    private Long carId;
}
