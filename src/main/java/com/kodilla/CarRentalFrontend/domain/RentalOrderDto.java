package com.kodilla.CarRentalFrontend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalOrderDto {
    private Long id;
    private double cost;
    private double costPaid;
    private OrderStatus orderStatus;
    private double fuelLevel;
    private Long drivenKilometers;
    private Long reservationId;
    private Long damageId;
}
