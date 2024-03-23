package com.fyp2024.parentalcontrol.androidapp.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Child extends User {
	private String parentEmail;
	private int TAC;
	private ArrayList<App> apps = new ArrayList<>();
	private ArrayList<Contact> Contacts = new ArrayList<>();
	private Location location;
	private HashMap<String, Message> messages = new HashMap<>();
	private HashMap<String, Call> calls = new HashMap<>();
	private ScreenLock screenLock;
	private String profileImage;
	private boolean appDeleted;
	private boolean verified;

	public Child() {
	}
	
	public Child(String name, String email, String parentEmail, int TAC) {
		super(name, email);
		this.parentEmail = parentEmail;
		this.TAC = TAC;
		this.verified = false;
	}

	public boolean getVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getParentEmail() {
		return parentEmail;
	}
	
	public void setParentEmail(String parentEmail) {
		this.parentEmail = parentEmail;
	}

	public int getTAC() {
		return TAC;
	}

	public void setTAC(int TAC) {
		this.TAC = TAC;
	}
	
	public ArrayList<App> getApps() {
		return apps;
	}
	
	public void setApps(ArrayList<App> apps) {
		this.apps = apps;
	}
	
	public ArrayList<Contact> getContacts() {
		return Contacts;
	}

	public void setContacts(ArrayList<Contact> contacts) {
		Contacts = contacts;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public HashMap<String, Message> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<String, Message> messages) {
		this.messages = messages;
	}

	public HashMap<String, Call> getCalls() {
		return calls;
	}

	public void setCalls(HashMap<String, Call> calls) {
		this.calls = calls;
	}
	
	public ScreenLock getScreenLock() {
		return screenLock;
	}
	
	public void setScreenLock(ScreenLock screenLock) {
		this.screenLock = screenLock;
	}
	
	@Override
	public String getProfileImage() {
		return profileImage;
	}
	
	@Override
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
	
	public boolean isAppDeleted() {
		return appDeleted;
	}
	
	public void setAppDeleted(boolean appDeleted) {
		this.appDeleted = appDeleted;
	}
}
