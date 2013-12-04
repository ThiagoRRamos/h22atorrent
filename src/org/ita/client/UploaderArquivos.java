package org.ita.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.ita.server.MD5Checksum;

public class UploaderArquivos implements Runnable {

	private Socket client;

	public UploaderArquivos(Socket clientSocket) {
		this.client = clientSocket;
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
			if (listLines.size() == 4) {
				String aptr = listLines.get(0);
				String rand = listLines.get(1);
				String filename = listLines.get(2);
				String pieceNumber = listLines.get(3);
				if (aptr.equals("Voce tem")) {
					int pn = Integer.parseInt(pieceNumber.replaceAll("pedaco:",
							""));
					if (tenhoarquivo(filename, pn)) {
						enviarArquivo(filename, pn, rand, out);
					} else {
						out.println("Nao");
						out.println(rand);
					}
				}

			} else {
				System.out.println("Temos erro");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void enviarArquivo(String filename, int pieceNumber, String rand,
			PrintWriter out) {
		out.println("Sim");
		out.println(rand);
		Path p = FileSystems.getDefault().getPath(
				filename + ".h22apart." + pieceNumber);
		out.println("Quantidade de bytes:" + getQtdeBytes(p));
		out.println("Hash:" + getHash(p));
		try {
			for (byte b : Files.readAllBytes(p)) {
				out.print(String.format("%02X", b));
			}
			out.println("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.println("Fim do pedaco");
	}

	private boolean tenhoarquivo(String filename, int pieceNumber) {
		String f = filename + ".h22apart." + pieceNumber;
		Path p = FileSystems.getDefault().getPath(f);
		return Files.exists(p);
	}

	private String getHash(Path p) {
		try {
			return MD5Checksum.getMD5Checksum(p.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "FAIL";
	}

	private int getQtdeBytes(Path p) {
		try {
			return Files.readAllBytes(p).length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

}
