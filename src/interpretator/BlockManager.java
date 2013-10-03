
package interpretator;

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.swing.JOptionPane;

/**
 *
 * @author Robin
 */
public class BlockManager {
	private static int nrOfBlocks;
	private static Block[] blocks;
	private static Block baseBlock;

	public static void loadBlocksFromFile(String path)
	{
		Scanner in;
		try
		{
			in = new Scanner(new FileReader(path));
		}
		catch(FileNotFoundException ex)
		{
			JOptionPane.showMessageDialog(null, "Could not find " + 
                                path);
			return;
		}

		in.next(); //###PROGRAM###
		nrOfBlocks = in.nextInt();
		blocks = new Block[nrOfBlocks];
		int blockID, staticFather, size, nrOfDeclarations, nrOfTokens;
		for(int i=0; i<nrOfBlocks; i++)
		{
			in.next(); //##BLOCK##
			blockID = in.nextInt();
			staticFather = in.nextInt();
			size = in.nextInt();
			nrOfDeclarations = in.nextInt();
			nrOfTokens = in.nextInt();

			in.next(); //#DEKLARATIONER#
			Symbol[] symbols = new Symbol[nrOfDeclarations];
			for(int j=0; j<nrOfDeclarations; j++)
			{
				int symbolID = in.nextInt();
				int type = in.nextInt();
				int kind = in.nextInt();
				int info1 = in.nextInt();
				int info2 = in.nextInt();
				int info3 = in.nextInt();
				int relativeAddress = in.nextInt();
				String extnamn = in.next();
				String limits = in.next();
				String name = in.next();
				symbols[j] = new Symbol(symbolID, type, kind, 
                                        info1, info2, info3, relativeAddress,
										extnamn, limits, name);
			}

			in.next(); //#KOD#
			Token[] tokens = new Token[nrOfTokens];
			for(int j=0; j<nrOfTokens; j++)
			{
				int code = in.nextInt();
				int type = in.nextInt();
				String text = in.next();
				tokens[j] = new Token(type, code, text);
			}
			
			in.next(); //##BLOCKSLUT##

			//create the block
			blocks[i] = new Block(blockID, staticFather, size, 
                                nrOfDeclarations, nrOfTokens, symbols, tokens);
			if(staticFather == -1)
			{
				baseBlock = blocks[i];
			}
		}
	}

	public static Block getBaseBlock()
	{
		return baseBlock;
	}

	public static Block getBlock(int blockNr)
	{
		return blocks[blockNr];
	}
}
