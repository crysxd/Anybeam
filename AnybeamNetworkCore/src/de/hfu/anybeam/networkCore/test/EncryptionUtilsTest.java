package de.hfu.anybeam.networkCore.test;

import java.util.Arrays;

import de.hfu.anybeam.networkCore.EncryptionType;
import de.hfu.anybeam.networkCore.EncryptionUtils;

public class EncryptionUtilsTest {
	
	public static void main(String[] args) throws Exception {
		byte[] key = EncryptionUtils.generateSecretKey(EncryptionType.DES);
		
		System.out.println("Checksum: " + EncryptionUtils.getKeyChecksum(key));
		String humanReadable =  EncryptionUtils.getHumanReadableKeyFromKey(key);
		System.out.println("Human Readable: " + humanReadable);
		byte[] rebuildKey = EncryptionUtils.getKeyFromHumanReadableKey(humanReadable);
		System.out.println("Backwards success: " + Arrays.equals(rebuildKey, key));
	}

}
