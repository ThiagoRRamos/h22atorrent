package org.ita.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class AvisadorPosse {

	String hostName = "localhost";
	int portNumber = 4567;

	public boolean avisarPosse(String myIp, int myPort, String fileName,
			int pedaco) {
		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				InputStream is = echoSocket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(isr);
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			while (true) {
				System.out.println("Inicio do aviso");
				out.println("Posse");
				int rand = 35;
				out.println(rand);
				out.println(fileName);
				out.println("pedaco:" + pedaco);
				out.println("ip:" + myIp);
				out.println("porta:" + myPort);
				out.println("");
				System.out.println("Aviso enviado");

				String resposta = in.readLine();
				System.out.println("Reposta:" + resposta);
				if (resposta.equals("OK")) {
					resposta = in.readLine();
					if (Integer.parseInt(resposta) == rand) {
						System.out.println("Funcionou beleza");
						return true;
					} else {
						System.out.println("Rand não bateu");
						return false;
					}
				} else if (resposta.equals("Repetir")) {
					if (Integer.parseInt(resposta) == rand)
						System.out.println("Rand bateu, repetir mensagem");
					else {
						System.out.println("Rand não bateu");
						return false;
					}
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

}
