package com.kodilla.CarRentalFrontend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {
    public Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean vipStatus;
}
