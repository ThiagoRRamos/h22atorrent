package org.ita.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class AvisadorPosse {
	
	String hostName = "localhost";
	int portNumber = 4567;
	
	public boolean avisarPosse(String fileName, int pedaco){
		try (Socket echoSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(echoSocket.getOutputStream(),
						true);
				InputStream is = echoSocket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(isr);
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			//Avisar posse aqui
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
