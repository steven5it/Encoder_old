import java.io.*;
import java.util.*;

import javax.print.attribute.standard.PrinterResolution;
public class Encoder {
	// input is an array of frequencies, indexed by character code
    public static HuffmanTree buildTree(int[] charFreqs, Map <String, Integer> stringFreqs, int j) {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<HuffmanTree>();
        // initially, we have a forest of leaves
        // one for each non-empty character
        if (j == 1)
        {
            for (int i = 0; i < charFreqs.length; i++)
            {
                if (charFreqs[i] > 0)
                {
                	String s = Character.toString((char)('A' + i));
                    trees.offer(new HuffmanLeaf(charFreqs[i], s));
                    stringFreqs.put(s, i);
                }
            }
        }
        
        // charfreqs2 index value is now [idx/26][idx%26] for j = 2
        else
        {
        	for (int i = 0; i < charFreqs.length; i++)
        	{
        		if (charFreqs[i] > 0)
        		{
        			String s = Character.toString((char)('A'+ i/26)) + 
        					Character.toString((char)('A' + i%26));
        			trees.offer(new HuffmanLeaf(charFreqs[i], s));
        			stringFreqs.put(s, i);
        			
        		}
        	}
        }

        assert trees.size() > 0;
        // loop until there is only one tree left
        while (trees.size() > 1) {
            // two trees with least frequency
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();
 
            // put into new node and re-insert into queue
            trees.offer(new HuffmanNode(a, b));
        }
        return trees.poll();
    }
 
    // prints out the huffman encodings based on frequencies and saves encoding in an array
    public static void printCodes(HuffmanTree tree, StringBuffer prefix, Map <String, String> encodings) {
        assert tree != null;
        if (tree instanceof HuffmanLeaf) {
            HuffmanLeaf leaf = (HuffmanLeaf)tree;
            
            // print out character, frequency, and code for this leaf (which is just the prefix)
            System.out.println(leaf.value + "\t" + leaf.frequency + "\t" + prefix);
            
            // populate encodings mapping from the string to its bit encoding
            encodings.put(leaf.value, prefix.toString());     

 
        } else if (tree instanceof HuffmanNode) {
            HuffmanNode node = (HuffmanNode)tree;
 
            // traverse left
            prefix.append('0');
            printCodes(node.left, prefix, encodings);
            prefix.deleteCharAt(prefix.length()-1);
 
            // traverse right
            prefix.append('1');
            printCodes(node.right, prefix, encodings);
            prefix.deleteCharAt(prefix.length()-1);
        }
    }
    
    // generate a volume of texts based on the character frequencies gathered from input file
	private static void generateVolume(int[] charFreqs, int sum, BufferedWriter bw, String s, int numberSymbols) throws IOException {
		System.out.println("\nGenerating random volume of text of k chars based on probabilities from part 1...\n");
		System.out.println("Sum is: " + sum);
		Random rand = new Random();
		int k = Integer.parseInt(s);

		for (int i = 0; i < k; i++)
		{
			int randomInt = rand.nextInt(sum);
			int index = 0;	// representing the current letter spot in charFreq array
			int sumcount = 0;
			// add up to the random integer amount, increasing the letter it represents based on frequencies
			while (sumcount <= randomInt)
			{
					sumcount += charFreqs[index++];
			}
			index--;
			if (numberSymbols == 1)					// single symbol
				bw.write((char)('A'+ index));
			else 									// multiple symbols
			{
				while (numberSymbols > 0)
				{
					numberSymbols--;
				}
			}
			
		}
		bw.close();
	}
    
    // read in testText character by character to encode each character to new file
    public static void encodeVolume (HuffmanTree tree, BufferedWriter bwEncoded, 
    		Map <String, String> encodings, int j) throws IOException 
    	{
        assert tree != null;
        BufferedReader brEncoded = new BufferedReader(new InputStreamReader(new FileInputStream("testText")));
        int c;
        int count = 1;
        String s = "";
        while((c = brEncoded.read()) != -1) 
        {
        	char character = (char) c;
        	s += character;
        	
        	// need to check another character if count has not reached the j value
        	if (count++ < j)
        	{
        		continue;
        	}
        	else
        	{
        		bwEncoded.write(encodings.get(s));
        		count = 1;
        		s = "";
        	}
        }
        bwEncoded.close();
        brEncoded.close();
    }
    
    
    // read in testText.enc1 and decode it with the huffman algorithm
    public static void decodeVolume (HuffmanTree tree, BufferedWriter bwDecoded, 
    					Map <String, String> encodings, int iteration, int binaryCount[]) throws IOException 
    {
        assert tree != null;
        BufferedReader brDecoded = new BufferedReader(new InputStreamReader(new FileInputStream("testText.enc" + iteration)));
        int c;
        StringBuffer buff = new StringBuffer();
        while((c = brDecoded.read()) != -1) 
        {
          char character = (char) c;
          buff.append(character);
    	  for (Map.Entry<String, String> entry: encodings.entrySet())
    	  {
    		  if (entry.getValue().equals(buff.toString()))
    		  {
    			  bwDecoded.write(entry.getKey());
    			  buff = new StringBuffer();
    			  break;
    		  }
    	  }
          binaryCount[0]++;
        }
        bwDecoded.close();
        brDecoded.close();
    }
 
    public static void main(String[] args) throws IOException {
    	// check for proper command arguments
    	if (args.length != 2)
    	{
    		System.err.println("Improper number of arguments. Usage: frequenciesFile k");
    	}
    	if (!isInteger(args[1]))
    	{
    		System.err.println("Second argument must be integer value.");
    	}
    	
    	
        // in our n-character alphabet, we assume that n < 27
        int[] charFreqs = new int[26];
        int[] charFreqs2 = new int[26*26];
        String [] chars1 = new String[26];		// single characters
        String [] chars2 = new String[26*26];	// double letters
        //mapping of string to its frequency
        Map <String, Integer> stringFreqs1 = new HashMap <String, Integer>();
        Map <String, Integer> stringFreqs2 = new HashMap <String, Integer>();
        //mapping of string to its encoding
        Map <String, String> encodings1 = new HashMap <String, String>();
        Map <String, String> encodings2 = new HashMap <String, String>();
        
        // populate the arrays with the string combinations we will be evaluating
        for (int i = 0; i < chars1.length; i++)
        {
        	chars1[i] = Character.toString((char)('A' + i));
        }
        for (int i = 0; i < chars2.length; i++)
        {
        	chars2[i] = "";
        }
        for (int i = 0; i < chars2.length; i++)
        {
        	String s2 = Character.toString((char)('A'+ i/26)) + 
					Character.toString((char)('A' + i%26));
        	chars2[i] = s2;
        }


        BufferedReader br = null;
        BufferedWriter bw1 = new BufferedWriter(new FileWriter("testText")); // volume of text of char frequencies
        BufferedWriter bwEncoded1 = new BufferedWriter(new FileWriter("testText.enc1"));
        BufferedWriter bwDecoded1 = new BufferedWriter(new FileWriter("testText.dec1"));
        BufferedWriter bwEncoded2 = new BufferedWriter(new FileWriter("testText.enc2"));
        BufferedWriter bwDecoded2 = new BufferedWriter(new FileWriter("testText.dec2"));
        
        // read in the file of frequencies and populate the frequency array
        try
        {
        	br = new BufferedReader(new FileReader(args[0]));
        	System.out.println("Reading from file: " + args[0]);
        	String line;
        	int i = 0;
        	int sum = 0;
        	while ((line = br.readLine()) != null)
        	{
        		if (line.equals(""))
        		{
        			charFreqs[i++] = 0;
        			continue;
        		}
        		charFreqs[i++] = Integer.parseInt(line);
        		sum += Integer.parseInt(line);
        	}
        	
        	//compute entropy of language based on char frequencies
        	double entropySum = 0;
        	for (int e = 0; e < charFreqs.length; e++)
        	{
        		// log x(base n)=log x(base e)/log n(base e) 
        		if (charFreqs[e] == 0) continue; //skip if value is 0 because log 0 is NaN
        		entropySum -= ((double)charFreqs[e]/(double)sum) * 
        				(log2((double)charFreqs[e]/(double)sum));
        	}
        	
            // build tree
        	System.out.println("Now building tree for 1-character encoding");
            HuffmanTree tree = buildTree(charFreqs,stringFreqs1, 1);
     
            // print out results
            System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
            printCodes(tree, new StringBuffer(), encodings1);
            
            int count1[] = {0};

            generateVolume(charFreqs, sum, bw1, args[1], 1);
            encodeVolume(tree, bwEncoded1, encodings1, 1);
            decodeVolume(tree, bwDecoded1, encodings1, 1, count1);
            
            
            //2 symbol encoding
        	int sum2 = 0;
        	// compute frequencies for 2 symbol case starting with AA, AB, AC... ZY, ZZ
        	for (int j = 0; j < charFreqs.length; j++)
        	{
        		for (int k = 0; k < charFreqs.length; k++)
        		{
        			charFreqs2[(j*26)+k] = charFreqs[j] * charFreqs[k];
        			sum2 += charFreqs2[(j*26)+k];
        		}
        	}
        	// charfreqs2 index value is now [idx/26][idx%26]
        	
        	// build tree for 2 character encoding
        	System.out.println("Now building tree for 2-character encoding");
            HuffmanTree tree2 = buildTree(charFreqs2, stringFreqs2, 2);
            
            int []count2 = {0};
            System.out.println("SYMBOL\tWEIGHT\tHUFFMAN CODE");
            printCodes(tree2, new StringBuffer(), encodings2);
            encodeVolume(tree2, bwEncoded2, encodings2, 2);
            decodeVolume(tree2, bwDecoded2, encodings2, 2, count2);
        	System.out.println("Entropy of the language is: " + entropySum);
        	double average1 = count1[0]/Double.parseDouble(args[1]);
        	double average2 = count2[0]/Double.parseDouble(args[1]);
            System.out.println("1 symbol symbol count is: " + count1[0] + ", average bits per symbol: " + average1);
            System.out.println("Percentage increase from entropy = %" + (100*(average1 - entropySum)/(entropySum)));
            System.out.println("2 symbol symbol count is: " + count2[0] + ", average bits per symbol: " + average2);
            System.out.println("Percentage increase from entropy = %" + (100*(average2 - entropySum)/(entropySum)));
            
            
        }
		catch (IOException e) {
			System.err.print("The file was not found.\n");
		}

 
    }

	public static boolean isInteger(String str) {
	    try {
	        Integer.parseInt(str);
	        return true;
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	}
	// calculate the log base 2 of x
	// log x(base n)=log x(base e)/log n(base e) 
    static double log2(double x) {  
        return Math.log(x)/Math.log(2.0d);  
   }  
}
