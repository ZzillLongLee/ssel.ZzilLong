package ssel.jbnu.ZzilLongChain.BlockChain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ssel.jbnu.ZzilLongChain.Block.Block;
import ssel.jbnu.ZzilLongChain.DigitalSignature.DigitalSignature;
import ssel.jbnu.ZzilLongChain.Transaction.Transaction;
import ssel.jbnu.ZzilLongChain.Transaction.TransactionInput;
import ssel.jbnu.ZzilLongChain.Transaction.TransactionOutput;
import ssel.jbnu.ZzilLongChain.Wallet.Wallet;

public class ZzilLongChain {

	private List<Block> blockChain = new ArrayList<Block>();
	public static int difficulty = 3;
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); // list of all unspent transactions.
	public static float minimumTransaction = 0.1f;
	public static Transaction genesisTransaction;
	public static Wallet walletA;
	public static Wallet walletB;
	private static DigitalSignature digitalSignature;

	public static void main(String[] args) {
		//add our blocks to the blockchain ArrayList:
				Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
				
				//Create wallets:
				walletA = new Wallet();
				walletB = new Wallet();		
				Wallet coinbase = new Wallet();
				
				//create genesis transaction, which sends 100 NoobCoin to walletA: 
				genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
				digitalSignature = new DigitalSignature(genesisTransaction);
				digitalSignature.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
				genesisTransaction.transactionId = "0"; //manually set the transaction id
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
				UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
				
				System.out.println("Creating and Mining Genesis block... ");
				ZzilLongChain ziChain = new ZzilLongChain();
				Block genesis = ziChain.getLatestBlock();
				genesis.addTransaction(genesisTransaction);
//				addBlock(genesis);
				
				//testing
				Block block1 = new Block(ziChain.getLatestBlock().hash);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
				block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
				ziChain.addBlock(block1);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				Block block2 = new Block(ziChain.getLatestBlock().hash);
				System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
				block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
				ziChain.addBlock(block2);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				Block block3 = new Block(ziChain.getLatestBlock().hash);
				System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
				block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				ziChain.isChainValid();
				
				System.out.println("test is going");
	}
	
	
	public ZzilLongChain() {
		this.blockChain.add(createGenesisBlock());
	}

	public Block createGenesisBlock() {
		return new Block("0");
	}

	public Block getLatestBlock() {
		return blockChain.get(blockChain.size() - 1);
	}

	public void addBlock(Block block) {
		block.mineBlock(difficulty);
		this.blockChain.add(block);
	}

	public Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); // a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		// loop through blockchain to check hashes:
		for (int i = 1; i < this.blockChain.size(); i++)
		{
			currentBlock = this.blockChain.get(i);
			previousBlock = this.blockChain.get(i - 1);
			// compare registered hash and calculated hash:
			if (!currentBlock.hash.equals(currentBlock.calculateHash()))
			{
				System.out.println("Current Hashes not equal");
				return false;
			}
			// compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash))
			{
				System.out.println("Previous Hashes not equal");
				return false;
			}
			// check if hash is solved
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget))
			{
				System.out.println("This block hasn't been mined");
				return false;
			}

			// loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.transactions.size(); t++)
			{
				Transaction currentTransaction = currentBlock.transactions.get(t);

				if (!currentTransaction.DigitalSignature.verifiySignature())
				{
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false;
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue())
				{
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false;
				}

				for (TransactionInput input : currentTransaction.inputs)
				{
					tempOutput = tempUTXOs.get(input.transactionOutputId);

					if (tempOutput == null)
					{
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}

					if (input.UTXO.value != tempOutput.value)
					{
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.transactionOutputId);
				}

				for (TransactionOutput output : currentTransaction.outputs)
				{
					tempUTXOs.put(output.id, output);
				}

				if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient)
				{
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender)
				{
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
			}
		}
		return true;
	}
}
