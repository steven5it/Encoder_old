abstract class HuffmanTree implements Comparable<HuffmanTree> {
    public final int frequency; // the frequency of this tree
    public HuffmanTree ()
    {
    	frequency = 0;
    }
    public HuffmanTree(int freq) { 
    	frequency = freq; 
    }
 
    // compares on the frequency
    public int compareTo(HuffmanTree tree) {
        return frequency - tree.frequency;
    }
}