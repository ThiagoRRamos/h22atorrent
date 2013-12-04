package org.ita.client;

public class Fornecedor {
	private String ip;
	private int porta;
	
	public Fornecedor(String ip, int porta) {
		super();
		this.ip = ip;
		this.porta = porta;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}
	
}
