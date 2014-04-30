package de.hfu.anybeam.networkCore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


public class UrlParameterBundle {
	
	private final Map<String, String> DATA = new HashMap<String, String>();
	
	
	public UrlParameterBundle() {
		
	}
	
	public UrlParameterBundle(String data) {
		this.parseDataString(data);
	}
	
	public UrlParameterBundle put(String key, Object value) {
		if(key.contains("&")) 
			throw new IllegalArgumentException("Key contains illegal pattern ';'. (key=" + key + ")");
		
		if(key.contains("=")) 
			throw new IllegalArgumentException("Key contains illegal pattern '='. (key=" + key + ")");
	
		String valueString = value.toString();
		
		if(valueString.contains("&")) 
			throw new IllegalArgumentException("Value contains illegal pattern ';'. (value=" + valueString + ")");
		
		if(valueString.contains("=")) 
			throw new IllegalArgumentException("Value contains illegal pattern '='. (value=" + valueString + ")");
		
		this.DATA.put(key, valueString);
		return this;

	}
	
	public String get(String key) {
		return this.DATA.get(key);
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
		String parts[] = data.split("&");
		
		for(String p : parts) {
			int index = p.indexOf('=');
		
			if(index > 0) {
				String key = p.substring(0, index);
				String value = p.substring(index+1, p.length());
				this.put(key, value);
			}
		}
	}
	
	public String generateHeaderString() {
		StringBuilder s = new StringBuilder();
	
		Iterator<String> keys = this.DATA.keySet().iterator();
		Iterator<String> values = this.DATA.values().iterator();
		
		while(keys.hasNext()) {
			s.append(String.format(Locale.ENGLISH, "%s=%s&", keys.next(), values.next()));
		}
		
		s.deleteCharAt(s.length()-1);
		
		return s.toString();
	}

}
