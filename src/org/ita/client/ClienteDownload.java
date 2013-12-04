package org.ita.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.json.parsers.JSONParser;
import com.json.parsers.JsonParserFactory;
import com.json.utils.JSONUtility;

public class ClienteDownload {
	
	private ArquivoDownload arquivo;
	
	public ClienteDownload() {
		String conteudoJson = null;
		BufferedReader bufReader = null;
		String fileName = "file";
		try {
			FileReader f = new FileReader(fileName+"-local.tracker");
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
		
		JsonParserFactory factory=JsonParserFactory.getInstance();
		JSONParser parser=factory.newJsonParser();
		Map jsonMap=parser.parseJson(conteudoJson);
		
		System.out.println(jsonMap.get("tamanho"));
		this.arquivo = new ArquivoDownload(fileName, Integer.parseInt((String)jsonMap.get("tamanho")));
		System.out.println(jsonMap.get("pedacos"));
		
		ArrayList<Map> second = (ArrayList<Map>) jsonMap.get("pedacos");
		for(Map pedacoM : (ArrayList<Map>) jsonMap.get("pedacos")){
			Pedaco p = new Pedaco();
			for(Map fornecedorM : (ArrayList<Map>) pedacoM.get("fornecedores")){
				p.addFornecedor((String) fornecedorM.get("ip"), Integer.parseInt((String) fornecedorM.get("porta")));
			}
			this.arquivo.addPedaco(p);
		}
		this.arquivo.setMd5((String) jsonMap.get("md5"));
	}
	
	public boolean tentarBaixarPedacos(){
		//Baixar os pedacos em arquivo
		return false;
	}
	
	public static void main(String[] args) {
		ClienteDownload cli = new ClienteDownload();
		
	}
}
