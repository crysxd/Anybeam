package de.hfu.anybeam.networkCore;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * A class that can be used to create and parse URL encoded {@link String}s to store and transmit data in.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public class UrlParameterBundle {
	
	//A Map in which all values are stored
	private final Map<String, String> DATA = new HashMap<String, String>();
	
	/**
	 * Creates an empty {@link UrlParameterBundle} object.
	 */
	public UrlParameterBundle() {

	}
	
	/**
	 * Creates a new {@link UrlParameterBundle} object and populates it with the values from the given URL {@link String}
	 * @param url the URL to get the values from.
	 */
	public UrlParameterBundle(String url) {
		//Cut off uneccessary informations
		if(url.lastIndexOf('/') > 0)
			url = url.substring(url.lastIndexOf('/'));
			
		this.parseUrlString(url);
	}
	
	/**
	 * Returns a map containing all parameters of this {@link UrlParameterBundle}.
	 * @return a map containing all parameters of this {@link UrlParameterBundle}
	 */
	public Map<String, String> getMap() {
		//Copy and return
		Map<String, String> map = new HashMap<String, String>();
		map.putAll(this.DATA);
		return map;
	}
	
	/**
	 * Adds a parameter.
	 * @param key the key identifying the value
	 * @param value the value. Keep in mind that all values are stored as {@link String}.
	 * @return the {@link UrlParameterBundle}
	 */
	public UrlParameterBundle put(String key, Object value) {
		this.DATA.put(key, value.toString());
		return this;

	}
	
	/**
	 * Returns the value of the parameter with the given key.
	 * @param key the key of the requested parameter
	 * @return the {@link String} value of the value
	 */
	public String get(String key) {
		return this.DATA.get(key);
		
	}
	
	/**
	 * Returns the value of the parameter with the given key.
	 * @param key the key of the requested parameter
	 * @return the {@link Double} value of the value
	 */
	public double getDouble(String key) {
		return Double.valueOf(this.DATA.get(key));
		
	}
	
	/**
	 * Returns the value of the parameter with the given key.
	 * @param key the key of the requested parameter
	 * @return the {@link Integer} value of the value
	 */
	public int getInteger(String key) {
		return Integer.valueOf(this.DATA.get(key));
		
	}
	
	/**
	 * Returns the value of the parameter with the given key.
	 * @param key the key of the requested parameter
	 * @return the {@link Long} value of the value
	 */
	public long getLong(String key) {
		return Long.valueOf(this.DATA.get(key));
		
	}
	
	/**
	 * Returns the value of the parameter with the given key.
	 * @param key the key of the requested parameter
	 * @return the {@link Float} value of the value
	 */
	public float getFloat(String key) {
		return Float.valueOf(this.DATA.get(key));
		
	}
	
	/**
	 * Returns the value of the parameter with the given key.
	 * @param key the key of the requested parameter
	 * @return the {@link Boolean} value of the value
	 */
	public boolean getBoolean(String key) {
		return Boolean.valueOf(this.DATA.get(key));
		
	}
	
	/**
	 * Parses the given {@link String} and adds it parameter pairs to this
	 */
	private void parseUrlString(String data) {		
		String parts[] = data.split("&");
		
		for(String p : parts) {
			int index = p.indexOf('=');
		
			if(index > 0) {
				String key;
				try {
					key = URLDecoder.decode(p.substring(0, index), "UTF-8");
					String value = URLDecoder.decode(p.substring(index+1, p.length()), "UTF-8");
					this.put(key, value);
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
		
				}
			}
		}
	}
	
	/**
	 * Creates an URL encoded {@link String} containing all parameters of this {@link UrlParameterBundle} object.
	 * @return the generated String containing all parameters of this {@link UrlParameterBundle} object
	 */
	public String generateUrlString() {
		StringBuilder s = new StringBuilder();
	
		Iterator<String> keys = this.DATA.keySet().iterator();
		Iterator<String> values = this.DATA.values().iterator();
		
		while(keys.hasNext()) {
			try {
				s.append(String.format(Locale.ENGLISH, "%s=%s&", URLEncoder.encode(keys.next(), "UTF-8"), URLEncoder.encode(values.next(), "UTF-8")));
			
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				
			}
		}
		
		s.deleteCharAt(s.length()-1);
		
		return s.toString();
	}

}
