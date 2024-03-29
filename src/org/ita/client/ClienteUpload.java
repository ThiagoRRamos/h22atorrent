package org.ita.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClienteUpload {

	static ExecutorService ES = Executors.newCachedThreadPool();

	public static void main(String[] args) throws IOException {
		int portNumber = 4568;
		if (args.length > 0) {
			portNumber = Integer.parseInt(args[0]);
		}

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			System.out.println("Cliente Upload iniciado com sucesso");
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Requisicao recebida de "
						+ clientSocket.getLocalAddress());
				processarRequisicao(clientSocket);
			}
		} catch (IOException e) {
			System.out
					.println("Exception caught when trying to listen on port "
							+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}

	private static void processarRequisicao(Socket clientSocket)
			throws IOException {
		UploaderArquivos st = new UploaderArquivos(clientSocket);
		ES.submit(st);
	}
}
