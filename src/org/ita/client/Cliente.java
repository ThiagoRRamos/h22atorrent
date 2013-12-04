package org.ita.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Cliente {
	public static void main(String[] args) throws IOException {

		String hostName = "localhost";
		int portNumber = 4567;

		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						echoSocket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			while(true){
				System.out.println("Escreva o nome do arquivo .tracker");
				String userInput;
				boolean ok = true;
				while (ok && (userInput = stdIn.readLine()) != null) {
					System.out.println(userInput.isEmpty());
					if (userInput.isEmpty())
						ok = false;
					out.println(userInput);
				}
				System.out.println("Mandado");
				String resposta = in.readLine();
				System.out.println("Reposta:" + resposta);
				if(resposta.equals("Nao")){
					System.out.println("Resposta negativa");
					break;
				} else if(resposta.equals("Sim")) {
					System.out.println("Comecou leitura");
					String quantBytes = in.readLine();
					quantBytes = quantBytes.replaceFirst("Quantidade de bytes: ", "");
					int infoBytes = Integer.parseInt(quantBytes);
					String hash = in.readLine();
					hash = hash.replaceFirst("Hash: ", "");
					String conteudo;
					String emLeitura = in.readLine();
					conteudo = emLeitura;
					ok = true;
					while(ok && (emLeitura = in.readLine()) != null){
						if (emLeitura.isEmpty()){
							ok = false;
							conteudo += emLeitura;
						}else
							conteudo += emLeitura + "\n";
					}
					System.out.println("Terminou leitura");
					int tamanhoBytes = conteudo.getBytes().length;
//					String hashCalculated = calculateMD5(conteudo);
					if(tamanhoBytes == infoBytes /*&& hashCalculated.equals(hash)*/){
						System.out.println("arquivo recebido com sucesso");
						System.out.println(conteudo);
						break;
					}
					if(tamanhoBytes != infoBytes){
						System.out.println("Tam Esperado " + infoBytes);
						System.out.println("Tam Recebido " + tamanhoBytes);
					}
					
					//Por enquanto ignorar hash
//					if(!hashCalculated.equals(hash)){
//						System.out.println("Hash Esperado " + hash);
//						System.out.println("Hash Recebido " + hashCalculated);
//					}
					System.out.println("Os dados do arquivo recebido não batem");
					continue;
				} else {
					System.out.println("Resposta desconhecida do servidor: " + resposta);
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
	}
	
	private static String calculateMD5(String text){
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		m.reset();
		m.update(text.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1,digest);
		String hashtext = bigInt.toString(16);
		while(hashtext.length() < 32 ){
		  hashtext = "0"+hashtext;
		}
		return hashtext;
	}
	
}
