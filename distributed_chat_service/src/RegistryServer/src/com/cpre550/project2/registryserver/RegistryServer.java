package com.cpre550.project2.registryserver;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import com.cpre550.project2.registry.RmiRegistry;

public class RegistryServer {

	public static void main(String args[]) {
		try {
			Registry registry = null;
			try {
				registry = LocateRegistry.getRegistry(1099);
				registry.list();
			}
			catch (RemoteException e) {
				registry = LocateRegistry.createRegistry(1099);
			}
			
			RmiRegistry engine = new RmiRegistryEngine();
			Naming.rebind("//localhost:1099/RegistryServer", engine);
			System.out.println("RegistryServer RMI interface bound");
		}
		catch (Exception e) {
			System.err.println("RegistryServer exception:");
            e.printStackTrace();
		}
	}
}
