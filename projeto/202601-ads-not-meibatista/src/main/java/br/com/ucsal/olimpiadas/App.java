package br.com.ucsal.olimpiadas;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

	static long proximoParticipanteId = 1;
	static long proximaProvaId = 1;
	static long proximaQuestaoId = 1;
	static long proximaTentativaId = 1;

	static final List<Participante> participantes = new ArrayList<>();
	static final List<Prova> provas = new ArrayList<>();
	static final List<Questao> questoes = new ArrayList<>();
	static final List<Tentativa> tentativas = new ArrayList<>();

	private static final Scanner in = new Scanner(System.in);

	public static void main(String[] args) {
		seed();
		executarMenu();
	}

	// ================= MENU =================

	private static void executarMenu() {
		boolean rodando = true;

		while (rodando) {
			mostrarMenu();
			String opcao = in.nextLine();

			switch (opcao) {
			case "1":
				cadastrarParticipante();
				break;
			case "2":
				cadastrarProva();
				break;
			case "3":
				cadastrarQuestao();
				break;
			case "4":
				aplicarProva();
				break;
			case "5":
				listarTentativas();
				break;
			case "0":
				System.out.println("tchau");
				rodando = false;
				break;
			default:
				System.out.println("opção inválida");
			}
		}
	}

	private static void mostrarMenu() {
		System.out.println("\n=== OLIMPÍADA DE QUESTÕES (V1) ===");
		System.out.println("1) Cadastrar participante");
		System.out.println("2) Cadastrar prova");
		System.out.println("3) Cadastrar questão");
		System.out.println("4) Aplicar prova");
		System.out.println("5) Listar tentativas");
		System.out.println("0) Sair");
		System.out.print("> ");
	}

	// ================= CADASTRO =================

	static void cadastrarParticipante() {
		System.out.print("Nome: ");
		String nome = in.nextLine();

		System.out.print("Email: ");
		String email = in.nextLine();

		if (nome == null || nome.trim().isEmpty()) {
			System.out.println("nome inválido");
			return;
		}

		Participante p = new Participante();
		p.setId(proximoParticipanteId++);
		p.setNome(nome);
		p.setEmail(email);

		participantes.add(p);
		System.out.println("Participante cadastrado: " + p.getId());
	}

	static void cadastrarProva() {
		System.out.print("Título da prova: ");
		String titulo = in.nextLine();

		if (titulo == null || titulo.trim().isEmpty()) {
			System.out.println("título inválido");
			return;
		}

		Prova prova = new Prova();
		prova.setId(proximaProvaId++);
		prova.setTitulo(titulo);

		provas.add(prova);
		System.out.println("Prova criada: " + prova.getId());
	}

	static void cadastrarQuestao() {
		if (provas.isEmpty()) {
			System.out.println("não há provas cadastradas");
			return;
		}

		Long provaId = escolherProva();
		if (provaId == null) return;

		System.out.println("Enunciado:");
		String enunciado = in.nextLine();

		String[] alternativas = new String[5];
		for (int i = 0; i < 5; i++) {
			char letra = (char) ('A' + i);
			System.out.print("Alternativa " + letra + ": ");
			alternativas[i] = letra + ") " + in.nextLine();
		}

		System.out.print("Alternativa correta (A–E): ");
		char correta;

		try {
			correta = Questao.normalizarAlternativa(in.nextLine().trim().charAt(0));
		} catch (Exception e) {
			System.out.println("alternativa inválida");
			return;
		}

		Questao q = new Questao();
		q.setId(proximaQuestaoId++);
		q.setProvaId(provaId);
		q.setEnunciado(enunciado);
		q.setAlternativas(alternativas);
		q.setAlternativaCorreta(correta);

		questoes.add(q);
		System.out.println("Questão cadastrada: " + q.getId());
	}

	// ================= PROVA =================

	static void aplicarProva() {
		if (participantes.isEmpty()) {
			System.out.println("cadastre participantes primeiro");
			return;
		}

		if (provas.isEmpty()) {
			System.out.println("cadastre provas primeiro");
			return;
		}

		Long participanteId = escolherParticipante();
		if (participanteId == null) return;

		Long provaId = escolherProva();
		if (provaId == null) return;

		List<Questao> lista = questoes.stream()
				.filter(q -> q.getProvaId() == provaId)
				.toList();

		if (lista.isEmpty()) {
			System.out.println("prova sem questões");
			return;
		}

		Tentativa tentativa = new Tentativa();
		tentativa.setId(proximaTentativaId++);
		tentativa.setParticipanteId(participanteId);
		tentativa.setProvaId(provaId);

		System.out.println("\n--- Início da Prova ---");

		for (Questao q : lista) {

			System.out.println("\nQuestão #" + q.getId());
			System.out.println(q.getEnunciado());

			imprimirTabuleiroFen(q.getFenInicial());

			for (String alt : q.getAlternativas()) {
				System.out.println(alt);
			}

			System.out.print("Resposta: ");
			char marcada;

			try {
				marcada = Questao.normalizarAlternativa(in.nextLine().trim().charAt(0));
			} catch (Exception e) {
				System.out.println("inválida (considerada errada)");
				marcada = 'X';
			}

			Resposta r = new Resposta(
					q.getId(),
					marcada,
					q.isRespostaCorreta(marcada)
			);

			tentativa.adicionarResposta(r);
		}

		tentativas.add(tentativa);

		int nota = calcularNota(tentativa);

		System.out.println("\n--- Fim da Prova ---");
		System.out.println("Nota: " + nota + "/" + tentativa.getRespostas().size());
	}

	// ================= AUX =================

	public static int calcularNota(Tentativa tentativa) {
		int acertos = 0;

		for (Resposta r : tentativa.getRespostas()) {
			if (r.isCorreta()) {
				acertos++;
			}
		}

		return acertos;
	}

	static void listarTentativas() {
		System.out.println("\n--- Tentativas ---");

		for (Tentativa t : tentativas) {
			System.out.printf(
					"#%d | participante=%d | prova=%d | nota=%d/%d%n",
					t.getId(),
					t.getParticipanteId(),
					t.getProvaId(),
					calcularNota(t),
					t.getRespostas().size()
			);
		}
	}

	// ================= ESCOLHAS =================

	static Long escolherParticipante() {
		System.out.println("\nParticipantes:");
		for (Participante p : participantes) {
			System.out.printf("%d - %s%n", p.getId(), p.getNome());
		}

		try {
			long id = Long.parseLong(in.nextLine());
			return participantes.stream().anyMatch(p -> p.getId() == id) ? id : null;
		} catch (Exception e) {
			return null;
		}
	}

	static Long escolherProva() {
		System.out.println("\nProvas:");
		for (Prova p : provas) {
			System.out.printf("%d - %s%n", p.getId(), p.getTitulo());
		}

		try {
			long id = Long.parseLong(in.nextLine());
			return provas.stream().anyMatch(p -> p.getId() == id) ? id : null;
		} catch (Exception e) {
			return null;
		}
	}

	// ================= TABULEIRO =================

	static void imprimirTabuleiroFen(String fen) {
		String[] linhas = fen.split(" ")[0].split("/");

		for (String linha : linhas) {
			for (char c : linha.toCharArray()) {
				if (Character.isDigit(c)) {
					int n = c - '0';
					for (int i = 0; i < n; i++) {
						System.out.print(". ");
					}
				} else {
					System.out.print(c + " ");
				}
			}
			System.out.println();
		}
	}

	// ================= SEED =================

	static void seed() {
		Prova prova = new Prova();
		prova.setId(proximaProvaId++);
		prova.setTitulo("Prova exemplo");
		provas.add(prova);
	}
}
