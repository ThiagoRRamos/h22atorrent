package org.ita.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServidorTracker implements Runnable {

	private Socket client;

	public ServidorTracker(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		PrintWriter out;
		try {
			out = new PrintWriter(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			System.out.println(client.isClosed());
			List<String> listLines = new ArrayList<>();
			String inputLine;
			boolean ok = true;
			while (ok && (inputLine = in.readLine()) != null) {
				if (inputLine.isEmpty()) {
					ok = false;
				} else {
					listLines.add(inputLine);
				}
			}
			if (ehRequisicaodeArquivo(listLines)) {
				String nomeArquivo = getNomeArquivo(listLines);
				if (temArquivo(nomeArquivo)) {
					enviarArquivo(nomeArquivo, out);
				} else {
					enviarNaoTenhoArquivo(out);
				}
			}else if(ehAvisodePosse(listLines)){
				if(avisoDePosseCorreto(listLines)){
					
				}else{
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean avisoDePosseCorreto(List<String> listLines) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean ehAvisodePosse(List<String> listLines) {
		// TODO Auto-generated method stub
		return false;
	}

	private void enviarNaoTenhoArquivo(PrintWriter out) {
		System.out.println("Nao tem arquivo");
		out.println("Nao\n");
	}

	private void enviarArquivo(String nomeArquivo, PrintWriter out) {

	}

	private boolean temArquivo(String nomeArquivo) {
		return false;
	}

	private String getNomeArquivo(List<String> listLines) {
		return null;
	}

	private boolean ehRequisicaodeArquivo(List<String> listLines) {
		return true;
	}

}
