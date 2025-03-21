package com.example.secret_santa.ui.view;

import com.example.secret_santa.ui.tool.ButtonInitializer;
import com.example.secret_santa.ui.tool.MenuInitializer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("welcome")
public class WelcomeView extends VerticalLayout {


    private final MenuInitializer menuInitializer;
    private final ButtonInitializer buttonInitializer;

    public WelcomeView(@Autowired MenuInitializer menuInitializer, @Autowired ButtonInitializer buttonInitializer) {
        this.menuInitializer = menuInitializer;
        this.buttonInitializer = buttonInitializer;

        HorizontalLayout menu = menuInitializer.createHorizontalMenu(List.of(
                buttonInitializer.createNavButton("New list", this, NewListView.class,"300px"),
                //                buttonInitializer.createActButton("New list", () -> getUI().ifPresent(ui -> ui.navigate("new-list")), "200px"),
                buttonInitializer.createActButton("View existing", () -> getUI().ifPresent(ui -> ui.navigate("")), "200px")));
//        var todosList = new VerticalLayout();
//        var taskField = new TextField();
//
//        var addButton = new Button("Add");
//
//        addButton.addClickListener(click -> {
//            Checkbox checkbox = new Checkbox(taskField.getValue());
//            todosList.add(checkbox);
//        });
//        addButton.addClickShortcut(Key.ENTER);

        add(
                new H1("Welcome to SecretSantaApp!"),
                menu);
    }
}
