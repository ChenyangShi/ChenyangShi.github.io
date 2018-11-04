package srtp.cpm;

import java.util.*;
public class Community {
    
    public ArrayList<Integer> shared_node = new ArrayList<Integer>();
    /*
     * @override toString����
     * @see java.lang.Object#toString()
     */
    public String toString(){
        String s = "";
        for(int node: shared_node){
            s = s + (node) + " ";
        }
        s += "\n";
        return s;
    }
}
