package org.ita.client;

import java.util.ArrayList;
import java.util.List;

public class Pedaco {

	private List<Fornecedor> fornecedores;
	private int codigo;

	public Pedaco(int i) {
		fornecedores = new ArrayList<>();
		codigo = i;
	}

	public void addFornecedor(String ip, int porta) {
		fornecedores.add(new Fornecedor(ip, porta));
	}

	public List<Fornecedor> getFornecedores() {
		return fornecedores;
	}

	public String toString() {
		return "Pedaco " + codigo + " - " + fornecedores.size();
	}
}
