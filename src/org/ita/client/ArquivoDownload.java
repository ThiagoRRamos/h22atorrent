package org.ita.client;

import java.util.ArrayList;
import java.util.List;

public class ArquivoDownload {
	
	private String nome;
	private int tamanho;
	private List<Pedaco> pedacos;
	private String md5;
	
	public ArquivoDownload(String nome, int tamanho) {
		this.nome = nome;
		this.tamanho = tamanho;
		this.pedacos = new ArrayList<>();
		md5 = "";
	}
	
	public void addPedaco(Pedaco p){
		pedacos.add(p);
	}
	
	public int getTamanho() {
		return tamanho;
	}
	
	public void setTamanho(int tamanho) {
		this.tamanho = tamanho;
	}
	
	public List<Pedaco> getPedacos() {
		return pedacos;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
}
