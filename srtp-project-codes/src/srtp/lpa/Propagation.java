package srtp.lpa;

import java.util.*;
import java.util.Map.*;
import javax.swing.JTextArea;

public class Propagation {
    public double overlap;
    public List<Node> nodes;
    public Map<Integer,Community> coms=new HashMap<Integer,Community>();
        private JTextArea taInfo;
    
    public Propagation(List<Node> nodes, JTextArea taInfo) {
        this.nodes=nodes;
                this.taInfo = taInfo;
    }
    
    
    public void start(double overlap) {
        this.overlap=overlap;
        LbPropagation();
        groupNode();
    }
    
    
    public Map<Integer,String> getResualt(){
        Map<Integer,String> res=new HashMap<Integer,String>();
        for(int i=0;i<nodes.size();i++) {
            Node c=nodes.get(i);
            String la_str="";
            for(Iterator label_entries=c.labels.entrySet().iterator();label_entries.hasNext();) {
                Entry label_entry=(Entry)label_entries.next();
                Label l=(Label)label_entry.getValue();
                if(!label_entries.hasNext()) {
                    la_str+=l.lid;
                }else if(label_entries.hasNext()){
                    la_str+=l.lid+"-";
                }
            }
            res.put(c.id, la_str);
        }
        return res;
    }
    
    public boolean LbIterator() {
        for(int i=0;i<nodes.size();i++) {
            Node source=nodes.get(i);    
            for(Iterator entries=source.labels.entrySet().iterator();entries.hasNext();) {
                Entry entry=(Entry)entries.next();
                int lid=(int)entry.getKey();
                Label label=(Label)entry.getValue();
                source.next_labels.put(label.lid, new Label(label.lid,label.lvalue));
            }
        }
        
        boolean isCon=true;
        for(int i=0;i<nodes.size();i++) {
            Node source=nodes.get(i);    
            for(int j=0;j<source.edges.size();j++) {
                Node target=source.edges.get(j);
                for(Iterator entries=target.labels.entrySet().iterator();entries.hasNext();) {
                    Entry entry=(Entry)entries.next();
                    int lid=(int)entry.getKey();
                    Label tlabel=(Label)entry.getValue();
                    double ppavalue=tlabel.lvalue*(target.lr/source.edges_lr_sum);
        
                    if(!source.next_labels.containsKey(lid)) {
                        Label slabel=new Label(lid,ppavalue);
                        source.next_labels.put(lid, slabel);
                    }else {
                        source.next_labels.get(lid).lvalue+=ppavalue;
                    }
                }
            }
        }
        
        for(int i=0;i<nodes.size();i++) {
            Node source=nodes.get(i);    
            for(Iterator entries=source.next_labels.entrySet().iterator();entries.hasNext();) {
                Entry entry=(Entry)entries.next();
                int lid=(int)entry.getKey();
                if(isCon==true&&!source.labels.containsKey(lid)) {
                    isCon=false;
                }
            }
            source.labels=source.next_labels;
            source.next_labels=new HashMap<Integer,Label>();
        }
        return isCon;
    }
    
    
    public void LbPropagation() {
        printInfo("OV Param: " + this.overlap, true);
        for(int i=0;i<nodes.size();i++) {
            Node source=nodes.get(i);
            double min_lr=Double.POSITIVE_INFINITY;
            for(int j=0;j<source.edges.size();j++) {
                source.edges_lr_sum+=source.edges.get(j).lr;
                if(min_lr>=source.edges.get(j).lr)
                    min_lr=source.edges.get(j).lr;
            }
            source.min_lr=min_lr;
        }
        //��ǩ����
        int con_num=0;
        //�����ϱ�ǩ
        while(!LbIterator()) {
            printInfo("LabelPropagation第"+(++con_num)+"次迭代!", true);
        }
        printInfo("LabelPropagation第"+(++con_num)+"次迭代!", true);
        Node.showAllNodesAndLabels(taInfo, nodes);
    }
    
    
    public void groupNode() {
        for(int i=0;i<nodes.size();i++) {
            Node node1=nodes.get(i);    
            for(int j=0;j<nodes.size();j++) {
                Node node2=nodes.get(j);
                if(node2.labels.containsKey(node1.id)) {
                    node1.edges_lvalue_sum+=node2.labels.get(node1.id).lvalue;
                }
            }
        }    
        nodes.sort(new Comparator<Node>() {
            public int compare(Node arg0, Node arg1) {
                return (int)((arg1.lr-arg0.lr)*10000);
            }
        });
        for(int i=0;i<nodes.size();i++) {
            Node node=nodes.get(i);
            Label maxlabel=new Label(-1,0.0);
            for(Iterator entries=node.labels.entrySet().iterator();entries.hasNext();) {
                Entry entry=(Entry)entries.next();
                int lid=(int)entry.getKey();
                Label label=(Label)entry.getValue();
                if(label.lvalue>=maxlabel.lvalue) {
                    maxlabel=label;
                }
            }
            for(Iterator entries=node.labels.entrySet().iterator();entries.hasNext();) {
                Entry entry=(Entry)entries.next();
                int lid=(int)entry.getKey();
                Label label=(Label)entry.getValue();
                if(label.lvalue<maxlabel.lvalue*(1-overlap)) {
                    entries.remove();
                }
            }
        }
        Node.showAllNodesAndLabels(taInfo, nodes);
        
        for(int i=0;i<nodes.size();i++) {
            Node node=nodes.get(i);
            for(Iterator la_entries=node.labels.entrySet().iterator();la_entries.hasNext();) {
                Entry la_entry=(Entry)la_entries.next();
                int lid=(int)la_entry.getKey();
                boolean isExist=false;
                for(Iterator com_entries=coms.entrySet().iterator();com_entries.hasNext();) {
                    Entry com_entry=(Entry)com_entries.next();
                    Community c=(Community)com_entry.getValue();
                    if(c.cnodes.containsKey(lid)) {
                        isExist=true;
                        c.cnodes.put(node.id, node);
                    }
                }
                if(!isExist) {
                    Community c=new Community(lid);
                    c.cnodes.put(node.id, node);
                    coms.put(lid, c);
                }
            }
        }
        Community.showCommunity(coms, taInfo);
    }
    
    private void printInfo(String s, boolean wrapLine) {
        if (wrapLine)
            this.taInfo.append(" " + s + "\r\n");
        else
            this.taInfo.append(" " + s);
    }
}
