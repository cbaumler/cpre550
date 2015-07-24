package com.cpre550.project2.provider;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.cpre550.project2.registry.EntityInformation;
import com.cpre550.project2.registry.RmiRegistry;


public class ChatRoomProvider {

	public static void main(String args[]) {
		
		HashMap<String, Thread> chatRooms = new HashMap<String, Thread>();
		
		// Validate the input arguments
		if (args.length == 0 || (args.length % 2 == 1)) {
			System.err.println("Usage: java ChatRoomProvider <Room Name 1> <Port 1> ... <Room Name n> <Port n>");
			System.exit(1);
		}
		else {
			try {
				RmiRegistry remoteRegistry = (RmiRegistry)Naming.lookup("//localhost/RegistryServer");
				
				for (int idx = 0; idx < args.length; idx += 2) {
					String name = args[idx];
					int port = Integer.parseInt(args[idx+1]);
					String roomId = InetAddress.getLocalHost().getCanonicalHostName() + ":" + port + "/" + name;
					
					// Register the chat room
					EntityInformation info = new EntityInformation(true, port, InetAddress.getLocalHost());
					remoteRegistry.dcsRegister(roomId, info);
					
					// Start a server thread for the chat room
					Thread thread = new Thread(new ChatRoomServer(port, name));
					thread.start();
					
					// Save a record of the chat room
					chatRooms.put(roomId, thread);
					
					System.out.println("Registered chat room: " + roomId);
				}
				
				while (!chatRooms.isEmpty()) {
					// Sleep for 500 milliseconds
					Thread.sleep(500);
					
					// Iterate through each server thread
					Iterator<Entry<String, Thread>> iterator = chatRooms.entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<String, Thread> next = iterator.next();
						
						// Check Whether the thread has died
						if (!next.getValue().isAlive()) {
							
							// Unregister the chat room
							remoteRegistry.dcsUnregister(next.getKey());
							
							// Remove the chat room from the record
							iterator.remove();
						}
					}
				}

			}
			catch (Exception e) {
				System.err.println("ChatRoomProvider exception:");
	            e.printStackTrace();
			}
		}
	}
}
