package de.hfu.anybeam.networkCore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class HeaderBundle {
	
	Map<String, String> DATA = new HashMap<String, String>();
	
	
	public HeaderBundle() {
		
	}
	
	public HeaderBundle(String data) {
		this.parseDataString(data);
	}
	
	public HeaderBundle put(String key, Object value) {
		if(key.contains(";")) 
			throw new IllegalArgumentException("Key contains illegal pattern ';'. (key=" + key + ")");
		
		if(key.contains("=")) 
			throw new IllegalArgumentException("Key contains illegal pattern '='. (key=" + key + ")");
	
		String valueString = value.toString();
		
		if(valueString.contains(";")) 
			throw new IllegalArgumentException("Value contains illegal pattern ';'. (value=" + valueString + ")");
		
		if(valueString.contains("=")) 
			throw new IllegalArgumentException("Value contains illegal pattern '='. (value=" + valueString + ")");
		
		this.DATA.put(key, valueString);
		return this;

	}
	
	public String get(String key) {
		return (String) this.DATA.get(key);
	}
	
	public double getDouble(String key) {
		return Double.valueOf(this.DATA.get(key));
	}
	
	public int getInt(String key) {
		return Integer.valueOf(this.DATA.get(key));
	}
	
	public long getLong(String key) {
		return Long.valueOf(this.DATA.get(key));
	}
	
	public float getFloat(String key) {
		return Float.valueOf(this.DATA.get(key));
	}
	
	public boolean getBoolean(String key) {
		return Boolean.valueOf(this.DATA.get(key));
	}
	
	private void parseDataString(String data) {
		int index=0;
		
		while(index >= 0 && index < data.length()) {
			int endKey = data.indexOf('=', index);
			int endValue = data.indexOf(';', endKey);
			
			if(endKey < 0 || endValue < 0)
				break;
			
			String key = data.substring(index, endKey);
			String value = data.substring(endKey+1, endValue);
			this.put(key, value);
			
			index = endValue + 1;
		}
	}
	
	public String generateHeaderString() {
		StringBuilder s = new StringBuilder();
	
		Iterator<String> keys = this.DATA.keySet().iterator();
		Iterator<String> values = this.DATA.values().iterator();
		
		while(keys.hasNext()) {
			s.append(String.format(Locale.ENGLISH, "%s=%s;", keys.next(), values.next()));
		}
		
		return s.toString();
	}

}
