package de.hfu.anybeam.networkCore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class EncryptionUtils {
	
	public static String getEncryptionName(EncryptionType type) {
		switch(type) {
		case AES128:
		case AES256: return "AES";
		case DES: return "DES";
		case NONE: return "NONE";
		}
		
		return null;
	}
	
	public static int getKeyLength(EncryptionType type) {
		switch(type) {
		case AES128: return 128;
		case AES256: return 256;
		case DES: return 56;
		case NONE: return 0;
		}
	
		return -1;
	}
	
	public static int getKeyLengthByte(EncryptionType type) {
		switch(type) {
		case AES128: return 16;
		case AES256: return 32;
		case DES: return 8;
		case NONE: return 0;
		}
	
		return -1;
	}
	
	public static byte[] generateSecretKey(EncryptionType type) throws Exception {
		if(type == EncryptionType.NONE)
			return new byte[0];
		
		KeyGenerator keyGen = KeyGenerator.getInstance(EncryptionUtils.getEncryptionName(type));
		keyGen.init(EncryptionUtils.getKeyLength(type)); // for example
		SecretKey secretKey = keyGen.generateKey();
		
		return secretKey.getEncoded();
	}
	
	public static byte[] generateSecretKeyFromPassword(String password, EncryptionType type) {
		if(type == EncryptionType.NONE)
			return new byte[0];
		
		byte secretKey[] = null;
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			secretKey = sha.digest(password.getBytes());
			secretKey = Arrays.copyOf(secretKey, EncryptionUtils.getKeyLengthByte(type));
			
		} catch (NoSuchAlgorithmException e) {
		}
		
		return secretKey;
	}
	
	
	public static Cipher createCipher(EncryptionType type) throws NoSuchAlgorithmException, NoSuchPaddingException {
		switch(type) {
		case AES128: 
		case AES256: return Cipher.getInstance("AES/ECB/PKCS5Padding");
		case DES: return Cipher.getInstance("DES/ECB/PKCS5Padding");
		default: return null;
		}
	}
	
	
	public static SecretKeySpec createKey(EncryptionType type, byte[] rawKeyData) {
		if(type == EncryptionType.NONE)
			return null;
		
		return new SecretKeySpec(rawKeyData, EncryptionUtils.getEncryptionName(type));
	}
	
	public static int getTrasmissionPaddingMaxLength() {
		return 32;
	}
	
	public static int getKeyChecksum(byte[] key) {
		
		double crc = 0;
		for(int i=0; i<key.length; i++) {
			crc += Math.pow(i+1, 2) * key[i];
		}
		
		return (int) crc % 9999;
		
	}
	
	public static String getHumanReadableKeyFromKey(byte[] key) {
		
		 HexBinaryAdapter adapter = new HexBinaryAdapter();
		 StringBuilder s =  new StringBuilder(adapter.marshal(key));
		 
		 for(int i=0; i<s.length(); i++) {
			 
			 if((i+1)%5==0) {
				 s.insert(i, '-');
			 }
		 }
		 
		 return s.toString();
	}
	
	public static byte[] getKeyFromHumanReadableKey(String humanReadable) {
		 HexBinaryAdapter adapter = new HexBinaryAdapter();
		 return adapter.unmarshal(humanReadable.replaceAll("-", ""));
	}

}
