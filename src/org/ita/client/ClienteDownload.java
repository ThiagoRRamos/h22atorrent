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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.ita.server.MD5Checksum;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;

public class ClienteDownload {

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

		this.arquivo = new ArquivoDownload(fileName,
				Integer.parseInt((String) jsonMap.get("tamanho")));

		ArrayList<Map> second = (ArrayList<Map>) jsonMap.get("pedacos");
		int i = 0;
		for (Map pedacoM : (ArrayList<Map>) jsonMap.get("pedacos")) {
			Pedaco p = new Pedaco(++i);
			for (Map fornecedorM : (ArrayList<Map>) pedacoM.get("fornecedores")) {
				p.addFornecedor((String) fornecedorM.get("ip"),
						Integer.parseInt((String) fornecedorM.get("porta")));
			}
			this.arquivo.addPedaco(p);
		}
		this.arquivo.setMd5((String) jsonMap.get("md5"));
	}

	public File juntarPedacos() throws IOException {
		arquivo.getMd5();
		File ff = new File(filename);
		ff.createNewFile();
		byte[] b = new byte[arquivo.getTamanho()];
		int currentSize = 0;
		for (int i = 0; i < arquivo.getPedacos().size(); i++) {
			File f = new File(filename + ".h22apart." + (i + 1));
			FileInputStream fis = new FileInputStream(f);
			fis.read(b, currentSize, (int) f.length());
			currentSize += f.length();
			fis.close();
		}
		FileOutputStream fos = new FileOutputStream(ff);
		fos.write(b);
		fos.flush();
		fos.close();
		try {
			if (MD5Checksum.getMD5Checksum(filename).equals(arquivo.getMd5())) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ff;
	}

	public boolean pedirArquivo(String hostName, int portNumber,
			String filename, int pedaco) {
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
				InputStream is = echoSocket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(isr);
				BufferedReader stdIn = new BufferedReader(
						new InputStreamReader(System.in))) {
			out.println("Voce tem");
			int rand = (new Random()).nextInt();
			out.println(rand);
			out.println(filename);
			out.println("pedaco:" + pedaco);
			out.println("");
			String resposta = in.readLine();
			if (resposta.equals("Nao")) {
				echoSocket.close();
				return false;
			} else if (resposta.equals("Sim")) {
				String rando = in.readLine();
				if (Integer.parseInt(rando) != rand) {
					echoSocket.close();
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
					String calculatedHash = MD5Checksum.getMD5Checksum(filename
							+ ".h22apart." + (pedaco));
					echoSocket.close();
					if (!hash.equals(calculatedHash))
						return false;
					return true;
				} else {
					echoSocket.close();
					return false;
				}
			} else {
				return false;
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
		} catch (SocketTimeoutException e) {
			System.err.println("Socket timeout em " + hostName);
			return false;
		}catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static byte[] lerArquivo(BufferedReader is, int quantBytes) {
		byte[] res = new byte[quantBytes];
		try {
			String sres = is.readLine();
			for (int i = 0; i < quantBytes; i++) {
				String at = sres.substring(2 * i, 2 * i + 2);
				res[i] = parsearByte(at);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	private static byte parsearByte(String at) {
		byte res = 0;
		switch (at.charAt(0)) {
		case '0':
			res += 0;
			break;
		case '1':
			res += 16;
			break;
		case '2':
			res += 16 * 2;
			break;
		case '3':
			res += 16 * 3;
			break;
		case '4':
			res += 16 * 4;
			break;
		case '5':
			res += 16 * 5;
			break;
		case '6':
			res += 16 * 6;
			break;
		case '7':
			res += 16 * 7;
			break;
		case '8':
			res += 16 * 8;
			break;
		case '9':
			res += 16 * 9;
			break;
		case 'A':
			res += 16 * 10;
			break;
		case 'B':
			res += 16 * 11;
			break;
		case 'C':
			res += 16 * 12;
			break;
		case 'D':
			res += 16 * 13;
			break;
		case 'E':
			res += 16 * 14;
			break;
		case 'F':
			res += 16 * 15;
			break;
		}
		switch (at.charAt(1)) {
		case '0':
			res += 0;
			break;
		case '1':
			res += 1;
			break;
		case '2':
			res += 2;
			break;
		case '3':
			res += 3;
			break;
		case '4':
			res += 4;
			break;
		case '5':
			res += 5;
			break;
		case '6':
			res += 6;
			break;
		case '7':
			res += 7;
			break;
		case '8':
			res += 8;
			break;
		case '9':
			res += 9;
			break;
		case 'A':
			res += 10;
			break;
		case 'B':
			res += 11;
			break;
		case 'C':
			res += 12;
			break;
		case 'D':
			res += 13;
			break;
		case 'E':
			res += 14;
			break;
		case 'F':
			res += 15;
			break;
		}
		return res;
	}

	public boolean tentarBaixarPedacos() {
		int i = 0;

		for (Pedaco p : arquivo.getPedacos()) {
			i++;
			if (!pedacosBaixados.contains(p)) {
				for (Fornecedor f : p.getFornecedores()) {
					if (pedirArquivo(f.getIp(), f.getPorta(), filename, i)) {
						pedacosBaixados.add(p);
						new AvisadorPosse().avisarPosse(Cliente.myAddress,
								Cliente.myPort, filename, i - 1);
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