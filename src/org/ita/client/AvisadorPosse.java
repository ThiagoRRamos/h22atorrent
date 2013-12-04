package org.ita.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class AvisadorPosse {

	public AvisadorPosse() {
	}

	public boolean avisarPosse(String myIp, int myPort, String fileName,
			int pedaco) {
		try (Socket echoSocket = new Socket(Cliente.serverName,
				Cliente.serverPort);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				InputStream is = echoSocket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(isr);
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			while (true) {
				out.println("Posse");
				int rand = (new Random()).nextInt((int) Math.pow(2, 32));
				out.println(rand);
				out.println(fileName);
				out.println("pedaco:" + pedaco);
				out.println("ip:" + myIp);
				out.println("porta:" + myPort);
				out.println("");

				String resposta = in.readLine();
				if (resposta.equals("OK")) {
					resposta = in.readLine();
					if (Integer.parseInt(resposta) == rand) {
						return true;
					} else {
						System.err.println("Rand não bateu");
						return false;
					}
				} else if (resposta.equals("Repetir")) {
					if (Integer.parseInt(resposta) == rand)
						System.out.println("Rand bateu, repetir mensagem");
					else {
						System.err.println("Rand não bateu");
						return false;
					}
				}
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + Cliente.serverName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ Cliente.serverName);
			System.exit(1);
		}
		return false;
	}

}
