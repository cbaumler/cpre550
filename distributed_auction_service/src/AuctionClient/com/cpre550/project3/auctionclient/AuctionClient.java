package com.cpre550.project3.auctionclient;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class AuctionClient extends JApplet {

	private static final long serialVersionUID = -1752824182022610903L;
	private AuctionClientPanel contentPane;

	@Override
	// Called when this applet is loaded into the browser
	public void init() {
		// Execute a job on the event-dispatching thread, creating this applet's GUI
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					System.out.println("entering run");
					contentPane = new AuctionClientPanel();
					System.out.println("finished contentPane constructor");
					setContentPane(contentPane);
				}
			});
		} catch (Exception e) {
			System.out.println("setContentPane didn't complete successfully");
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
	}
	
	@Override
	public void start() {
		// Start a background task to periodically update the list of auctions
		Thread thread = new Thread(new AuctionClientBackgroundTask(contentPane));
		thread.start();
	}
	
	@Override
	public void stop() {
		
	}
}
