package com.refeved.monitor.util;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DesUtil {

	private final static String DES = "DES";

//	public static void main(String[] args) throws Exception {
//		String data = "123 456";
//		String key = "wang!@#$%";
//		System.err.println(encrypt(data, key));
//		System.err.println(decrypt(encrypt(data, key), key));
//
//	}
	
	/**
	 * Description ���ݼ�ֵ���м���
	 * @param data 
	 * @param key  ���ܼ�byte����
	 * @return
	 * @throws Exception
	 */
/*	private static String encrypt(String data, String key) throws Exception {
		byte[] bt = encrypt(data.getBytes(), key.getBytes());
		String aString =bt.toString();
		//String aString = new String(bt);
		return new String(bt);
	}*/

	/**
	 * Description ���ݼ�ֵ���н���
	 * @param data
	 * @param key  ���ܼ�byte����
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
/*	private static String decrypt(String data, String key) throws IOException,
			Exception {
		if (data == null)
			return null;

		byte[] bt = decrypt(data.getBytes(),key.getBytes());

		return new String(bt);
	}*/

	/**
	 * Description ���ݼ�ֵ���м���
	 * @param data
	 * @param key  ���ܼ�byte����
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// ����һ�������ε������Դ
		SecureRandom sr = new SecureRandom();

		// ��ԭʼ��Կ���ݴ���DESKeySpec����
		DESKeySpec dks = new DESKeySpec(key);

		// ����һ����Կ������Ȼ��������DESKeySpecת����SecretKey����
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher����ʵ����ɼ��ܲ���
		Cipher cipher = Cipher.getInstance(DES);

		// ����Կ��ʼ��Cipher����
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}
	
	
	/**
	 * Description ���ݼ�ֵ���н���
	 * @param data
	 * @param key  ���ܼ�byte����
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// ����һ�������ε������Դ
		SecureRandom sr = new SecureRandom();

		// ��ԭʼ��Կ���ݴ���DESKeySpec����
		DESKeySpec dks = new DESKeySpec(key);

		// ����һ����Կ������Ȼ��������DESKeySpecת����SecretKey����
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher����ʵ����ɽ��ܲ���
		Cipher cipher = Cipher.getInstance(DES);

		// ����Կ��ʼ��Cipher����
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}
}