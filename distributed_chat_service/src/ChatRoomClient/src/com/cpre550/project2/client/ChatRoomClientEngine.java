package com.cpre550.project2.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.cpre550.project2.registry.EntityInformation;
import com.cpre550.project2.registry.RmiRegistry;

public class ChatRoomClientEngine {
	
	public static final char JOIN_TYPE  = 0;
	public static final char LEAVE_TYPE = 1;
	public static final char CHAT_TYPE  = 2;
	
	private DatagramChannel udpChannel;
	
	private SelectableChannel stdin;
	
	private RmiRegistry remoteRegistry;
	
	private String username;
	
	private String clientId;
	
	private int clientPort;
	
	private HashMap<String, ChatServer> chatServers = new HashMap<String, ChatServer>();
	
	public ChatRoomClientEngine() {
		
	}
	
	public void execute(int port) {
		clientPort = port;
		EntityInformation info = new EntityInformation(false);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Distributed Chat Client v1.0.");
		System.out.print("Enter a username: ");
		
		try {
			// Retrieve the user's name
			username = in.readLine();
			clientId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + clientPort + "/" + username;
			
			// Register with the Registry Server
			remoteRegistry = (RmiRegistry)Naming.lookup("//localhost/RegistryServer");
			remoteRegistry.dcsRegister(clientId, info);
			System.out.println("Registered");
			
			System.out.println("Commands:");
			System.out.println("SHOW      - Display available chat rooms");
			System.out.println("JOIN x    - Join chat room x");
			System.out.println("LEAVE x   - Leave chat room x");
			System.out.println("ACTIVE x  - Posts will go to chat room x");
			System.out.println("QUIT      - Exit chat client");
			
			// Set up a selector to multiplex input streams
			Selector selector = Selector.open();
			
			// Set up a channel for stdin and register it with the selector
			SystemInPipe stdinPipe = new SystemInPipe();
			stdin = stdinPipe.getStdinChannel();
			stdin.register (selector, SelectionKey.OP_READ, "stdinSelect");
			stdinPipe.start();
			
			// Set up a channel for UDP packets and register it with the selector
	    	udpChannel = DatagramChannel.open();
	    	udpChannel.socket().bind(new InetSocketAddress(clientPort));
	    	udpChannel.configureBlocking(false);
	    	udpChannel.register(selector, SelectionKey.OP_READ, "udpSelect");
			
			while (true) {
				// Block until an input is received
				selector.select();
				
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				
				// Iterate through the selected channels
				while (it.hasNext()) {
					SelectionKey key = (SelectionKey) it.next();
					String identifier = (String)key.attachment();
					
					if (identifier.equals("stdinSelect")) {
						// Process the user input
						ReadableByteChannel channel = (ReadableByteChannel) key.channel();
						String userInput = ReadStdinChannel(channel);
						ProcessUserInput(userInput);
					}
					else if (identifier.equals("udpSelect")) {
						// Process the UDP packet
						DatagramChannel channel = (DatagramChannel) key.channel();
						ReadUdpPacket(channel);
					}
					
					// Remove the key now that it has been handled
					it.remove();
				}
			}
		}
		catch (Exception e) {
			System.err.println("ChatRoomClient exception:");
            e.printStackTrace();
		}
	}
	
	private String ReadStdinChannel(ReadableByteChannel channel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate (32);
		
		// Fill a ByteBuffer with the data from the channel
		buffer.clear();
        int count = channel.read (buffer);
        buffer.flip();
        
        // Hack to convert the ByteBuffer into a byte[]
        byte[] buf = new byte[count];
        int counter = 0;
        while (buffer.hasRemaining()) {
        	buf[counter] = (buffer.get());
        	counter++;
        }
 
        // Convert the byte[] into a string
        String userInput = new String(buf);
        
        // Trim the leading and trailing spaces
        userInput = userInput.trim();
        
        return userInput;
	}
	
	private void ProcessUserInput(String userInput) throws RemoteException, SocketException, IOException {
		// Parse the input command
        if (userInput.toUpperCase().startsWith("JOIN ")) {
        	String chatRoom = userInput.substring(5);
        	JoinChatRoom(chatRoom);
        }
        else if (userInput.toUpperCase().startsWith("LEAVE ")) {
        	String chatRoom = userInput.substring(6);
        	LeaveChatRoom(chatRoom);
        }
        else if (userInput.toUpperCase().equals("SHOW")) {
        	ShowAvailableChatRooms();
        }
        else if (userInput.toUpperCase().startsWith("ACTIVE ")) {
        	String chatRoom = userInput.substring(7);
        	ActivateChatRoom(chatRoom);
        }
        else if (userInput.toUpperCase().equals("QUIT")) {
        	udpChannel.close();
        	stdin.close();
        	remoteRegistry.dcsUnregister(clientId);
        	System.exit(1);
        }
        else {
        	String chat = username + ": " + userInput; 
        	SendChat(chat);
        }
	}
	
	private void ReadUdpPacket(DatagramChannel channel) throws IOException {

		// Fill a ByteBuffer with the data from the channel
		ByteBuffer buffer = ByteBuffer.allocate(256);
		buffer.clear();
		channel.receive(buffer);
		
		buffer.flip();
		
		// Hack to convert the ByteBuffer into a byte[]
        byte[] buf = new byte[256];
        int counter = 0;
        while (buffer.hasRemaining()) {
        	buf[counter] = (buffer.get());
        	counter++;
        }
        
        // Convert the byte[] into a string
        String message = new String(buf);
        
        // Display the message for the user
        System.out.println(message);
	}
	
	private void JoinChatRoom(String chatRoom) throws IOException {

    	// Get the port number and address of the chat room's server
    	EntityInformation info = remoteRegistry.dcsGetInfo(chatRoom);
    	
    	if (info == null) {
    		System.out.println(chatRoom + " does not exist");
    	}
    	else {
    		System.out.println("Joining " + chatRoom);
    		
    		// Send a JOIN request to the server
        	SendPacket(info.serverPort, info.serverAddress, JOIN_TYPE, null);
    	}
	}
	
	private void LeaveChatRoom(String chatRoom) throws IOException {

		// Get the port number and address of the chat room's server
    	EntityInformation info = remoteRegistry.dcsGetInfo(chatRoom);
    	
    	if (info == null) {
    		System.out.println(chatRoom + " does not exist");
    	}
    	else {
    		System.out.println("Leaving " + chatRoom);
    		
    		// Send a LEAVE request to the server
        	SendPacket(info.serverPort, info.serverAddress, LEAVE_TYPE, null);
    	}
	}
	
	private void ActivateChatRoom(String chatRoom) throws RemoteException {
		
		// Get the port number and address of the chat room's server
    	EntityInformation info = remoteRegistry.dcsGetInfo(chatRoom);
    	
    	if (info == null) {
    		System.out.println(chatRoom + " does not exist");
    	}
    	else {
    		System.out.println("Chats will go to " + chatRoom);
        	
    		// Clear the local database
    		chatServers.clear();
    		
        	// Add the chat room to the local database
        	ChatServer server = new ChatServer(info.serverPort, info.serverAddress);
        	String key = info.serverAddress.toString() + ":" + info.serverPort;
        	chatServers.put(key, server);
    	}
	}
	
	private void SendChat(String chat) throws IOException {
		
		Iterator<Entry<String, ChatServer>> iterator = chatServers.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, ChatServer> next = iterator.next();
			SendPacket(next.getValue().port, next.getValue().address, CHAT_TYPE, chat);
		}
		
	}
	
	private void SendPacket(int port, InetAddress address, char requestType, String payload) throws IOException{
    	
		// Create the payload
		ByteBuffer buffer = ByteBuffer.allocate (256);
		buffer.clear();
        buffer.put((byte)requestType);
        
        if (requestType == CHAT_TYPE && payload != null) {
        	buffer.put(payload.getBytes());
        }
        
        buffer.flip();
        
        // Send the request
        udpChannel.send(buffer, new InetSocketAddress(address, port));
	}
	
	private void ShowAvailableChatRooms() throws RemoteException {
		
		// Get a list of the registered chat rooms from the registry server
		ArrayList<String> chatRooms = remoteRegistry.dcsGetAllChatRooms();
		
		System.out.println("Available Chat Rooms:");
		
		// Display all
		Iterator<String> iterator = chatRooms.iterator();
		while (iterator.hasNext()) {
			System.out.println(iterator.next());
		}
	}
	
	private class ChatServer {
		public int port;
		public InetAddress address;
		
		public ChatServer(int p, InetAddress a) {
			port = p;
			address = a;
		}
	}

}
