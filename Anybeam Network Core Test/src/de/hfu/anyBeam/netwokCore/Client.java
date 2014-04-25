package de.hfu.anyBeam.netwokCore;

import java.net.InetAddress;

public class Client {
	
	private InetAddress address;
	private String name;
	
	public Client(InetAddress a, String n) {
		this.setAddress(a);
		this.setName(n);
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return String.format("{address:\"%s\", name:\"%s\"}", this.getAddress(), this.getName());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Client)) {
			return false;
		}
		
		Client c = (Client) obj;
		
		return c.getName().equals(this.getName()) && c.getAddress().equals(this.getAddress());
	}
	

}
