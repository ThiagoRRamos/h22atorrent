package org.ita.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileStarter {

	public static void main(String args[]) throws IOException {
		int port = 4568;
		int maxChunkSize = 10000;
		String filename = "file";
		String ipAddress = "localhost";
		if (args.length > 0) {
			filename = args[0];
			if(args.length > 1){
				ipAddress = args[1];
			}
		}
		ByteReadAndWrite.readAndFragment(filename, maxChunkSize);
		createTracker(filename, numeroPedacos(filename, maxChunkSize),
				ipAddress, port);
	}

	private static int numeroPedacos(String filename, int i) {
		File f = new File(filename);
		if (f.length() % i != 0)
			return (int) f.length() / i + 1;
		return (int) f.length() / i;
	}

	private static File createTracker(String filename, int numeroPedacos,
			String ipAddress, int port) {
		File f = new File(filename + ".tracker");
		File o = new File(filename);
		FileWriter fw;
		String c = null;
		try {
			c = MD5Checksum.getMD5Checksum(filename);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String a = "{\"fornecedores\":[{\"porta\":\"" + port + "\",\"ip\":\""
				+ ipAddress + "\"}]}";
		try {
			fw = new FileWriter(f);
			fw.write("{\"tamanho\":\"" + o.length() + "\",\"md5\":\"" + c
					+ "\",\"pedacos\":[");
			for (int i = 0; i < numeroPedacos - 1; i++) {
				fw.write(a + ",");
			}
			fw.write(a + "]}");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
		return f;
	}
}
