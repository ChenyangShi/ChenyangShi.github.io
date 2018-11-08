package srtp.lpa;

import java.io.*;
import java.util.*;
import javax.swing.JTextArea;

public class LeaderRank {
    public String pathname; 
    public List<Node> nodes=new ArrayList<Node>();
    private JTextArea taInfo;
    
    public LeaderRank(String pathname,boolean undirect, JTextArea taInfo) {
        this.pathname=pathname;
        this.taInfo = taInfo;
        if(undirect)
            initDataUN_csv();
        else
            initDataDE();
        getLRindex();
    }
    
    public void initDataUN_csv() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.pathname)));
            String linetext=null;
            br.readLine();
            while((linetext=br.readLine())!=null) {
                linetext=linetext.trim();
                String[] info=linetext.split(",");
                double source_id_d=Double.valueOf(info[0]);
                double target_id_d=Double.valueOf(info[1]);
                int source_id=(int)source_id_d;
                int target_id=(int)target_id_d;
                if(!Node.isExist(nodes, source_id)) {
                    Node source=new Node(source_id);
                    nodes.add(source);
                }
                if(!Node.isExist(nodes, target_id)) {
                    Node target=new Node(target_id);
                    nodes.add(target);
                }
                Node source=Node.searchOrder(nodes, source_id);
                Node target=Node.searchOrder(nodes, target_id);
                source.edges.add(target);
                target.edges.add(source);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void initDataUN() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.pathname)));
            String linetext=null;
            int state=0;
            while((linetext=br.readLine())!=null) {
                linetext=linetext.trim();
                if(linetext.equals("node")) state=1;
                else if(linetext.equals("edge")) state=3;
                else if(linetext.equals("[")&&state==1) state=2;
                else if(linetext.equals("[")&&state==3) state=4;
                else if(linetext.equals("]")&&(state==2||state==4)) state=0;
                else if(state==2) {
                    String[] nodeinfo=linetext.split(" ");
                    if(nodeinfo[0].equals("id")) {
                        Node n=new Node(Integer.valueOf(nodeinfo[1]));
                        nodes.add(n);
                    }            
                }
                else if(state==4) {
                    String[] source=linetext.split(" ");
                    String[] target=br.readLine().trim().split(" ");
                    Node source_node=Node.searchOrder(nodes, Integer.valueOf(source[1]));
                    Node target_node=Node.searchOrder(nodes, Integer.valueOf(target[1]));
                    source_node.edges.add(target_node);
                    target_node.edges.add(source_node);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void initDataDE() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.pathname)));
            String linetext=null;
            int state=0;
            while((linetext=br.readLine())!=null) {
                linetext=linetext.trim();
                if(linetext.equals("node")) state=1;
                else if(linetext.equals("edge")) state=3;
                else if(linetext.equals("[")&&state==1) state=2;
                else if(linetext.equals("[")&&state==3) state=4;
                else if(linetext.equals("]")&&(state==2||state==4)) state=0;
                else if(state==2) {
                    String[] nodeinfo=linetext.split(" ");
                    if(nodeinfo[0].equals("id")) {
                        Node n=new Node(Integer.valueOf(nodeinfo[1]));
                        nodes.add(n);
                    }            
                }
                else if(state==4) {
                    String[] source=linetext.split(" ");
                    String[] target=br.readLine().trim().split(" ");
                    Node source_node=Node.searchOrder(nodes, Integer.valueOf(source[1]));
                    Node target_node=Node.searchOrder(nodes, Integer.valueOf(target[1]));
                    source_node.edges.add(target_node);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public boolean LRiterator() {
        boolean isCo=true;
        
        for(int i=0;i<nodes.size();i++) {
            Node n=nodes.get(i);
            for(int j=0;j<n.edges.size();j++) {
                n.edges.get(j).next_lr+=n.lr/n.edges.size();
            }
        }
        
        for(int i=0;i<nodes.size();i++) {
            Node n=nodes.get(i);
            if(n.lr!=n.next_lr) {
                isCo=false;
                break;
            }
        }
        return isCo;
    }    
    
    
    public void getLRindex() {
        Node ground=new Node(-1);
        ground.lr=0.0;
        for(int i=0;i<nodes.size();i++) {
            ground.edges.add(nodes.get(i));
        }
        for(int i=0;i<nodes.size();i++) {
            nodes.get(i).edges.add(ground);
        }
        nodes.add(ground);
        
        boolean isCo=false;
        int it_num=0;
        
        while(!isCo) {
            printInfo("LeaderRank第"+(++it_num)+"迭代!");
            isCo=LRiterator();
            for(int i=0;i<nodes.size();i++) {
                Node n=nodes.get(i);
                n.lr=n.next_lr;
                n.next_lr=0.0;
            }
        }
        
        nodes.remove(ground);
        for(int i=0;i<nodes.size();i++) {
            Node n=nodes.get(i);
            n.edges.remove(ground);
            n.lr+=ground.lr/nodes.size();
        }
        Node.showAllNodes(this.taInfo, nodes);
    }
    
    private void printInfo(String s) {
        this.taInfo.append("  " + s + "\r\n");
    }
}
