package de.hfu.anyBeam.netwokCore;

import java.io.IOException;

public class NetworkEnvironmentTest {
	
	public static void main(String[] args) {

		try {
			new NetworkEnvironment(4242, true).createClientList();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
