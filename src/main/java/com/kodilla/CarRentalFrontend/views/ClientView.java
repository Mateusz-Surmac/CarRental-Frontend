package com.kodilla.CarRentalFrontend.views;

import com.kodilla.CarRentalFrontend.client.ClientClient;
import com.kodilla.CarRentalFrontend.domain.ClientDto;
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

@Route("client")
public class ClientView extends VerticalLayout {

    private final ClientClient clientClient;
    private final Grid<ClientDto> clientGrid;
    private final Binder<ClientDto> clientBinder;

    @Autowired
    public ClientView(ClientClient clientClient) {
        this.clientClient = clientClient;

        clientGrid = new Grid<>(ClientDto.class);
        clientGrid.setColumns("id", "firstName", "lastName", "email", "vipStatus");
        clientGrid.asSingleSelect().addValueChangeListener(event -> openUpdateClientDialog(event.getValue()));

        Button addButton = new Button("Add");
        addButton.addClickListener(event -> openAddClientDialog());

        Button deleteButton = new Button("Delete selected");
        deleteButton.addClickListener(event -> deleteSelectedClient());

        clientBinder = new BeanValidationBinder<>(ClientDto.class);

        Button refreshButton = new Button("Refresh");
        refreshButton.addClickListener(event -> refreshGrid());

        Button vipStatusClientsButton = new Button("Vip Clients");
        vipStatusClientsButton.addClickListener(event -> vipStatusClientsGrid());

        add(clientGrid, addButton, refreshButton, vipStatusClientsButton, deleteButton);
        setSizeFull();
        refreshGrid();
    }

    private void refreshGrid() {
        List<ClientDto> clients = clientClient.getClients();
        clientGrid.setItems(clients);
    }

    private void vipStatusClientsGrid() {
        List<ClientDto> clients = clientClient.getVipStatusClients();
        clientGrid.setItems(clients);
    }

    private void openAddClientDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField firstNameField = new TextField("First Name");
        TextField lastNameField = new TextField("Last Name");
        TextField emailField = new TextField("Email");
        Checkbox vipStatusCheckbox = new Checkbox("Vip Status");

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            try {
                ClientDto newClient = new ClientDto();
                clientBinder.writeBean(newClient);
                clientClient.createClient(newClient);
                dialog.close();
                clearFields(firstNameField, lastNameField, emailField, vipStatusCheckbox);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
            dialog.close();
            clearFields(firstNameField, lastNameField, emailField, vipStatusCheckbox);
        });

        clientBinder.bind(firstNameField, ClientDto::getFirstName, ClientDto::setFirstName);
        clientBinder.bind(lastNameField, ClientDto::getLastName, ClientDto::setLastName);
        clientBinder.bind(emailField, ClientDto::getFirstName, ClientDto::setEmail);
        clientBinder.bind(vipStatusCheckbox, ClientDto::isVipStatus, ClientDto::setVipStatus);

        dialogLayout.addFormItem(firstNameField, "First Name");
        dialogLayout.addFormItem(lastNameField, "Last Name");
        dialogLayout.addFormItem(emailField, "Email");
        dialogLayout.addFormItem(vipStatusCheckbox, "Vip Status");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.open();

    }

    private void openUpdateClientDialog(ClientDto clientDto) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        FormLayout dialogLayout = new FormLayout();

        TextField firstNameField = new TextField("First Name");
        TextField lastNameField = new TextField("Last Name");
        TextField emailField = new TextField("Email");
        Checkbox vipStatusCheckbox = new Checkbox("Vip Status");

        try {
            firstNameField.setValue(clientDto.getFirstName());
            lastNameField.setValue(clientDto.getLastName());
            emailField.setValue(clientDto.getEmail());
            vipStatusCheckbox.setValue(clientDto.isVipStatus());
        } catch (RuntimeException e) {
            dialog.close();
        }

        Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
            clientDto.setFirstName(firstNameField.getValue());
            clientDto.setLastName(lastNameField.getValue());
            clientDto.setEmail(emailField.getValue());
            clientDto.setVipStatus(vipStatusCheckbox.getValue());

            clientClient.updateClient(clientDto.getId(),clientDto);
            dialog.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> dialog.close());

        dialogLayout.addFormItem(firstNameField, "First Name");
        dialogLayout.addFormItem(lastNameField, "Last Name");
        dialogLayout.addFormItem(emailField, "Email");
        dialogLayout.addFormItem(vipStatusCheckbox, "Vip Status");
        dialogLayout.add(saveButton, cancelButton);

        dialog.add(dialogLayout);
        dialog.close();
        dialog.open();
    }

    private void clearFields(TextField fistName, TextField lastName, TextField email, Checkbox vipStatus) {
        fistName.clear();
        lastName.clear();
        email.clear();
        vipStatus.setValue(false);
    }

    private void deleteSelectedClient() {
        ClientDto selectedClient = clientGrid.asSingleSelect().getValue();
        if (selectedClient != null) {
            clientClient.deleteClient(selectedClient.getId());
            refreshGrid();
        }
    }
}
