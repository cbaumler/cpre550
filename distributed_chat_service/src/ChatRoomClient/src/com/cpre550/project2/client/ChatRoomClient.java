package com.cpre550.project2.client;


public class ChatRoomClient {
	
	public static void main(String args[]) {
		
		// Validate the input arguments
		if (args.length == 0 ) {
			System.err.println("Usage: java ChatRoomClient <port>");
			System.exit(1);
		}
		else {
		new ChatRoomClientEngine().execute(Integer.parseInt(args[0]));
		}
	}
	
	
}
