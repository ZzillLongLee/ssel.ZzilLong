package ssel.jbnu.ZzilLongChain.BlockChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.GsonBuilder;

import ssel.jbnu.ZzilLongChain.Block.Block;
import ssel.jbnu.ZzilLongChain.BlockChain.ZzilLongChain;



public class ZzilLongChainTest  {
	
	public static void main(String[] args) {
		// add our blocks to the blockchain ArrayList:
		ZzilLongChain zc = new ZzilLongChain();
		
		zc.addBlock(new Block("0"));
		System.out.println("Trying to Mine block 1... ");
		zc.getLatestBlock().mineBlock(zc.difficulty);

		System.out.println("\nBlockchain is Valid: " + zc.isChainValid());
		
		zc.addBlock(new Block(zc.getLatestBlock().hash));
		System.out.println("Trying to Mine block 2... ");
		zc.getLatestBlock().mineBlock(zc.difficulty);

		System.out.println("\nBlockchain is Valid: " + zc.isChainValid());
		
		zc.addBlock(new Block( zc.getLatestBlock().hash));
		System.out.println("Trying to Mine block 3... ");
		zc.getLatestBlock().mineBlock(zc.difficulty);
		
		System.out.println("\nBlockchain is Valid: " + zc.isChainValid());

		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(zc);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);
	}
}
