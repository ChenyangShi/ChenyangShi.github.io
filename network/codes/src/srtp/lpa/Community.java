package srtp.lpa;

import java.util.*;
import java.util.Map.*;
import javax.swing.JTextArea;

public class Community {
	public int id;
	Map<Integer,Node> cnodes=new HashMap<Integer,Node>();
	
	public Community(int id) {
		this.id=id;
	}
	
	public static void showCommunity(Map<Integer,Community> coms, JTextArea taInfo) {
		for(Iterator com_entries=coms.entrySet().iterator();com_entries.hasNext();) {
			Entry com_entry=(Entry)com_entries.next();
			Community com=(Community)com_entry.getValue();
			printInfo(taInfo, "Community"+com.id+":", false);
			for(Iterator cnode_entries=com.cnodes.entrySet().iterator();cnode_entries.hasNext();) {
				Entry cnode_entry=(Entry)cnode_entries.next();
				int cnode_id=(int)cnode_entry.getKey();
				printInfo(taInfo, cnode_id+" ", false);
			}
			printInfo(taInfo, "", true);
		}
	}
        
        private static void printInfo(JTextArea taInfo, String s, boolean wrapLine) {
            if (wrapLine)
                taInfo.append("  " + s + "\r\n");
            else
                taInfo.append("  " + s);
        }
}
