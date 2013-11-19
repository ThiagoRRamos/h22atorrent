package org.ita.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				out.println(inputLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
