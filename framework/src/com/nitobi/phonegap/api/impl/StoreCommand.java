/**
 * The MIT License
 * -------------------------------------------------------------
 * Copyright (c) 2008, Rob Ellis, Brock Whitten, Brian Leroux, Joe Bowser, Dave Johnson, Nitobi
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.nitobi.phonegap.api.impl;

import java.util.Enumeration;
import java.util.Hashtable;

import com.nitobi.phonegap.PhoneGap;
import com.nitobi.phonegap.api.Command;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

public class StoreCommand implements Command {
	private static final int SAVE_COMMAND = 0;
	private static final int LOADALL_COMMAND = 1;
	private static final int LOAD_COMMAND = 2;
	private static final String CODE = "PhoneGap=store";
	private static final String TCK_SAVE = ";if (navigator.store.save_success != null) { navigator.store.save_success(true); };";
	private long KEY = 0x4a9ab8d0f0147f4cL;
	static PersistentObject store;
	static {
		store = PersistentStore.getPersistentObject( KEY );
	}

	public StoreCommand(PhoneGap phoneGap) {
		// Generate a Persistent Storage key based on the application UID, which is generated at build-time by less-than-ideal string replacement inside PhoneGap.java :/
		try {
			this.KEY = new Long(phoneGap.APPLICATION_UID);
		} catch(NumberFormatException e) {
			// Just keep the stock one for now if something fucked up.
		}
	}
	
	public boolean accept(String instruction) {
		return instruction != null && instruction.startsWith(CODE);
	}
	private int getCommand(String instruction) {
		String command = instruction.substring(CODE.length()+1);
		if (command.startsWith("save")) return SAVE_COMMAND;
		if (command.startsWith("loadAll")) return LOADALL_COMMAND;
		if (command.startsWith("load")) return LOAD_COMMAND;
		return -1;
	}

	public String execute(String instruction) {
		switch (getCommand(instruction)) {
			case SAVE_COMMAND: // Saves data associated to a key to the store hash.
				String serialized = instruction.substring(CODE.length() + 6);
				Hashtable hash = new Hashtable(); // The existing hash that we have in the system.
				String[] keyValuePair = PhoneGap.splitString(serialized, '/', false);
				// Retrieve the stored hash.
				synchronized(store) {
					Object tempO = store.getContents();
					if (tempO != null) {
						hash = (Hashtable)tempO;
					}
					tempO = null;
				}
				// Add the new key/value pair to the hash.
				hash.put(keyValuePair[0], keyValuePair[1]);
				synchronized(store) {
					store.setContents(hash);
					store.commit();
				}
				serialized = null;
				hash = null;
				keyValuePair = null;
				return TCK_SAVE;
			case LOADALL_COMMAND: // Retrieves the entire hash, composes the JS object for it and returns it to the browser.
				String retVal = "";
				Hashtable hash = new Hashtable();
				synchronized(store) {
					Object storeObj = store.getContents();
					if (storeObj != null) {
						hash = (hash)storeObj;
						Enumeration e = hash.keys();
						retVal = "{";
						while (e.hasMoreElements()) {
							String key = (String)e.nextElement();
							String value = (String)hash.get(key);
							retVal += "\"" + key + "\":\"" + value + "\",";
						}
						if (retVal.length() > 1) retVal = retVal.substring(0, retVal.length()-1);
						retVal += "}";
					} else {
						retVal = "{}";
					}
					storeObj = null;
				}
				return ";if (navigator.store.loadAll_success != null) { navigator.store.loadAll_success('" + retVal + "'); };";
			case LOAD_COMMAND: // Retrives a particular value associated to a key in the hash.
				String retVal = "null"; // default return value. return empty string? return null?
				String key = instruction.substring(CODE.length() + 6);
				Hashtable hash = new Hashtable();
				synchronized(store) {
					Object storeObj = store.getContents();
					if (storeObj != null) {
						hash = (hash)storeObj;
						if (hash.containsKey(key)) {
							String value = (String)hash.get(key);
							if (value != null) {
								retVal = "'" + value + "'";
							}
						}
					}
					storeObj = null;
				}
				return ";if (navigator.store.load_success != null) { navigator.store.load_success('" + retVal + "'); };";
		}
		return null;
	}
}
