package org.ita.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
			} else if (ehAvisodePosse(listLines)) {
				if (avisoDePosseCorreto(listLines)) {

				} else {

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
		System.out.println("Nao\n");
		out.println("Nao\n");
	}

	private void enviarArquivo(String nomeArquivo, PrintWriter out) {
		out.println("Sim");
		Path p = pegarArquivo(nomeArquivo);
		int qteBytes = getQtdeBytes(p);
		out.println("Quantidade de bytes: " + qteBytes);
		System.out.println("Quantidade de bytes: " + qteBytes);
		String hash = getHash(p);
		out.println("Hash: " + hash);
		System.out.println("Hash: " + hash);
		try (BufferedReader br = new BufferedReader(
				new FileReader(p.toString()))) {
			String a;
			while ((a = br.readLine()) != null) {
				out.println(a);
				System.out.println(a);
			}
			out.println("");
			System.out.println("");
		} catch (IOException e) {

		}

	}

	private String getHash(Path p) {
		try {
			return MD5Checksum.getMD5Checksum(p.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "FAIL";
	}

	private int getQtdeBytes(Path p) {
		try {
			return Files.readAllBytes(p).length;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private Path pegarArquivo(String nomeArquivo) {
		return FileSystems.getDefault().getPath("file.tracker");
	}

	private boolean temArquivo(String nomeArquivo) {
		return true;
	}

	private String getNomeArquivo(List<String> listLines) {
		return null;
	}

	private boolean ehRequisicaodeArquivo(List<String> listLines) {
		return true;
	}

}
