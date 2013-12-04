package org.ita.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.ita.server.MD5Checksum;

public class Cliente {

	public static String serverName = "localhost";
	public static int serverPort = 4567;
	public static String myAddress = "localhost";
	public static int myPort = 4568;

	public static void main(String[] args) {
		String fileName = "file";
		if (args.length > 0) {
			fileName = args[0];
		}
		if (args.length > 1) {
			serverName = args[1];
		}
		if (args.length > 2) {
			serverPort = Integer.parseInt(args[2]);
		}
		if (args.length > 3) {
			myAddress = args[3];
		}
		if (args.length > 4) {
			myPort = Integer.parseInt(args[4]);
		}
		boolean chegouServidor = false;
		int tentativasServidor = 5;
		while(!chegouServidor && tentativasServidor > 0){
			chegouServidor = pedirArquivoServidor(fileName, serverName, serverPort);
			tentativasServidor--;
			if(!chegouServidor)
				System.err.println("Tentando de novo");
		}
		if (chegouServidor) {
			boolean teveProblema = false;
			int tentativasTotais = 5;
			do {
				tentativasTotais--;
				teveProblema = false;
				ClienteDownload cd = new ClienteDownload(fileName);
				int tentativasBaixar = 5;
				while (!cd.tentarBaixarPedacos() && tentativasBaixar > 0) {
					tentativasBaixar--;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					if (cd.juntarPedacos() == null) {
						teveProblema = true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} while (!teveProblema && tentativasTotais > 0);

		}else{
			System.out.println("Nao deu!");
		}
	}

	private static boolean pedirArquivoServidor(String fileName,
			String hostName, int portNumber) {
		Socket echoSocket = new Socket();
		try {
			echoSocket.connect(new InetSocketAddress(hostName, portNumber),
					5000);
			echoSocket.setSoTimeout(5000);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
				true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {

			out.println("Voce tem " + fileName + ".tracker");
			out.println();
			String resposta = in.readLine();
			if (resposta.equals("Nao")) {
				System.out.println("Resposta negativa");
				echoSocket.close();
				return false;
			} else if (resposta.equals("Sim")) {
				String quantBytes = in.readLine();
				quantBytes = quantBytes.replaceFirst("Quantidade de bytes: ",
						"");
				int infoBytes = Integer.parseInt(quantBytes);
				String hash = in.readLine();
				hash = hash.replaceFirst("Hash: ", "");
				String conteudo;
				String emLeitura = in.readLine();
				conteudo = emLeitura;
				boolean ok = true;
				while (ok && (emLeitura = in.readLine()) != null) {
					if (emLeitura.isEmpty()) {
						ok = false;
						conteudo += emLeitura;
					} else
						conteudo += emLeitura + "\n";
				}
				int tamanhoBytes = conteudo.getBytes().length;
				File arquivo = new File(fileName + "-local.tracker");
				FileWriter writer = new FileWriter(arquivo);
				writer.write(conteudo);
				writer.close();
				Path path = pegarArquivo(fileName + "-local.tracker");
				String hashCalculated = getHash(path);
				if (tamanhoBytes == infoBytes && hashCalculated.equals(hash)) {
					echoSocket.close();
					return true;
				}
				if (tamanhoBytes != infoBytes) {
					System.out.println("Tam Esperado " + infoBytes);
					System.out.println("Tam Recebido " + tamanhoBytes);
					echoSocket.close();
					return false;
				}

				if (!hashCalculated.equals(hash)) {
					System.out.println("Hash Esperado " + hash);
					System.out.println("Hash Recebido " + hashCalculated);
					echoSocket.close();
					return false;
				}
				System.out.println("Os dados do arquivo recebido não batem");
				return false;
			} else {
				System.out.println("Resposta desconhecida do servidor: "
						+ resposta);
				return false;
			}
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
		} catch(SocketTimeoutException s){
			System.err.println("Socket timeout pedindo tracker em " + hostName);
			return false;
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
		} finally {
		}
		return false;
	}

	private static String getHash(Path p) {
		try {
			return MD5Checksum.getMD5Checksum(p.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "FAIL";
	}

	private static Path pegarArquivo(String nomeArquivo) {
		return FileSystems.getDefault().getPath(nomeArquivo);
	}

}
