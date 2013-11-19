package org.ita.server;

import java.net.Socket;

public interface TrackerInterface {

	public boolean sendTrackerFile(Socket client, String Filename);
	
}
