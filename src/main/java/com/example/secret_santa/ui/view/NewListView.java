package com.example.secret_santa.ui.view;

import com.example.secret_santa.dto.Person;
import com.example.secret_santa.dto.SantasList;
import com.example.secret_santa.repository.DBMock;
import com.example.secret_santa.ui.tool.ButtonInitializer;
import com.example.secret_santa.ui.tool.MenuInitializer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Route("new-list")
public class NewListView extends VerticalLayout {

    Grid<Person> grid = new Grid<>(Person.class);
    List<Person> people = new ArrayList<>();

    public NewListView(@Autowired MenuInitializer menuInitializer, @Autowired ButtonInitializer buttonInitializer) {
        //TODO: DELETE - FOR PROD
        people = DBMock.LISTS.get(0).getPeople();
        grid.setItems(people);
        //

        setAlignItems(Alignment.CENTER);


        HorizontalLayout textLayout = new HorizontalLayout();
        TextField textField = new TextField("Enter the name of the List.", " Eq. Christmas-Calgary 2024");
        //TODO: DELETE - FOR PROD
        textField.setValue("TEST_LIST");
        //

        textField.setWidth("300px");
        textLayout.add(
                textField
        );

        add(
                new H1("Create a new list"),
                textLayout);

        VerticalLayout newEntryLayout = new VerticalLayout();
        newEntryLayout.setAlignItems(Alignment.CENTER);
        TextField nameField = new TextField("Name of person you want to add", "Eq. Daniel");
        nameField.setWidth("600px");
        TextField emailField = new TextField("Email address of person you want to add", "Eq. Dan@seznam.cz");
        emailField.setWidth("600px");

        Button addPerson = buttonInitializer.createActButton("Add", () -> {
            String name = nameField.getValue();
            String email = emailField.getValue();
            people.add(new Person(name, email));
            grid.setItems(people);
        }, "300px");

        Button removePerson = buttonInitializer.createActButton("Remove selected", () -> {
            Set<Person> selectedPeople = grid.getSelectedItems();
            selectedPeople.forEach(
                    i -> {
                        people.remove(i);
                    }
            );
            grid.setItems(people);
        }, "300px");

        newEntryLayout.add(nameField, emailField, addPerson, removePerson);

        VerticalLayout gridLayout = new VerticalLayout();
        gridLayout.setAlignItems(Alignment.AUTO);
        grid.setColumns("name", "email");
        grid.setWidth("800px");

        gridLayout.add(grid);
        add(gridLayout);

        HorizontalLayout listLayout = new HorizontalLayout(gridLayout, newEntryLayout);
        add(listLayout);

        Button saveButton = buttonInitializer.createActButton("Save List", () -> {
            String nameFieldValue = nameField.getValue();

            if (DBMock.LISTS.stream()
                    .map(SantasList::getName)
                    .noneMatch(i -> i.equals(nameFieldValue))) {
                SantasList santasList = new SantasList(nameFieldValue);
                santasList.setPeople(people);
                DBMock.LISTS.add(santasList);
            } else {
                System.out.println("Error: List with this name already exists.");
            }
            System.out.println(Arrays.toString(DBMock.LISTS.toArray()));
        }, "300px");
        saveButton.setHeight("200px");

        Button backButton = buttonInitializer.createNavButton("Back", this, WelcomeView.class, "300px");
        backButton.setHeight("200px");
        HorizontalLayout navLayout = new HorizontalLayout(
                saveButton,
                backButton
        );

        newEntryLayout.add(navLayout);


    }
}
