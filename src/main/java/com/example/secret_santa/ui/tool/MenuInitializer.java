package com.example.secret_santa.ui.tool;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class MenuInitializer {

    public HorizontalLayout createHorizontalMenu(List<Button> buttons) {
        HorizontalLayout menu = new HorizontalLayout();

        buttons.forEach(menu::add);

        menu.setAlignItems(FlexComponent.Alignment.CENTER);

        return menu;
    }

    public VerticalLayout createVerticalMenu(List<Component> buttons) {
        VerticalLayout menu = new VerticalLayout();

        buttons.forEach(menu::add);

        menu.setAlignItems(FlexComponent.Alignment.CENTER);

        return menu;
    }


}
