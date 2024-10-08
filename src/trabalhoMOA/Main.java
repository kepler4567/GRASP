package trabalhoMOA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Locale.setDefault(Locale.US);
		Scanner sc = new Scanner(System.in);

		System.out.println("Entre com o caminho do arquivo de entrada: ");
		String path = sc.nextLine();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {

			String line = br.readLine();
			int qtdMochilas = Integer.parseInt(line);
			int[] capacidadesMochila = new int[qtdMochilas];

			line = br.readLine();
			int qtdItens = Integer.parseInt(line);

			for (int i = 0; i < qtdMochilas; i++) {
				line = br.readLine();
				capacidadesMochila[i] = Integer.parseInt(line);
			}

			Item[] arrayItens = new Item[qtdItens];
			for (int i = 0; i < qtdItens; i++) {
				line = br.readLine();
				String[] fields = line.split("\\s+");

				int peso = Integer.parseInt(fields[0]);
				int valor = Integer.parseInt(fields[1]);
				arrayItens[i] = new Item(peso, valor);
			}
			
			long startTime = System.nanoTime();

			// Ordenar os itens com base na razão valor/peso
			Arrays.sort(arrayItens,
					(itemA, itemB) -> Double.compare(itemB.getValueWeightRatio(), itemA.getValueWeightRatio()));

			int[] melhorDistribuicaoItens = construirSolucaoGulosa(arrayItens, capacidadesMochila.clone());

			// Calcular o valor total da solução
			int valorTotal = calcularValor(melhorDistribuicaoItens, arrayItens);

			imprimirSolucaoDetalhada(melhorDistribuicaoItens, arrayItens, capacidadesMochila, valorTotal);
			
			// Fim do tempo de execução
            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;

            // Imprimir o tempo de execução em segundos
            System.out.println("=====================================");
            System.out.println("Tempo de execução: " + String.format("%.2f", (executionTime / 1_000_000_000.0)) + " segundos");
            System.out.println("=====================================");

		} catch (IOException e) {
			System.out.println("Erro: " + e);
		}

		sc.close();
	}

	private static int[] construirSolucaoGulosa(Item[] itens, int[] capacidadesMochila) {
		int numItens = itens.length;
		int numMochilas = capacidadesMochila.length;
		int[] distribuicaoItens = new int[numItens * numMochilas];

		// Tentar alocar os itens com base na razão valor/peso
		for (int i = 0; i < numItens; i++) {
			for (int j = 0; j < numMochilas; j++) {
				boolean cabeItemNaMochida = capacidadesMochila[j] >= itens[i].getWeight();
				if (cabeItemNaMochida) {
					// Aloca o item na mochila j
					int posicaoItem = i + j * numItens;
					distribuicaoItens[posicaoItem] = 1;

					// retira o peso do item da mochila
					int pesoItem = itens[i].getWeight();
					capacidadesMochila[j] -= pesoItem;
					break;
				}
			}
		}

		return distribuicaoItens;
	}

	private static int calcularValor(int[] solucao, Item[] itens) {
		int valorTotal = 0;
		for (int i = 0; i < solucao.length; i++) {
			boolean itenEstaAlocado = solucao[i] == 1;
			if (itenEstaAlocado) {
				int posicaoItem = i % itens.length;
				valorTotal += itens[posicaoItem].getValue();
			}
		}
		return valorTotal;
	}

	private static void imprimirSolucaoDetalhada(int[] solucao, Item[] itens, int[] capacidadesIniciais,
			int valorTotal) {
		int numMochilas = capacidadesIniciais.length;
		int numItens = itens.length;

		System.out.println("Solução Final Detalhada:");
		for (int j = 0; j < numMochilas; j++) {
			int somaPesos = 0;
			int somaValores = 0;
			System.out.println("Mochila " + j + " (Capacidade Inicial: " + capacidadesIniciais[j] + ")");
			for (int i = 0; i < numItens; i++) {
				boolean itemEstaAlocado = solucao[i + j * numItens] == 1;
				if (itemEstaAlocado) {
					somaPesos += itens[i].getWeight();
					somaValores += itens[i].getValue();
					System.out.println(
							"  Item " + i + " - Peso: " + itens[i].getWeight() + ", Valor: " + itens[i].getValue());
				}
			}
			int capacidadeRestante = capacidadesIniciais[j] - somaPesos;

			if (capacidadeRestante < 0) {
				System.out.println("** Erro: A capacidade restante não pode ser negativa. **");
			}

			System.out.println("  Soma dos Pesos: " + somaPesos);
			System.out.println("  Soma dos Valores: " + somaValores);
			System.out.println("  Capacidade Restante após alocação: " + capacidadeRestante);
			System.out.println();
		}
		System.out.println("Valor Total da Solução: " + valorTotal);
	}

}
