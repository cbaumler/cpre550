package com.cpre550.project2.provider;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class ChatRoomServer implements Runnable{
	
	public static final char JOIN_TYPE  = 0;
	public static final char LEAVE_TYPE = 1;
	public static final char CHAT_TYPE  = 2;

	private DatagramSocket socket = null;
	
	private String chatRoomName;
	
	private HashMap<String, Client> clients = new HashMap<String, Client>();
	
	public ChatRoomServer(int port, String name) throws SocketException {
		socket = new DatagramSocket(port);
		chatRoomName = name;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				byte[] buf = new byte[256];
				
				// Receive request
	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            socket.receive(packet);
	            
	            // Parse the packet
	            int length = packet.getLength();
	            int port = packet.getPort();
	            InetAddress address = packet.getAddress();
	            String key = address.toString() + ":" + port;
	            if (length > 0) {
	            	switch (buf[0]) {
	            	case JOIN_TYPE:
	            		// Add the client to the database if it is not already there
	            		if (!clients.containsKey(key)) {
	            			clients.put(key, new Client(port, address));
	            			System.out.println(key + " joined " + chatRoomName);
	            		}
	            		break;
	            	case LEAVE_TYPE:
	            		// Remove the client from the database
	            		clients.remove(key);
	            		System.out.println(key + " left " + chatRoomName);
	            		break;
	            	case CHAT_TYPE:
						// Copy the data with the chat room name appended (minus the request type)
	            		String prefix = chatRoomName + "/";
	            		byte chat[] = new byte[256 + prefix.length()];
	            		System.arraycopy(prefix.getBytes(), 0, chat, 0, prefix.length());
						System.arraycopy(buf, 1, chat, prefix.length(), length-1);
						
	            		// Send the chat to all of the joined clients
	            		Iterator<Entry<String, Client>> iterator = clients.entrySet().iterator();
						while (iterator.hasNext()) {
							Entry<String, Client> next = iterator.next();
							packet = new DatagramPacket(chat, chat.length, next.getValue().address, next.getValue().port);
							socket.send(packet);
						}
	            		break;
	            	default:
	            		System.out.println("Received unrecognized message");
	            		break;
	            	}
	            }
	            else {
	            	System.out.println("Received empty message");
	            }
			}
			catch (Exception e) {
				System.err.println("ChatRoomServer exception:");
	            e.printStackTrace();
			}
		}
	}
	
	private static class Client {
		public int port;
		public InetAddress address;
		
		public Client(int p, InetAddress a) {
			port = p;
			address = a;
		}
	}

}
