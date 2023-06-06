package com.kodilla.CarRentalFrontend.views;

import com.kodilla.CarRentalFrontend.client.CarClient;
import com.kodilla.CarRentalFrontend.domain.CarClass;
import com.kodilla.CarRentalFrontend.domain.CarDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("car")
public class CarView extends VerticalLayout {

    private final CarClient carClient;
    private final Grid<CarDto> carGrid;
    private final Binder<CarDto> carBinder;
    private final TextField modelFilterField;
    private final ComboBox<CarClass> classFilterField;
    private final IntegerField seatsNumberFilterField;
    private final Checkbox gearboxFilterCheckbox;
    private final Checkbox filterByGearboxCheckbox;
    private final IntegerField productionYearFilterField;
    private final IntegerField mileageFilterField;

    @Autowired
    public CarView(CarClient carClient) {
        this.carClient = carClient;

        carGrid = new Grid<>(CarDto.class);
        carGrid.setColumns("id", "brand", "model", "engineCapacity", "carClass", "seatsNumber", "manualGearbox", "productionYear", "mileage", "damaged");
        carGrid.asSingleSelect().addValueChangeListener(event -> openUpdateCarDialog(event.getValue()));

        Button addButton = new Button("Add");
        addButton.addClickListener(event -> openAddCarDialog());

        Button deleteButton = new Button("Delete selected");
        deleteButton.addClickListener(event -> deleteSelectedCar());

        carBinder = new BeanValidationBinder<>(CarDto.class);

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(event -> refreshGrid());

        modelFilterField = new TextField("Model");
        classFilterField = new ComboBox<>("Car Class");
        classFilterField.setItems(CarClass.values());
        seatsNumberFilterField = new IntegerField("Seats Number");
        seatsNumberFilterField.setPlaceholder("more than");
        filterByGearboxCheckbox = new Checkbox("Filter by Gearbox");
        gearboxFilterCheckbox = new Checkbox("Manual Gearbox");
        productionYearFilterField = new IntegerField("Production Year");
        productionYearFilterField.setPlaceholder("younger than");
        mileageFilterField = new IntegerField("Mileage");
        mileageFilterField.setPlaceholder("less than");

        Button filterButton = new Button("Filter");
        filterButton.addClickListener(event -> filterCars());


        HorizontalLayout filtersButtonLayout = new HorizontalLayout();
        filtersButtonLayout.add(modelFilterField, classFilterField, seatsNumberFilterField,filterByGearboxCheckbox, gearboxFilterCheckbox, productionYearFilterField, mileageFilterField, filterButton, carGrid);

        add(filtersButtonLayout);
        add(carGrid, addButton, refreshButton, deleteButton);


        setSizeUndefined();
        refreshGrid();
    }

    private void refreshGrid() {
        List<CarDto> cars = carClient.getCars();
        carGrid.setItems(cars);
    }

    private void openAddCarDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField brandField = new TextField("Brand");
        TextField modelField = new TextField("Model");
        TextField engineCapacityField = new TextField("Engine Capacity");
        ComboBox<CarClass> carClassField = new ComboBox<>("Car Class");
        TextField seatsNumberField = new TextField("Seats Number");
        Checkbox manualGearboxCheckbox = new Checkbox("Manual Gearbox");
        TextField productionYearField = new TextField("Production Year");
        TextField mileageField = new TextField("Mileage");
        Checkbox damagedCheckbox = new Checkbox("Damaged");

        carClassField.setItems(CarClass.values());

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            CarDto newCar = new CarDto();
            newCar.setBrand(brandField.getValue());
            newCar.setModel(modelField.getValue());
            newCar.setEngineCapacity(Double.parseDouble(engineCapacityField.getValue()));
            newCar.setCarClass(carClassField.getValue());
            newCar.setSeatsNumber(Integer.parseInt(seatsNumberField.getValue()));
            newCar.setManualGearbox(manualGearboxCheckbox.getValue());
            newCar.setProductionYear(Integer.parseInt(productionYearField.getValue()));
            newCar.setMileage(Long.parseLong(mileageField.getValue()));
            newCar.setDamaged(damagedCheckbox.getValue());

            carClient.saveCar(newCar);
            dialog.close();
            clearFields(brandField, modelField, engineCapacityField, carClassField, seatsNumberField,
                    manualGearboxCheckbox, productionYearField, mileageField, damagedCheckbox);
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
            dialog.close();
            clearFields(brandField, modelField, engineCapacityField, carClassField, seatsNumberField,
                    manualGearboxCheckbox, productionYearField, mileageField, damagedCheckbox);
        });

        carBinder.bind(brandField, CarDto::getBrand, CarDto::setBrand);
        carBinder.bind(modelField, CarDto::getModel, CarDto::setModel);
        carBinder.bind(engineCapacityField, car -> String.valueOf(car.getEngineCapacity()), (car, engineCapacity) -> car.setEngineCapacity(Double.parseDouble(engineCapacity)));
        carBinder.bind(carClassField, CarDto::getCarClass, CarDto::setCarClass);
        carBinder.bind(seatsNumberField, car -> String.valueOf(car.getSeatsNumber()), (car, seatsNumber) -> car.setSeatsNumber(Integer.parseInt(seatsNumber)));
        carBinder.bind(manualGearboxCheckbox, CarDto::isManualGearbox, CarDto::setManualGearbox);
        carBinder.bind(productionYearField, car -> String.valueOf(car.getProductionYear()), (car, productionYear) -> car.setProductionYear(Integer.parseInt(productionYear)));
        carBinder.bind(mileageField, car -> String.valueOf(car.getMileage()), (car, mileage) -> car.setMileage(Long.parseLong(mileage)));
        carBinder.bind(damagedCheckbox, CarDto::isDamaged, CarDto::setDamaged);

        dialogLayout.addFormItem(brandField, "Brand");
        dialogLayout.addFormItem(modelField, "Model");
        dialogLayout.addFormItem(engineCapacityField, "Engine Capacity");
        dialogLayout.addFormItem(carClassField, "Car Class");
        dialogLayout.addFormItem(seatsNumberField, "Seats Number");
        dialogLayout.addFormItem(manualGearboxCheckbox, "Manual Gearbox");
        dialogLayout.addFormItem(productionYearField, "Production Year");
        dialogLayout.addFormItem(mileageField, "Mileage");
        dialogLayout.addFormItem(damagedCheckbox, "Damaged");

        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void openUpdateCarDialog(CarDto carDto) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField brandField = new TextField("Brand");
        TextField modelField = new TextField("Model");
        TextField engineCapacityField = new TextField("Engine Capacity");
        ComboBox<CarClass> carClassField = new ComboBox<>("Car Class");
        TextField seatsNumberField = new TextField("Seats Number");
        Checkbox manualGearboxCheckbox = new Checkbox("Manual Gearbox");
        TextField productionYearField = new TextField("Production Year");
        TextField mileageField = new TextField("Mileage");
        Checkbox damagedCheckbox = new Checkbox("Damaged");

        carClassField.setItems(CarClass.values());

        try {
            brandField.setValue(carDto.getBrand());
            modelField.setValue(carDto.getModel());
            engineCapacityField.setValue(String.valueOf(carDto.getEngineCapacity()));
            carClassField.setItems(CarClass.values());
            carClassField.setValue(carDto.getCarClass());
            seatsNumberField.setValue(String.valueOf(carDto.getSeatsNumber()));
            manualGearboxCheckbox.setValue(carDto.isManualGearbox());
            productionYearField.setValue(String.valueOf(carDto.getProductionYear()));
            mileageField.setValue(String.valueOf(carDto.getMileage()));
            damagedCheckbox.setValue(carDto.isDamaged());
        } catch (RuntimeException e) {
            dialog.close();
        }

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            carDto.setBrand(brandField.getValue());
            carDto.setModel(modelField.getValue());
            carDto.setEngineCapacity(Double.parseDouble(engineCapacityField.getValue()));
            carDto.setCarClass(carClassField.getValue());
            carDto.setSeatsNumber(Integer.parseInt(seatsNumberField.getValue()));
            carDto.setManualGearbox(manualGearboxCheckbox.getValue());
            carDto.setProductionYear(Integer.parseInt(productionYearField.getValue()));
            carDto.setMileage(Long.parseLong(mileageField.getValue()));
            carDto.setDamaged(damagedCheckbox.getValue());

            carClient.updateCar(carDto.getId(), carDto);
            dialog.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> dialog.close());

        dialogLayout.addFormItem(brandField, "Brand");
        dialogLayout.addFormItem(modelField, "Model");
        dialogLayout.addFormItem(engineCapacityField, "Engine Capacity");
        dialogLayout.addFormItem(carClassField, "Car Class");
        dialogLayout.addFormItem(seatsNumberField, "Seats Number");
        dialogLayout.addFormItem(manualGearboxCheckbox, "Manual Gearbox");
        dialogLayout.addFormItem(productionYearField, "Production Year");
        dialogLayout.addFormItem(mileageField, "Mileage");
        dialogLayout.addFormItem(damagedCheckbox, "Damaged");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.open();
    }


    private void clearFields(TextField brand, TextField model, TextField engineCapacity, ComboBox<CarClass> carClass,
                             TextField seatsNumber, Checkbox manualGearbox, TextField productionYear,
                             TextField mileage, Checkbox damaged) {
        brand.clear();
        model.clear();
        engineCapacity.clear();
        carClass.clear();
        seatsNumber.clear();
        manualGearbox.setValue(false);
        productionYear.clear();
        mileage.clear();
        damaged.setValue(false);
    }

    private void deleteSelectedCar() {
        CarDto selectedCar = carGrid.asSingleSelect().getValue();
        if (selectedCar != null) {
            carClient.deleteCar(selectedCar.getId());
        }
    }

    private void filterCars() {
        refreshGrid();
        String modelFilter = modelFilterField.getValue();
        CarClass classFilter = classFilterField.getValue();

        int seatsNumberFilter;
        if (seatsNumberFilterField.getValue() == null) {
            seatsNumberFilter = 0;
        } else {
            seatsNumberFilter = seatsNumberFilterField.getValue();
        }
        boolean filterByGearbox = filterByGearboxCheckbox.getValue();
        boolean gearboxFilter = gearboxFilterCheckbox.getValue();

        int productionYearFilter;
        if (productionYearFilterField.getValue() == null) {
            productionYearFilter = 0;
        } else {
            productionYearFilter = productionYearFilterField.getValue();
        }

        long mileageFilter;
        if (mileageFilterField.getValue() == null) {
            mileageFilter = 0;
        } else {
            mileageFilter= mileageFilterField.getValue().longValue();
        }

        List<CarDto> filteredCars = carClient.getCarsByFilters(modelFilter, classFilter, seatsNumberFilter, filterByGearbox, gearboxFilter, productionYearFilter, mileageFilter);
        carGrid.setItems(filteredCars);
    }

}

