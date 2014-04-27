package de.hfu.anyBeam.netwokCore;

import java.net.InetAddress;

public class Client {
	
	private InetAddress address;
	private String name;
	private int dataPort;
	private String id;
	
	public Client(InetAddress a, String n, int dataPort, String id) {
		this.setAddress(a);
		this.setName(n);
		this.setId(id);
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
	
	public void setDataPort(int dataPort) {
		this.dataPort = dataPort;
	}
	
	public int getDataPort() {
		return this.dataPort;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
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
		
		return this == c || c.getName().equals(this.getName()) && c.getAddress().equals(this.getAddress());
	}
	

}
