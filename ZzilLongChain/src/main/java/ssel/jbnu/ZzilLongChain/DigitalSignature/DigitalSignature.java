package ssel.jbnu.ZzilLongChain.DigitalSignature;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.util.Base64;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import ssel.jbnu.ZzilLongChain.Transaction.Transaction;
import ssel.jbnu.ZzilLongChain.Util.StringUtil;

public class DigitalSignature {

	private Transaction Transaction;

	public DigitalSignature(Transaction transaction) {
		Security.addProvider(new BouncyCastleProvider());
		this.Transaction = transaction;
	}


	private byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try
		{
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return output;
	}

	// Verifies a String signature
	private boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try
		{
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	//Signs all the data we dont wish to be tampered with.
	public byte[] generateSignature(PrivateKey privateKey,PublicKey sender,PublicKey reciepient,float value) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value);
		byte[] signature = applyECDSASig(privateKey, data);
		return signature;
	}
	
	//Signs all the data we dont wish to be tampered with.
	public byte[] generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(this.Transaction.sender) + StringUtil.getStringFromKey(this.Transaction.reciepient)
				+ Float.toString(this.Transaction.value);
		byte[] signature = applyECDSASig(privateKey, data);
		return signature;
	}
	
	//Verifies the data we signed hasn't been tampered with
	public boolean verifiySignature(byte[] signature, PublicKey sender,PublicKey reciepient,float value) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
		return verifyECDSASig(sender, data, signature);
	}
	
	//Verifies the data we signed hasn't been tampered with
	public boolean verifiySignature() {
		String data = StringUtil.getStringFromKey(this.Transaction.sender) + StringUtil.getStringFromKey(this.Transaction.reciepient) + Float.toString(this.Transaction.value)	;
		return verifyECDSASig(this.Transaction.sender, data, this.Transaction.signature);
	}
	
}
