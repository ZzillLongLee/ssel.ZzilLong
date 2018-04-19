package ssel.jbnu.ZzilLongChain.Wallet;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

import ssel.jbnu.ZzilLongChain.Wallet.Wallet;

public class WalletTest {
	
	public static void main(String[] args) {
		Wallet wallet = new Wallet();
		System.out.println("wallet has private key" +wallet.privateKey+"and public key "+wallet.publicKey);
	}
	
}