package com.example.demo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.LinkedHashMap;
import java.util.Map;


public class PainelVariaveis extends GridPane {

    private final Map<String, Label> valores = new LinkedHashMap<>();

    public PainelVariaveis() {
        setHgap(15);
        setVgap(6);
        setPadding(new Insets(10));
    }

    public void limpar() {
        Platform.runLater(() -> {
            getChildren().clear();
            valores.clear();
        });
    }


    public void atualizar(String nome, String valor) {
        Platform.runLater(() -> {
            Label labelValor = valores.get(nome);
            if (labelValor == null) {
                int linha = valores.size();

                Label labelNome = new Label(nome + ":");
                labelNome.setFont(Font.font("Arial", FontWeight.BOLD, 14));

                labelValor = new Label(valor);
                labelValor.setFont(Font.font(14));

                add(labelNome, 0, linha);
                add(labelValor, 1, linha);
                valores.put(nome, labelValor);
            } else {
                labelValor.setText(valor);
            }
        });
    }
}
