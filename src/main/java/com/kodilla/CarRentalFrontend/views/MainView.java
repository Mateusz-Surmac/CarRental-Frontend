package com.kodilla.CarRentalFrontend.views;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;


@Route(value = "mainView")
public class MainView extends VerticalLayout implements RouterLayout {
    public MainView() {
        RouterLink carViewLink = new RouterLink("Car View", CarView.class);
        RouterLink clientViewLink = new RouterLink("Client View", ClientView.class);
        RouterLink driverViewLink = new RouterLink("Driver View", DriverView.class);
        RouterLink damageViewLink = new RouterLink("Damage View",DamageView.class );
        RouterLink reservationViewLink = new RouterLink("Reservation View", ReservationView.class);
        RouterLink rentalOrderViewLink = new RouterLink("Rental Order View", RentalOrderView.class);

        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.add(new H3("Select View"));
        linksLayout.add(carViewLink, clientViewLink, damageViewLink, driverViewLink, reservationViewLink, rentalOrderViewLink);

        add(linksLayout);
    }

}
