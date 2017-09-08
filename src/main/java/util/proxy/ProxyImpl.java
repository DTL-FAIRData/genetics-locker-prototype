package util.proxy;
// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class uses the Server class to provide a multi-threaded server 
 * framework for a relatively simple proxy service.  The main() method
 * starts up the server.  The nested Proxy class implements the 
 * Server.Service interface and provides the proxy service.
 **/
public class ProxyImpl implements Proxy{

	private SecretKeySpec secretKey;
	private Cipher cipher;
	private URLConnection uc;
	
	
	public static void main(String[] argv) throws MalformedURLException, ProxyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		ProxyImpl p = new ProxyImpl();
		URL url = p.obfuscateURL(new URL("http://www.sapo.pt/"));
		
		System.out.println(url.toString());
		
		URL durl = p.resolveObfuscatedURL("Ct9L5jx5D3kxgvepgkdKTel3pU2EOTlVPe9Y9ne4W24%3D");
		
		System.out.println(durl.toString());
	}
	
	//TODO create exception
	public ProxyImpl() throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
		byte[] key = fixSecret("=ThiSiSAn~Amzing.!", 16);
		this.secretKey = new SecretKeySpec(key, "AES");
		System.out.println("secret key");
		System.out.println(this.secretKey);
		this.cipher = Cipher.getInstance("AES");
		
	}
	
	private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
		if (s.length() < length) {
			int missingLength = length - s.length();
			for (int i = 0; i < missingLength; i++) {
				s += " ";
			}
		}
		return s.substring(0, length).getBytes("UTF-8");
	}
	
	private String encryptAndEncode(String str) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		byte[] encrypted = cipher.doFinal(str.getBytes());
		String base64encrypt = Base64.getEncoder().encodeToString(encrypted);
		return URLEncoder.encode( base64encrypt , "UTF-8");
		//return encrypted.toString();
	}
	
	private String decryptAndDecode(String str) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
		String base64encrypt = URLDecoder.decode(str, "UTF-8");
		byte[] encrypted = cipher.doFinal(Base64.getDecoder().decode(base64encrypt.getBytes()));
		//byte[] encrypted = cipher.doFinal(Base64.getDecoder().decode(str.getBytes()));
		//byte[] encrypted = cipher.doFinal(URLDecoder.decode(str, "UTF-8").getBytes());
		//byte[] encrypted = cipher.doFinal(str.getBytes());
		return new String(encrypted);
	}
	
	
	public URL obfuscateURL(URL remoteUrl) throws ProxyException {
		String obfuscatedURL;
		URL url = null;
		String baseurl = "http://127.0.0.1:8080/fdp";
		try {
			obfuscatedURL = encryptAndEncode(remoteUrl.toString());
			url = new URL(baseurl+"/resource/"+obfuscatedURL);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | MalformedURLException | UnsupportedEncodingException e) {
			ProxyException pe = new ProxyException("The url can't be obfuscated", e.getCause());
			throw pe;
		}
		
		return url;
	}
	
	public URL resolveObfuscatedURL(String remoteURL) throws ProxyException  {
		String clearURL = null;
		URL url;
	
		try {
			clearURL = decryptAndDecode(remoteURL);
			url = new URL(clearURL);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | MalformedURLException | UnsupportedEncodingException e) {
			e.printStackTrace();
			ProxyException pe = new ProxyException("The url can't be desobfuscated", e.getCause());
			throw pe;
		}
		
		return url;
	}
	
	public InputStream get(URL url) throws ProxyException{
		InputStream inputStream;
		try {
			uc = url.openConnection();
			inputStream = uc.getInputStream();
			//InputStream in = new BufferedInputStream(raw);
		} catch (IOException e) {
			throw new ProxyException("The URL "+url+" is not available or is invalid", e.getCause());
		}
		
		String contentType = uc.getContentType();
		int contentLength = uc.getContentLength();
		
		//if (contentType.startsWith("text/") || contentLength == -1) {
		//  throw new IOException("This is not a binary file.");
		//}
		 
	    return inputStream;

	}
	
	public String getContentType() {
		//if(this.uc!=null)
		return this.uc.getContentType();
	}
	
	
}