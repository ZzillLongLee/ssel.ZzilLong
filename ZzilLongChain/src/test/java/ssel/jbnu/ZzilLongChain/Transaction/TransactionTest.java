package ssel.jbnu.ZzilLongChain.Transaction;

import java.security.Security;
import java.util.ArrayList;

import ssel.jbnu.ZzilLongChain.Block.Block;
import ssel.jbnu.ZzilLongChain.DigitalSignature.DigitalSignature;
import ssel.jbnu.ZzilLongChain.Transaction.Transaction;
import ssel.jbnu.ZzilLongChain.Util.StringUtil;
import ssel.jbnu.ZzilLongChain.Wallet.Wallet;

public class TransactionTest {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 5;
	public static Wallet walletA;
	public static Wallet walletB;

	public static void main(String[] args) {
		// Setup Bouncey castle as a Security Provider
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		// Create the new wallets
		walletA = new Wallet();
		walletB = new Wallet();
		// Test public and private keys
		System.out.println("Private and public keys:");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		// Create a test transaction from WalletA to walletB
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		DigitalSignature digitalSignature = new DigitalSignature(transaction);
		byte[] signature = digitalSignature.generateSignature(walletA.privateKey);
		// Verify the signature works and verify it from the public key
		System.out.println("Is signature verified");
		System.out.println(digitalSignature.verifiySignature(signature, transaction.sender,transaction. reciepient, transaction.value));

	}
	
}
