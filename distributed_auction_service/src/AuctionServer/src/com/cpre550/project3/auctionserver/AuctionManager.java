package com.cpre550.project3.auctionserver;

import java.util.HashMap;

import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StringHolder;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import AuctionApp.Auction;
import AuctionApp.AuctionFactory;
import AuctionApp.AuctionFactoryHelper;
import AuctionApp.AuctionFactoryPOA;
import AuctionApp.AuctionFailure;
import AuctionApp.AuctionHelper;
import AuctionApp.ErrorType;
import AuctionApp.AuctionFactoryPackage.AuctionsHolder;

class AuctionFactoryImpl extends AuctionFactoryPOA {
	
	private static int MAX_AUCTIONS = 10;
	
	private HashMap<String, String> users = new HashMap<String, String>();
	private String auctionList[] = new String[MAX_AUCTIONS];
	NamingContextExt ncRef;
	
	AuctionFactoryImpl(String args[]) throws InvalidName {
		super();
		
		// create and initialize the ORB
		ORB orb = ORB.init(args, null);
		
		// get the root naming context
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		
		// Use NamingContextExt instead of NamingContext. This is 
		// part of the Interoperable naming Service.  
		ncRef = NamingContextExtHelper.narrow(objRef);
		
		// Initialize each auction
		for (int idx = 0; idx < MAX_AUCTIONS; idx++) {
			auctionList[idx] = "auction" + Integer.toString(idx);

			// Start a server thread for each auction
			Thread thread = new Thread(new AuctionServer(auctionList[idx], args));
			thread.start();	
		}
		
		// Add the auction manager as a user
		users.put("AuctionManager", "password");
	}

	@Override
	public synchronized void authenticate(String userId, String password) throws AuctionFailure {
		System.out.println("Recieved authenticate request from " + userId);
		if (users.containsKey(userId)) {
			// Compare the saved password to the input password
			if (!password.equals(users.get(userId))) {
				throw new AuctionFailure(ErrorType.INVALID_USER, "Incorrect password");
			}
			// else the password matched - don't do anything
		}
		else {
			// Add a new user
			users.put(userId, password);
		}
	}

	@Override
	public void getActiveAuctions(String userId, AuctionsHolder sellerAuctions,
			IntHolder numSellerAuctions, AuctionsHolder bidderAuctions,
			IntHolder numBidderAuctions) throws AuctionFailure {
		
		numSellerAuctions.value = 0;
		numBidderAuctions.value = 0;
		sellerAuctions.value = new String[MAX_AUCTIONS];
		bidderAuctions.value = new String[MAX_AUCTIONS];
		
		// Check each auction server
		for (int idx = 0; idx < MAX_AUCTIONS; idx++) {
			sellerAuctions.value[idx] = "";
			bidderAuctions.value[idx] = "";
			try {
				// resolve the Object Reference in Naming
				Auction auctionImpl = AuctionHelper.narrow(ncRef.resolve_str(auctionList[idx]));
				
				// Ask the auction server for the seller name
				try {
					String seller = auctionImpl.viewSeller("AuctionManager");
					if (userId.equals(seller)) {
						// The user is the seller for this auction
						sellerAuctions.value[numSellerAuctions.value] = auctionList[idx];
						numSellerAuctions.value++;
					}
					else {
						// The user is a bidder for this auction
						bidderAuctions.value[numBidderAuctions.value] = auctionList[idx];
						numBidderAuctions.value++;
					}
				}
				catch (AuctionFailure e) {
					// The auction is not active
					// Don't add it to the list
				}
				
			} catch (Exception e) {
				System.err.println("ERROR: " + e);
				e.printStackTrace();
				throw new AuctionFailure(ErrorType.GENERIC_ERROR, "Failed to contact auction servers");
			} 
		}
	}

	@Override
	public synchronized String getInactiveAuction() throws AuctionFailure {
		System.out.println("Recieved getInactiveAuction request");
		
		String auction = "none";
		
		// Check for an available auction server
		for (int idx = 0; idx < MAX_AUCTIONS; idx++) {
			try {
				// resolve the Object Reference in Naming
				Auction auctionImpl = AuctionHelper.narrow(ncRef.resolve_str(auctionList[idx]));
				
				// Ask the auction server for status
				BooleanHolder isActive = new BooleanHolder();
				auctionImpl.viewAuctionStatus("AuctionManager", isActive, new StringHolder(), new IntHolder(), new StringHolder());
				
				if (!isActive.value) {
					auction = auctionList[idx];
					break;
				}
				
			} catch (Exception e) {
				System.err.println("ERROR: " + e);
				e.printStackTrace();
				throw new AuctionFailure(ErrorType.GENERIC_ERROR, "Failed to contact auction servers");
			} 
		}
		
		// No servers were available
		if (auction.equals("none")) {
			throw new AuctionFailure(ErrorType.MAX_AUCTIONS_REACHED, "No auction servers available");
		}
		
		return auction;
	}
	
}

public class AuctionManager {
	
	public static void main(String args[]) {
		try{
			System.out.println("Auction Manager started");
			
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);

			// get reference to rootpoa & activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant
			AuctionFactoryImpl auctionFactoryImpl = new AuctionFactoryImpl(args);
			
			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(auctionFactoryImpl);
			AuctionFactory href = AuctionFactoryHelper.narrow(ref);
	          
	      	// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			String name = "AuctionFactory";
			NameComponent path[] = ncRef.to_name( name );
			ncRef.rebind(path, href);

			// wait for invocations from clients
			orb.run();
	    }    
		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
	          
	    System.out.println("Auction Manager Exiting");      
	  }

}
