package com.cpre550.project3.auctionclient;


public class AuctionClientBackgroundTask implements Runnable {

	AuctionClientPanel gui;
	
	AuctionClientBackgroundTask(AuctionClientPanel theGui) {
		super();
		gui = theGui;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				if (gui.isLoggedIn()) {
					// Update the auction lists
					gui.updateAuctionLists(true);
				}
				
				Thread.sleep(10000);
				
			} catch (Exception e) {
				gui.appletDisplay("Background update task failed");
				System.out.println("ERROR : " + e) ;
				e.printStackTrace(System.out);
				break;
			}
		}
		
	}
		

}
