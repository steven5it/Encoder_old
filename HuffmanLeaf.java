public class HuffmanLeaf extends HuffmanTree {
    public final String value; // the character this leaf represents
 
    public HuffmanLeaf(int freq, String val) {
        super(freq);
        value = val;
    }
}