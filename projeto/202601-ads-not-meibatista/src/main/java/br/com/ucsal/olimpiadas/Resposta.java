package br.com.ucsal.olimpiadas;

public class Resposta {

	private long questaoId;
	private char alternativaMarcada;
	private boolean correta;

	public Resposta() {
	}

	public Resposta(long questaoId, char alternativaMarcada, boolean correta) {
		this.questaoId = questaoId;
		this.alternativaMarcada = alternativaMarcada;
		this.correta = correta;
	}

	public long getQuestaoId() {
		return questaoId;
	}

	public void setQuestaoId(long questaoId) {
		this.questaoId = questaoId;
	}

	public char getAlternativaMarcada() {
		return alternativaMarcada;
	}

	public void setAlternativaMarcada(char alternativaMarcada) {
		this.alternativaMarcada = alternativaMarcada;
	}

	public boolean isCorreta() {
		return correta;
	}

	public void setCorreta(boolean correta) {
		this.correta = correta;
	}
}
