package com.example.demo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class PainelCodigo extends VBox {

    private static final Color COR_FUNDO_DESTAQUE = Color.web("#0a3d0a"); // verde escuro
    private static final Color COR_TEXTO_DESTAQUE = Color.web("#7CFC00"); // verde claro

    private Label[] linhasLabel;
    private int linhaAtual = -1;

    public PainelCodigo() {
        setStyle("-fx-background-color: black;");
        setSpacing(2);
        setPadding(new Insets(10));
        setFillWidth(true);
    }

    /** Substitui o código exibido pelo array de linhas informado. */
    public void carregarCodigo(String[] linhas) {
        Platform.runLater(() -> {
            getChildren().clear();
            linhasLabel = new Label[linhas.length];
            for (int i = 0; i < linhas.length; i++) {
                Label l = new Label(linhas[i]);
                l.setTextFill(Color.WHITE);
                l.setFont(Font.font("Consolas", 14));
                l.setMaxWidth(Double.MAX_VALUE);
                linhasLabel[i] = l;
                getChildren().add(l);
            }
            linhaAtual = -1;
        });
    }

    public void destacar(int indice) {
        Platform.runLater(() -> {
            if (linhasLabel == null) return;

            if (linhaAtual >= 0 && linhaAtual < linhasLabel.length) {
                linhasLabel[linhaAtual].setTextFill(Color.WHITE);
                linhasLabel[linhaAtual].setStyle("-fx-background-color: transparent;");
            }
            if (indice >= 0 && indice < linhasLabel.length) {
                linhasLabel[indice].setTextFill(COR_TEXTO_DESTAQUE);
                linhasLabel[indice].setStyle("-fx-background-color: #0a3d0a;");
            }
            linhaAtual = indice;
        });
    }
}
