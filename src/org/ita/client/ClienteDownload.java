package org.ita.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class ClienteDownload {

	public static void main(String[] args) throws IOException {
		String hostName = "localhost";
		int portNumber = 4568;
		String f = "file";
		ClienteDownload cd = new ClienteDownload(f);
		cd.tentarBaixarPedacos();
		cd.juntarPedacos();
	}

	public File juntarPedacos() throws IOException {
		File ff = new File(filename);
		ff.createNewFile();
		byte[] b = new byte[arquivo.getTamanho()];
		int currentSize = 0;
		for (int i = 0; i < arquivo.getPedacos().size(); i++) {
			File f = new File(filename + ".h22apart." + (i + 1));
			FileInputStream fis = new FileInputStream(f);
			fis.read(b, currentSize, (int) f.length());
			fis.close();
		}
		FileOutputStream fos = new FileOutputStream(ff);
		fos.write(b);
		fos.flush();
		fos.close();
		return ff;
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
			String resposta = in.readLine();
			if (resposta.equals("Nao")) {
				return false;
			} else if (resposta.equals("Sim")) {
				String rando = in.readLine();
				if (Integer.parseInt(rando) != rand) {
					return false;
				}
				String quantBytes = in.readLine();
				quantBytes = quantBytes
						.replaceFirst("Quantidade de bytes:", "");
				int infoBytes = Integer.parseInt(quantBytes);
				String hash = in.readLine();
				hash = hash.replaceFirst("Hash:", "");
				byte[] arquivo = lerArquivo(in, infoBytes);
				String fim = in.readLine();
				File f = new File(filename + ".h22apart." + (pedaco));
				f.createNewFile();
				if (fim.equals("Fim do pedaco")) {
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(arquivo);
					fos.flush();
					fos.close();
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
		}
		return false;
	}

	private static byte[] lerArquivo(BufferedReader is, int quantBytes) {
		byte[] res = new byte[quantBytes];
		try {
			String sres = is.readLine();
			for (int i = 0; i < quantBytes; i++) {
				res[i] = Byte.parseByte(sres.substring(2 * i, 2 * i + 2), 16);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	private ArquivoDownload arquivo;
	private String filename;
	List<Pedaco> pedacosBaixados = new ArrayList<>();

	public ClienteDownload(String fileName) {
		String conteudoJson = null;
		BufferedReader bufReader = null;
		this.filename = fileName;
		try {
			FileReader f = new FileReader(fileName + "-local.tracker");
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

		System.out.println(jsonMap.get("tamanho"));
		this.arquivo = new ArquivoDownload(fileName,
				Integer.parseInt((String) jsonMap.get("tamanho")));
		System.out.println(jsonMap.get("pedacos"));

		ArrayList<Map> second = (ArrayList<Map>) jsonMap.get("pedacos");
		for (Map pedacoM : (ArrayList<Map>) jsonMap.get("pedacos")) {
			Pedaco p = new Pedaco();
			for (Map fornecedorM : (ArrayList<Map>) pedacoM.get("fornecedores")) {
				p.addFornecedor((String) fornecedorM.get("ip"),
						Integer.parseInt((String) fornecedorM.get("porta")));
			}
			this.arquivo.addPedaco(p);
		}
		this.arquivo.setMd5((String) jsonMap.get("md5"));
	}

	public boolean tentarBaixarPedacos() {
		int i = 0;
		System.out.println(pedacosBaixados);

		for (Pedaco p : arquivo.getPedacos()) {
			i++;
			if (!pedacosBaixados.contains(p)) {
				for (Fornecedor f : p.getFornecedores()) {
					if (pedirArquivo(f.getIp(), f.getPorta(), filename, i)) {
						pedacosBaixados.add(p);
						break;
					}
				}
			}
		}
		if (pedacosBaixados.size() == arquivo.getPedacos().size()) {
			return true;
		}
		return false;
	}
}