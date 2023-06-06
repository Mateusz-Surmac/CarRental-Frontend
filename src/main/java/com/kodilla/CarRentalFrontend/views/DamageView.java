package com.kodilla.CarRentalFrontend.views;

import com.kodilla.CarRentalFrontend.client.DamageClient;
import com.kodilla.CarRentalFrontend.domain.DamageDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Route("damage")
public class DamageView extends VerticalLayout {

    private final DamageClient damageClient;
    private final Grid<DamageDto> damageGrid;
    private final Binder<DamageDto> damageBinder;

    @Autowired
    public DamageView(DamageClient damageClient) {
        this.damageClient = damageClient;

        damageGrid = new Grid<>(DamageDto.class);
        damageGrid.setColumns("id", "description", "date", "cost", "carId");
        damageGrid.asSingleSelect().addValueChangeListener(event -> openUpdateDamageDialog(event.getValue()));

        Button addButton = new Button("Add");
        addButton.addClickListener(event -> openAddDamageDialog());

        damageBinder = new BeanValidationBinder<>(DamageDto.class);

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(event -> refreshGrid());

        add(damageGrid, addButton, refreshButton);
        setSizeFull();
        refreshGrid();
    }

    private void refreshGrid() {
        List<DamageDto> damages = damageClient.getDamageList();
        damageGrid.setItems(damages);
    }

    private void openAddDamageDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField descriptionField = new TextField("Description");
        DatePicker dateField = new DatePicker("Date");
        NumberField costField = new NumberField("Cost");
        TextField carIdField = new TextField("Car ID");

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            try {
                DamageDto newDamage = new DamageDto();
                damageBinder.writeBean(newDamage);
                damageClient.saveDamage(newDamage);
                dialog.close();
                clearFields(descriptionField, dateField, costField, carIdField);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
            dialog.close();
            clearFields(descriptionField, dateField, costField, carIdField);
        });

        damageBinder.bind(descriptionField, DamageDto::getDescription, DamageDto::setDescription);
        damageBinder.bind(dateField, DamageDto::getDate, DamageDto::setDate);
        damageBinder.bind(costField, DamageDto::getCost, DamageDto::setCost);
        damageBinder.forField(carIdField)
                .withConverter(new StringToLongConverter(""))
                .bind(DamageDto::getCarId, DamageDto::setCarId);

        dialogLayout.addFormItem(descriptionField, "Description");
        dialogLayout.addFormItem(dateField, "Date");
        dialogLayout.addFormItem(costField, "Cost");
        dialogLayout.addFormItem(carIdField, "Car ID");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openUpdateDamageDialog(DamageDto damageDto) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField descriptionField = new TextField("Description");
        DatePicker dateField = new DatePicker("Date");
        TextField costField = new TextField("Cost");
        TextField carIdField = new TextField("Car ID");

        try {
            descriptionField.setValue(damageDto.getDescription());
            dateField.setValue(LocalDate.parse(damageDto.getDate().toString()));
            costField.setValue(String.valueOf(damageDto.getCost()));
            carIdField.setValue(String.valueOf(damageDto.getCarId()));
        } catch (RuntimeException ignore) {
            dialog.close();
        }

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            damageDto.setDescription(descriptionField.getValue());
            damageDto.setDate(dateField.getValue());
            damageDto.setCost(Double.parseDouble(costField.getValue()));
            damageDto.setCarId(Long.parseLong(carIdField.getValue()));

            damageClient.updateDamage(damageDto.getId(), damageDto);
            dialog.close();

        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> dialog.close());

        dialogLayout.addFormItem(descriptionField, "Description");
        dialogLayout.addFormItem(dateField, "Date");
        dialogLayout.addFormItem(costField, "Cost");
        dialogLayout.addFormItem(carIdField, "Car ID");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.close();
        dialog.open();
    }

    private void clearFields(TextField descriptionField, DatePicker dateField, NumberField costField, TextField carIdField) {
        descriptionField.clear();
        dateField.clear();
        costField.clear();
        carIdField.clear();
    }
}
