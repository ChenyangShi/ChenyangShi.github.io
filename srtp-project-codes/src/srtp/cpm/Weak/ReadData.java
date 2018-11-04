import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class ReadData
{
    public int nodenum;	//节点数
    public int edgenum = 0;//所有边数

    public Set<Integer> neighborSet[];//邻居节点表
    public Set<Integer> V;//顶点集

	public int degree[];//节点度数

    public ReadData(String filename)
    {

        try
        {
            FileReader fr = new FileReader(filename);
            BufferedReader reader = new BufferedReader(fr);//以行读取文件
            String line = "";
        //    line = reader.readLine();//读一行文件
        //    String atts[] = line.split("\\s+");//按照空白符分割成数组元素
        //    nodenum = Integer.parseInt(atts[0]);//取得总结点数

        //    System.out.println("nodenum: " + nodenum);
            System.out.println("reading: " + filename);

            V = new LinkedHashSet<Integer>();

            neighborSet = new LinkedHashSet[nodenum];//每个节点都引出一条链表 存放相邻节点
            for(int i=0;i<nodenum;i++)
                neighborSet[i] = new LinkedHashSet<Integer>();

            degree = new int[nodenum];

            int linenumber = 0;

            String atts2[];
            int nodeid1;
            int nodeid2;
            while((line = reader.readLine())!=null)//读取的一行不为空时
            {
                linenumber++;
                System.out.println("reading line: "+linenumber);//显示正在读的行数
                atts2 = line.split("\\s+");//以空白符为分隔符 把正在读取的一行的数据划分后存入字符数组attr2[]
                System.out.println(atts2[0]);
                System.out.println(atts2[1]);

                nodeid1 = Integer.parseInt(atts2[0]);
                nodeid2 = Integer.parseInt(atts2[1]);

                if(!neighborSet[nodeid1].contains(nodeid2))
                {
                    if(nodeid1 != nodeid2)
                    edgenum++;//增加一条边

                    V.add(nodeid1);
                    V.add(nodeid2);

                    degree[nodeid1]++;//节点度加1
                    degree[nodeid2]++;

                    neighborSet[nodeid1].add(nodeid2);//节点nodeid2连在nodeid1的链表上
                    neighborSet[nodeid2].add(nodeid1);//节点nodeid1连在nodeid2的链表上
                }
            }
            System.out.println("nodenum: "+nodenum);
            System.out.println("edgenum: "+edgenum);
            System.out.println();

            reader.close();//关闭文件
            fr.close();
        }
        catch(IOException e)
        {
            System.out.println("Can not find the file " + filename);
        }
    }
}