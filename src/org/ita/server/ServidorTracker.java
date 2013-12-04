package org.ita.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.json.generators.JSONGenerator;
import com.json.generators.JsonGeneratorFactory;
import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

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
			System.out.println(listLines);
			if (ehRequisicaodeArquivo(listLines)) {
				String nomeArquivo = getNomeArquivo(listLines);
				if (temArquivo(nomeArquivo)) {
					enviarArquivo(nomeArquivo, out);
				} else {
					enviarNaoTenhoArquivo(out);
				}
			} else if (ehAvisodePosse(listLines)) {
				if (avisoDePosseCorreto(listLines)) {
					adicionarAoTracker(listLines.get(2),
							Integer.parseInt(listLines.get(3).replaceFirst(
									"pedaco:", "")), listLines.get(4)
									.replaceFirst("ip:", ""),
							Integer.parseInt(listLines.get(5).replaceFirst(
									"porta:", "")));
					out.println("OK");
					out.println(listLines.get(1));
				} else {

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void adicionarAoTracker(String fileName, int numPedaco,
			String ip, int porta) {

		String conteudoJson = null;
		BufferedReader bufReader = null;
		try {
			FileReader f = new FileReader(fileName + ".tracker");
			bufReader = new BufferedReader(f);
			conteudoJson = bufReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			bufReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JsonParserFactory factory = JsonParserFactory.getInstance();
		JSONParser parser = factory.newJsonParser();
		Map jsonMap = parser.parseJson(conteudoJson);
		ArrayList<Map> pedacos = (ArrayList<Map>) jsonMap.get("pedacos");
		Map pedaco = pedacos.get(numPedaco);

		ArrayList<Map> fornecedores = (ArrayList<Map>) pedaco
				.get("fornecedores");

		HashMap<String, String> novoFornecedor = new HashMap<>();
		novoFornecedor.put("ip", ip);
		novoFornecedor.put("porta", "" + porta);

		fornecedores.add(novoFornecedor);

		JsonGeneratorFactory factoryOut = JsonGeneratorFactory.getInstance();
		JSONGenerator generator = factoryOut.newJsonGenerator();
		String json = generator.generateJson(jsonMap);
		json = json.substring(1, json.length() - 1);
		File arquivo = new File(fileName + ".tracker");
		FileWriter writer;
		try {
			writer = new FileWriter(arquivo);
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean avisoDePosseCorreto(List<String> listLines) {
		return true;
	}

	private boolean ehAvisodePosse(List<String> listLines) {
		if (listLines.get(0).contains("Posse"))
			return true;
		return false;
	}

	private void enviarNaoTenhoArquivo(PrintWriter out) {
		out.println("Nao\n");
	}

	private void enviarArquivo(String nomeArquivo, PrintWriter out) {
		out.println("Sim");
		Path p = pegarArquivo(nomeArquivo);
		int qteBytes = getQtdeBytes(p);
		out.println("Quantidade de bytes: " + qteBytes);
		String hash = getHash(p);
		out.println("Hash: " + hash);
		try (BufferedReader br = new BufferedReader(
				new FileReader(p.toString()))) {
			String a;
			while ((a = br.readLine()) != null) {
				out.println(a);
			}
			out.println("");
		} catch (IOException e) {

		}

	}

	private String getHash(Path p) {
		try {
			return MD5Checksum.getMD5Checksum(p.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
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

	private Path pegarArquivo(String nomeArquivo) {
		return FileSystems.getDefault().getPath(nomeArquivo);
	}

	private boolean temArquivo(String nomeArquivo) {
		return Files.exists(FileSystems.getDefault().getPath(nomeArquivo));
	}

	private String getNomeArquivo(List<String> listLines) {
		return listLines.get(0).split(" ")[2];
	}

	private boolean ehRequisicaodeArquivo(List<String> listLines) {
		if (listLines.get(0).startsWith("Voce tem")) {
			if (listLines.get(0).endsWith(".tracker")) {
				return true;
			}
		}
		return false;
	}

}
