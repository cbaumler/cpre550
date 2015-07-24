package com.cpre550.project3.auctionserver;

import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StringHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import AuctionApp.Auction;
import AuctionApp.AuctionHelper;
import AuctionApp.AuctionPOA;
import AuctionApp.ErrorType;
import AuctionApp.AuctionFailure;

class AuctionImpl extends AuctionPOA {

	private boolean auctionIsActive = false;
	private String sellerId = "N/A";
	private String highBidderId = "N/A";
	private String itemDesc = "N/A";
	private int itemPrice = 0;
	
	@Override
	public synchronized void bid(String _bidderId, int _bidPrice) throws AuctionFailure {
		System.out.println("Recieved bid request from " + _bidderId);
		if (this.auctionIsActive) {
			if (_bidderId.equals(this.sellerId)) {
				throw new AuctionFailure(ErrorType.ACTION_NOT_PERMITTED, "The seller may not bid.");
			}
			else {
				if (_bidPrice > this.itemPrice) {
					// Update the high bidder and the current price
					this.itemPrice = _bidPrice;
					this.highBidderId = _bidderId;
				}
			}
		}
		else {
			throw new AuctionFailure(ErrorType.NO_ACTIVE_AUCTION, "No auction is currently active.");
		}
	}

	@Override
	public synchronized void offerItem(String _sellerId, String _itemDesc, int _itemPrice) throws AuctionFailure {
		System.out.println("Recieved offerItem request from " + _sellerId);
		if (this.auctionIsActive) {
			throw new AuctionFailure(ErrorType.MAX_AUCTIONS_REACHED, "An auction is already in progress.");
		}
		else {
			// Start a new auction
			this.auctionIsActive = true;
			this.sellerId = _sellerId;
			this.itemDesc = _itemDesc;
			this.itemPrice = _itemPrice;
		}
	}

	@Override
	public synchronized void sellItem(String _sellerId) throws AuctionFailure {
		System.out.println("Recieved sellItem request from " + _sellerId);
		if (this.auctionIsActive) {
			if (!this.sellerId.equals(_sellerId)) {
				throw new AuctionFailure(ErrorType.ACTION_NOT_PERMITTED, "Only the seller may sell an item.");
			}
			else if (this.highBidderId.equals("N/A")) {
				throw new AuctionFailure(ErrorType.ACTION_NOT_PERMITTED, "No bids have been received.");
			}
			else {
				// End the current auction
				this.auctionIsActive = false;
				this.sellerId = "N/A";
				this.highBidderId = "N/A";
				this.itemDesc = "N/A";
				this.itemPrice = 0;
			}
		}
		else {
			throw new AuctionFailure(ErrorType.NO_ACTIVE_AUCTION, "No auction is currently active.");
		}
	}

	@Override
	public void viewAuctionStatus(String _userId, BooleanHolder _isActive, StringHolder _itemDesc, IntHolder _price, StringHolder _highBidder) {
		System.out.println("Recieved viewAuctionStatus request from " + _userId);
		
		// Set _isActive and provide default values for other parameters
		_isActive.value = this.auctionIsActive;
		_itemDesc.value = "N/A";
		_price.value = 0;
		_highBidder.value = "N/A";
		
		// Update other parameters as appropriate
		if (this.auctionIsActive) {
			_itemDesc.value = this.itemDesc;
			_price.value = this.itemPrice;
			
			if (_userId.equals(this.sellerId) || _userId.equals(this.highBidderId)) {
				_highBidder.value = this.highBidderId;
			}
			else {
				_highBidder.value = "unknown";
			}
		}
	}

	@Override
	public String viewHighBidder(String _userId) throws AuctionFailure {
		System.out.println("Recieved viewHighBidder request from " + _userId);
		String theHighBidder = "unknown";
		if (this.auctionIsActive) {
			// Only return the ID of the high bidder if the caller is the seller or the current high bidder
			if (_userId.equals(sellerId) || _userId.equals(this.highBidderId)) {
				theHighBidder = this.highBidderId;
			}
			else {
				theHighBidder = "unknown";
			}
		}
		else {
			throw new AuctionFailure(ErrorType.NO_ACTIVE_AUCTION, "No auction is currently active");
		}
		
		return theHighBidder;
	}

	@Override
	public String viewSeller(String _userId) throws AuctionFailure {
		String seller;
		if (_userId.equals("AuctionManager")) {
			if (this.auctionIsActive) {
				seller = this.sellerId;
			}
			else {
				throw new AuctionFailure(ErrorType.NO_ACTIVE_AUCTION, "No auction is currently active");
			}
		}
		else {
			throw new AuctionFailure(ErrorType.ACTION_NOT_PERMITTED, "Restricted to Auction Manager");
		}
		
		return seller;
	}
	
}

public class AuctionServer implements Runnable {
	
	String serverName;
	String args[];
	
	AuctionServer(String _serverName, String _args[]) {
		serverName = _serverName;
		args = _args;
	}
	
	public void run () {
		try{
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);

			// get reference to rootpoa & activate the POAManager
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant
			AuctionImpl auctionImpl = new AuctionImpl();

			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(auctionImpl);
			Auction href = AuctionHelper.narrow(ref);
	          
	      	// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			// Use NamingContextExt which is part of the Interoperable
			// Naming Service (INS) specification.
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			NameComponent path[] = ncRef.to_name( serverName );
			ncRef.rebind(path, href);

			System.out.println(serverName + " launched");

			// wait for invocations from clients
			orb.run();
	    }    
		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
	          
	    System.out.println(serverName + " Exiting");      
	  }
}
