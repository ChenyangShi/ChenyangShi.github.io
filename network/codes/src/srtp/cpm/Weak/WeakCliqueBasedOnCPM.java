
import java.util.*;

public class WeakCliqueBasedOnCPM
{
    int counter = 0; //计数
    final float T = 0.3f;
    int nodenum; //总结点数目
    Set<Integer> V;
    int degree[];

    float P[];//priority
    HashMap<Integer, Float> SI_v_in_u;//SI

    boolean visited[];

    Set<Integer> neighborSet[]; //邻居节点表
    ArrayList<Set<Integer>> WClique;//派系的集合
//    ArrayList<Community> S = new ArrayList<Community>();//所有社区集合
    ArrayList<Set<Integer>> S = new ArrayList<Set<Integer>>();//所有社区集合
    /**
     * 构造函数，初始化变量
     */
    public WeakCliqueBasedOnCPM(String filename)
    {
        ReadData rd = new ReadData(filename);
        nodenum = rd.nodenum;

        neighborSet = new LinkedHashSet[nodenum];//分配头结点
        for(int i = 0; i < nodenum; i++)
            neighborSet[i] = new LinkedHashSet<Integer>();//分配表节点
        neighborSet = rd.neighborSet;

        degree = new int[nodenum];//读度数
        degree = rd.degree;

        V = new LinkedHashSet<Integer>();
        V = rd.V;

        P = new float[nodenum];
    }

    /**
     * Algorithm 1 : General Framework of W-CPM
     */
    public void Action()
    {
        //S = new LinkedList<Integer>();//S ← ∅
        IdentifyWeakClique();//WClique ← IdentifyWeakClique(G)

        /*all the weak cliques initially unvisited*/
        visited = new boolean[ WClique.size() ];//分配visited[], 初始化
        for(int i = 0; i < WClique.size(); i++)
            visited[i] = false;

         /*if weak clique i is not visited, merge it with its neighboring weak cliques*/
        int i = 0;
        for(Set<Integer> wclique : WClique)//for each wclique ∈ WClique do
        {
            if(!visited[i])//if wclique is unvisited then
                MergeClique(wclique, T);//Merge(wclique, S, T)
        }//end for
     //return S
    }

    /**
     * Algorithm2 : IdentifyWeakClique(G)
     */
    public void IdentifyWeakClique() {
        int u, v;
        Set<Integer> W_u;
        WClique = new ArrayList<Set<Integer>>();//WClique ← ∅

        CalculatePriority();//P(i) ← Compute the priority of node i by Equation 2
        while (V.size() != 0)//while V! = ∅ do
        {
            u = MaxP(P);//u ← arg max(P (i))
            CalculateSaltonIndex(u);//SI ←Calculate Salton index for each neighbor v of node u by Equation 3

            W_u = new LinkedHashSet<Integer>();//Wu ← ∅
            while (SI_v_in_u.size() != 0)//while SI ! = ∅ do
            {
                /* Select the node v which has the maximal similarity
                with u in set SI */
                v = MaxH(SI_v_in_u);//v ← arg max(SI (i))
                for (int elem1 : neighborSet[u]) {
                        if (neighborSet[v].contains(elem1))
                            W_u.add(elem1);
                }
                W_u.add(u);
                W_u.add(v);//Wu ← Wu ∪ ({u, v} ∪ N(u) ∩ N(v))

                for (int e : W_u) {
                    SI_v_in_u.remove(e);//Remove Wu from SI. 一并移除 key-value 即 v_Id - SI_v_in_u
                }
            }//end while
            WClique.add(W_u);//find one W_clique
            counter++;
            System.out.println("~~~~" + counter);

            for(int id : W_u) {//Remove Wu from V
                V.remove(id);
                P[id] = 0.0f;//lower the removed node's priority
            }
        }//end while
        //return WClique
    }

    /**
     * Algorithm 3 : Merge(wclique, S, T )
     * @param wclique weak clique wclique
     * @param T threshold T = 0.3
     */
    public void MergeClique(Set<Integer> wclique, float T)
    {
        /*Define two variables: Comm is a community, Container is a weak clique set*/
        Set<Set<Integer>> Container;
        Set<Integer> Comm;
        Set<Integer> Temp;
        Comm = new LinkedHashSet<Integer>(); //Comm ← ∅
        Container = new LinkedHashSet<Set<Integer>>(); //Container ← ∅

        Comm.addAll(wclique); //Comm ← Comm ∪ wclique
        Container.add(wclique); //add wclique into Container
        visited[0] = true;
        while(Container.size() != 0) //while Container ! = ∅ do
        {
            Temp = new LinkedHashSet<Integer>(); //temp ← select a weak clique from Container
            Iterator it = Container.iterator();
            if(it.hasNext())
                Temp = (Set<Integer>) it.next();
        //    for(Set<Integer> first : Container)
        //    {   Temp = first; break;  }

            for(int i = 1; i < WClique.size(); i++)
            {
                if( (WS(Temp, WClique.get(i)) > T) && !visited[i] ) //if W Stemp,neighbor_temp > T and neighbor_temp unvisited then
                {
                    Comm.addAll(WClique.get(i)); //Comm ← Comm ∪ neighbor_temp
                    Container.add(WClique.get(i)); //add neighbor_temp into Container
                    visited[i] = true; //mark neighbor_temp as visited
                }//end if
            }//end for
            Container.remove(Temp);
        }//end while
        this.S.add(Comm);
/*        Community community = new Community();
        community.CCom.addAll(Comm);
        S.add(community); //S ← S ∪ Comm
*/
    }

    /**
     * 计算每个节点的优先级
     */
    public void CalculatePriority()//寻找u
    {
        boolean mark[];
        mark = new boolean[nodenum];
        for(int i = 0 ; i < nodenum; i++)
            mark[i] = true;
        int  m;
        for(int i = 0; i < nodenum; i++)//every node
        {
            m = 0;
            for (int el1 : neighborSet[i])//遍历头结点i的set
            {
                for (int el2 : neighborSet[i]) {
                    if (!mark[el2]||el1==el2)
                        continue;//跳过已经比较的点
                    if (neighborSet[el1].contains(el2))//el1与el2之间存在link（肯定没有自环）
                        m++;
                }
                mark[el1] = false;
            }
            //m is the number of links between the neighbors of node i

            //compute priority of node i  : Pu = mu + k / k + 1
            P[i] = (float)(m + degree[i]) / (degree[i] + 1);
        }
    }

    /**
     * 计算节点与其每个邻居的Salton指数
     */
    public void CalculateSaltonIndex(int u)//寻找u对应的v
    {
        SI_v_in_u = new HashMap<Integer, Float>();

        int x = 0;//分子
        float y;//分母

        for (int v : neighborSet[u])//calculate SI for each neighbor v of node u
        {
            for (int elem : neighborSet[v])//遍历u的邻居的所有邻居
            {
                if (neighborSet[u].contains(elem))
                    x++;
            }
            y = (float)(neighborSet[u].size() * neighborSet[v].size());
            /* SIuv = |Nu ∩ Nv|   /  √|Nu| ∗ |Nv| */
            SI_v_in_u.put(v, (x / (float)(Math.sqrt(y)))); //节点 v 对应的值为 value
            x = 0;
        }
    }

    /**
     * 计算两个w_clique 的派系相似度
     * @param C1 a weak cliques in G
     * @param C2 a weak cliques in G
     * @return Weak clique Similarity of C1_C2
     */
    public double WS(Set<Integer> C1, Set<Integer> C2)
    {
        int V = 0; //the number of common elements in weak clique (C1, C2)
        int E = 0; //E(C1, C2) is the set of links between weak cliques C1 and C2
        int min = 0;
        float WSC1_C2;
        for(int node1 : C1) {
            for(int node2 : C2) {
                if(node1 == node2)
                    V++;
                if(neighborSet[node1].contains(node2))//exclude self-circle
                    E++;
            }
        }
        if((C1.size()<C2.size()) && C1.size() != 0 && C2.size() != 0)
            min = C1.size();
        else min = C2.size();
    //    min = ( C1.size() < C2.size() ) ? C1.size(): C2.size();
        /* W SC1C2 = |V (C1) ∩ V (C2)| + |E(C1, C2)|   /  min(|V (C1)|,|V (C2)|) */
        WSC1_C2 = (float)( V + E ) / min;
        return WSC1_C2;
    }

    /**
     * 计算最大的优先级
     * @param a : P[]
     * @return : node Id
     */
    public int MaxP(float a[]) {
        int res = 0;
        double t = a[0];
        for (int i = 0; i < a.length; i++) {
            if (a[i] > t) {
                t = a[i];
                res = i;
            }
        }
        return res;//取下标返回 下标就是节点Id
    }

    /**
     * 计算最大的Salton指数
     * @param map ： SI_v_in_u
     * @return : node Id
     */
    public int MaxH(HashMap<Integer, Float> map) {
        Float value = 0.0f; //值
        Integer max = 0; //最大的Key
        Set<Integer> set = map.keySet(); //Key的集合 即 节点Id的集合
        Iterator it = set.iterator();
        Integer KKey;
        while (it.hasNext())
        {
            KKey = (Integer) it.next();
            Float init = map.get(KKey);
            if (value <= init) {
                value = init;
                max = KKey;
            }
        }
        return max;
    }
    /*    List list = new ArrayList();

        Iterator it = map.entrySet().iterator();
        while(it.hasNext())// EntrySet 将同一个元素的key与value 关联在一起，当当前元素的value等于按从小到大排序的最后（最大）的元素的value值，输出当前元素的键key与值value
        {   maxKey = (Integer) it.next();
            Map.Entry res = (Map.Entry)it.next();
            value = (float)res.getValue();
            list.add(res.getValue());
            Collections.sort(list);

            if(value == list.get(list.size()-1))
            {
                maxKey = (Integer) res.getKey();
            }
        }
      */
    /*  int res = 0, i = 0;
        for(i = 0; i < map.size(); i++)//找到第一个不为空的Key (Id)
        {
            if (map.get(i) == null)
                i++;
            else
                break;
        }
    */
     /*   float t1 = (SI.get(i) == null ? 0.0f : SI.get(i)); //不是和Id零比较

        for( int j : KKey) //j就是Id值
        {
            float t2 = (SI.get(j) == null ? 0.0f : SI.get(j));
            if(t2 > t1) {
                t1 = t2; //t1是最大的值
                res = j;//j是最大的Id
            }
        }
        return res;
    }
*/
    public void Show()
    {
        for(int i = 0; i < S.size(); i++)
        {
            System.out.print("community "+i +": ");
            System.out.print(S.get(i));
        }
    }

    public static void main(String [] args)
    {
        WeakCliqueBasedOnCPM wcpm = new WeakCliqueBasedOnCPM("football1.txt");

        wcpm.Action();
        wcpm.Show();
        System.out.println("End");
    }
}

