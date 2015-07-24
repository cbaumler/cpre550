package com.cpre550.project2.registryserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import com.cpre550.project2.registry.EntityInformation;
import com.cpre550.project2.registry.RmiRegistry;

public class RmiRegistryEngine extends UnicastRemoteObject implements RmiRegistry {

	private static final long serialVersionUID = 8474272695398653096L;
	
	private static final ConcurrentHashMap<String, EntityInformation> mEntities = new ConcurrentHashMap<String, EntityInformation>();

	protected RmiRegistryEngine() throws RemoteException {
		super();
	}

	@Override
	public int dcsRegister(String entityName, EntityInformation info)
			throws RemoteException {
		mEntities.put(entityName, info);
		System.out.println(entityName + " registered");
		return 0;
	}

	@Override
	public int dcsUnregister(String entityName) throws RemoteException {
		mEntities.remove(entityName);
		System.out.println(entityName + " unregistered");
		return 0;
	}

	@Override
	public EntityInformation dcsGetInfo(String entityName)
			throws RemoteException {
		EntityInformation entityInfo = mEntities.get(entityName);
		return entityInfo;
	}

	@Override
	public ArrayList<String> dcsGetAllChatRooms() throws RemoteException {
		ArrayList<String> list = new ArrayList<String>();
		Enumeration<String> keys = mEntities.keys();
		while (keys.hasMoreElements()) {
			String next = keys.nextElement();
			if (mEntities.get(next).isChatRoom) {
				list.add(next);
			}
		}
		return list;
	}
}
