package com.cpre550.project2.registry;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RmiRegistry extends Remote {
	
	public int dcsRegister(String entityName, EntityInformation info) throws RemoteException;
	
	public int dcsUnregister(String entityName) throws RemoteException;
	
	public EntityInformation dcsGetInfo(String entityName) throws RemoteException;
	
	public ArrayList<String> dcsGetAllChatRooms() throws RemoteException;
}
