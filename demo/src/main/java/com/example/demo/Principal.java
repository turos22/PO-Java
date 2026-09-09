package com.example.demo;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import java.util.Random;

public class Principal extends Application {
    AnchorPane pane;
    Button botao_inicio;
    Label Texto;
    private Button vet[];
    Label pivo;


    public static void main(String[] args)
    {
        launch(args);
    }

    public void Trocar(int i, int j){
        Button aux;
        aux = vet[j];
        vet[j] = vet[i];
        vet[i] = aux;
    }

    public class MetodosOrd {
        public MetodosOrd() {}



        public void gnomeSort() {
            int i = 0;

            while (i < vet.length) {
                if ( i == 0 || Integer.parseInt(vet[i].getText()) >= Integer.parseInt(vet[i-1].getText()))
                    i++;
                else
                {
                    Trocar(i, i-1);
                    move_botoes(vet, i, i-1);
                    i--;
                }
            }
        }

        //public void timSort() {
        //}

        public void countingSort() {
            int k = 0;
            for (int i = 0; i < vet.length; i++) {
                if (k < Integer.parseInt(vet[i].getText()))
                    k =  Integer.parseInt(vet[i].getText());
            }

            int cumulativo[] = new int[k+1];
            //Guarda as frequencias
            for (int i = 0; i<vet.length;i++){
                cumulativo[Integer.parseInt(vet[i].getText())] += 1;
            }

            //Junta elas de forma cumulativa
            for (int i = 1; i<cumulativo.length;i++){
                cumulativo[i] +=  cumulativo[i-1];
            }

            Button vetor_resultado[] = new Button[vet.length];
            for (int i = vet.length - 1; i >= 0; i--) {
                int valor = Integer.parseInt(vet[i].getText());
                vetor_resultado[cumulativo[valor] - 1] = vet[i];
                cumulativo[valor]--;
            }

            vet = vetor_resultado;
        }

        public void quickSortPivo(int ini, int fim) {
           int pivto = Integer.parseInt(vet[(ini+fim)/2].getText());
           pivo.setText(Integer.toString(pivto));
           int aux;
           int i = ini, j = fim;
           while(i<j){
               while(Integer.parseInt(vet[i].getText()) < pivto)i++;
               while(Integer.parseInt(vet[j].getText()) > pivto)j--;
               if (i <= j){
                   Trocar(i, j);
                   move_botoes(vet, i, j);
                   i++;
                   j--;
               }
           }
           if (ini < j)
               quickSortPivo(ini, j);
           if (fim > i)
               quickSortPivo(i, fim);
        }

        //public void mergeSort() {
        //}

        //public void radixSort() {
        //}

        //public void combSort() {
        //}

        //public void bucketSort() {
        //}

        public void heapSort() {
            int TL2 = vet.length, pai, F1, F2;
            int aux, maiorF;

            for(TL2 = vet.length; TL2 > 1; TL2--){
                {
                    pai = TL2/2-1;
                    while(pai >=0){
                        F1 = pai*2+1;
                        F2 = F1+1;
                        maiorF = F1;
                        if (F2 < TL2 && Integer.parseInt(vet[F2].getText()) > Integer.parseInt(vet[F1].getText()))
                            maiorF = F2;
                        if (Integer.parseInt(vet[maiorF].getText()) > Integer.parseInt(vet[pai].getText())){
                            Trocar(pai, maiorF);
                            move_botoes(vet, pai, maiorF);
                        }
                        pai--;
                    }

                }
                Trocar(0, TL2-1);
                move_botoes(vet, 0, TL2-1);
            }
        }

        public void shellSort() {
        }
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setTitle("Pesquisa e Ordenacao");
        pane = new AnchorPane();
        AnchorPane paneVetor = new AnchorPane();


        pivo =new Label();
        pivo.setLayoutX(20); pivo.setLayoutY(140);
        pivo.setText("safewefewefsefsaefsfsefsfsfsefsefsfsefsfsdf");
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
        botao_inicio.setLayoutX(10); botao_inicio.setLayoutY(100);
        botao_inicio.setText("Gerar Novos números desordenados");
        botao_inicio.setOnAction(e -> {GerarAleatorio(vet, paneVetor);});

        Button botao_quicksempivo = new Button();
        botao_quicksempivo.setLayoutX(10); botao_quicksempivo.setLayoutY(140);
        botao_quicksempivo.setText("QuickSort Com Pivo");
        botao_quicksempivo.setOnAction(e -> {Texto.setText("QuickSort Com Pivo");
            Thread threadOrdenacao = new Thread(() -> mt.quickSortPivo(0, 19));
            threadOrdenacao.setDaemon(true);
            threadOrdenacao.start();
        });

        Button botao_Gnome = new Button();
        botao_Gnome.setLayoutX(150); botao_Gnome.setLayoutY(140);
        botao_Gnome.setText("Gnome");
        botao_Gnome.setOnAction(e -> {Texto.setText("Gnome Sort");
            Thread threadOrdenacao = new Thread(() -> mt.gnomeSort());
            threadOrdenacao.setDaemon(true);
            threadOrdenacao.start();
        });

        Button botao_count = new Button();
        botao_count.setLayoutX(220); botao_count.setLayoutY(140);
        botao_count.setText("Counting Sort");
        botao_count.setOnAction(e -> {
            Texto.setText("Counting Sort");
            paneVetor.getChildren().clear();

            Thread threadOrdenacao = new Thread(() -> {

                // Ordena primeiro
                mt.countingSort();

                // Depois atualiza a interface
                Platform.runLater(() -> {

                    int layoutX = 100;

                    for (int i = 0; i < vet.length; i++, layoutX += 60) {
                        vet[i] = new Button(String.valueOf(vet[i].getText()));

                        vet[i].setLayoutX(layoutX);
                        vet[i].setLayoutY(200);
                        vet[i].setMinHeight(40);
                        vet[i].setMinWidth(40);
                        vet[i].setFont(new Font(18));

                        paneVetor.getChildren().add(vet[i]);
                    }
                });
            });

            threadOrdenacao.setDaemon(true);
            threadOrdenacao.start();
        });

        Button botao_heap = new Button();
        botao_heap.setLayoutX(320); botao_heap.setLayoutY(140);
        botao_heap.setText("Heap Sort");
        botao_heap.setOnAction(e -> {Texto.setText("Heap Sort");
            Thread threadOrdenacao = new Thread(() -> mt.heapSort());
            threadOrdenacao.setDaemon(true);
            threadOrdenacao.start();
        });



        pane.getChildren().addAll(botao_inicio, botao_quicksempivo,  botao_Gnome,  botao_count,   botao_heap);

        Texto = new Label();
        Texto.setText("Ola");
        Texto.setFont(new Font(20));
        Texto.setStyle("-fx-font-weight: bold;");
        Texto.setAlignment(Pos.CENTER);
        Texto.setMouseTransparent(true);

        AnchorPane.setTopAnchor(Texto, 100.0);
        AnchorPane.setLeftAnchor(Texto, 0.0);
        AnchorPane.setRightAnchor(Texto, 0.0);

        pane.getChildren().add(Texto);
        vet = new Button[20];
        paneVetor.setMouseTransparent(true);
        pane.getChildren().addAll(paneVetor);
        Scene scene = new Scene(pane, 1600, 900);
        stage.setScene(scene);
        stage.show();
    }

//    public void IniciarAcao(){
//        Thread threadOrdenacao = new Thread(() -> mt.quickSortSemPivo(0, 19));
//        threadOrdenacao.setDaemon(true);
//        threadOrdenacao.start();
//    }

    private void move_botoes(Button vet[], int i, int j)
    {
        if (i == j) return;

        Button botaoI = vet[i];
        Button botaoJ = vet[j];
        double distancia = botaoJ.getLayoutX() - botaoI.getLayoutX(); // corrigido: era getLayoutY()
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

    private void GerarAleatorio(Button vet[], AnchorPane pane){
        pane.getChildren().clear();
        int layoutX = 100;

        for (int i = 0; i < 20; i++, layoutX+=60)
        {
            Random rand = new Random();
            int num = rand.nextInt(1, 101);
            vet[i] = new Button(String.valueOf(num));
            vet[i].setLayoutX(layoutX); vet[i].setLayoutY(200);
            vet[i].setMinHeight(40); vet[i].setMinWidth(40);
            vet[i].setFont(new Font(18));
            pane.getChildren().add(vet[i]);
        }
    }
}