package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class HelloController {

    @FXML
    private TextField campoNumero;

    @FXML
    private ListView<Integer> listaNumeros;

    @FXML
    private Label labelStatus;

    private final int[] vetor = new int[10];
    private int quantidade = 0; // controla quantas posições já foram preenchidas

    private final ObservableList<Integer> itensExibidos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        listaNumeros.setItems(itensExibidos);
    }

    @FXML
    private void adicionarNumero() {
        if (quantidade >= vetor.length) {
            labelStatus.setText("Vetor cheio! Máximo de 10 números.");
            return;
        }

        String texto = campoNumero.getText().trim();
        if (texto.isEmpty()) {
            labelStatus.setText("Digite um número antes de adicionar.");
            return;
        }

        try {
            int numero = Integer.parseInt(texto);
            vetor[quantidade] = numero;
            quantidade++;

            itensExibidos.add(numero);
            campoNumero.clear();
            labelStatus.setText("Adicionado! (" + quantidade + "/10)");
        } catch (NumberFormatException e) {
            labelStatus.setText("Isso não é um número válido.");
        }
    }
}