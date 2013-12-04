package org.ita.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.ita.server.MD5Checksum;

public class Cliente {
	public static void main(String[] args){
		String fileName = "file";
		if (pedirArquivoServidor(fileName)) {
			System.out.println("Arquivo chegou");
			ClienteDownload cd = new ClienteDownload(fileName);
			System.out.println("Aca");
			while(!cd.tentarBaixarPedacos()){
				System.out.println("AAA");
				try {
					System.out.println("Ainda nao deu");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("OPSaa");
			try {
				cd.juntarPedacos();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private static boolean pedirArquivoServidor(String fileName) {
		String hostName = "localhost";
		int portNumber = 4567;

		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			while (true) {
				out.println("Voce tem " + fileName + ".tracker");
				out.println();
				System.out.println("Mandado");
				String resposta = in.readLine();
				System.out.println("Reposta:" + resposta);
				if (resposta.equals("Nao")) {
					System.out.println("Resposta negativa");
					break;
				} else if (resposta.equals("Sim")) {
					String quantBytes = in.readLine();
					quantBytes = quantBytes.replaceFirst(
							"Quantidade de bytes: ", "");
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
					if (tamanhoBytes == infoBytes
							&& hashCalculated.equals(hash)) {
						System.out.println("arquivo recebido com sucesso");
						return true;
					}
					if (tamanhoBytes != infoBytes) {
						System.out.println("Tam Esperado " + infoBytes);
						System.out.println("Tam Recebido " + tamanhoBytes);
						return false;
					}

					if (!hashCalculated.equals(hash)) {
						System.out.println("Hash Esperado " + hash);
						System.out.println("Hash Recebido " + hashCalculated);
						return false;
					}
					System.out
							.println("Os dados do arquivo recebido não batem");
					continue;
				} else {
					System.out.println("Resposta desconhecida do servidor: "
							+ resposta);
					continue;
				}
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
