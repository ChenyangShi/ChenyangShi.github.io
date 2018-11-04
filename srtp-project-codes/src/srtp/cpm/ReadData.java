package srtp.cpm;

import java.util.*;
import java.io.*;
import javax.swing.JTextArea;

public class ReadData {

    int nodenum;//�ڵ���
    int edgenum;//����
    private JTextArea taInfo;

    public HashMap<Integer, Integer> node_map = new HashMap<Integer, Integer>();
    public HashMap<Integer, ArrayList<Integer>> neighbormap = new HashMap<Integer, ArrayList<Integer>>();

    public ReadData (String node_file, String edge_file, JTextArea taInfo){
        this.taInfo = taInfo;
        try{
            BufferedReader node_reader = new BufferedReader(new FileReader(node_file));
            BufferedReader edge_reader = new BufferedReader(new FileReader(edge_file));

            int id = 0;
            String aline=node_reader.readLine();
            while((aline=node_reader.readLine())!=null){
                String arrays[] = aline.split(",");
                Node new_node=new Node(Integer.parseInt(arrays[0]));
                node_map.put(id, new_node.getName());
                neighbormap.put(new_node.getName(),new ArrayList<Integer>());//����Node�ռ�
                id++;
            }
            nodenum = node_map.size();
            printInfo("nodenum:" + nodenum);
            node_reader.close();//�ر��ļ�

            int line = 0;//ͳ���ļ�����

            String bline = edge_reader.readLine();
            
            while((bline = edge_reader.readLine()) != null) //�����ļ�
            {
                line++;
                printInfo("reading line: " + line);//��ʾ���ڶ�������

                String arrays[] = bline.split(",");
                int source_node = Integer.parseInt(arrays[0]);//create��ȡ�����ڵ�
                int target_node = Integer.parseInt(arrays[1]);

                //��ӡ���ڶ��������ڵ�
                printInfo(arrays[0]);
                printInfo(arrays[1]);

                if(!neighbormap.get(source_node).contains(target_node))
                {
                    edgenum++;//ͳ�Ʊ�����1
                    neighbormap.get(source_node).add(target_node);
                    neighbormap.get(target_node).add(source_node);
                }
            }//��ȡ�ļ�����
            edge_reader.close();//�ر��ļ�

            //��ӡ���нڵ����ͱ���
            printInfo("nodenum: "+nodenum);
            printInfo("edgenum: "+edgenum);

        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void printInfo(String s) {
        this.taInfo.append("  " + s + "\r\n");
    }
}
