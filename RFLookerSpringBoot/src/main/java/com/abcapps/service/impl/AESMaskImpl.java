package com.abcapps.service.impl;

import com.abcapps.service.AESMask;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Service
public class AESMaskImpl implements AESMask {
	String secret = "gani$krishan$pankaj";
	private SecretKeySpec secretKey;
	private byte[] key;

	public void setKey(String myKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest sha = null;
		key = myKey.getBytes("UTF-8");
		sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16);
		secretKey = new SecretKeySpec(key, "AES");
	}

	@Override
	public String encode(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		setKey(secret);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes("UTF-8")));
	}

	@Override
	public String decode(String data) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
		setKey(secret);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		return new String(cipher.doFinal(Base64.getDecoder().decode(data)));

	}

}
