package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Random;

public class Principal extends Application {

    // ---------- Tempo de pausa (em ms) usado para "animar" a execução linha a linha ----------
    private static final long DELAY_LINHA = 100;

    // ---------- Geometria do vetor (usada para centralizar as caixinhas) ----------
    private static final int QTD_ELEMENTOS = 16;
    private static final double LARGURA_BOTAO_VETOR = 40;
    private static final double PASSO_VETOR = 45;
    private static final double LARGURA_PAINEL_ESQUERDO = 1000;
    private static final double Y_VETOR = 320;

    AnchorPane pane;
    Button botao_inicio;
    Label Texto;
    private Button vet[];
    Label pivo;

    // ---------- Painel de código e painel de variáveis ----------
    private PainelCodigo painelCodigo;
    private PainelVariaveis painelVariaveis;

    public static void main(String[] args) {
        launch(args);
    }

    public void Trocar(int i, int j) {
        Button aux;
        aux = vet[j];
        vet[j] = vet[i];
        vet[i] = aux;
    }

    private void destacarEsperar(int linha) {
        painelCodigo.destacar(linha);
        try {
            Thread.sleep(DELAY_LINHA);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private final String COR_PADRAO = "-fx-background-color: #3498db; -fx-text-fill: white;"; // Azul
    private final String COR_DESTAQUE = "-fx-background-color: #e74c3c; -fx-text-fill: white;"; // Vermelho (ponteiros i, pai, etc)
    private final String COR_SECUNDARIA = "-fx-background-color: #f1c40f; -fx-text-fill: black;"; // Amarelo (ponteiros j, F2, etc)
    private final String COR_PIVO = "-fx-background-color: #9b59b6; -fx-text-fill: white;"; // Roxo (Pivô)
    private final String COR_ORDENADO = "-fx-background-color: #2ecc71; -fx-text-fill: white;"; // Verde (Ordenado)

    /**
     * Pinta índices específicos com cores personalizadas, mantendo o restante padrão ou verde.
     */
    private void colorirIndices(int idx1, String cor1, int idx2, String cor2) {
        Platform.runLater(() -> {
            for (int k = 0; k < vet.length; k++) {
                String estiloAtual = vet[k].getStyle();
                // Se já estiver verde (ordenado), não perde o verde
                if (estiloAtual != null && estiloAtual.contains("#2ecc71")) {
                    continue;
                }

                if (k == idx1) {
                    vet[k].setStyle(cor1);
                } else if (k == idx2) {
                    vet[k].setStyle(cor2);
                } else {
                    vet[k].setStyle(COR_PADRAO);
                }
            }
        });
    }

    /**
     * Marca um elemento específico como definitivamente ordenado (Verde).
     */
    private void marcarVerde(int indice) {
        if (indice >= 0 && indice < vet.length) {
            Platform.runLater(() -> vet[indice].setStyle(COR_ORDENADO));
        }
    }

    /**
     * Reseta todo o vetor para a cor padrão (útil ao iniciar).
     */
    private void resetarCoresVetor() {
        Platform.runLater(() -> {
            for (Button b : vet) {
                b.setStyle(COR_PADRAO);
            }
        });
    }

    public class MetodosOrd {

        private final String[] CODIGO_GNOME = {
                "public void gnomeSort() {",                                   // 0
                "    int i = 0;",                                              // 1
                "    while (i < vet.length) {",                                // 2
                "        if (i == 0 || vet[i] >= vet[i-1])",                   // 3
                "            i++;",                                           // 4
                "        else {",                                             // 5
                "            Trocar(i, i-1);",                                 // 6
                "            move_botoes(vet, i, i-1);",                       // 7
                "            i--;",                                           // 8
                "        }",                                                  // 9
                "    }",                                                      // 10
                "}"                                                           // 11
        };

        private final String[] CODIGO_QUICK = {
                "public void quickSortPivo(int ini, int fim) {",               // 0
                "    int pivo = vet[(ini+fim)/2];",                            // 1
                "    int i = ini, j = fim;",                                   // 2
                "    while (i <= j) {",                                       // 3
                "        while (i<=fim && vet[i] < pivo) i++;",               // 4
                "        while (j>=ini && vet[j] > pivo) j--;",               // 5
                "        if (i <= j) {",                                      // 6
                "            Trocar(i, j);",                                  // 7
                "            move_botoes(vet, i, j);",                        // 8
                "            i++;",                                           // 9
                "            j--;",                                           // 10
                "        }",                                                  // 11
                "    }",                                                      // 12
                "    if (ini < j) quickSortPivo(ini, j);",                    // 13
                "    if (fim > i) quickSortPivo(i, fim);",                    // 14
                "}"                                                           // 15
        };

        private final String[] CODIGO_HEAP = {
                "public void heapSort() {",                                   // 0
                "    int TL2 = vet.length;",                                  // 1
                "    for (TL2 = vet.length; TL2 > 1; TL2--) {",               // 2
                "        int pai = TL2/2 - 1;",                               // 3
                "        while (pai >= 0) {",                                 // 4
                "            int F1 = pai*2+1, F2 = F1+1;",                   // 5
                "            int maiorF = F1;",                               // 6
                "            if (F2 < TL2 && vet[F2] > vet[F1])",             // 7
                "                maiorF = F2;",                               // 8
                "            if (vet[maiorF] > vet[pai]) {",                  // 9
                "                Trocar(pai, maiorF);",                       // 10
                "                move_botoes(vet, pai, maiorF);",             // 11
                "            }",                                              // 12
                "            pai--;",                                         // 13
                "        }",                                                  // 14
                "        Trocar(0, TL2-1);",                                  // 15
                "        move_botoes(vet, 0, TL2-1);",                        // 16
                "    }",                                                      // 17
                "}"                                                           // 18
        };

        public MetodosOrd() {
        }

        public void gnomeSort() {
            painelCodigo.carregarCodigo(CODIGO_GNOME);
            painelVariaveis.limpar();
            resetarCoresVetor();

            int i = 0;
            destacarEsperar(1);
            painelVariaveis.atualizar("i", String.valueOf(i));

            destacarEsperar(2);
            while (i < vet.length) {
                colorirIndices(i, COR_DESTAQUE, (i > 0 ? i-1: -1), COR_SECUNDARIA);
                destacarEsperar(3);
                if (i == 0 || Integer.parseInt(vet[i].getText()) >= Integer.parseInt(vet[i - 1].getText())) {
                    destacarEsperar(4);
                    i++;
                    painelVariaveis.atualizar("i", String.valueOf(i));
                } else {
                    destacarEsperar(6);
                    Trocar(i, i - 1);
                    destacarEsperar(7);
                    move_botoes(vet, i, i - 1);
                    destacarEsperar(8);
                    i--;
                    painelVariaveis.atualizar("i", String.valueOf(i));
                }
                destacarEsperar(2);
            }
            for (int k = 0; k < vet.length; k++) marcarVerde(k);
            destacarEsperar(10);
            painelCodigo.destacar(-1);
        }

        public void quickSortPivo(int ini, int fim) {
            boolean chamadaInicial = (ini == 0 && fim == vet.length - 1);
            if (chamadaInicial) {
                painelCodigo.carregarCodigo(CODIGO_QUICK);
                painelVariaveis.limpar();
                resetarCoresVetor();
            }

            destacarEsperar(1);
            int pivto = Integer.parseInt(vet[(ini + fim) / 2].getText());
            painelVariaveis.atualizar("pivo", String.valueOf(pivto));
            painelVariaveis.atualizar("ini", String.valueOf(ini));
            painelVariaveis.atualizar("fim", String.valueOf(fim));

            destacarEsperar(2);
            int i = ini, j = fim;
            painelVariaveis.atualizar("i", String.valueOf(i));
            painelVariaveis.atualizar("j", String.valueOf(j));

            destacarEsperar(3);
            while (i <= j) {
                colorirIndices(i,COR_DESTAQUE,j,COR_SECUNDARIA);
                destacarEsperar(4);
                while (i <= fim && Integer.parseInt(vet[i].getText()) < pivto) {
                    i++;
                    colorirIndices(i, COR_DESTAQUE, j, COR_SECUNDARIA);
                    painelVariaveis.atualizar("i", String.valueOf(i));
                    destacarEsperar(4);
                }
                destacarEsperar(5);
                while (j >= ini && Integer.parseInt(vet[j].getText()) > pivto) {
                    j--;
                    colorirIndices(i, COR_DESTAQUE, j, COR_SECUNDARIA);
                    painelVariaveis.atualizar("j", String.valueOf(j));
                    destacarEsperar(5);
                }
                destacarEsperar(6);
                if (i <= j) {
                    destacarEsperar(7);
                    Trocar(i, j);
                    destacarEsperar(8);
                    move_botoes(vet, i, j);
                    destacarEsperar(9);
                    i++;
                    painelVariaveis.atualizar("i", String.valueOf(i));
                    destacarEsperar(10);
                    j--;
                    painelVariaveis.atualizar("j", String.valueOf(j));
                }
                destacarEsperar(3);
            }

            destacarEsperar(13);
            if (ini < j)
                quickSortPivo(ini, j);
            destacarEsperar(14);
            if (fim > i)
                quickSortPivo(i, fim);

            if (chamadaInicial) {
                painelCodigo.destacar(-1);
                Platform.runLater(() -> pivo.setText(""));
                for (int k = 0; k < vet.length; k++) marcarVerde(k);
            }
        }

        public void heapSort() {
            painelCodigo.carregarCodigo(CODIGO_HEAP);
            painelVariaveis.limpar();
            resetarCoresVetor();

            int TL2 = vet.length, pai, F1, F2;
            int maiorF;
            destacarEsperar(1);
            painelVariaveis.atualizar("TL2", String.valueOf(TL2));

            destacarEsperar(2);
            for (TL2 = vet.length; TL2 > 1; TL2--) {
                painelVariaveis.atualizar("TL2", String.valueOf(TL2));

                destacarEsperar(3);
                pai = TL2 / 2 - 1;
                painelVariaveis.atualizar("pai", String.valueOf(pai));

                destacarEsperar(4);
                while (pai >= 0) {
                    destacarEsperar(5);
                    F1 = pai * 2 + 1;
                    F2 = F1 + 1;
                    painelVariaveis.atualizar("F1", String.valueOf(F1));
                    painelVariaveis.atualizar("F2", String.valueOf(F2));

                    destacarEsperar(6);
                    maiorF = F1;
                    painelVariaveis.atualizar("maiorF", String.valueOf(maiorF));

                    destacarEsperar(7);
                    if (F2 < TL2 && Integer.parseInt(vet[F2].getText()) > Integer.parseInt(vet[F1].getText())) {
                        destacarEsperar(8);
                        maiorF = F2;
                        painelVariaveis.atualizar("maiorF", String.valueOf(maiorF));
                    }
                    colorirIndices(pai, COR_DESTAQUE, maiorF, COR_SECUNDARIA);

                    destacarEsperar(9);
                    if (Integer.parseInt(vet[maiorF].getText()) > Integer.parseInt(vet[pai].getText())) {
                        destacarEsperar(10);
                        Trocar(pai, maiorF);
                        destacarEsperar(11);
                        move_botoes(vet, pai, maiorF);
                    }

                    destacarEsperar(13);
                    pai--;
                    painelVariaveis.atualizar("pai", String.valueOf(pai));
                    destacarEsperar(4);
                }

                destacarEsperar(15);
                Trocar(0, TL2 - 1);
                destacarEsperar(16);
                move_botoes(vet, 0, TL2 - 1);
                marcarVerde(TL2 - 1);
                destacarEsperar(2);
            }
            marcarVerde(0);
            painelCodigo.destacar(-1);
        }

        public void shellSort() {
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Pesquisa e Ordenacao");

        pane = new AnchorPane();
        pane.setPrefWidth(LARGURA_PAINEL_ESQUERDO);
        AnchorPane paneVetor = new AnchorPane();

        pivo = new Label();
        pivo.setLayoutX(20);
        pivo.setLayoutY(140);
        pivo.setText("");
        pivo.setFont(new Font(20));
        pivo.setStyle("-fx-font-weight: bold;");
        pivo.setAlignment(Pos.TOP_RIGHT);
        pivo.setMouseTransparent(true);

        AnchorPane.setTopAnchor(pivo, 100.0);
        AnchorPane.setLeftAnchor(pivo, 0.0);
        AnchorPane.setRightAnchor(pivo, 0.0);
        pane.getChildren().add(pivo);

        MetodosOrd mt = new MetodosOrd();

        Button botao_inicio = new Button();
        botao_inicio.setLayoutX(10);
        botao_inicio.setLayoutY(20);
        botao_inicio.setText("Gerar Novos números desordenados");
        botao_inicio.setOnAction(e -> {
            GerarAleatorio(vet, paneVetor);
            painelVariaveis.limpar();
            painelCodigo.destacar(-1);
        });

        Button botao_quicksempivo = new Button();
        botao_quicksempivo.setLayoutX(10);
        botao_quicksempivo.setLayoutY(60);
        botao_quicksempivo.setText("QuickSort Com Pivo");
        botao_quicksempivo.setOnAction(e -> {
            Texto.setText("QuickSort Com Pivo");
            Thread threadOrdenacao = new Thread(() -> mt.quickSortPivo(0, vet.length - 1));
            threadOrdenacao.setDaemon(true);
            threadOrdenacao.start();
        });

        Button botao_Gnome = new Button();
        botao_Gnome.setLayoutX(180);
        botao_Gnome.setLayoutY(60);
        botao_Gnome.setText("Gnome Sort");
        botao_Gnome.setOnAction(e -> {
            Texto.setText("Gnome Sort");
            Thread threadOrdenacao = new Thread(() -> mt.gnomeSort());
            threadOrdenacao.setDaemon(true);
            threadOrdenacao.start();
        });

        Button botao_heap = new Button();
        botao_heap.setLayoutX(310);
        botao_heap.setLayoutY(60);
        botao_heap.setText("Heap Sort");
        botao_heap.setOnAction(e -> {
            Texto.setText("Heap Sort");
            Thread threadOrdenacao = new Thread(() -> mt.heapSort());
            threadOrdenacao.setDaemon(true);
            threadOrdenacao.start();
        });

        pane.getChildren().addAll(botao_inicio, botao_quicksempivo, botao_Gnome, botao_heap);

        Texto = new Label();
        Texto.setText("Ola");
        Texto.setFont(new Font(22));
        Texto.setStyle("-fx-font-weight: bold;");
        Texto.setAlignment(Pos.CENTER);
        Texto.setMouseTransparent(true);

        AnchorPane.setTopAnchor(Texto, 105.0);
        AnchorPane.setLeftAnchor(Texto, 0.0);
        AnchorPane.setRightAnchor(Texto, 0.0);
        pane.getChildren().add(Texto);

        vet = new Button[QTD_ELEMENTOS];
        paneVetor.setMouseTransparent(true);
        pane.getChildren().addAll(paneVetor);
        GerarAleatorio(vet, paneVetor);

        painelVariaveis = new PainelVariaveis();
        painelCodigo = new PainelCodigo();

        Label tituloVariaveis = new Label("Variáveis e seus respectivos Valores");
        tituloVariaveis.setFont(Font.font("Arial", 18));
        tituloVariaveis.setStyle("-fx-font-weight: bold;");
        tituloVariaveis.setMaxWidth(Double.MAX_VALUE);
        tituloVariaveis.setAlignment(Pos.CENTER);

        Label tituloCodigo = new Label("Código da Ordenação Escolhida");
        tituloCodigo.setFont(Font.font("Arial", 18));
        tituloCodigo.setStyle("-fx-font-weight: bold;");
        tituloCodigo.setMaxWidth(Double.MAX_VALUE);
        tituloCodigo.setAlignment(Pos.CENTER);

        Region divisorHorizontal = new Region();
        divisorHorizontal.setStyle("-fx-background-color: black;");
        divisorHorizontal.setPrefHeight(4);

        ScrollPane scrollVariaveis = new ScrollPane(painelVariaveis);
        scrollVariaveis.setFitToWidth(true);
        scrollVariaveis.setPrefHeight(250);

        ScrollPane scrollCodigo = new ScrollPane(painelCodigo);
        scrollCodigo.setFitToWidth(true);
        VBox.setVgrow(scrollCodigo, Priority.ALWAYS);

        VBox painelDireito = new VBox(8);
        painelDireito.setPadding(new Insets(10));
        painelDireito.setPrefWidth(600);
        painelDireito.setStyle("-fx-background-color: #f2f2f2;");
        painelDireito.getChildren().addAll(
                tituloVariaveis,
                divisorHorizontal,
                scrollVariaveis,
                tituloCodigo,
                scrollCodigo
        );

        Region divisorVertical = new Region();
        divisorVertical.setStyle("-fx-background-color: black;");
        divisorVertical.setPrefWidth(4);

        HBox raiz = new HBox();
        raiz.getChildren().addAll(pane, divisorVertical, painelDireito);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setHgrow(painelDireito, Priority.NEVER);

        Scene scene = new Scene(raiz, 1600, 900);
        stage.setScene(scene);
        stage.show();
    }

    private void move_botoes(Button vet[], int i, int j) {
        if (i == j) return;

        Button botaoI = vet[i];
        Button botaoJ = vet[j];
        double distancia = botaoJ.getLayoutX() - botaoI.getLayoutX();
        double andar = distancia / 16;

        for (int k = 0; k < 10; k++) {
            Platform.runLater(() -> botaoI.setLayoutY(botaoI.getLayoutY() + 5));
            Platform.runLater(() -> botaoJ.setLayoutY(botaoJ.getLayoutY() - 5));
            try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        for (int k = 0; k < 16; k++) {
            Platform.runLater(() -> botaoI.setLayoutX(botaoI.getLayoutX() + andar));
            Platform.runLater(() -> botaoJ.setLayoutX(botaoJ.getLayoutX() - andar));
            try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        for (int k = 0; k < 10; k++) {
            Platform.runLater(() -> botaoI.setLayoutY(botaoI.getLayoutY() - 5));
            Platform.runLater(() -> botaoJ.setLayoutY(botaoJ.getLayoutY() + 5));
            try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private void GerarAleatorio(Button vet[], AnchorPane pane) {
        pane.getChildren().clear();

        double larguraTotal = (QTD_ELEMENTOS - 1) * PASSO_VETOR + LARGURA_BOTAO_VETOR;
        double layoutX = (LARGURA_PAINEL_ESQUERDO - larguraTotal) / 2;

        Random rand = new Random();
        for (int i = 0; i < QTD_ELEMENTOS; i++, layoutX += PASSO_VETOR) {
            int num = rand.nextInt(1, 100);
            vet[i] = new Button(String.valueOf(num));
            vet[i].setLayoutX(layoutX);
            vet[i].setLayoutY(Y_VETOR);
            vet[i].setMinHeight(42);
            vet[i].setMinWidth(LARGURA_BOTAO_VETOR);
            vet[i].setFont(new Font(15));

            Label lblIndice = new Label(String.valueOf(i));
            lblIndice.setFont(new Font(14));
            lblIndice.setStyle("-fx-font-weight: bold; -fx-text-fill: #555555;");
            lblIndice.setMinWidth(LARGURA_BOTAO_VETOR);
            lblIndice.setAlignment(Pos.CENTER);
            lblIndice.setLayoutX(layoutX);
            lblIndice.setLayoutY(Y_VETOR + 50);

            pane.getChildren().addAll(vet[i], lblIndice);
        }
    }
}
