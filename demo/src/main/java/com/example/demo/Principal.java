package com.example.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Principal extends Application {
    private long DELAY_LINHA = 50;
    private int QTD_ELEMENTOS = 16;
    private double LARGURA_BOTAO_VETOR = 40;
    private double PASSO_VETOR = 45;
    private double LARGURA_PAINEL_ESQUERDO = 1000;
    private double Y_VETOR = 210;

    // Área das estruturas auxiliares (abaixo do vetor principal)
    private double Y_CAIXA_AUX = Y_VETOR + 75;   // caixinha "aux" do insertion/shell
    private double Y_AUX1 = 360;                 // 1ª linha auxiliar
    private double Y_AUX2 = 450;                 // 2ª linha auxiliar
    private double Y_BUCKETS = 320;              // 1º bucket
    private double Y_SAIDA_BUCKET = 770;         // vetor saida do bucket
    private double ALTURA_CELULA = 28;
    private double PASSO_LINHA_AUX = 42;

    AnchorPane pane;
    AnchorPane paneVetor;
    AnchorPane paneAux;
    Button botao_inicio;
    Label Texto;
    private Button vet[];
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
        esperar(DELAY_LINHA);
    }

    private void esperar(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private String COR_PADRAO = "-fx-background-color: #3498db; -fx-text-fill: white;"; // Azul
    private String COR_DESTAQUE = "-fx-background-color: #e74c3c; -fx-text-fill: white;"; // Vermelho
    private String COR_SECUNDARIA = "-fx-background-color: #f1c40f; -fx-text-fill: black;"; // Amarelo
    private String COR_PIVO = "-fx-background-color: #9b59b6; -fx-text-fill: white;"; // Roxo (Pivô)
    private String COR_ORDENADO = "-fx-background-color: #2ecc71; -fx-text-fill: white;"; // Verde (Ordenado)
    private String COR_FAIXA = "-fx-background-color: #85c1e9; -fx-text-fill: black;"; // Azul claro (faixa em trabalho)
    private String COR_ESCRITA = "-fx-background-color: #e67e22; -fx-text-fill: white;"; // Laranja (posição escrita)
    private String COR_AUX = "-fx-background-color: #ecf0f1; -fx-text-fill: black; -fx-border-color: #95a5a6;"; // Célula auxiliar


    private void colorirIndices(int idx1, String cor1, int idx2, String cor2, int pivo) {
        Platform.runLater(() -> {
            for (int k = 0; k < vet.length; k++) {
                String estiloAtual = vet[k].getStyle();
                if (!(estiloAtual != null && estiloAtual.contains("#2ecc71"))){
                    if (k == idx1) {
                        vet[k].setStyle(cor1);
                    } else if (k == idx2) {
                        vet[k].setStyle(cor2);
                    } else if(pivo != -1 && k == pivo){
                        vet[k].setStyle(COR_PIVO);
                    }
                    else {
                        vet[k].setStyle(COR_PADRAO);
                    }
                }
            }
        });
    }

    // Igual ao colorirIndices, mas pinta de azul claro a faixa [ini, fim] em que o algoritmo está trabalhando
    private void colorirFaixa(int ini, int fim, int idx1, int idx2) {
        Platform.runLater(() -> {
            for (int k = 0; k < vet.length; k++) {
                String estiloAtual = vet[k].getStyle();
                if (estiloAtual != null && estiloAtual.contains("#2ecc71")) continue;
                if (k == idx1) vet[k].setStyle(COR_DESTAQUE);
                else if (k == idx2) vet[k].setStyle(COR_SECUNDARIA);
                else if (k >= ini && k <= fim) vet[k].setStyle(COR_FAIXA);
                else vet[k].setStyle(COR_PADRAO);
            }
        });
    }


    private void marcarVerde(int indice) {
        if (indice >= 0 && indice < vet.length) {
            Platform.runLater(() -> vet[indice].setStyle(COR_ORDENADO));
        }
    }


    private void resetarCoresVetor() {
        Platform.runLater(() -> {
            for (Button b : vet) {
                b.setStyle(COR_PADRAO);
            }
        });
    }

    // ===================== Utilitários de animação =====================

    // Executa na thread do JavaFX e espera terminar. Garante que um setText feito
    // aqui já esteja visível para o próximo getText da thread de ordenação.
    private void naFX(Runnable acao) {
        if (Platform.isFxApplicationThread()) {
            acao.run();
            return;
        }
        CountDownLatch trava = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                acao.run();
            } finally {
                trava.countDown();
            }
        });
        try {
            trava.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private double xDoIndice(int i) {
        double larguraTotal = (QTD_ELEMENTOS - 1) * PASSO_VETOR + LARGURA_BOTAO_VETOR;
        return (LARGURA_PAINEL_ESQUERDO - larguraTotal) / 2 + i * PASSO_VETOR;
    }

    private void mostrarVars(Object... paresNomeValor) {
        for (int p = 0; p + 1 < paresNomeValor.length; p += 2)
            painelVariaveis.atualizar(String.valueOf(paresNomeValor[p]), String.valueOf(paresNomeValor[p + 1]));
    }

    // Escreve um valor em vet[idx] (equivale ao vet[idx] = valor) e pinta de laranja
    private void escreverVet(int idx, String texto) {
        naFX(() -> {
            vet[idx].setText(texto);
            String estiloAtual = vet[idx].getStyle();
            if (!(estiloAtual != null && estiloAtual.contains("#2ecc71")))
                vet[idx].setStyle(COR_ESCRITA);
        });
        esperar(DELAY_LINHA);
    }

    private Button criarCelula(String texto, double x, double y) {
        Button celula = new Button(texto);
        celula.setMinWidth(LARGURA_BOTAO_VETOR);
        celula.setPrefWidth(LARGURA_BOTAO_VETOR);
        celula.setMinHeight(ALTURA_CELULA);
        celula.setPrefHeight(ALTURA_CELULA);
        celula.setFont(new Font(13));
        celula.setStyle(COR_AUX);
        celula.setLayoutX(x);
        celula.setLayoutY(y);
        return celula;
    }

    // Cria um vetor auxiliar desenhado em linhas de "colunas" células, com título e índices
    private Button[] criarAuxiliar(String titulo, int tamanho, int colunas, double y, String textoInicial) {
        Button[] celulas = new Button[tamanho];
        double largura = (colunas - 1) * PASSO_VETOR + LARGURA_BOTAO_VETOR;
        double x0 = (LARGURA_PAINEL_ESQUERDO - largura) / 2;
        naFX(() -> {
            Label lblTitulo = new Label(titulo);
            lblTitulo.setFont(new Font(14));
            lblTitulo.setStyle("-fx-font-weight: bold;");
            lblTitulo.setLayoutX(x0);
            lblTitulo.setLayoutY(y - 24);
            paneAux.getChildren().add(lblTitulo);
            for (int c = 0; c < tamanho; c++) {
                double x = x0 + (c % colunas) * PASSO_VETOR;
                double yc = y + (c / colunas) * PASSO_LINHA_AUX;
                celulas[c] = criarCelula(textoInicial, x, yc);

                Label lblIndice = new Label(String.valueOf(c));
                lblIndice.setFont(new Font(10));
                lblIndice.setStyle("-fx-text-fill: #555555;");
                lblIndice.setMinWidth(LARGURA_BOTAO_VETOR);
                lblIndice.setAlignment(Pos.CENTER);
                lblIndice.setLayoutX(x);
                lblIndice.setLayoutY(yc + ALTURA_CELULA);
                paneAux.getChildren().addAll(celulas[c], lblIndice);
            }
        });
        return celulas;
    }

    // Uma linha por bucket; as células começam invisíveis e aparecem conforme recebem valor
    private Button[][] criarBuckets(int qtdBuckets) {
        Button[][] celulas = new Button[qtdBuckets][vet.length];
        naFX(() -> {
            for (int b = 0; b < qtdBuckets; b++) {
                double y = Y_BUCKETS + b * PASSO_LINHA_AUX;
                Label lbl = new Label("B" + b + " [" + (b * 10) + "-" + (b * 10 + 9) + "]");
                lbl.setFont(new Font(13));
                lbl.setStyle("-fx-font-weight: bold;");
                lbl.setLayoutX(20);
                lbl.setLayoutY(y + 5);
                paneAux.getChildren().add(lbl);
                for (int j = 0; j < vet.length; j++) {
                    celulas[b][j] = criarCelula("", xDoIndice(j), y);
                    celulas[b][j].setVisible(false);
                    paneAux.getChildren().add(celulas[b][j]);
                }
            }
        });
        return celulas;
    }

    private void escreverCelula(Button celula, String texto) {
        naFX(() -> {
            celula.setVisible(true);
            celula.setText(texto);
            celula.setStyle(COR_ESCRITA);
        });
        esperar(DELAY_LINHA);
        naFX(() -> celula.setStyle(COR_AUX));
    }

    private void limparCelulas(Button[] celulas) {
        naFX(() -> {
            for (Button c : celulas) {
                c.setText("");
                c.setStyle(COR_AUX);
            }
        });
    }

    private void limparAuxiliar() {
        naFX(() -> paneAux.getChildren().clear());
    }

    private void removerNo(Node no) {
        naFX(() -> paneAux.getChildren().remove(no));
    }

    // Caixinha roxa que representa a variável aux (insertion / shell), fica embaixo do índice pos
    private Button criarCaixaAux() {
        Button caixa = new Button();
        naFX(() -> {
            Button modelo = criarCelula("", xDoIndice(0), Y_CAIXA_AUX);
            caixa.setMinWidth(modelo.getMinWidth());
            caixa.setPrefWidth(modelo.getPrefWidth());
            caixa.setMinHeight(modelo.getMinHeight());
            caixa.setPrefHeight(modelo.getPrefHeight());
            caixa.setFont(modelo.getFont());
            caixa.setStyle(COR_PIVO);
            caixa.setVisible(false);
            paneAux.getChildren().add(caixa);
        });
        return caixa;
    }

    private void mostrarCaixaAux(Button caixa, int valor, int idx) {
        naFX(() -> {
            caixa.setText(String.valueOf(valor));
            caixa.setLayoutX(xDoIndice(idx));
            caixa.setLayoutY(Y_CAIXA_AUX);
            caixa.setVisible(true);
        });
    }

    // Move um nó existente até (x2, y2) em pequenos passos
    private void deslizar(Node no, double x2, double y2) {
        double x1 = no.getLayoutX(), y1 = no.getLayoutY();
        int passos = 10;
        for (int p = 1; p <= passos; p++) {
            double t = (double) p / passos;
            double x = x1 + (x2 - x1) * t;
            double y = y1 + (y2 - y1) * t;
            Platform.runLater(() -> {
                no.setLayoutX(x);
                no.setLayoutY(y);
            });
            esperar(12);
        }
    }

    // Um "fantasma" laranja com o valor voa da origem ao destino (cópia de valor entre posições).
    // Se origem e destino estão na mesma linha, faz um arco por cima para não passar sobre os botões.
    private void voar(String texto, double x1, double y1, double x2, double y2) {
        Label fantasma = new Label(texto);
        naFX(() -> {
            fantasma.setFont(new Font(14));
            fantasma.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
            fantasma.setMinWidth(LARGURA_BOTAO_VETOR);
            fantasma.setMinHeight(ALTURA_CELULA);
            fantasma.setAlignment(Pos.CENTER);
            fantasma.setLayoutX(x1);
            fantasma.setLayoutY(y1);
            paneAux.getChildren().add(fantasma);
        });
        double arco = Math.abs(y1 - y2) < 1 ? 50 : 0;
        int passos = 14;
        for (int p = 1; p <= passos; p++) {
            double t = (double) p / passos;
            double x = x1 + (x2 - x1) * t;
            double y = y1 + (y2 - y1) * t - arco * 4 * t * (1 - t);
            Platform.runLater(() -> {
                fantasma.setLayoutX(x);
                fantasma.setLayoutY(y);
            });
            esperar(15);
        }
        removerNo(fantasma);
    }

    private void voarDoVetParaCelula(int idx, Button celula) {
        voar(vet[idx].getText(), xDoIndice(idx), Y_VETOR, celula.getLayoutX(), celula.getLayoutY());
    }

    private void voarDaCelulaParaVet(Button celula, int idx) {
        voar(celula.getText(), celula.getLayoutX(), celula.getLayoutY(), xDoIndice(idx), Y_VETOR);
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

        private String[] CODIGO_QUICK = {
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

        private String[] CODIGO_HEAP = {
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

        private final String[] CODIGO_COUNTING = {
                "public void counting_sort(){",                               // 0
                "    int k=0;",                                               // 1
                "    int[] novo_vetor = new int[vet.length];",                // 2
                "    for (int i = 0; i <vet.length; i++)",                    // 3
                "        if (k <= vet[i]) k = vet[i];",                       // 4
                "    int[] vet_aux = new int[k];",                            // 5
                "    //constroi vetor com os valores de i",                   // 6
                "    for (int i =0; i<vet.length; i++)",                      // 7
                "        vet_aux[vet[i] - 1] += 1;",                          // 8
                "    //preenche a soma cumulativa",                           // 9
                "    for (int i = 1; i < k; i++)",                            // 10
                "        vet_aux[i] = vet_aux[i] + vet_aux[i-1];",            // 11
                "    for (int i = vet.length-1; i>=0; i--) {",                // 12
                "        int ai = vet[i] -1;",                                // 13
                "        novo_vetor[ vet_aux[ai]-1] = vet[i];",               // 14
                "        vet_aux[ai] -=1;",                                   // 15
                "    }",                                                      // 16
                "    //Popular vetor com o novo ordenado",                    // 17
                "    for (int i = 0; i < novo_vetor.length; i++)",            // 18
                "        vet[i] = novo_vetor[i];",                            // 19
                "}"                                                           // 20
        };

        private final String[] CODIGO_INSERTION = {
                "public void insertion_sort(int ini, int fim){",              // 0
                "    int pos,aux, i;",                                        // 1
                "    i = ini;",                                               // 2
                "    while(i <= fim){",                                       // 3
                "        pos = i;",                                           // 4
                "        aux = vet[i];",                                      // 5
                "        while(pos > ini && aux < vet[pos-1])",               // 6
                "        {",                                                  // 7
                "            vet[pos] = vet[pos-1];",                         // 8
                "            pos--;",                                         // 9
                "        }",                                                  // 10
                "        vet[pos] = aux;",                                    // 11
                "        i++;",                                               // 12
                "    }",                                                      // 13
                "}"                                                           // 14
        };

        private final String[] CODIGO_MERGE_SO = {
                "public void Merge(int esq, int dir, int aux[]){",            // 0
                "    if (esq < dir){",                                        // 1
                "        int meio = (esq+dir)/2;",                            // 2
                "        Merge(esq, meio, aux);",                             // 3
                "        Merge(meio+1, dir, aux);",                           // 4
                "        Fusao(esq, meio, meio+1, dir, aux);",                // 5
                "    }",                                                      // 6
                "}"                                                           // 7
        };

        private final String[] CODIGO_FUSAO = {
                "public void Fusao(int ini1, int fim1, int ini2, int fim2, int aux[]){", // 0
                "    int k=0, i =ini1, j = ini2;",                            // 1
                "    while(i <= fim1 && j <= fim2){",                         // 2
                "        if (vet[i] < vet[j])",                               // 3
                "            aux[k++] = vet[i++];",                           // 4
                "        else",                                               // 5
                "            aux[k++] = vet[j++];",                           // 6
                "    }",                                                      // 7
                "    while(i <= fim1)",                                       // 8
                "        aux[k++] = vet[i++];",                               // 9
                "    while(j <= fim2)",                                       // 10
                "        aux[k++] = vet[j++];",                               // 11
                "    //troca no vetor original",                              // 12
                "    for(int alvo = 0; alvo<k; alvo++){",                     // 13
                "        vet[alvo+ini1] = aux[alvo];",                        // 14
                "    }",                                                      // 15
                "}"                                                           // 16
        };

        private final String[] CODIGO_TIM_SO = {
                "public void TimSort(){",                                     // 0
                "    int run_size = 4;",                                      // 1
                "    for (int i=0; i< vet.length; i+=run_size)",              // 2
                "        insertion_sort(i, i+run_size > vet.length ? vet.length -1 : i+run_size-1);", // 3
                "    int[] aux = new int[vet.length];",                       // 4
                "    for (int particoes = run_size;  particoes < vet.length; particoes *=2)", // 5
                "        for (int esq = 0; esq<vet.length; esq += 2 * particoes) {", // 6
                "            int meio = esq + particoes - 1;",                // 7
                "            int dir = esq + 2*particoes-1 > vet.length-1 ? vet.length -1 : esq + 2*particoes-1;", // 8
                "            if (meio < dir)",                                // 9
                "                Fusao(esq, meio, meio+1, dir, aux);",        // 10
                "        }",                                                  // 11
                "}"                                                           // 12
        };

        private final String[] CODIGO_RADIX_SO = {
                "public void RadixSort(){",                                   // 0
                "    int k=0;",                                               // 1
                "    for (int i = 0; i <vet.length; i++)",                    // 2
                "        if (k <= vet[i]) k = vet[i];",                       // 3
                "    for(int exp = 1; k/exp>0; exp *=10)",                    // 4
                "        count(exp);",                                        // 5
                "}"                                                           // 6
        };

        private final String[] CODIGO_COUNT = {
                "public void count(int exp){",                                // 0
                "    int[] array_aux = new int[10];",                         // 1
                "    int[] saida = new int[vet.length];",                     // 2
                "    for (int i = 0; i<vet.length; i++)",                     // 3
                "        array_aux[(vet[i]/exp) % 10]++;",                    // 4
                "    for (int i = 1; i < array_aux.length; i++)",             // 5
                "        array_aux[i] += array_aux[i-1];",                    // 6
                "    for (int i = vet.length-1; i>=0; i--){",                 // 7
                "        saida[ array_aux[(vet[i]/exp) % 10]-1 ] = vet[i];",  // 8
                "        array_aux[(vet[i]/exp) % 10]--;",                    // 9
                "    }",                                                      // 10
                "    for (int i = 0; i < saida.length; i++)",                 // 11
                "        vet[i] = saida[i];",                                 // 12
                "}"                                                           // 13
        };

        private final String[] CODIGO_BUCKET_SO = {
                "public void BucketSort(){",                                  // 0
                "    int k=0;",                                               // 1
                "    for (int i = 0; i <vet.length; i++)",                    // 2
                "        if (k <= vet[i]) k = vet[i];",                       // 3
                "    int[][] buckets = new int[k/10+1][vet.length];",         // 4
                "    int[] tamanhos = new int[k/10+1];",                      // 5
                "    for (int i = 0; i< vet.length; i++){",                   // 6
                "        int indice_bucket = vet[i]/10;",                     // 7
                "        int pos = tamanhos[indice_bucket]++;",               // 8
                "        buckets[vet[i]/10][pos] = vet[i];",                  // 9
                "    }",                                                      // 10
                "    for(int b = 0; b < buckets.length; b++)",                // 11
                "        bucket_insertion_sort(buckets[b], tamanhos[b]);",    // 12
                "    int[] saida = new int[vet.length];",                     // 13
                "    int saida_i=0;",                                         // 14
                "    for(int i = 0; i< buckets.length; i++){",                // 15
                "        for(int j =0; j<tamanhos[i]; j++){",                 // 16
                "            saida[saida_i++] = buckets[i][j];",              // 17
                "        }",                                                  // 18
                "    }",                                                      // 19
                "    for(int i = 0; i< vet.length; i++){",                    // 20
                "        vet[i] = saida[i];",                                 // 21
                "    }",                                                      // 22
                "}"                                                           // 23
        };

        private final String[] CODIGO_BUCKET_INS = {
                "public void bucket_insertion_sort(int vetor[], int tam){",   // 0
                "    int pos,aux, i;",                                        // 1
                "    i = 0;",                                                 // 2
                "    while(i < tam){",                                        // 3
                "        pos = i;",                                           // 4
                "        aux = vetor[pos];",                                  // 5
                "        while(pos > 0 && aux < vetor[pos-1])",               // 6
                "        {",                                                  // 7
                "            vetor[pos] = vetor[pos-1];",                     // 8
                "            pos--;",                                         // 9
                "        }",                                                  // 10
                "        vetor[pos] = aux;",                                  // 11
                "        i++;",                                               // 12
                "    }",                                                      // 13
                "}"                                                           // 14
        };

        private final String[] CODIGO_COMB = {
                "public void combSort(){",                                    // 0
                "    int gap = vet.length;",                                  // 1
                "    int trocado = 1;",                                       // 2
                "    while(gap != 1 || trocado == 1){",                       // 3
                "        gap =  proximoGap(gap);",                            // 4
                "        trocado = 0;",                                       // 5
                "        for (int i = 0; i<vet.length-gap; i++){",            // 6
                "            if (vet[i] > vet[i+gap]){",                      // 7
                "                Trocar(i, i+gap);",                          // 8
                "                move_botoes(vet, i, i+gap);",                // 9
                "                trocado = 1;",                               // 10
                "            }",                                              // 11
                "        }",                                                  // 12
                "    }",                                                      // 13
                "}",                                                          // 14
                "",                                                           // 15
                "public int proximoGap(int gap){",                            // 16
                "    return (gap*10)/13 < 1 ? 1 : (gap*10)/13;",              // 17
                "}"                                                           // 18
        };

        private final String[] CODIGO_SHELL = {
                "public void ShellSort(){",                                   // 0
                "    int dist, pos, aux, i;",                                 // 1
                "    dist  = 1;",                                             // 2
                "    while(dist < vet.length)",                               // 3
                "        dist = (dist*2) +1;",                                // 4
                "    dist/=2;",                                               // 5
                "    while(dist > 0){",                                       // 6
                "        i = dist;",                                          // 7
                "        while(i < vet.length){",                             // 8
                "            pos = i;",                                       // 9
                "            aux = vet[pos];",                                // 10
                "            while(pos >= dist && aux < vet[pos-dist]){",     // 11
                "                vet[pos] = vet[pos-dist];",                  // 12
                "                pos-=dist;",                                 // 13
                "            }",                                              // 14
                "            vet[pos] = aux;",                                // 15
                "            i++;",                                           // 16
                "        }",                                                  // 17
                "        dist/=2;",                                           // 18
                "    }",                                                      // 19
                "}"                                                           // 20
        };

        // Códigos compostos (método principal + auxiliares), separados por uma linha em branco
        private final String[] CODIGO_MERGE = juntar(CODIGO_MERGE_SO, CODIGO_FUSAO);
        private final String[] CODIGO_TIM = juntar(CODIGO_TIM_SO, CODIGO_INSERTION, CODIGO_FUSAO);
        private final String[] CODIGO_RADIX = juntar(CODIGO_RADIX_SO, CODIGO_COUNT);
        private final String[] CODIGO_BUCKET = juntar(CODIGO_BUCKET_SO, CODIGO_BUCKET_INS);

        // Deslocamento da linha do método auxiliar dentro do código composto exibido
        private int desl_insercao = 0;
        private int desl_fusao = 0;
        private int desl_count = 0;
        private int desl_bucket_ins = 0;

        private Button[] celulasAux;      // vetor aux do Merge / TimSort
        private Button[] celulasBucket;   // bucket que o bucket_insertion_sort está ordenando

        private String[] juntar(String[]... partes) {
            List<String> linhas = new ArrayList<>();
            for (int p = 0; p < partes.length; p++) {
                if (p > 0) linhas.add("");
                for (String l : partes[p]) linhas.add(l);
            }
            return linhas.toArray(new String[0]);
        }

        private void preparar(String[] codigo) {
            painelCodigo.carregarCodigo(codigo);
            painelVariaveis.limpar();
            resetarCoresVetor();
            limparAuxiliar();
        }

        private void concluir() {
            for (int k = 0; k < vet.length; k++) marcarVerde(k);
            painelCodigo.destacar(-1);
        }

        public void gnomeSort() {
            painelCodigo.carregarCodigo(CODIGO_GNOME);
            painelVariaveis.limpar();
            resetarCoresVetor();
            limparAuxiliar();

            int i = 0;
            destacarEsperar(1);
            painelVariaveis.atualizar("i", String.valueOf(i));

            destacarEsperar(2);
            while (i < vet.length) {
                colorirIndices(i, COR_DESTAQUE, (i > 0 ? i-1: -1), COR_SECUNDARIA, -1);
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
                limparAuxiliar();
            }

            destacarEsperar(1);
            int indice_pivo = (ini + fim) / 2;
            int pivto = Integer.parseInt(vet[indice_pivo].getText());
            colorirIndices(-1,COR_DESTAQUE,-1,COR_SECUNDARIA,indice_pivo);
            painelVariaveis.atualizar("pivo", String.valueOf(pivto));
            painelVariaveis.atualizar("ini", String.valueOf(ini));
            painelVariaveis.atualizar("fim", String.valueOf(fim));

            destacarEsperar(2);
            int i = ini, j = fim;
            painelVariaveis.atualizar("i", String.valueOf(i));
            painelVariaveis.atualizar("j", String.valueOf(j));

            destacarEsperar(3);
            while (i <= j) {
                colorirIndices(i,COR_DESTAQUE,j,COR_SECUNDARIA,indice_pivo);
                destacarEsperar(4);
                while (i <= fim && Integer.parseInt(vet[i].getText()) < pivto) {
                    i++;
                    colorirIndices(i, COR_DESTAQUE, j, COR_SECUNDARIA,indice_pivo);
                    painelVariaveis.atualizar("i", String.valueOf(i));
                    destacarEsperar(4);
                }
                destacarEsperar(5);
                while (j >= ini && Integer.parseInt(vet[j].getText()) > pivto) {
                    j--;
                    colorirIndices(i, COR_DESTAQUE, j, COR_SECUNDARIA,indice_pivo);
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
                //Platform.runLater(() -> pivo.setText(""));
                for (int k = 0; k < vet.length; k++) marcarVerde(k);
            }
        }

        public void heapSort() {
            painelCodigo.carregarCodigo(CODIGO_HEAP);
            painelVariaveis.limpar();
            resetarCoresVetor();
            limparAuxiliar();

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
                    colorirIndices(pai, COR_DESTAQUE, maiorF, COR_SECUNDARIA,-1);

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

        public void counting_sort(){
            preparar(CODIGO_COUNTING);

            destacarEsperar(1);
            int k=0;
            mostrarVars("k", k);
            destacarEsperar(2);
            int[] novo_vetor = new int[vet.length];
            Button[] celNovo = criarAuxiliar("novo_vetor", vet.length, vet.length, Y_AUX1, "");

            for (int i = 0; i <vet.length; i++) {
                destacarEsperar(3);
                mostrarVars("i", i);
                colorirIndices(i, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);
                destacarEsperar(4);
                if (k <= Integer.parseInt(vet[i].getText())) k = Integer.parseInt(vet[i].getText());
                mostrarVars("k", k);
            }

            destacarEsperar(5);
            int[] vet_aux = new int[k];
            Button[] celAux = criarAuxiliar("vet_aux   (índice = valor - 1)", k, 10, Y_AUX2, "0");

            //constroi vetor com os valores de i
            for (int i =0; i<vet.length; i++) {
                destacarEsperar(7);
                mostrarVars("i", i);
                colorirIndices(i, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);
                destacarEsperar(8);
                vet_aux[Integer.parseInt(vet[i].getText()) - 1] += 1;
                int ind = Integer.parseInt(vet[i].getText()) - 1;
                voarDoVetParaCelula(i, celAux[ind]);
                escreverCelula(celAux[ind], String.valueOf(vet_aux[ind]));
            }
            colorirIndices(-1, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);

            //preenche a soma cumulativa
            for (int i = 1; i < k; i++) {
                destacarEsperar(10);
                mostrarVars("i", i);
                destacarEsperar(11);
                vet_aux[i] = vet_aux[i] + vet_aux[i-1];
                escreverCelula(celAux[i], String.valueOf(vet_aux[i]));
            }

            for (int i = vet.length-1; i>=0; i--)
            {
                destacarEsperar(12);
                mostrarVars("i", i);
                colorirIndices(i, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);
                //Criacao do novo vetor
                destacarEsperar(13);
                int ai = Integer.parseInt(vet[i].getText()) -1;
                mostrarVars("ai", ai);
                destacarEsperar(14);
                int destino = vet_aux[ai]-1;
                novo_vetor[ vet_aux[ai]-1] = Integer.parseInt(vet[i].getText());
                voarDoVetParaCelula(i, celNovo[destino]);
                escreverCelula(celNovo[destino], String.valueOf(novo_vetor[destino]));
                destacarEsperar(15);
                vet_aux[ai] -=1;
                escreverCelula(celAux[ai], String.valueOf(vet_aux[ai]));
                //Explicacao: vet_aux[ai] -> Quantidade acumulada para o numero obtido no vetor A
                // Se a quantidade acumulada para o numero 10 do vetor A por exemplo for 5
                // isso indica que o lugar "ideal" seria na 5 posicao do vetor
                // ou seja o indice 4, pot isso o vet_aux[ai]-1 na linha de cima
            }

            //Popular vetor com o novo ordenado
            for (int i = 0; i < novo_vetor.length; i++) {
                destacarEsperar(18);
                mostrarVars("i", i);
                destacarEsperar(19);
                voarDaCelulaParaVet(celNovo[i], i);
                escreverVet(i, String.valueOf(novo_vetor[i]));
                marcarVerde(i);
            }
            concluir();
        }

        public void animarInsertion() {
            preparar(CODIGO_INSERTION);
            desl_insercao = 0;
            insertion_sort(0, vet.length - 1);
            concluir();
        }

        public void insertion_sort(int ini, int fim){
            int d = desl_insercao;
            Button caixaAux = criarCaixaAux();
            int pos,aux, i;
            destacarEsperar(d + 1);
            destacarEsperar(d + 2);
            i = ini;
            mostrarVars("ini", ini, "fim", fim, "i", i);

            destacarEsperar(d + 3);
            while(i <= fim){
                destacarEsperar(d + 4);
                pos = i;
                mostrarVars("pos", pos);
                colorirFaixa(ini, fim, i, -1);
                destacarEsperar(d + 5);
                aux = Integer.parseInt(vet[i].getText());
                mostrarVars("aux", aux);
                voar(String.valueOf(aux), xDoIndice(i), Y_VETOR, xDoIndice(pos), Y_CAIXA_AUX);
                mostrarCaixaAux(caixaAux, aux, pos);

                colorirFaixa(ini, fim, pos, pos - 1 >= ini ? pos - 1 : -1);
                destacarEsperar(d + 6);
                while(pos > ini && aux < Integer.parseInt(vet[pos-1].getText()))
                {
                    destacarEsperar(d + 8);
                    voar(vet[pos-1].getText(), xDoIndice(pos-1), Y_VETOR, xDoIndice(pos), Y_VETOR);
                    escreverVet(pos, String.valueOf(vet[pos-1].getText()));
                    destacarEsperar(d + 9);
                    pos--;
                    mostrarVars("pos", pos);
                    deslizar(caixaAux, xDoIndice(pos), Y_CAIXA_AUX);
                    colorirFaixa(ini, fim, pos, pos - 1 >= ini ? pos - 1 : -1);
                    destacarEsperar(d + 6);
                }
                destacarEsperar(d + 11);
                voar(String.valueOf(aux), xDoIndice(pos), Y_CAIXA_AUX, xDoIndice(pos), Y_VETOR);
                escreverVet(pos, String.valueOf(aux));
                naFX(() -> caixaAux.setVisible(false));
                destacarEsperar(d + 12);
                i++;
                mostrarVars("i", i);
                destacarEsperar(d + 3);
            }
            removerNo(caixaAux);
        }

        public void animarMerge() {
            preparar(CODIGO_MERGE);
            desl_fusao = CODIGO_MERGE_SO.length + 1;
            int[] aux = new int[vet.length];
            celulasAux = criarAuxiliar("aux", vet.length, vet.length, Y_AUX1, "");
            Merge(0, vet.length - 1, aux);
            concluir();
        }

        public void Merge(int esq, int dir, int aux[]){
            mostrarVars("esq", esq, "dir", dir);
            colorirFaixa(esq, dir, -1, -1);
            destacarEsperar(1);
            if (esq < dir){
                destacarEsperar(2);
                int meio = (esq+dir)/2;
                mostrarVars("meio", meio);
                destacarEsperar(3);
                Merge(esq, meio, aux);
                mostrarVars("esq", esq, "meio", meio, "dir", dir);
                colorirFaixa(esq, dir, -1, -1);
                destacarEsperar(4);
                Merge(meio+1, dir, aux);
                mostrarVars("esq", esq, "meio", meio, "dir", dir);
                colorirFaixa(esq, dir, -1, -1);
                destacarEsperar(5);
                Fusao(esq, meio, meio+1, dir, aux);
            }
        }

        public void Fusao(int ini1, int fim1, int ini2, int fim2, int aux[]){
            int d = desl_fusao;
            limparCelulas(celulasAux);
            destacarEsperar(d + 1);
            int k=0, i =ini1, j = ini2;
            mostrarVars("ini1", ini1, "fim1", fim1, "ini2", ini2, "fim2", fim2, "k", k, "i", i, "j", j);

            destacarEsperar(d + 2);
            while(i <= fim1 && j <= fim2){
                colorirFaixa(ini1, fim2, i, j);
                destacarEsperar(d + 3);
                if (Integer.parseInt(vet[i].getText()) < Integer.parseInt(vet[j].getText())) {
                    destacarEsperar(d + 4);
                    int origem = i;
                    aux[k++] =  Integer.parseInt(vet[i++].getText());
                    copiarParaAux(origem, k - 1, aux[k - 1]);
                }
                else {
                    destacarEsperar(d + 6);
                    int origem = j;
                    aux[k++] =  Integer.parseInt(vet[j++].getText());
                    copiarParaAux(origem, k - 1, aux[k - 1]);
                }
                mostrarVars("k", k, "i", i, "j", j);
                destacarEsperar(d + 2);
            }
            destacarEsperar(d + 8);
            while(i <= fim1) {
                colorirFaixa(ini1, fim2, i, -1);
                destacarEsperar(d + 9);
                int origem = i;
                aux[k++] = Integer.parseInt(vet[i++].getText());
                copiarParaAux(origem, k - 1, aux[k - 1]);
                mostrarVars("k", k, "i", i);
                destacarEsperar(d + 8);
            }
            destacarEsperar(d + 10);
            while(j <= fim2) {
                colorirFaixa(ini1, fim2, -1, j);
                destacarEsperar(d + 11);
                int origem = j;
                aux[k++] = Integer.parseInt(vet[j++].getText());
                copiarParaAux(origem, k - 1, aux[k - 1]);
                mostrarVars("k", k, "j", j);
                destacarEsperar(d + 10);
            }

            //troca no vetor original
            colorirFaixa(ini1, fim2, -1, -1);
            destacarEsperar(d + 13);
            for(int alvo = 0; alvo<k; alvo++){
                mostrarVars("alvo", alvo);
                destacarEsperar(d + 14);
                voarDaCelulaParaVet(celulasAux[alvo], alvo + ini1);
                escreverVet(alvo+ini1, String.valueOf(aux[alvo]));
                destacarEsperar(d + 13);
            }
        }

        // vet[origem] -> aux[destino] na tela
        private void copiarParaAux(int origem, int destino, int valor) {
            voarDoVetParaCelula(origem, celulasAux[destino]);
            escreverCelula(celulasAux[destino], String.valueOf(valor));
        }

        public void TimSort(){
            //Insertion Sort + merge Sort utilizando grupos
            // Cada grupo é uma Run, onde cada run eh ordenado com insertion sort
            //Depois Merge une todos
            preparar(CODIGO_TIM);
            desl_insercao = CODIGO_TIM_SO.length + 1;
            desl_fusao = desl_insercao + CODIGO_INSERTION.length + 1;

            destacarEsperar(1);
            int run_size = 4;
            mostrarVars("run_size", run_size);
            destacarEsperar(2);
            for (int i=0; i< vet.length; i+=run_size) {
                mostrarVars("i (TimSort)", i);
                destacarEsperar(3);
                insertion_sort(i, i+run_size > vet.length ? vet.length -1 : i+run_size-1);
                destacarEsperar(2);
            }

            destacarEsperar(4);
            int[] aux = new int[vet.length];
            celulasAux = criarAuxiliar("aux", vet.length, vet.length, Y_AUX1, "");
            // Merge Sort de 2 runs, por isso 2 * run_size
            destacarEsperar(5);
            for (int particoes = run_size;  particoes < vet.length; particoes *=2) {
                mostrarVars("particoes", particoes);
                destacarEsperar(6);
                for (int esq = 0; esq<vet.length; esq += 2 * particoes) {
                    mostrarVars("esq", esq);
                    destacarEsperar(7);
                    int meio = esq + particoes - 1;
                    destacarEsperar(8);
                    int dir = esq + 2*particoes-1 > vet.length-1 ? vet.length -1 : esq + 2*particoes-1;
                    mostrarVars("meio", meio, "dir", dir);
                    colorirFaixa(esq, dir, -1, -1);
                    destacarEsperar(9);
                    if (meio < dir) {
                        destacarEsperar(10);
                        Fusao(esq, meio, meio+1, dir, aux);
                    }
                    destacarEsperar(6);
                }
                destacarEsperar(5);
            }
            concluir();
            desl_insercao = 0;
            desl_fusao = 0;
        }

        public void RadixSort(){
            // Counting Sort Diferente
            // Para cada iteracao, oa inves de fazer de modo geral
            // Ele faz pegando pelo tamanho em digitos
            preparar(CODIGO_RADIX);
            desl_count = CODIGO_RADIX_SO.length + 1;

            destacarEsperar(1);
            int k=0;
            mostrarVars("k", k);
            for (int i = 0; i <vet.length; i++) {
                destacarEsperar(2);
                mostrarVars("i", i);
                colorirIndices(i, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);
                destacarEsperar(3);
                if (k <= Integer.parseInt(vet[i].getText())) k = Integer.parseInt(vet[i].getText());
                mostrarVars("k", k);
            }

            destacarEsperar(4);
            for(int exp = 1; k/exp>0; exp *=10) {
                mostrarVars("exp", exp);
                destacarEsperar(5);
                count(exp);
                destacarEsperar(4);
            }
            concluir();
        }

        public void count(int exp){
            int d = desl_count;
            limparAuxiliar();
            destacarEsperar(d + 1);
            int[] array_aux = new int[10];
            Button[] celDig = criarAuxiliar("array_aux   (dígito = (vet[i]/" + exp + ") % 10)", 10, 10, Y_AUX1, "0");
            destacarEsperar(d + 2);
            int[] saida = new int[vet.length];
            Button[] celSaida = criarAuxiliar("saida", vet.length, vet.length, Y_AUX2, "");

            for (int i = 0; i<vet.length; i++) {
                destacarEsperar(d + 3);
                int dig = (Integer.parseInt(vet[i].getText())/exp) % 10;
                mostrarVars("i", i, "dígito", dig);
                colorirIndices(i, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);
                destacarEsperar(d + 4);
                array_aux[(Integer.parseInt(vet[i].getText())/exp) % 10]++;
                voarDoVetParaCelula(i, celDig[dig]);
                escreverCelula(celDig[dig], String.valueOf(array_aux[dig]));
            }
            colorirIndices(-1, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);

            for (int i = 1; i < array_aux.length; i++) {
                destacarEsperar(d + 5);
                mostrarVars("i", i);
                destacarEsperar(d + 6);
                array_aux[i] += array_aux[i-1];
                escreverCelula(celDig[i], String.valueOf(array_aux[i]));
            }

            for (int i = vet.length-1; i>=0; i--){
                destacarEsperar(d + 7);
                int dig = (Integer.parseInt(vet[i].getText())/exp) % 10;
                mostrarVars("i", i, "dígito", dig);
                colorirIndices(i, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);
                destacarEsperar(d + 8);
                int destino = array_aux[dig]-1;
                saida[ array_aux[(Integer.parseInt(vet[i].getText())/exp) % 10]-1 ] = Integer.parseInt(vet[i].getText());
                voarDoVetParaCelula(i, celSaida[destino]);
                escreverCelula(celSaida[destino], String.valueOf(saida[destino]));
                destacarEsperar(d + 9);
                array_aux[(Integer.parseInt(vet[i].getText())/exp) % 10]--;
                escreverCelula(celDig[dig], String.valueOf(array_aux[dig]));
            }
            colorirIndices(-1, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);

            for (int i = 0; i < saida.length; i++) {
                destacarEsperar(d + 11);
                mostrarVars("i", i);
                destacarEsperar(d + 12);
                voarDaCelulaParaVet(celSaida[i], i);
                escreverVet(i, String.valueOf(saida[i]));
            }
        }

        public void bucket_insertion_sort(int vetor[], int tam){
            int d = desl_bucket_ins;
            Button[] cel = celulasBucket;
            int pos,aux, i;
            destacarEsperar(d + 1);
            destacarEsperar(d + 2);
            i = 0;
            mostrarVars("tam", tam, "i", i);

            destacarEsperar(d + 3);
            while(i < tam){
                destacarEsperar(d + 4);
                pos = i;
                destacarEsperar(d + 5);
                aux = vetor[pos];
                mostrarVars("pos", pos, "aux", aux);
                Button celulaAux = cel[pos];
                naFX(() -> celulaAux.setStyle(COR_PIVO));
                destacarEsperar(d + 6);
                while(pos > 0 && aux < vetor[pos-1])
                {
                    destacarEsperar(d + 8);
                    vetor[pos] = vetor[pos-1];
                    voar(String.valueOf(vetor[pos-1]), cel[pos-1].getLayoutX(), cel[pos-1].getLayoutY(),
                            cel[pos].getLayoutX(), cel[pos].getLayoutY());
                    escreverCelula(cel[pos], String.valueOf(vetor[pos]));
                    destacarEsperar(d + 9);
                    pos--;
                    mostrarVars("pos", pos);
                    destacarEsperar(d + 6);
                }
                destacarEsperar(d + 11);
                vetor[pos] = aux;
                escreverCelula(cel[pos], String.valueOf(aux));
                destacarEsperar(d + 12);
                i++;
                mostrarVars("i", i);
                destacarEsperar(d + 3);
            }
        }

        public void BucketSort(){
            //Bucket Sort consiste em separa o Array em Caixinhas que tenham algo em comum
            // A depender da regra, eh possível fazer com floats, com  Strings e por ai vai
            // Depois de colocar tudo nos buckets, pode se aplicar qualquer metodo de ordenacao para os mesmo
            // aqui eu coloquei o insertion_sort por ser facil de alterar
            // Cada bucket guarda um range de 10, o numero 21 por exemplo, cairia no bucket 2 pois
            // bucket 0: 0-9, bucket1:10-19, bucket2: 20-29
            preparar(CODIGO_BUCKET);
            desl_bucket_ins = CODIGO_BUCKET_SO.length + 1;

            destacarEsperar(1);
            int k=0;
            mostrarVars("k", k);
            for (int i = 0; i <vet.length; i++) {
                destacarEsperar(2);
                mostrarVars("i", i);
                colorirIndices(i, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);
                destacarEsperar(3);
                if (k <= Integer.parseInt(vet[i].getText())) k = Integer.parseInt(vet[i].getText());
                mostrarVars("k", k);
            }

            destacarEsperar(4);
            int[][] buckets = new int[k/10+1][vet.length];
            Button[][] celBuckets = criarBuckets(k/10+1);
            destacarEsperar(5);
            int[] tamanhos = new int[k/10+1]; // quantos elementos cada bucket ja tem

            for (int i = 0; i< vet.length; i++){
                destacarEsperar(6);
                mostrarVars("i", i);
                colorirIndices(i, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);
                destacarEsperar(7);
                int indice_bucket = Integer.parseInt(vet[i].getText())/10;
                destacarEsperar(8);
                int pos = tamanhos[indice_bucket]++;
                mostrarVars("indice_bucket", indice_bucket, "pos", pos);
                destacarEsperar(9);
                buckets[Integer.parseInt(vet[i].getText())/10][pos] = Integer.parseInt(vet[i].getText());
                voarDoVetParaCelula(i, celBuckets[indice_bucket][pos]);
                escreverCelula(celBuckets[indice_bucket][pos], String.valueOf(buckets[indice_bucket][pos]));
            }
            colorirIndices(-1, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);

            for(int b = 0; b < buckets.length; b++) {
                destacarEsperar(11);
                mostrarVars("b", b);
                destacarEsperar(12);
                celulasBucket = celBuckets[b];
                bucket_insertion_sort(buckets[b], tamanhos[b]);
            }

            destacarEsperar(13);
            int[] saida = new int[vet.length];
            Button[] celSaida = criarAuxiliar("saida", vet.length, vet.length, Y_SAIDA_BUCKET, "");
            destacarEsperar(14);
            int saida_i=0;
            mostrarVars("saida_i", saida_i);
            for(int i = 0; i< buckets.length; i++){
                destacarEsperar(15);
                mostrarVars("i", i);
                for(int j =0; j<tamanhos[i]; j++){
                    destacarEsperar(16);
                    mostrarVars("j", j);
                    destacarEsperar(17);
                    int destino = saida_i;
                    saida[saida_i++] = buckets[i][j];
                    mostrarVars("saida_i", saida_i);
                    Button origem = celBuckets[i][j];
                    voar(origem.getText(), origem.getLayoutX(), origem.getLayoutY(),
                            celSaida[destino].getLayoutX(), celSaida[destino].getLayoutY());
                    escreverCelula(celSaida[destino], String.valueOf(saida[destino]));
                }
            }
            for(int i = 0; i< vet.length; i++){
                destacarEsperar(20);
                mostrarVars("i", i);
                destacarEsperar(21);
                voarDaCelulaParaVet(celSaida[i], i);
                escreverVet(i, String.valueOf(saida[i]));
                marcarVerde(i);
            }
            concluir();
        }

        public int proximoGap(int gap){
            return (gap*10)/13 < 1 ? 1 : (gap*10)/13; //mesma coisa que dividir por 1.3
        }

        public void combSort(){
            //Bubble Sort com Gaps
            //Resolve o problema de Tartarugas e Coelhos: Tartarugas(pequenos valores proximos do final da lista)
            // Coelho(grandes valores no comeco da lista)
            //Para isso sao utilizados os Gaps, que sao calculados por Gap anterior / 1.3
            // Para que as tartarugas deem grandes saltos e assim possamos ordenar masi rapidamente
            preparar(CODIGO_COMB);

            destacarEsperar(1);
            int gap = vet.length;
            destacarEsperar(2);
            int trocado = 1;
            mostrarVars("gap", gap, "trocado", trocado);
            // a variavel trocado eh para verificar se houve mudanca no while
            //se houve, mesmo que gap == 1, eu preciso continuar realizando o bubble sort
            destacarEsperar(3);
            while(gap != 1 || trocado == 1){
                destacarEsperar(4);
                destacarEsperar(17);
                gap =  proximoGap(gap);
                destacarEsperar(5);
                trocado = 0;
                mostrarVars("gap", gap, "trocado", trocado);
                destacarEsperar(6);
                for (int i = 0; i<vet.length-gap; i++){
                    mostrarVars("i", i);
                    colorirIndices(i, COR_DESTAQUE, i+gap, COR_SECUNDARIA, -1);
                    destacarEsperar(7);
                    if (Integer.parseInt(vet[i].getText()) > Integer.parseInt(vet[i+gap].getText())){
                        destacarEsperar(8);
                        Trocar(i, i+gap);
                        destacarEsperar(9);
                        move_botoes(vet, i, i+gap);
                        destacarEsperar(10);
                        trocado = 1;
                        mostrarVars("trocado", trocado);
                    }
                    destacarEsperar(6);
                }
                destacarEsperar(3);
            }
            concluir();
        }

        public void ShellSort(){
            preparar(CODIGO_SHELL);
            Button caixaAux = criarCaixaAux();

            destacarEsperar(1);
            int dist, pos, aux, i;
            destacarEsperar(2);
            dist  = 1;
            mostrarVars("dist", dist);
            destacarEsperar(3);
            while(dist < vet.length) {
                destacarEsperar(4);
                dist = (dist*2) +1;
                mostrarVars("dist", dist);
                destacarEsperar(3);
            }
            destacarEsperar(5);
            dist/=2;
            mostrarVars("dist", dist);
            destacarEsperar(6);
            while(dist > 0){
                destacarEsperar(7);
                i = dist;
                mostrarVars("i", i);
                destacarEsperar(8);
                while(i < vet.length){
                    destacarEsperar(9);
                    pos = i;
                    mostrarVars("pos", pos);
                    colorirIndices(pos, COR_DESTAQUE, -1, COR_SECUNDARIA, -1);
                    destacarEsperar(10);
                    aux = Integer.parseInt(vet[pos].getText());
                    mostrarVars("aux", aux);
                    voar(String.valueOf(aux), xDoIndice(pos), Y_VETOR, xDoIndice(pos), Y_CAIXA_AUX);
                    mostrarCaixaAux(caixaAux, aux, pos);

                    colorirIndices(pos, COR_DESTAQUE, pos >= dist ? pos - dist : -1, COR_SECUNDARIA, -1);
                    destacarEsperar(11);
                    while(pos > 0 && aux < Integer.parseInt(vet[pos-dist].getText())){
                        destacarEsperar(12);
                        voar(vet[pos-dist].getText(), xDoIndice(pos-dist), Y_VETOR, xDoIndice(pos), Y_VETOR);
                        escreverVet(pos, String.valueOf(vet[pos-dist].getText()));
                        destacarEsperar(13);
                        pos-=dist;
                        mostrarVars("pos", pos);
                        deslizar(caixaAux, xDoIndice(pos), Y_CAIXA_AUX);
                        colorirIndices(pos, COR_DESTAQUE, pos >= dist ? pos - dist : -1, COR_SECUNDARIA, -1);
                        destacarEsperar(11);
                    }
                    destacarEsperar(15);
                    voar(String.valueOf(aux), xDoIndice(pos), Y_CAIXA_AUX, xDoIndice(pos), Y_VETOR);
                    escreverVet(pos, String.valueOf(aux));
                    naFX(() -> caixaAux.setVisible(false));
                    destacarEsperar(16);
                    i++;
                    mostrarVars("i", i);
                    destacarEsperar(8);
                }
                destacarEsperar(18);
                dist/=2;
                mostrarVars("dist", dist);
                destacarEsperar(6);
            }
            removerNo(caixaAux);
            concluir();
        }

    }

    private void rodarOrdenacao(String titulo, Runnable metodo) {
        Texto.setText(titulo);
        Thread threadOrdenacao = new Thread(metodo);
        threadOrdenacao.setDaemon(true);
        threadOrdenacao.start();
    }

    private Button criarBotaoOrdenacao(String titulo, Runnable metodo) {
        Button botao = new Button(titulo);
        botao.setOnAction(e -> rodarOrdenacao(titulo, metodo));
        return botao;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Pesquisa e Ordenacao");

        pane = new AnchorPane();
        pane.setPrefWidth(LARGURA_PAINEL_ESQUERDO);
        paneVetor = new AnchorPane();
        paneAux = new AnchorPane();

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

        HBox linhaNovosMetodos = new HBox(8);
        linhaNovosMetodos.setLayoutX(10);
        linhaNovosMetodos.setLayoutY(95);
        linhaNovosMetodos.getChildren().addAll(
                criarBotaoOrdenacao("Counting Sort", () -> mt.counting_sort()),
                criarBotaoOrdenacao("Insertion Sort", () -> mt.animarInsertion()),
                criarBotaoOrdenacao("Merge Sort", () -> mt.animarMerge()),
                criarBotaoOrdenacao("Tim Sort", () -> mt.TimSort()),
                criarBotaoOrdenacao("Radix Sort", () -> mt.RadixSort()),
                criarBotaoOrdenacao("Bucket Sort", () -> mt.BucketSort()),
                criarBotaoOrdenacao("Comb Sort", () -> mt.combSort()),
                criarBotaoOrdenacao("Shell Sort", () -> mt.ShellSort())
        );
        pane.getChildren().add(linhaNovosMetodos);

        Texto = new Label();
        Texto.setText("Ola");
        Texto.setFont(new Font(22));
        Texto.setStyle("-fx-font-weight: bold;");
        Texto.setAlignment(Pos.CENTER);
        Texto.setMouseTransparent(true);

        AnchorPane.setTopAnchor(Texto, Double.valueOf(135.0));
        AnchorPane.setLeftAnchor(Texto, Double.valueOf(0.0));
        AnchorPane.setRightAnchor(Texto, Double.valueOf(0.0));
        pane.getChildren().add(Texto);

        vet = new Button[QTD_ELEMENTOS];
        paneVetor.setMouseTransparent(true);
        paneAux.setMouseTransparent(true);
        pane.getChildren().addAll(paneVetor, paneAux);
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
        if (paneAux != null) paneAux.getChildren().clear();

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
