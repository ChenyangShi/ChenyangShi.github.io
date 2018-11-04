package srtp.cpm;

import java.util.*;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/*
 * @CliquePercolationMethod
 *
 */

public class CliquePercolationMethod{
    int k_clique;//4-clique
    private JTextArea taInfo;

    int nodenum;//�ڵ���Ŀ
    int counter = 0;//������ ͳ���ҵ���4-clique��Ŀ,��ӡ��ʾ

    //    LinkedList<Integer> neighbor[];//���ͷ�ڵ�+��ڵ�
    public HashMap<Integer, Integer> node_map = new HashMap<Integer, Integer>();
    public HashMap<Integer, ArrayList<Integer>> neighbormap = new HashMap<Integer, ArrayList<Integer>>();
    ArrayList<Community> communities=new ArrayList<Community>();//������������

    boolean visit[];
    /*
     * @���췽��
     * ʵ�ֱ�����ʼ���Լ����ļ��д�ֵ
     */
    public CliquePercolationMethod(String node_file, String edge_file, JTextArea taInfo)
    {
        this.taInfo = taInfo;
        ReadData r = new ReadData(node_file, edge_file, this.taInfo);
        nodenum = r.nodenum;

        //��ʼ��visit[]
        visit = new boolean[nodenum];
        for(int i = 0; i< nodenum; i++) //i����node_map()��Id,��Id������node�Ƿ���ʹ�
        {
                visit[i] = false;
        }
        neighbormap = r.neighbormap;
        node_map = r.node_map;
    }

    /*
     *@Beginning
     *�˷���ʵ����ϵ�����㷨
     */
    public void start(int k_clique){
        this.k_clique=k_clique;
        int visitednum = 0;
        while(visitednum < nodenum)
        {

            for (Map.Entry etr : node_map.entrySet())
            {
                int i = (int) etr.getKey();
                if (visit[i] == false)//��ͷ��ʼ��δ�����ʹ���ͷ�ڵ�i
                {
                    //ÿ��for-eschѭ��������һ��common_node����
                    LinkedList<Integer> common_node = new LinkedList<Integer>();

                    visit[i] = true;//����ѷ���
                    visitednum++;

                    common_node.add((int) etr.getValue());//����ǰ���ʵ�ͷ�ڵ���ӽ�common_node������

                    boolean found = false;

                    /*
                     * ��������ѭ�������ҵ�һ��4-clique
                     */
                    for (int j = 0; j < neighbormap.get(etr.getValue()).size(); j++)//����ͷ�ڵ�i�����б�ڵ�
                    {
                        int nb_j = neighbormap.get(etr.getValue()).get(j);//get()��������ȡi��Key����Ӧ�����飨Value���еĵ�j+1������

                        for (int m = j + 1; m < neighbormap.get(etr.getValue()).size(); m++) {
                            int nb_m = neighbormap.get(etr.getValue()).get(m);//��ȡi��Key����Ӧ�����飨Value���еĵ�j+2������

                            for (int n = m + 1; n < neighbormap.get(etr.getValue()).size(); n++) {
                                int nb_n = neighbormap.get(etr.getValue()).get(n);//��ȡi��Key����Ӧ�����飨Value���еĵ�j+3������

                            /*
                             * if�����ж��Ƿ񹹳�һ��4�ڵ��clique
                             * ��j+2�������д��ڵ�j+1�������ҵ�j+2�������д��ڵ�j+3������
                             */
                                if (neighbormap.get(nb_m).contains(nb_j) && neighbormap.get(nb_m).contains(nb_n) && neighbormap.get(nb_j).contains(nb_n))
                                //    if(neighbormap.get(nb_m).contains(nb_j))
                                {
                                    found = true;//�ҵ���һ��4-clique
                                    counter++;
                                    printInfo("~~~~" + counter);

                                    //�ڵ�i, nb_j, nb_m, nb_n Ϊһ��clique
                                    //��3���ڵ���ӵ� common_node������
                                    common_node.add(nb_j);
                                    common_node.add(nb_m);
                                    common_node.add(nb_n);

                                    for (Map.Entry entry : node_map.entrySet()) {
                                        if ((int) entry.getValue() == nb_j || (int) entry.getValue() == nb_m || (int) entry.getValue() == nb_n) {
                                            if (visit[(int) entry.getKey()] == false) {
                                                visitednum++;
                                                visit[(int) entry.getKey()] = true;
                                            }
                                        }
                                    }
                                    break;
                                }//end for
                            }
                            break;
                        }//end for

                        if (found == true)
                            break;
                    }//�������forѭ��


            /*
             * ���ҵ�һ��4-cliqueʱ�����ҹ�����3���ڵ������4-clique,
             * ����3���ڵ�����common_node������,����������ĵ�4���ڵ�����3���ڵ㶼������
             * ˵�����ҵ�һ��4-clique��������֮ǰ��4-clique������3���ڵ�
             * ��ô��5���ڵ�Ӧ����ͬһ��community
             */
                    if (found == true) {
                        //��ͷ��ʼ�������нڵ�,�ҵ�common_node������û�еĽڵ�j
                        for (Map.Entry et : node_map.entrySet()) {
                            if (!common_node.contains(et.getValue())) {
                                int sharednode = 0;//ͳ�ƹ���ڵ���

                                //����common_node�����������е�3���ڵ��Ƿ�����j
                                for (int k = 0; k < common_node.size(); k++)//common_node.size()һ����4����Ϊk_cliqueȡֵΪ4
                                {
                                    int node = common_node.get(k);//���η���common_node�����нڵ���±�
                                    if (neighbormap.get(node).contains(et.getValue()))
                                        sharednode++;

                                    if (sharednode >= k_clique - 1)//����3���ڵ�ʱ���˳�ѭ��
                                        break;
                                }

                                //���µĽڵ�j��ӵ�common_node������
                                if (sharednode >= k_clique - 1) {
                                    common_node.add((int) et.getValue());
                                    if (visit[(int) et.getKey()] == false) {
                                        visitednum++;
                                        visit[(int) et.getKey()] = true;
                                    }
                                } //end if
                            }//end if
                        }//end for
                    }//end if

                //һ��ѭ��֮�� �γɵ�һ��common_node���������нڵ�Ӧ������ͬһ��community
                Community community = new Community();
                community.shared_node.addAll(common_node);//��common_node�����е����ж�����ӵ�shared_node��
                if (common_node.size() != 0)//common_node = community
                    communities.add(community);//��һ��������ӵ���������
                }//end if
            }//end for-each
        }//end while
    }//end
    /*
     * @Show
     * ��ʾ�����л��ֺõ�����
     */
    public void Show(){
        for(int i = 0; i < communities.size(); i++){
            printInfo("community "+i +": ");
            printInfo("" + communities.get(i));
        }
    }
    
    public Map<Integer,String> getResult() {
        Map<Integer,String> res=new HashMap<Integer,String>();
        for(int i = 0; i < communities.size(); i++){
            Community c=communities.get(i);
            for(int j=0;j<c.shared_node.size();j++) {
                if(!res.containsKey(c.shared_node.get(j))) {
                    res.put(c.shared_node.get(j), ""+i);
                }else {
                    String label=res.get(c.shared_node.get(j));
                    label+="-"+i;
                    res.put(c.shared_node.get(j), label);
                }
            }
        }
        return res;
    }

    private void printInfo(String s) {
        this.taInfo.append("  " + s + "\r\n");
    }
}

