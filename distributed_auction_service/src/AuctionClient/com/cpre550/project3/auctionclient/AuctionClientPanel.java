package com.cpre550.project3.auctionclient;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.omg.CORBA.BooleanHolder;
import org.omg.CORBA.IntHolder;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StringHolder;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import AuctionApp.Auction;
import AuctionApp.AuctionFactory;
import AuctionApp.AuctionFactoryHelper;
import AuctionApp.AuctionFailure;
import AuctionApp.AuctionHelper;
import AuctionApp.ErrorType;
import AuctionApp.AuctionFactoryPackage.AuctionsHolder;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class AuctionClientPanel extends JPanel {
	
	private static final long serialVersionUID = -1006763597344569335L;
	static AuctionFactory auctionFactoryImpl;
	NamingContextExt ncRef;
	String userName;
	boolean loggedIn = false;
	private final JTextField usernameField = new JTextField();
	private JTextField passwordField;
	private final JLabel lblBid = new JLabel("Bid:");
	private JTextField txtDescription;
	private JTextField txtStartingPrice;
	private JTextField txtBidPrice;
	private JTextField serverAddressField;
	private JTextField serverPortField;
	private JTextArea infoBox;
	private JButton btnLogIn;
	private JButton btnRefresh;
	private JButton btnCreateNewAuction;
	private JButton btnSellItem;
	private JButton btnBid;
	private JList bidList;
	private JList sellList;
	private DefaultListModel sellListModel;
	private DefaultListModel bidListModel;
	private JTextArea sellAuctionDescription;
	private JTextArea bidAuctionDescription;
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;
	private JScrollPane scrollPane_4;
	
	AuctionClientPanel() {
		super();
		this.setPreferredSize(new Dimension(500,700));
		this.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				ColumnSpec.decode("165px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(70dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(18dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.MIN_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("top:default"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(50dlu;default)"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(50dlu;default):grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblServerAddress = new JLabel("Server Address:");
		add(lblServerAddress, "3, 3, left, default");
		
		serverAddressField = new JTextField();
		serverAddressField.setText("192.168.222.135");
		add(serverAddressField, "5, 3, fill, default");
		serverAddressField.setColumns(10);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Connect to the server
				onConnectClicked();
			}
		});
		add(btnConnect, "7, 3");
		
		JLabel lblServerPort = new JLabel("Server Port:");
		add(lblServerPort, "3, 5, left, default");
		
		serverPortField = new JTextField();
		serverPortField.setText("1050");
		add(serverPortField, "5, 5, fill, default");
		serverPortField.setColumns(10);
		
		JLabel lblUsername = new JLabel("Username:");
		this.add(lblUsername, "3, 9, left, top");
		usernameField.setEnabled(false);
		this.add(usernameField, "5, 9");
		usernameField.setColumns(10);
		
		btnLogIn = new JButton("Log In");
		btnLogIn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnLogIn.isEnabled()) {
					onLoginClicked();
				}
			}
		});
		btnLogIn.setEnabled(false);
		this.add(btnLogIn, "7, 9");
		
		JLabel lblPassword = new JLabel("Password:");
		this.add(lblPassword, "3, 11, left, top");
		
		passwordField = new JTextField();
		passwordField.setEnabled(false);
		this.add(passwordField, "5, 11, fill, default");
		passwordField.setColumns(10);
		
		JLabel lblAvailableAuctions = new JLabel("Available Auctions");
		this.add(lblAvailableAuctions, "3, 13");
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnRefresh.isEnabled()) {
					onRefreshClicked();
				}
			}
		});
		btnRefresh.setEnabled(false);
		this.add(btnRefresh, "7, 13");
		
		JLabel lblSell = new JLabel("Sell:");
		this.add(lblSell, "3, 15");
		this.add(lblBid, "5, 15");
		
		sellListModel = new DefaultListModel();
		
		bidListModel = new DefaultListModel();
		
		scrollPane_4 = new JScrollPane();
		add(scrollPane_4, "3, 17, fill, fill");
		sellList = new JList(sellListModel);
		sellList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (sellList.isEnabled()) {
					//onSellListSelectionChanged();
				}
			}
		});
		scrollPane_4.setViewportView(sellList);
		sellList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (sellList.isEnabled()) {
					onSellListSelectionChanged();
				}
			}
		});
		sellList.setEnabled(false);
		sellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollPane_3 = new JScrollPane();
		add(scrollPane_3, "5, 17, fill, fill");
		bidList = new JList(bidListModel);
		bidList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (bidList.isEnabled()) {
					//onBidListSelectionChanged();
				}
			}
		});
		scrollPane_3.setViewportView(bidList);
		bidList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (bidList.isEnabled()) {
					onBidListSelectionChanged();
				}
			}
		});
		bidList.setEnabled(false);
		bidList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		scrollPane_2 = new JScrollPane();
		add(scrollPane_2, "3, 19, fill, fill");
		
		sellAuctionDescription = new JTextArea();
		scrollPane_2.setViewportView(sellAuctionDescription);
		sellAuctionDescription.setLineWrap(true);
		sellAuctionDescription.setWrapStyleWord(true);
		sellAuctionDescription.setEditable(false);
		
		scrollPane_1 = new JScrollPane();
		add(scrollPane_1, "5, 19, fill, fill");
		
		bidAuctionDescription = new JTextArea();
		scrollPane_1.setViewportView(bidAuctionDescription);
		bidAuctionDescription.setLineWrap(true);
		bidAuctionDescription.setWrapStyleWord(true);
		bidAuctionDescription.setEditable(false);
		
		JLabel lblSellerCommands = new JLabel("Seller Commands");
		this.add(lblSellerCommands, "3, 21");
		
		btnCreateNewAuction = new JButton("New Auction");
		btnCreateNewAuction.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnCreateNewAuction.isEnabled()) {
					onCreateNewAuctionClicked();
				}
			}
		});
		btnCreateNewAuction.setEnabled(false);
		btnCreateNewAuction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		this.add(btnCreateNewAuction, "3, 23");
		
		txtDescription = new JTextField();
		txtDescription.setEnabled(false);
		txtDescription.setText("Description");
		this.add(txtDescription, "5, 23, fill, default");
		txtDescription.setColumns(10);
		
		txtStartingPrice = new JTextField();
		txtStartingPrice.setEnabled(false);
		txtStartingPrice.setText("Starting Price");
		this.add(txtStartingPrice, "7, 23, fill, default");
		txtStartingPrice.setColumns(10);
		
		btnSellItem = new JButton("Sell Item");
		btnSellItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnSellItem.isEnabled()) {
					onSellItemClick();
				}
			}
		});
		btnSellItem.setEnabled(false);
		this.add(btnSellItem, "3, 25");
		
		JLabel lblBidderCommands = new JLabel("Bidder Commands");
		this.add(lblBidderCommands, "3, 29");
		
		btnBid = new JButton("Bid");
		btnBid.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnBid.isEnabled()) {
					onBidClicked();
				}
			}
		});
		btnBid.setEnabled(false);
		this.add(btnBid, "3, 31");
		
		txtBidPrice = new JTextField();
		txtBidPrice.setEnabled(false);
		txtBidPrice.setText("Bid Price");
		this.add(txtBidPrice, "5, 31, fill, default");
		txtBidPrice.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, "3, 35, 5, 1, fill, fill");
		
		infoBox = new JTextArea();
		infoBox.setEditable(false);
		scrollPane.setViewportView(infoBox);
		infoBox.setText("Welcome to Auction Client 1.0");
		
		this.setVisible(true);
	}
	
	// Set up the client
	private void onConnectClicked()
	{	
		// Create the arguments array
		String args[] = {"-ORBInitialHost", serverAddressField.getText(), 
						 "-ORBInitialPort", serverPortField.getText()};
		
		try{
			// create and initialize the ORB
			ORB orb = ORB.init(args, null);

			// get the root naming context
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			
			// Use NamingContextExt instead of NamingContext. This is 
			// part of the Interoperable naming Service.  
			ncRef = NamingContextExtHelper.narrow(objRef);
 
			// resolve the Object Reference in Naming
			String name = "AuctionFactory";
			auctionFactoryImpl = AuctionFactoryHelper.narrow(ncRef.resolve_str(name));
			
			appletDisplay("Connected to server");
			
			// Activate the login functionality
			btnLogIn.setEnabled(true);
			usernameField.setEnabled(true);
			passwordField.setEnabled(true);
			
		} catch (Exception e) {
			appletDisplay("Unable to connect to server");
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
			
			// Deactivate functionality
			btnLogIn.setEnabled(false);
			usernameField.setEnabled(false);
			passwordField.setEnabled(false);
			btnRefresh.setEnabled(false);
			sellList.setEnabled(false);
			bidList.setEnabled(false);
			btnCreateNewAuction.setEnabled(false);
			txtDescription.setEnabled(false);
			txtStartingPrice.setEnabled(false);
			btnSellItem.setEnabled(false);
			btnBid.setEnabled(false);
			txtBidPrice.setEnabled(false);
			loggedIn = false;
		}
	}
	
	private void onLoginClicked() {
		try {
			if (usernameField.getText().equals("") || passwordField.getText().equals("")) {
				throw new AuctionFailure(ErrorType.GENERIC_ERROR, "Invalid login info");
			}
			userName = usernameField.getText();
			auctionFactoryImpl.authenticate(userName, passwordField.getText());
			
			// Authentication was successful
			appletDisplay(usernameField.getText() + " logged in");
			
			// Enable functionality
			btnRefresh.setEnabled(true);
			sellList.setEnabled(true);
			bidList.setEnabled(true);
			btnCreateNewAuction.setEnabled(true);
			txtDescription.setEnabled(true);
			txtStartingPrice.setEnabled(true);
			btnSellItem.setEnabled(true);
			btnBid.setEnabled(true);
			txtBidPrice.setEnabled(true);
			loggedIn = true;
			
			// Update the auction lists
			updateAuctionLists(false);
			
		} catch (AuctionFailure e) {
			appletDisplay(e.description);
			
			// Disable functionality
			btnRefresh.setEnabled(false);
			sellList.setEnabled(false);
			bidList.setEnabled(false);
			btnCreateNewAuction.setEnabled(false);
			txtDescription.setEnabled(false);
			txtStartingPrice.setEnabled(false);
			btnSellItem.setEnabled(false);
			btnBid.setEnabled(false);
			txtBidPrice.setEnabled(false);
			loggedIn = false;
		} catch (Exception e1) {
			appletDisplay("Unable to log in");
			System.out.println("ERROR : " + e1) ;
			e1.printStackTrace(System.out);
		}
	}
	
	private void onCreateNewAuctionClicked() {
		try {
			int startingPrice;
			String description = txtDescription.getText();
			
			if (txtStartingPrice.getText().equals("")) {
				startingPrice = 0;
			}
			else {
				startingPrice = Integer.parseInt(txtStartingPrice.getText());
			}

			// Get a reference to an inactive auction from the auction manager.
			String auctionRef = auctionFactoryImpl.getInactiveAuction();
	
			// resolve the Object Reference in Naming
			Auction auctionImpl = AuctionHelper.narrow(ncRef.resolve_str(auctionRef));
			
			// Put an item up for sale
			auctionImpl.offerItem(userName, description, startingPrice);
			
			// Update the auction lists
			updateAuctionLists(false);
			
			appletDisplay("Started new auction");
			
		} catch (NumberFormatException e1) {
			appletDisplay("Invalid starting price");
		}
		catch (AuctionFailure e2) {
			appletDisplay(e2.description);
		} catch (Exception e3) {
			appletDisplay("Unable to create a new auction");
			System.out.println("ERROR : " + e3) ;
			e3.printStackTrace(System.out);
		}
	}
	
	private void onBidClicked() {
		try {
			// Determine which bidder auction is selected
			String auctionName = (String) bidList.getSelectedValue();
			
			// Determine what the bid is
			int bidPrice = Integer.parseInt(txtBidPrice.getText());
			
			if (auctionName != null) {
				// resolve the Object Reference in Naming
				Auction auctionImpl = AuctionHelper.narrow(ncRef.resolve_str(auctionName));
				
				// Place bid
				auctionImpl.bid(userName, bidPrice);
				appletDisplay("Placed bid of $" + Integer.toString(bidPrice) + " on " + auctionName);
				
				// Update the auction lists
				updateAuctionLists(false);
			};
			
		} catch (NumberFormatException e1) {
			appletDisplay("Invalid bid price");
		}
		catch (AuctionFailure e2) {
			appletDisplay(e2.description);
		} catch (Exception e3) {
			appletDisplay("Unable to place bid");
			System.out.println("ERROR : " + e3) ;
			e3.printStackTrace(System.out);
		}
	}
	
	private void onSellItemClick() {
		try {
			// Determine which seller auction is selected
			String auctionName = (String) sellList.getSelectedValue();
			
			if (auctionName != null) {
				// resolve the Object Reference in Naming
				Auction auctionImpl = AuctionHelper.narrow(ncRef.resolve_str(auctionName));
				
				// Sell item
				auctionImpl.sellItem(userName);
				appletDisplay(auctionName + " has ended");
				
				// Update the auction lists
				updateAuctionLists(false);
			};
			
		} catch (NumberFormatException e1) {
			appletDisplay("Invalid bid price");
		}
		catch (AuctionFailure e2) {
			appletDisplay(e2.description);
		} catch (Exception e3) {
			appletDisplay("Unable to place bid");
			System.out.println("ERROR : " + e3) ;
			e3.printStackTrace(System.out);
		}
	}
	
	private void onRefreshClicked() {
		// Update the auction lists
		updateAuctionLists(false);
	}
	
	private void onSellListSelectionChanged() {		
		try {
			String auctionName = (String) sellList.getSelectedValue();
			
			if (auctionName != null) {
				// Get the auction status
				AuctionStatus status = getAuctionStatus(auctionName);
				
				// Display the auction details
				sellAuctionDescription.setText(status.itemDesc);
				sellAuctionDescription.append("\nCurrent Bid: $" + status.itemPrice);
				sellAuctionDescription.append("\nHigh Bidder: " + status.highBidder);
			}
			else {
				sellAuctionDescription.setText("");
			}
		} catch (AuctionFailure e) {
			appletDisplay(e.description);
		} catch (Exception e1) {
			appletDisplay("Unable to display auction details");
			System.out.println("ERROR : " + e1) ;
			e1.printStackTrace(System.out);
		}
		
	}
	
	private void onBidListSelectionChanged() {
		try {
			String auctionName = (String) bidList.getSelectedValue();
			
			 if (auctionName != null) {
				// Get the auction status
				AuctionStatus status = getAuctionStatus(auctionName);
				
				// Display the auction details
				bidAuctionDescription.setText(status.itemDesc);
				bidAuctionDescription.append("\nCurrent Bid: $" + status.itemPrice);
				bidAuctionDescription.append("\nHigh Bidder: " + status.highBidder);
			}
			else {
				bidAuctionDescription.setText("");
			}
		} catch (AuctionFailure e) {
			appletDisplay(e.description);
		} catch (Exception e1) {
			appletDisplay("Unable to display auction details");
			System.out.println("ERROR : " + e1) ;
			e1.printStackTrace(System.out);
		}
	}
	
	public synchronized void updateAuctionLists(boolean isBackgroundThread) {
		AuctionsHolder sellerAuctions = new AuctionsHolder();
		AuctionsHolder bidderAuctions = new AuctionsHolder();
		IntHolder numSellerAuctions = new IntHolder();
		IntHolder numBidderAuctions = new IntHolder();
		
		try {
			auctionFactoryImpl.getActiveAuctions(userName, sellerAuctions, numSellerAuctions, bidderAuctions, numBidderAuctions);
			
			int sellIndex = sellList.getSelectedIndex();
			int bidIndex = bidList.getSelectedIndex();
			
			// Clear the lists
			sellListModel.clear();
			bidListModel.clear();
			
			// Re-populate the lists
			for (int idx = 0; idx < numSellerAuctions.value; idx++) {
				sellListModel.addElement(sellerAuctions.value[idx]);
			}
			for (int idx = 0; idx < numBidderAuctions.value; idx++) {
				bidListModel.addElement(bidderAuctions.value[idx]);
			}
			
			// Set the selected list index back to what it was before if possible
			if (sellIndex < sellListModel.getSize()) {
				sellList.setSelectedIndex(sellIndex);
			}
			if (bidIndex < bidListModel.getSize()) {
				bidList.setSelectedIndex(bidIndex);
			}
			
			if (!isBackgroundThread) {
				appletDisplay("Updated auction lists");
			}
			
		} catch (AuctionFailure e) {
			appletDisplay(e.description);
		} catch (Exception e1) {
			appletDisplay("Unable to update auction lists");
			System.out.println("ERROR : " + e1) ;
			e1.printStackTrace(System.out);
		}
	}
	
	private AuctionStatus getAuctionStatus(String auctionServerName) throws Exception {

		BooleanHolder isActive = new BooleanHolder();
		StringHolder itemDesc = new StringHolder();
		IntHolder price = new IntHolder();
		StringHolder highBidder = new StringHolder();
		
		// resolve the Object Reference in Naming
		Auction auctionImpl = AuctionHelper.narrow(ncRef.resolve_str(auctionServerName));
		
		// Get the auction status
		auctionImpl.viewAuctionStatus(userName, isActive, itemDesc, price, highBidder);

		// Return the status
		return new AuctionStatus(isActive.value, itemDesc.value, price.value, highBidder.value);
	}
	
	public void appletDisplay(String text) {
		infoBox.append("\n" + text);
	}
	
	private class AuctionStatus {
		public String itemDesc;
		public int itemPrice;
		public String highBidder;	
		
		AuctionStatus (boolean _isActive, String _itemDesc, int _itemPrice, String _highBidder) {
			itemDesc = _itemDesc;
			itemPrice = _itemPrice;
			highBidder = _highBidder;
		}
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
}
