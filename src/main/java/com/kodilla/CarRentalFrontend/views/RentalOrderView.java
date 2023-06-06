package com.kodilla.CarRentalFrontend.views;

import com.kodilla.CarRentalFrontend.client.ClientClient;
import com.kodilla.CarRentalFrontend.client.RentalOrderClient;
import com.kodilla.CarRentalFrontend.domain.CarClass;
import com.kodilla.CarRentalFrontend.domain.OrderStatus;
import com.kodilla.CarRentalFrontend.domain.RentalOrderDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("rental-order")
public class RentalOrderView extends VerticalLayout {

    private final RentalOrderClient rentalOrderClient;
    private final Grid<RentalOrderDto> rentalOrderGrid;
    private final Binder<RentalOrderDto> rentalOrderBinder;
    private final ClientClient clientClient;

    @Autowired
    public RentalOrderView(RentalOrderClient rentalOrderClient, ClientClient clientClient) {
        this.rentalOrderClient = rentalOrderClient;
        this.clientClient = clientClient;

        rentalOrderGrid = new Grid<>(RentalOrderDto.class);
        rentalOrderGrid.setColumns("id", "cost", "costPaid", "orderStatus", "fuelLevel", "drivenKilometers", "reservationId", "damageId");
        rentalOrderGrid.asSingleSelect().addValueChangeListener(event -> openUpdateRentalOrderDialog(event.getValue()));

        Button addButton = new Button("Add");
        addButton.addClickListener(event -> openAddRentalOrderDialog());

        Button deleteButton = new Button("Delete selected");
        deleteButton.addClickListener(event -> deleteSelectedOrder());

        rentalOrderBinder = new BeanValidationBinder<>(RentalOrderDto.class);

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(event -> refreshGrid());

        TextField carIdFilterField = new TextField("Car ID");
        Button filterByCarButton = new Button("Filter By Car");
        filterByCarButton.addClickListener(event -> filterByCar(Long.valueOf(carIdFilterField.getValue())));

        TextField clientIdFilterField = new TextField("Client ID");
        Button filterByClientButton = new Button("Filter By Client");
        filterByClientButton.addClickListener(event -> filterByClient(Long.valueOf(clientIdFilterField.getValue())));

        ComboBox<OrderStatus> statusFilterComboBox = new ComboBox<>();
        statusFilterComboBox.setItems(OrderStatus.values());
        Button filterByStatusButton = new Button("Filter By Status");
        filterByStatusButton.addClickListener(event -> filterByOrderStatus(statusFilterComboBox.getValue()));

        HorizontalLayout filtersButtonLayout = new HorizontalLayout();
        filtersButtonLayout.add(carIdFilterField, filterByCarButton, clientIdFilterField, filterByClientButton, statusFilterComboBox, filterByStatusButton);

        TextField updateCostPaidOrderIdField = new TextField("Order ID");
        NumberField updateCostPaidField = new NumberField("Value");
        Button updateCostPaidButton = new Button("Update Cost Paid");
        updateCostPaidButton.addClickListener(event -> updateCostPaid(Long.valueOf(updateCostPaidOrderIdField.getValue()),updateCostPaidField.getValue()));

        TextField amountDueForClientField = new TextField("Client Id");
        Button showAmountDueButton = new Button("Client amount due");
        showAmountDueButton.addClickListener(event -> openAmountDueDialog(Long.valueOf(amountDueForClientField.getValue())));

        HorizontalLayout costOperationsLayout = new HorizontalLayout();
        costOperationsLayout.add(updateCostPaidOrderIdField, updateCostPaidField, updateCostPaidButton, amountDueForClientField, showAmountDueButton);

        add(filtersButtonLayout);
        add(rentalOrderGrid, addButton, refreshButton, deleteButton);
        add(costOperationsLayout);

        setSizeFull();
        refreshGrid();

    }

    private void refreshGrid() {
        List<RentalOrderDto> rentalOrders = rentalOrderClient.getRentalOrders();
        rentalOrderGrid.setItems(rentalOrders);
    }

    private void filterByCar(Long carId) {
        List<RentalOrderDto> ordersList = rentalOrderClient.getRentalOrdersByCarId(carId);
        rentalOrderGrid.setItems(ordersList);
    }

    private void filterByClient(Long clientId) {
        List<RentalOrderDto> ordersList = rentalOrderClient.getRentalOrdersByClientId(clientId);
        rentalOrderGrid.setItems(ordersList);
    }

    private void filterByOrderStatus(OrderStatus orderStatus) {
        List<RentalOrderDto> ordersList = rentalOrderClient.getRentalOrdersByOrderStatus(orderStatus);
        rentalOrderGrid.setItems(ordersList);
    }

    private void updateCostPaid(Long rentalOrderId, double value) {
        rentalOrderClient.updateCostPaid(rentalOrderId, value);
    }

    private void openAmountDueDialog(Long clientId) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        FormLayout dialogLayout = new FormLayout();
        Label label = new Label(getClientName(clientId) + " amount due = " + calculateAmountDue(clientId));
        Button closeButton = new Button("Close");
        closeButton.addClickListener(event -> dialog.close());

        dialogLayout.add(label, closeButton);
        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openAddRentalOrderDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        NumberField fuelLevelField = new NumberField("Fuel Level");
        TextField drivenKilometersField = new TextField("Driven Kilometers");
        TextField reservationIdField = new TextField("Reservation ID");

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            try {
                RentalOrderDto newRentalOrder = new RentalOrderDto();
                rentalOrderBinder.writeBean(newRentalOrder);
                rentalOrderClient.createRentalOrder(newRentalOrder);
                dialog.close();
                clearFields(fuelLevelField, drivenKilometersField, reservationIdField);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
            dialog.close();
            clearFields(fuelLevelField, drivenKilometersField, reservationIdField);
        });

        rentalOrderBinder.bind(fuelLevelField, RentalOrderDto::getFuelLevel, RentalOrderDto::setFuelLevel);
        rentalOrderBinder.bind(drivenKilometersField, rentalOrder -> String.valueOf(rentalOrder.getDrivenKilometers()), (rentalOrder, drivenKilometers) -> rentalOrder.setDrivenKilometers(Long.parseLong(drivenKilometers)));
        rentalOrderBinder.bind(reservationIdField, rentalOrder -> String.valueOf(rentalOrder.getReservationId()), (rentalOrder, reservationId) -> rentalOrder.setReservationId(Long.parseLong(reservationId)));

        dialogLayout.addFormItem(fuelLevelField, "Fuel Level");
        dialogLayout.addFormItem(drivenKilometersField, "Driven Kilometers");
        dialogLayout.addFormItem(reservationIdField, "Reservation ID");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openUpdateRentalOrderDialog(RentalOrderDto rentalOrderDto) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField fuelLevelField = new TextField("Fuel Level");
        TextField drivenKilometersField = new TextField("Driven Kilometers");
        TextField reservationIdField = new TextField("Reservation ID");

        try {
            fuelLevelField.setValue(String.valueOf(rentalOrderDto.getFuelLevel()));
            drivenKilometersField.setValue(String.valueOf(rentalOrderDto.getDrivenKilometers()));
            reservationIdField.setValue(String.valueOf(rentalOrderDto.getReservationId()));
        } catch (NullPointerException e) {
            dialog.close();
        }


        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            rentalOrderDto.setFuelLevel(Double.parseDouble(fuelLevelField.getValue()));
            rentalOrderDto.setDrivenKilometers(Long.parseLong(drivenKilometersField.getValue()));
            rentalOrderDto.setReservationId(Long.parseLong(reservationIdField.getValue()));

            rentalOrderClient.updateRentalOrder(rentalOrderDto.getId(), rentalOrderDto);
            dialog.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> dialog.close());

        dialogLayout.addFormItem(fuelLevelField, "Fuel Level");
        dialogLayout.addFormItem(drivenKilometersField, "Driven Kilometers");
        dialogLayout.addFormItem(reservationIdField, "Reservation ID");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.close();
        dialog.open();
    }

    private void clearFields( NumberField fuelLevelField, TextField drivenKilometersField, TextField reservationIdField) {
        fuelLevelField.clear();
        drivenKilometersField.clear();
        reservationIdField.clear();
    }

    private Double calculateAmountDue(Long clientId) {
        return rentalOrderClient.calculateAmountDueForClient(clientId);
    }

    private String getClientName(Long clientId) {
        return clientClient.getClient(clientId).getFirstName();
    }

    private void deleteSelectedOrder() {
        RentalOrderDto selectedOrder = rentalOrderGrid.asSingleSelect().getValue();
        if (selectedOrder != null) {
            rentalOrderClient.deleteRentalOrder(selectedOrder.getId());
        }
    }
}
