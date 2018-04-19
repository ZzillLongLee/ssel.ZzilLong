package ssel.jbnu.ZzilLongChain.Block;

import java.util.Date;

import ssel.jbnu.ZzilLongChain.Block.Block;

public class BlockTest {


	 public static void main(String[] args) {
		// add our blocks to the blockchain ArrayList:
		
		Block block1 = new Block("0");
		System.out.println(block1.hash);

		Block block2 = new Block(block1.hash);
		System.out.println(block2.hash);

		Block block3 = new Block(block2.hash);
		System.out.println(block3.hash);
		
	}

}
