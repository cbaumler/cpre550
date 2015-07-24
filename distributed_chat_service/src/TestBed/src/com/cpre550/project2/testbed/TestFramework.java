package com.cpre550.project2.testbed;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.Calendar;

import com.cpre550.project2.registry.EntityInformation;
import com.cpre550.project2.registry.RmiRegistry;

public class TestFramework {
	
	public static void main(String args[]) {
		try {
			int port = 5678;
			String name = InetAddress.getLocalHost().getCanonicalHostName() + ":" + port + "/TestRoom";
				
			RmiRegistry remoteRegistry = (RmiRegistry)Naming.lookup("//localhost/RegistryServer");
			
			EntityInformation info = new EntityInformation(true, port, InetAddress.getLocalHost());

			int numChatRooms = 100;
			int numExecutions = 1000;
			
			// Register additional chat rooms
			for (int idx = 0; idx < numChatRooms; idx++) {
				remoteRegistry.dcsRegister(name + idx, info);
			}
			
			long startTime = Calendar.getInstance().getTimeInMillis();
			
			// Perform the first test
			for (int idx = 1; idx < numExecutions; idx++) {
				remoteRegistry.dcsRegister(name, info);
				remoteRegistry.dcsUnregister(name);
			}
			
			long endTime = Calendar.getInstance().getTimeInMillis();
			
			long elapsedTime = endTime - startTime;
			float averageTime = (float)elapsedTime / (float)numExecutions;
			
			// Print the results
			System.out.println("Registration/De-registration Latency Test:");
			System.out.println("start time:    " + startTime + " ms");
			System.out.println("end time:      " + endTime + " ms");
			System.out.println("cycles:        " + numExecutions);
			System.out.println("elapsed time: " + elapsedTime);
			System.out.println("Average latency of a registration/de-registration cycle is " + averageTime + " ms");
			
			remoteRegistry.dcsRegister(name, info);
			
			startTime = Calendar.getInstance().getTimeInMillis();
			
			// Perform the second test
			for (int idx = 1; idx < numExecutions; idx++) {
				info = remoteRegistry.dcsGetInfo(name);
			}
			
			endTime = Calendar.getInstance().getTimeInMillis();
			
			elapsedTime = endTime - startTime;
			averageTime = (float)elapsedTime / (float)numExecutions;
			
			// Print the results
			System.out.println("Inquiry Latency Test:");
			System.out.println("start time:    " + startTime + " ms");
			System.out.println("end time:      " + endTime + " ms");
			System.out.println("cycles:        " + numExecutions);
			System.out.println("elapsed time: " + elapsedTime);
			System.out.println("Average latency of an inquiry is " + averageTime + " ms");
			
			remoteRegistry.dcsUnregister(name);
			
			// de-register additional chat rooms
			for (int idx = 1; idx < numChatRooms; idx++) {
				remoteRegistry.dcsUnregister(name + idx);
			}
		
		} catch (Exception e) {
			System.err.println("TestFramework exception:");
            e.printStackTrace();
		}
	}
}
