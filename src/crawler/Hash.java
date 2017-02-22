package crawler;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Hash {
	public static void main(String[] args){
		Scanner in = new Scanner(System.in);
		String test = in.nextLine();
		System.out.println(test.hashCode());
		System.out.println((test + "").hashCode());
		in.close();
	}
	public static String MD5(String toHash){
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(toHash.getBytes(Charset.forName("UTF-8")));
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < bytes.length; i++){
				sb.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		}
		catch(NoSuchAlgorithmException e){
			System.out.println("No algorithm!");
			return null;
		}
	}
}
