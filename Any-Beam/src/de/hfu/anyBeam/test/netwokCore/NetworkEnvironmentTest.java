package de.hfu.anyBeam.test.netwokCore;

import java.io.IOException;

import de.hfu.anyBeam.netwokCore.NetworkEnvironment;

public class NetworkEnvironmentTest {
	
	public static void main(String[] args) {

		try {
			new NetworkEnvironment(4242, true).createClientList();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
