package org.ita.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MeuBuffer {

	private InputStream is;
	private InputStreamReader isr;

	public MeuBuffer(InputStream is, InputStreamReader isr) {
		this.is = is;
		this.isr = isr;
	}

	public String readLine() {
		char c = '\n';
		List<Character> cs = new ArrayList<>();
		int val = 10;
		int i = 0;
		boolean ok = true;
		try {
			while (ok && (i = isr.read()) != -1) {
				if (i != val) {
					char cc = (char) i;
					cs.add(cc);
				} else {
					ok = false;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		char[] res = new char[cs.size()];
		int j = 0;
		for (char p : cs) {
			res[j] = p;
			j++;
		}
		return new String(res);

	}

	public byte[] readBytes(int nb) {
		byte[] res = new byte[nb];
		try {
			is.read(res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
