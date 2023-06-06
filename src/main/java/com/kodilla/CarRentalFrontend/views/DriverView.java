package com.kodilla.CarRentalFrontend.views;

import com.kodilla.CarRentalFrontend.client.DriverClient;
import com.kodilla.CarRentalFrontend.domain.DriverDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("driver")
public class DriverView extends VerticalLayout {

    private final DriverClient driverClient;
    private final Grid<DriverDto> driverGrid;
    private final Binder<DriverDto> driverBinder;

    @Autowired
    public DriverView(DriverClient driverClient) {
        this.driverClient = driverClient;

        driverGrid = new Grid<>(DriverDto.class);
        driverGrid.setColumns("id", "firstName", "lastName", "companyEmployee");
        driverGrid.asSingleSelect().addValueChangeListener(event -> openUpdateDriverDialog(event.getValue()));

        Button addButton = new Button("Add");
        addButton.addClickListener(event -> openAddDriverDialog());

        driverBinder = new BeanValidationBinder<>(DriverDto.class);

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(event -> refreshGrid());

        Button employeeDriversButton = new Button("Employee Drivers");
        employeeDriversButton.addClickListener(event -> employeeDriversGrid());

        add(driverGrid, addButton, refreshButton, employeeDriversButton);
        setSizeFull();
        refreshGrid();
    }

    private void refreshGrid() {
        List<DriverDto> drivers = driverClient.getDrivers();
        driverGrid.setItems(drivers);
    }
    private void employeeDriversGrid() {
        List<DriverDto> employeeDrivers = driverClient.getEmployeeDriverList();
        driverGrid.setItems(employeeDrivers);
    }

    private void openAddDriverDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField firstNameField = new TextField("First Name");
        TextField lastNameField = new TextField("Last Name");
        Checkbox companyEmployeeCheckbox = new Checkbox("Company Employee");

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            try {
                DriverDto newDriver = new DriverDto();
                driverBinder.writeBean(newDriver);
                driverClient.createDriver(newDriver);
                dialog.close();
                clearFields(firstNameField, lastNameField, companyEmployeeCheckbox);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
            dialog.close();
            clearFields(firstNameField, lastNameField, companyEmployeeCheckbox);
        });

        driverBinder.bind(firstNameField, DriverDto::getFirstName, DriverDto::setFirstName);
        driverBinder.bind(lastNameField, DriverDto::getLastName, DriverDto::setLastName);
        driverBinder.bind(companyEmployeeCheckbox, DriverDto::isCompanyEmployee, DriverDto::setCompanyEmployee);

        dialogLayout.addFormItem(firstNameField, "First Name");
        dialogLayout.addFormItem(lastNameField, "Last Name");
        dialogLayout.addFormItem(companyEmployeeCheckbox, "Company Employee");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openUpdateDriverDialog(DriverDto driverDto) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField firstNameField = new TextField("First Name");
        TextField lastNameField = new TextField("Last Name");
        Checkbox companyEmployeeCheckbox = new Checkbox("Company Employee");

        try {
            firstNameField.setValue(driverDto.getFirstName());
            lastNameField.setValue(driverDto.getLastName());
            companyEmployeeCheckbox.setValue(driverDto.isCompanyEmployee());
        } catch (RuntimeException ignore) {
            dialog.close();
        }

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
                driverDto.setFirstName(firstNameField.getValue());
                driverDto.setLastName(lastNameField.getValue());
                driverDto.setCompanyEmployee(companyEmployeeCheckbox.getValue());

                driverClient.updateDriver(driverDto.getId(), driverDto);
                dialog.close();

        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> dialog.close());

        dialogLayout.addFormItem(firstNameField, "First Name");
        dialogLayout.addFormItem(lastNameField, "Last Name");
        dialogLayout.addFormItem(companyEmployeeCheckbox, "Company Employee");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.close();
        dialog.open();
    }

    private void clearFields(TextField firstNameField, TextField lastNameField, Checkbox companyEmployeeCheckbox) {
        firstNameField.clear();
        lastNameField.clear();
        companyEmployeeCheckbox.setValue(false);
    }
}
