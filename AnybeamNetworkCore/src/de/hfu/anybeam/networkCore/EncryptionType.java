package de.hfu.anybeam.networkCore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * An enum containng all possible encryption types and useful functions.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
public enum EncryptionType {
	DES, AES128, AES256, NONE;

	/**
	 * Returns a human readable name of this encryption or null if the {@link EncryptionType} is {@link EncryptionType#NONE}.
	 * @return a human readable name of this encryption or null if the {@link EncryptionType} is {@link EncryptionType#NONE}
	 */
	public String getHumanEncryptionName() {
		switch(this) {
		case AES128: return "AES (128 bit)";
		case AES256: return "AES (256 bit)";
		case DES: return "DES (56 bit)";
		default: return null;
		}
	}


	/**
	 * Returns a technical name of this encryption or null if the {@link EncryptionType} is {@link EncryptionType#NONE}.
	 * @return a technical name of this encryption or null if the {@link EncryptionType} is {@link EncryptionType#NONE}
	 */
	public String getEncryptionName() {
		switch(this) {
		case AES128: 
		case AES256: return "AES";
		case DES: return "DES";
		default: return null;
		}
	}


	/**
	 * Returns the key length for this {@link EncryptionType} in bit.
	 * @return the key length for this {@link EncryptionType} in bit or 0 if this {@link EncryptionType} is {@link EncryptionType#NONE}
	 */
	public int getKeyLength() {
		switch(this) {
		case AES128: return 128;
		case AES256: return 256;
		case DES: return 56;
		default: return 0;
		}
	}

	/**
	 * Returns the key length for this {@link EncryptionType} in byte.
	 * @return the key length for this {@link EncryptionType} in byte or 0 if this {@link EncryptionType} is {@link EncryptionType#NONE}
	 */
	public int getKeyLengthByte() {
		switch(this) {
		case AES128: return 16;
		case AES256: return 32;
		case DES: return 8;
		default: return 0;
		}

	}

	/**
	 * Generates a random secret key using {@link KeyGenerator} matching the needs of a key this {@link EncryptionType}.
	 * @return a random secret key using {@link KeyGenerator} matching the needs of a key this {@link EncryptionType}
	 * @throws Exception
	 */
	public byte[] getRandomSecretKey() throws Exception {
		if(this == EncryptionType.NONE)
			return new byte[0];

		KeyGenerator keyGen = KeyGenerator.getInstance(this.getEncryptionName());
		keyGen.init(this.getKeyLength()); // for example
		SecretKey secretKey = keyGen.generateKey();

		return secretKey.getEncoded();
	}

	/**
	 * Generates a random secret key using the password's SHA-1 hash matching the needs of a key this {@link EncryptionType}.
	 * @return a random secret key using the password's SHA-1 hash matching the needs of a key this {@link EncryptionType}
	 */
	public byte[] getSecretKeyFromPassword(String password) {
		if(this == EncryptionType.NONE)
			return new byte[0];

		byte secretKey[] = null;
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			secretKey = sha.digest(password.getBytes());
			secretKey = Arrays.copyOf(secretKey, this.getKeyLengthByte());

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}

		return secretKey;
	}

	/**
	 * Creates a {@link Cipher} for this {@link EncryptionType}.
	 * @return a {@link Cipher} for this {@link EncryptionType} or null if this {@link EncryptionType} is {@link EncryptionType#NONE}
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public Cipher createCipher() {
		try {
			switch(this) {
			case AES128: 
			case AES256: return Cipher.getInstance("AES/ECB/PKCS5Padding");
			case DES: return Cipher.getInstance("DES/ECB/PKCS5Padding");
			default: return null;
			}	
			
		} catch(Exception e) {
			return null;
			
		}
	
	}

	/**
	 * Creates a {@link SecretKeySpec} from the given bytes. The length of the given byte array must match the needed length for this {@link EncryptionType}.
	 * @param rawKeyData
	 * @see #getKeyLengthByte()
	 * @see #getKeyLength()
	 * @return a {@link SecretKeySpec} from the given bytes
	 */
	public SecretKeySpec getSecretKeySpec(byte[] rawKeyData) {
		if(this == EncryptionType.NONE)
			return null;

		return new SecretKeySpec(rawKeyData, this.getEncryptionName());
	}

	/**
	 * Generates a human readable {@link String} representing the key.
	 * @param key the key that should be converted
	 * @return a {@link String} representing the given key
	 * @see #getSecretKeyFromHumanReadableKey(String)
	 */
	public String getHumanReadableKey(byte[] key) {
		key = Arrays.copyOfRange(key, 0, this.getKeyLengthByte());

		//To hex string
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<key.length; i++) {
			sb.append(String.format("%02x", key[i]&0xff));

		}

		//Insert separators
		for(int i=0; i<sb.length(); i++) {
			if((i+1)%5==0) {
				sb.insert(i, '-');
			}

		}	
		
		return sb.toString().toUpperCase();

	}
	
	/**
	 * Generates a key from a human readable {@link String} created with {@link #getHumanReadableKey(byte[])}
	 * @param password the {@link String} taht should be converted in a key
	 * @return the converted key
	 */
	public byte[] getSecretKeyFromHumanReadableKey(String password) {
		if(this == EncryptionType.NONE)
			return new byte[0];

		//create array and remove seperators
		byte key[] = new byte[this.getKeyLengthByte()];
		password = password.replaceAll("-", "");
		
		//Check length. To chars equal one byte in the key
		if(password.length() != this.getKeyLengthByte() * 2) {
			throw new RuntimeException("The presented strign has the wrong size. Check if you use the correct EncryptionType.");
		}

		//Restore key and return
		for(int i=0; i<password.length(); i+=2) {
			int j = Integer.parseInt(password.substring(i, i+2), 16);
			key[i/2] = (byte) j;
		}

		return key;
	}

}

