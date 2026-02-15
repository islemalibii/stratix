package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MyController {

    @FXML
    private Label label;

    @FXML
    public void initialize() {
        System.out.println("Controller initialisé !");
    }
}