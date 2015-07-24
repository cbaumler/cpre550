package com.cpre550.project2.registry;

import java.io.Serializable;
import java.net.InetAddress;

public class EntityInformation implements Serializable {
	
	private static final long serialVersionUID = -6886810793888151597L;
	
	public boolean isChatRoom = false;
	public int serverPort = 0;
	public InetAddress serverAddress = null;
	
	public EntityInformation(boolean isRoom) {
		super();
		isChatRoom = isRoom;
	}
	
	public EntityInformation(boolean isRoom, int port, InetAddress address) {
		super();
		isChatRoom = isRoom;
		serverPort = port;
		serverAddress = address;
	}

}
