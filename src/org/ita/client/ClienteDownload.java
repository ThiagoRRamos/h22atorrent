package org.ita.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteDownload {
	public static void main(String[] args) throws IOException {
		String hostName = "localhost";
		int portNumber = 4568;
		ClienteDownload cd = new ClienteDownload();
		cd.pedirArquivo(hostName, portNumber, "arquivo.nome", 1);
	}

	public boolean pedirArquivo(String hostName, int portNumber,
			String filename, int pedaco) {

		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				InputStream is = echoSocket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(isr);
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			out.println("Voce tem");
			int rand = 35;
			out.println(rand);
			out.println(filename);
			out.println("pedaco:" + pedaco);
			out.println("");
			System.out.println("Mandado");
			String resposta = in.readLine();
			System.out.println("Reposta:" + resposta);
			if (resposta.equals("Nao")) {
				System.out.println("Resposta negativa");
				return true;
			} else if (resposta.equals("Sim")) {
				System.out.println("Comecou leitura");
				String rando = in.readLine();
				if (Integer.parseInt(rando) == rand)
					System.out.println("Rand OK");
				String quantBytes = in.readLine();
				quantBytes = quantBytes
						.replaceFirst("Quantidade de bytes:", "");
				System.out.println(quantBytes);
				int infoBytes = Integer.parseInt(quantBytes);
				String hash = in.readLine();
				hash = hash.replaceFirst("Hash:", "");
				System.out.println(hash);
				byte[] arquivo = lerArquivo(in, infoBytes);
				System.out.println("Arquico lido");
				String fim = in.readLine();
				File f = new File("arroba.txt");
				f.createNewFile();
				if (fim.equals("Fim do pedaco")) {
					System.out.println("Fiimm");
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(arquivo);
					fos.flush();
					fos.close();
					return true;
				} else {
					System.out.println(fim);
					return false;
				}
			} else {
				return false;
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			System.exit(1);
		}
		return false;
	}

	private static byte[] lerArquivo(BufferedReader is, int quantBytes) {
		byte[] res = new byte[quantBytes];
		try {
			String sres = is.readLine();
			System.out.println(quantBytes + "-" + sres.length());
			for (int i = 0; i < quantBytes; i++) {
				res[i] = Byte.parseByte(sres.substring(2 * i, 2 * i + 2), 16);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}
