package com.example.demo;

/**
 * Todos os métodos de ordenação do projeto escritos sobre vetor de inteiros,
 * sem nenhuma dependência de JavaFX. Esta classe não é chamada em lugar nenhum:
 * serve de referência "limpa" dos algoritmos que a interface anima.
 *
 * Os métodos mantêm os mesmos nomes e a mesma estrutura das versões em Principal.
 * Pré-condição de counting_sort: valores >= 1 (o índice usado é valor - 1).
 */
public class OrdenacoesInteiros {

    private int[] vet;

    public OrdenacoesInteiros(int[] vet) {
        this.vet = vet;
    }

    public int[] getVet() {
        return vet;
    }

    public void Trocar(int i, int j) {
        int aux;
        aux = vet[j];
        vet[j] = vet[i];
        vet[i] = aux;
    }

    public void gnomeSort() {
        int i = 0;
        while (i < vet.length) {
            if (i == 0 || vet[i] >= vet[i - 1])
                i++;
            else {
                Trocar(i, i - 1);
                i--;
            }
        }
    }

    public void quickSortPivo(int ini, int fim) {
        int pivo = vet[(ini + fim) / 2];
        int i = ini, j = fim;
        while (i <= j) {
            while (i <= fim && vet[i] < pivo) i++;
            while (j >= ini && vet[j] > pivo) j--;
            if (i <= j) {
                Trocar(i, j);
                i++;
                j--;
            }
        }
        if (ini < j) quickSortPivo(ini, j);
        if (fim > i) quickSortPivo(i, fim);
    }

    public void heapSort() {
        int TL2, pai, F1, F2, maiorF;
        for (TL2 = vet.length; TL2 > 1; TL2--) {
            pai = TL2 / 2 - 1;
            while (pai >= 0) {
                F1 = pai * 2 + 1;
                F2 = F1 + 1;
                maiorF = F1;
                if (F2 < TL2 && vet[F2] > vet[F1])
                    maiorF = F2;
                if (vet[maiorF] > vet[pai])
                    Trocar(pai, maiorF);
                pai--;
            }
            Trocar(0, TL2 - 1);
        }
    }

    public void counting_sort(){
        int k=0;
        int[] novo_vetor = new int[vet.length];

        for (int i = 0; i <vet.length; i++)
            if (k <= vet[i]) k = vet[i];

        int[] vet_aux = new int[k];

        //constroi vetor com os valores de i
        for (int i =0; i<vet.length; i++)
            vet_aux[vet[i] - 1] += 1;

        //preenche a soma cumulativa
        for (int i = 1; i < k; i++)
            vet_aux[i] = vet_aux[i] + vet_aux[i-1];

        for (int i = vet.length-1; i>=0; i--)
        {
            //Criacao do novo vetor
            int ai = vet[i] -1;
            novo_vetor[ vet_aux[ai]-1] = vet[i];
            vet_aux[ai] -=1;
            //Explicacao: vet_aux[ai] -> Quantidade acumulada para o numero obtido no vetor A
            // Se a quantidade acumulada para o numero 10 do vetor A por exemplo for 5
            // isso indica que o lugar "ideal" seria na 5 posicao do vetor
            // ou seja o indice 4, pot isso o vet_aux[ai]-1 na linha de cima
        }

        //Popular vetor com o novo ordenado
        for (int i = 0; i < novo_vetor.length; i++)
            vet[i] = novo_vetor[i];
    }

    public void insertion_sort(int ini, int fim){
        int pos,aux, i;
        i = ini;

        while(i <= fim){
            pos = i;
            aux = vet[i];
            while(pos > ini && aux < vet[pos-1])
            {
                vet[pos] = vet[pos-1];
                pos--;
            }
            vet[pos] = aux;
            i++;
        }
    }

    public void Merge(int esq, int dir, int aux[]){
        if (esq < dir){
            int meio = (esq+dir)/2;
            Merge(esq, meio, aux);
            Merge(meio+1, dir, aux);
            Fusao(esq, meio, meio+1, dir, aux);
        }
    }

    public void Fusao(int ini1, int fim1, int ini2, int fim2, int aux[]){
        int k=0, i =ini1, j = ini2;
        while(i <= fim1 && j <= fim2){
            if (vet[i] < vet[j])
                aux[k++] = vet[i++];
            else
                aux[k++] = vet[j++];
        }
        while(i <= fim1)
            aux[k++] = vet[i++];
        while(j <= fim2)
            aux[k++] = vet[j++];

        //troca no vetor original
        for(int alvo = 0; alvo<k; alvo++){
            vet[alvo+ini1] = aux[alvo];
        }
    }

    public void TimSort(){
        //Insertion Sort + merge Sort utilizando grupos
        // Cada grupo é uma Run, onde cada run eh ordenado com insertion sort
        //Depois Merge une todos

        int run_size = 4;
        for (int i=0; i< vet.length; i+=run_size)
            insertion_sort(i, i+run_size > vet.length ? vet.length -1 : i+run_size-1);

        int[] aux = new int[vet.length];
        // Merge Sort de 2 runs, por isso 2 * run_size
        for (int particoes = run_size;  particoes < vet.length; particoes *=2)
            for (int esq = 0; esq<vet.length; esq += 2 * particoes) {
                int meio = esq + particoes - 1;
                int dir = esq + 2*particoes-1 > vet.length-1 ? vet.length -1 : esq + 2*particoes-1;
                if (meio < dir)
                    Fusao(esq, meio, meio+1, dir, aux);
            }
    }

    public void RadixSort(){
        // Counting Sort Diferente
        // Para cada iteracao, oa inves de fazer de modo geral
        // Ele faz pegando pelo tamanho em digitos
        int k=0;
        for (int i = 0; i <vet.length; i++)
            if (k <= vet[i]) k = vet[i];

        for(int exp = 1; k/exp>0; exp *=10)
            count(exp);
    }

    public void count(int exp){
        int[] array_aux = new int[10];
        int[] saida = new int[vet.length];
        for (int i = 0; i<vet.length; i++)
            array_aux[(vet[i]/exp) % 10]++;

        for (int i = 1; i < array_aux.length; i++)
            array_aux[i] += array_aux[i-1];

        for (int i = vet.length-1; i>=0; i--){
            saida[ array_aux[(vet[i]/exp) % 10]-1 ] = vet[i];
            array_aux[(vet[i]/exp) % 10]--;
        }

        for (int i = 0; i < saida.length; i++)
            vet[i] = saida[i];
    }

    public void bucket_insertion_sort(int vetor[], int tam){
        int pos,aux, i;
        i = 0;

        while(i < tam){
            pos = i;
            aux = vetor[pos];
            while(pos > 0 && aux < vetor[pos-1])
            {
                vetor[pos] = vetor[pos-1];
                pos--;
            }
            vetor[pos] = aux;
            i++;
        }
    }

    public void BucketSort(){
        //Bucket Sort consiste em separa o Array em Caixinhas que tenham algo em comum
        // A depender da regra, eh possível fazer com floats, com  Strings e por ai vai
        // Depois de colocar tudo nos buckets, pode se aplicar qualquer metodo de ordenacao para os mesmo
        // aqui eu coloquei o insertion_sort por ser facil de alterar
        // Cada bucket guarda um range de 10, o numero 21 por exemplo, cairia no bucket 2 pois
        // bucket 0: 0-9, bucket1:10-19, bucket2: 20-29
        int k=0;
        for (int i = 0; i <vet.length; i++)
            if (k <= vet[i]) k = vet[i];

        int[][] buckets = new int[k/10+1][vet.length];
        int[] tamanhos = new int[k/10+1]; // quantos elementos cada bucket ja tem

        for (int i = 0; i< vet.length; i++){
            int indice_bucket = vet[i]/10;
            int pos = tamanhos[indice_bucket]++;
            buckets[vet[i]/10][pos] = vet[i];
        }

        for(int b = 0; b < buckets.length; b++)
            bucket_insertion_sort(buckets[b], tamanhos[b]);

        int[] saida = new int[vet.length];
        int saida_i=0;
        for(int i = 0; i< buckets.length; i++){
            for(int j =0; j<tamanhos[i]; j++){
                saida[saida_i++] = buckets[i][j];
            }
        }
        for(int i = 0; i< vet.length; i++){
            vet[i] = saida[i];
        }
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

        int gap = vet.length;
        int trocado = 1;
        // a variavel trocado eh para verificar se houve mudanca no while
        //se houve, mesmo que gap == 1, eu preciso continuar realizando o bubble sort
        while(gap != 1 || trocado == 1){
            gap =  proximoGap(gap);
            trocado = 0;
            for (int i = 0; i<vet.length-gap; i++){
                if (vet[i] > vet[i+gap]){
                    Trocar(i, i+gap);
                    trocado = 1;
                }
            }
        }
    }

    public void ShellSort(){
        int dist, pos, aux, i;
        dist  = 1;
        while(dist < vet.length)
            dist = (dist*2) +1;
        dist/=2;
        while(dist > 0){
            i = dist;
            while(i < vet.length){
                pos = i;
                aux = vet[pos];
                while(pos >0 && aux < vet[pos-dist]){
                    vet[pos] = vet[pos-dist];
                    pos-=dist;
                }
                vet[pos] = aux;
                i++;
            }
            dist/=2;
        }
    }
}
