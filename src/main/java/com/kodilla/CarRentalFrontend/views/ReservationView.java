package com.kodilla.CarRentalFrontend.views;

import com.kodilla.CarRentalFrontend.client.ReservationClient;
import com.kodilla.CarRentalFrontend.domain.ReservationDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("reservation")
public class ReservationView extends VerticalLayout {

    private final ReservationClient reservationClient;
    private final Grid<ReservationDto> reservationGrid;
    private final Binder<ReservationDto> reservationBinder;

    @Autowired
    public ReservationView(ReservationClient reservationClient) {
        this.reservationClient = reservationClient;

        reservationGrid = new Grid<>(ReservationDto.class);
        reservationGrid.setColumns("id", "rentalStart", "rentalEnd", "rentalPlace", "returnPlace", "price", "carId", "driverId", "clientId");
        reservationGrid.asSingleSelect().addValueChangeListener(event -> openUpdateReservationDialog(event.getValue()));

        Button addButton = new Button("Add");
        addButton.addClickListener(event -> openAddReservationDialog());

        Button deleteButton = new Button("Delete selected");
        deleteButton.addClickListener(event -> deleteSelectedReservation());

        reservationBinder = new BeanValidationBinder<>(ReservationDto.class);

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(event -> refreshGrid());

        TextField carIdFilterField = new TextField("Car ID");
        Button filterByCarButton = new Button("Filter By Car");
        filterByCarButton.addClickListener(event -> filterByCar(Long.valueOf(carIdFilterField.getValue())));

        TextField clientIdFilterField = new TextField("Client ID");
        Button filterByClientButton = new Button("Filter By Client");
        filterByClientButton.addClickListener(event -> filterByClient(Long.valueOf(clientIdFilterField.getValue())));

        HorizontalLayout filtersButtonLayout = new HorizontalLayout();
        filtersButtonLayout.add(carIdFilterField, filterByCarButton, clientIdFilterField, filterByClientButton);


        add(filtersButtonLayout);
        add(reservationGrid, addButton, refreshButton, deleteButton);

        setSizeFull();
        refreshGrid();
    }

    private void refreshGrid() {
        List<ReservationDto> reservations = reservationClient.getReservations();
        reservationGrid.setItems(reservations);
    }

    private void filterByCar(Long carId) {
        List<ReservationDto> reservationList = reservationClient.getReservationsByCarId(carId);
        reservationGrid.setItems(reservationList);
    }

    private void filterByClient(Long clientId) {
        List<ReservationDto> reservationList = reservationClient.getReservationsByClientId(clientId);
        reservationGrid.setItems(reservationList);
    }

    private void openAddReservationDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField rentalPlaceField = new TextField("Rental Place");
        TextField returnPlaceField = new TextField("Return Place");
        TextField carIdField = new TextField("Car ID");
        TextField driverIdField = new TextField("Driver ID");
        TextField clientIdField = new TextField("Client ID");
        DatePicker rentalStartDatePicker = new DatePicker("Rental Start");
        DatePicker rentalEndDatePicker = new DatePicker("Rental End");

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            try {
                ReservationDto newReservation = new ReservationDto();
                reservationBinder.writeBean(newReservation);
                reservationClient.createReservation(newReservation);
                dialog.close();
                clearFields(rentalPlaceField, returnPlaceField, carIdField, driverIdField, clientIdField, rentalStartDatePicker, rentalEndDatePicker);
                refreshGrid();
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
            dialog.close();
            clearFields(rentalPlaceField, returnPlaceField, carIdField, driverIdField, clientIdField, rentalStartDatePicker, rentalEndDatePicker);
        });

        reservationBinder.bind(rentalPlaceField, ReservationDto::getRentalPlace, ReservationDto::setRentalPlace);
        reservationBinder.bind(returnPlaceField, ReservationDto::getReturnPlace, ReservationDto::setReturnPlace);
        reservationBinder.forField(carIdField)
                .withConverter(new StringToLongConverter(""))
                .bind(ReservationDto::getCarId, ReservationDto::setCarId);

        reservationBinder.forField(driverIdField)
                .withConverter(new StringToLongConverter(""))
                .bind(ReservationDto::getDriverId, ReservationDto::setDriverId);

        reservationBinder.forField(clientIdField)
                .withConverter(new StringToLongConverter(""))
                .bind(ReservationDto::getClientId, ReservationDto::setClientId);
        reservationBinder.bind(rentalStartDatePicker, ReservationDto::getRentalStart, ReservationDto::setRentalStart);
        reservationBinder.bind(rentalEndDatePicker, ReservationDto::getRentalEnd, ReservationDto::setRentalEnd);


        dialogLayout.addFormItem(rentalPlaceField, "Rental Place");
        dialogLayout.addFormItem(returnPlaceField, "Return Place");
        dialogLayout.addFormItem(carIdField, "Car ID");
        dialogLayout.addFormItem(driverIdField, "Driver ID");
        dialogLayout.addFormItem(clientIdField, "Client ID");
        dialogLayout.addFormItem(rentalStartDatePicker, "Rental Start");
        dialogLayout.addFormItem(rentalEndDatePicker, "Rental End");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.open();

    }

    private void openUpdateReservationDialog(ReservationDto reservationDto) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField rentalPlaceField = new TextField("Rental Place");
        TextField returnPlaceField = new TextField("Return Place");
        IntegerField carIdField = new IntegerField("Car ID");
        IntegerField driverIdField = new IntegerField("Driver ID");
        IntegerField clientIdField = new IntegerField("Client ID");
        DatePicker rentalStartDatePicker = new DatePicker("Rental Start");
        DatePicker rentalEndDatePicker = new DatePicker("Rental End");

        try {
            rentalPlaceField.setValue(reservationDto.getRentalPlace());
            returnPlaceField.setValue(reservationDto.getReturnPlace());
            carIdField.setValue(Math.toIntExact(reservationDto.getCarId()));
            driverIdField.setValue(Math.toIntExact(reservationDto.getDriverId()));
            clientIdField.setValue(Math.toIntExact(reservationDto.getClientId()));
            rentalStartDatePicker.setValue(reservationDto.getRentalStart());
            rentalEndDatePicker.setValue(reservationDto.getRentalEnd());
        } catch (NullPointerException ignore){
            dialog.close();
        }

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            reservationDto.setRentalPlace(rentalPlaceField.getValue());
            reservationDto.setReturnPlace(returnPlaceField.getValue());
            reservationDto.setCarId(Long.valueOf(carIdField.getValue()));
            reservationDto.setDriverId(Long.valueOf(driverIdField.getValue()));
            reservationDto.setClientId(Long.valueOf(clientIdField.getValue()));
            reservationDto.setRentalStart(rentalStartDatePicker.getValue());
            reservationDto.setRentalEnd(rentalEndDatePicker.getValue());

            reservationClient.updateReservation(reservationDto.getId(), reservationDto);
            dialog.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> dialog.close());

        dialogLayout.addFormItem(rentalPlaceField, "Rental Place");
        dialogLayout.addFormItem(returnPlaceField, "Return Place");
        dialogLayout.addFormItem(carIdField, "Car ID");
        dialogLayout.addFormItem(driverIdField, "Driver ID");
        dialogLayout.addFormItem(clientIdField, "Client ID");
        dialogLayout.addFormItem(rentalStartDatePicker, "Rental Start");
        dialogLayout.addFormItem(rentalEndDatePicker, "Rental End");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void clearFields(TextField rentalPlaceField, TextField returnPlaceField, TextField carIdField, TextField driverIdField, TextField clientIdField, DatePicker rentalStartDatePicker, DatePicker rentalEndDatePicker) {
        rentalPlaceField.clear();
        returnPlaceField.clear();
        carIdField.clear();
        driverIdField.clear();
        clientIdField.clear();
        rentalStartDatePicker.clear();
        rentalEndDatePicker.clear();
    }

    private void deleteSelectedReservation() {
        ReservationDto selectedReservation = reservationGrid.asSingleSelect().getValue();
        if (selectedReservation != null) {
            reservationClient.deleteReservation(selectedReservation.getId());
        }
    }
}
