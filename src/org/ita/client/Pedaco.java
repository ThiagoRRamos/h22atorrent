package org.ita.client;

import java.util.ArrayList;
import java.util.List;

public class Pedaco {
	
	private List<Fornecedor> fornecedores;
	

	public Pedaco() {
		fornecedores = new ArrayList<>();
	}
	
	public void addFornecedor(String ip, int porta){
		fornecedores.add(new Fornecedor(ip, porta));
	}
	
	public List<Fornecedor> getFornecedores() {
		return fornecedores;
	}
	
}
