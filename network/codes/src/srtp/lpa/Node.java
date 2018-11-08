package srtp.lpa;

import java.util.*;
import java.util.Map.*;
import javax.swing.JTextArea;

public class Node {
    public int id;
    public double lr;
    public double next_lr;
    public double min_lr;
    public double edges_lr_sum;
    public double edges_lvalue_sum;
    public List<Node> edges=new ArrayList<Node>();
    public Map<Integer,Label> labels=new HashMap<Integer,Label>();
    public Map<Integer,Label> next_labels=new HashMap<Integer,Label>();
    
    public Node(int id) {
        this.id=id;
        this.lr=1.0;
        this.next_lr=0.0;
        this.min_lr=0.0;
        this.edges_lr_sum=0.0;
        this.edges_lvalue_sum=0.0;
        Label label=new Label(this.id,1.0);
        labels.put(this.id, label);
    }
    
    /*��Array�в���һ���ڵ�*/
    public static Node searchOrder(List<Node> nodes,int id) {
        for(int i=0;i<nodes.size();i++) {
            if(nodes.get(i).id==id) 
                return nodes.get(i);
        }
        return null;
    }
    
    /*�ڵ��Ƿ����*/
     public static boolean isExist(List<Node> nodes,int id) {
         boolean isexist=false;
         for(int i=0;i<nodes.size();i++) {
            if(nodes.get(i).id==id) {
                isexist=true;
                break;
            }        
         }
         return isexist;
     }
    
    /*������нڵ�*/
    public static int showAllNodes(JTextArea taInfo, List<Node> nodes) {
        int node_num=0;
        for(int i=0;i<nodes.size();i++) {
            Node node=nodes.get(i);
            node_num++;
            taInfo.append("  " + node.id+"\tLR:"+node.lr + "\r\n");
        }
        return node_num;
    }
    
    /*������б�*/
    public static int showAllEdges(JTextArea taInfo, List<Node> nodes) {
        int edge_num=0;
        for(int i=0;i<nodes.size();i++) {
            Node source=nodes.get(i);
            for(int j=0;j<source.edges.size();j++) {
                Node target=nodes.get(j);
                edge_num++;
                taInfo.append("  " + "source:"+source.id+"\ttarget:"+target.id + "\r\n");
            }
        }
        return edge_num;
    }
    
    /*������нڵ�ͱ�ǩ*/
    public static void showAllNodesAndLabels(JTextArea taInfo, List<Node> nodes) {
        for(int i=0;i<nodes.size();i++) {
            Node node=nodes.get(i);
            taInfo.append("  " + node.id+"\t");
            for(Iterator entries=node.labels.entrySet().iterator();entries.hasNext();) {
                Entry entry=(Entry)entries.next();
                Label label=(Label)entry.getValue();
                taInfo.append("  " + label.lid+"=>"+label.lvalue+" ");
            }
            taInfo.append("\r\n");
        }
    }
}

/*��ǩ��*/
class Label{
    int lid;
    double lvalue;
    
    Label(int lid,double d){
        this.lid=lid;
        this.lvalue=d;
    }
}
