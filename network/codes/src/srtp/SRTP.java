/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srtp;

import java.awt.*;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.NodeIterable;
import org.gephi.preview.api.PreviewController;
import srtp.cpm.CliquePercolationMethod;
import srtp.lpa.Evaluation;
import srtp.lpa.LeaderRank;
import srtp.lpa.Propagation;

public class SRTP extends WindowAdapter implements ActionListener {

    public static SRTP gui = new SRTP();

    private static final int INT_RIGHT_TABLE_COLUMN_WIDTH    = 200;
    private static final int INT_RIGHT_TABLE_ROW_HEIGHT      = 30;
    private static final int INT_BOTTOM_TABLE_HEIGHT         = 200;
    private static final int INT_COMPONENT_MARGIN_HORIZONTAL = 10;
    private static final int INT_INFOR_TEXT_AREA_HEIGHT      = 100;

    private static final String STR_PROJECT_TITLE = "基于集成学习的社区发现算法研究";
    private static final String STR_FILE = "文件";
    private static final String STR_PROJECT = "项目";
    private static final String STR_OPEN_FILE = "打开文件";
    private static final String STR_CLOSE_FILE = "关闭文件";
    private static final String STR_SAVE_FILE = "保存文件";
    private static final String STR_EXIT = "退出";
    private static final String STR_START_PROCESS = "启动计算";
    private static final String STR_OUTPUT_RESULT = "输出结果";
    private static final String STR_CHOOSE_ALGORITHM = "算法选择:";
    private static final String STR_ALGORITHM_1 = "LPA";
    private static final String STR_ALGORITHM_2 = "CPM";
    private static final String STR_TABLE_RIGHT_HEADER_FROM_ID = "起始点";
    private static final String STR_TABLE_RIGHT_HEADER_TO_ID = "终止点";
    private static final String STR_TABLE_BOTTOM_HEADER_COMMUNITY_INDEX = "社区编号";
    private static final String STR_TABLE_BOTTOM_HEADER_NODES_IN_COMMUNITY = "社区内节点编号";
    private static final String STR_PLEASE_CHOOSE_FILE = "请选择文件!";
    private static final String STR_NODE_COUNT = "节点数量:";
    private static final String STR_EDGE_COUNT = "边数量:";
    private static final String STR_MODULE_DEGREE = "模块度:";
    private static final String STR_NMI_VALUE = "NMI:";
    private static final String STR_COMM_COUNT= "社区数量:";
    private static final String STR_PARAM_OV = "OV参数:";

    private Frame main_frame;
    //  菜单栏
    private MenuBar menu_bar;
    private Menu menu_file;
    private Menu menu_project;
    private MenuItem menu_item_file_open;
    private MenuItem menu_item_file_close;
    private MenuItem menu_item_file_save;
    private MenuItem menu_item_file_exit;
    private MenuItem menu_item_project_start;
    //  主窗体界面控件
    private JButton btn_open_file;
    private JButton btn_run;
    private JLabel label_sourceFilePath;
    private JLabel label_nodes_count;
    private JLabel label_edges_count;
    private JLabel label_module_degree;
    private JLabel label_nmi_value;
    private JLabel label_comm_count;
    private JComboBox<String> combo_algorithm;
    private JTable table_right;
    private JTable table_bottom;
    private JTextArea textarea_info;
    private PreviewSketch panel_graph;
    private Panel panel_center;
    private JTextField tf_param_ov;
    
    private SRTPTableModel dtm_edges;
    private SRTPTableModel dtm_communities;
    
    private Color bkgColor = new Color(0.95f, 0.95f, 0.95f);
    
    private String sourceFilePath = "             ";
    private String[][] edges = { {"0", "0"} };
    private String[][] communities = { {"0", "0"} };
    private String[] right_table_headers = {STR_TABLE_RIGHT_HEADER_FROM_ID, STR_TABLE_RIGHT_HEADER_TO_ID};
    private String[] bottom_table_headers = {STR_TABLE_BOTTOM_HEADER_COMMUNITY_INDEX, STR_TABLE_BOTTOM_HEADER_NODES_IN_COMMUNITY};

    private SRTP() {
        initGUI();
    }

    public static SRTP getInstance() {
        return gui;
    }

    private void initGUI() {
        Dimension screen_size = Toolkit.getDefaultToolkit().getScreenSize().getSize();
        int screen_width = (int)screen_size.getWidth();
        int screen_height= (int)screen_size.getHeight();
        //  初始化主窗体
        this.main_frame = new Frame(STR_PROJECT_TITLE);
        this.main_frame.setMaximumSize(screen_size);
        this.main_frame.setSize((int)(0.8 * screen_width), (int)(0.8 * screen_height));
        this.main_frame.setLocation((int)(screen_width * 0.1), (int)(screen_height * 0.1));
        this.main_frame.setMenuBar(this.menu_bar = new MenuBar());
        this.main_frame.addWindowListener(this);
        this.main_frame.setBackground(Color.WHITE);
        //  初始化菜单栏
        this.menu_bar.add(this.menu_file = new Menu(STR_FILE));
        this.menu_bar.add(this.menu_project = new Menu(STR_PROJECT));
        //  初始化文件菜单项
        this.menu_file.add(this.menu_item_file_open =
            new MenuItem(STR_OPEN_FILE, new MenuShortcut(KeyEvent.VK_O, false)));
        this.menu_item_file_open.addActionListener(this);
        this.menu_file.add(this.menu_item_file_close =
            new MenuItem(STR_CLOSE_FILE, new MenuShortcut(KeyEvent.VK_C, true)));
        this.menu_item_file_close.addActionListener(this);
        this.menu_file.addSeparator();
        this.menu_file.add(this.menu_item_file_save =
            new MenuItem(STR_SAVE_FILE, new MenuShortcut(KeyEvent.VK_S, false)));
        this.menu_item_file_save.addActionListener(this);
        this.menu_file.addSeparator();
        this.menu_file.add(this.menu_item_file_exit =
            new MenuItem(STR_EXIT, new MenuShortcut(KeyEvent.VK_E, false)));
        this.menu_item_file_exit.addActionListener(this);
        //  初始化项目菜单项
        this.menu_project.add(this.menu_item_project_start =
            new MenuItem(STR_START_PROCESS, new MenuShortcut(KeyEvent.VK_ENTER, false)));
        this.menu_item_project_start.addActionListener(this);
        
        //  界面主要部分的设计
        this.main_frame.setLayout(new BorderLayout());
        //  界面右侧布局
        Panel tmpPanel = new Panel(), p;
        tmpPanel.setBackground(bkgColor);
        tmpPanel.setLayout(new BorderLayout());
        Panel pp = new Panel();
        pp.setLayout(new BoxLayout(pp, BoxLayout.Y_AXIS));
        //  添加显示节点数量的控件
        p = new Panel(new BorderLayout());
        p.add(new Label(STR_NODE_COUNT, Label.CENTER), BorderLayout.WEST);
        p.add(this.label_nodes_count = new JLabel());
        pp.add(p);
        //  添加显示边数量的控件
        p = new Panel(new BorderLayout());
        p.add(new Label(STR_EDGE_COUNT, Label.CENTER), BorderLayout.WEST);
        p.add(this.label_edges_count = new JLabel());
        pp.add(p);
        tmpPanel.add(pp, BorderLayout.NORTH);
        //  添加表格
        this.table_right = new JTable(this.dtm_edges = new SRTPTableModel());
        this.dtm_edges.setColumnIdentifiers(this.right_table_headers);
        this.table_right.setPreferredScrollableViewportSize(
            new Dimension(INT_RIGHT_TABLE_COLUMN_WIDTH, INT_RIGHT_TABLE_ROW_HEIGHT)
        );
        this.table_right.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        JScrollPane jScrollPane = new JScrollPane(this.table_right);
        jScrollPane.setLayout(new ScrollPaneLayout());
        tmpPanel.add(jScrollPane, BorderLayout.CENTER);
        this.main_frame.add(tmpPanel, BorderLayout.EAST);

        //  底部布局
        tmpPanel  = new Panel();
        tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.X_AXIS));
        tmpPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        //  底部表格和评估结果输出部分
        p = new Panel();
        p.setLayout(new BorderLayout());
        this.table_bottom = new JTable(this.dtm_communities = new SRTPTableModel());
        this.dtm_communities.setColumnIdentifiers(bottom_table_headers);
        this.table_bottom.setPreferredScrollableViewportSize(
            new Dimension(INT_RIGHT_TABLE_COLUMN_WIDTH, INT_RIGHT_TABLE_ROW_HEIGHT)
        );  
        this.table_bottom.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);  
        jScrollPane = new JScrollPane(this.table_bottom);
        jScrollPane.setPreferredSize(new Dimension(0, INT_BOTTOM_TABLE_HEIGHT));
        p.add(jScrollPane, BorderLayout.CENTER);
        pp = new Panel();
        pp.setLayout(new BoxLayout(pp, BoxLayout.X_AXIS));
        pp.add(new Label(STR_COMM_COUNT, Label.LEFT));
        pp.add(this.label_comm_count = new JLabel("", Label.LEFT));
        pp.add(Box.createRigidArea(new Dimension(INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
        pp.add(new Label(STR_MODULE_DEGREE, Label.LEFT));
        pp.add(this.label_module_degree = new JLabel("", Label.LEFT));
        pp.add(Box.createRigidArea(new Dimension(4 * INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
//        pp.add(new Label(STR_NMI_VALUE, Label.LEFT));
//        pp.add(this.label_nmi_value = new JLabel("", Label.LEFT));
        pp.add(Box.createRigidArea(new Dimension(INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
        p.add(pp, BorderLayout.SOUTH);
        tmpPanel.add(p);
        tmpPanel.add(Box.createRigidArea(new Dimension(INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
        //  底部控制台信息部分
        this.textarea_info = new JTextArea();
        this.textarea_info.setEditable(false);
        this.textarea_info.setLineWrap(true);
        jScrollPane = new JScrollPane(this.textarea_info);
        jScrollPane.setPreferredSize(new Dimension(600, INT_INFOR_TEXT_AREA_HEIGHT));
        tmpPanel.add(jScrollPane);
        tmpPanel.add(Box.createRigidArea(new Dimension(INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
        this.main_frame.add(tmpPanel, BorderLayout.SOUTH);
        //  中心社区图控件     
        tmpPanel = new Panel();
        tmpPanel.setLayout(new BoxLayout(tmpPanel, BoxLayout.X_AXIS));
        tmpPanel.setBackground(bkgColor);
        tmpPanel.add(Box.createRigidArea(new Dimension(INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
        //  文件选择部分
        p = new Panel(new BorderLayout());
        p.add(this.btn_open_file = new JButton(STR_OPEN_FILE), BorderLayout.WEST);
        p.add(this.label_sourceFilePath = new JLabel(sourceFilePath, Label.LEFT));
        this.label_sourceFilePath.setSize(new Dimension(100, 0));
        tmpPanel.add(p);
        tmpPanel.add(Box.createRigidArea(new Dimension(INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
        //  算法选择部分
        p = new Panel(new BorderLayout());
        p.add(new Label(STR_CHOOSE_ALGORITHM, Label.CENTER), BorderLayout.WEST);
        p.add(this.combo_algorithm = new JComboBox<>());
        this.combo_algorithm.addItem(STR_ALGORITHM_1);
        this.combo_algorithm.addItem(STR_ALGORITHM_2);
        tmpPanel.add(p);
        tmpPanel.add(Box.createRigidArea(new Dimension(INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
        //  运行程序
        tmpPanel.add(this.btn_run = new JButton(STR_START_PROCESS));
        tmpPanel.add(Box.createRigidArea(new Dimension(INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
        //  参数设置
        p = new Panel(new BorderLayout());
        p.add(new Label(STR_PARAM_OV, Label.CENTER), BorderLayout.WEST);
        p.add(this.tf_param_ov = new JTextField("0.25"));
        tmpPanel.add(p);
        tmpPanel.add(Box.createRigidArea(new Dimension(20 * INT_COMPONENT_MARGIN_HORIZONTAL, 0)));
        this.btn_open_file.addActionListener(this);
        this.btn_run.addActionListener(this);
        //  添加中心部分
        this.main_frame.add(this.panel_center = new Panel(), BorderLayout.CENTER);
        this.panel_center.setLayout(new BorderLayout());
        this.panel_center.add(tmpPanel, BorderLayout.NORTH);
    }

    //  窗体关闭事件的响应函数
    @Override
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }

    //  文件菜单中 打开 的处理函数
    private boolean loadSourceFile() {
        FileDialog openFD = new FileDialog(this.main_frame, STR_PLEASE_CHOOSE_FILE, FileDialog.LOAD);
        openFD.setFilenameFilter(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".csv"))
                    return true;
                return false;
            }
        });
        openFD.setDirectory("/home/traitor/Downloads/karate");
        openFD.setVisible(true);
        String fileName = openFD.getFile();
        if (null == fileName)
            return false;
        sourceFilePath = openFD.getDirectory() +  fileName;
        this.label_sourceFilePath.setText(fileName);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = sourceFilePath;
                if (0 <= sourceFilePath.indexOf("[Node]"))
                    filePath = sourceFilePath.replace("[Node]", "[Edge]");
                DataVisiualizationHelper.visiualize(filePath, null, textarea_info, new IVisiualized() {
                    @Override
                    public void visiualized(PreviewController pc, PreviewSketch ps) {
                        panel_graph = ps;
                        panel_center.add(panel_graph, BorderLayout.CENTER);
                        printInfo("数据可视化完成!");
                        pc.refreshPreview();
                        main_frame.setVisible(true);
                    }
                    @Override
                    public void gotGraph(Graph g) {
                        dtm_edges.setRowCount(0);
                        for (Edge e : g.getEdges()) {
                            String[] edge = {"" + e.getSource().getId(), "" + e.getTarget().getId()};
                            dtm_edges.addRow(edge);
                        }
                        label_nodes_count.setText("" + g.getNodeCount());
                        label_edges_count.setText("" + g.getEdgeCount());
                    }
                });
            }
            
        });
        thread.start();
        return true;
    }

    //  文件菜单中 保存 的处理函数
    private boolean saveFile() {
        return true;
    }

    //  文件菜单中 关闭 的处理函数
    private boolean closeFile() {
        return true;
    }

    //  文件菜单中 开始 的处理函数
    private boolean startProcess() {
        String algorithm = (String)this.combo_algorithm.getSelectedItem();
        Thread thread;
        Evaluation el;
        if (STR_ALGORITHM_1.equals(algorithm)) {
            //  选择使用第一种算法进行聚类分析
            if (sourceFilePath.trim().isEmpty()) {
                printInfo("文件路径不能为空!");
                return false;
            }
            printInfo("启动数据可视化!");
            printInfo("源文件路径: " + sourceFilePath);
            printInfo("选择使用第一种算法进行聚类分析");
            String filePath = sourceFilePath;
            if (0 <= sourceFilePath.indexOf("[Node]"))
                filePath = sourceFilePath.trim().replace("[Node]", "[Edge]");
            final String fp = filePath;
            LeaderRank lr = new LeaderRank(filePath.trim(),true, this.textarea_info);
            Propagation ppg=new Propagation(lr.nodes, this.textarea_info);
            String ov = this.tf_param_ov.getText().trim();
            ppg.start(Double.parseDouble(ov));
            Map<Integer,String> res=ppg.getResualt();
            el=new Evaluation(ppg.coms);
            this.label_module_degree.setText("" + el.getModularity(filePath + ov));
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    DataVisiualizationHelper.visiualize(fp, res, textarea_info, new IVisiualized() {
                        @Override
                        public void visiualized(PreviewController pc, PreviewSketch ps) {
                            panel_graph = ps;
                            panel_center.add(panel_graph, BorderLayout.CENTER);
                            printInfo("数据可视化完成!");
                            pc.refreshPreview();
                            main_frame.setVisible(true);
                        }
                        @Override
                        public void gotGraph(Graph g) {
                            dtm_edges.setRowCount(0);
                            for (Edge e : g.getEdges()) {
                                String[] edge = {"" + e.getSource().getId(), "" + e.getTarget().getId()};
                                dtm_edges.addRow(edge);
                            }
                            label_nodes_count.setText("" + g.getNodeCount());
                            label_edges_count.setText("" + g.getEdgeCount());
                        }
                    });
                }

            });
            thread.start();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dtm_communities.setRowCount(0);
                    Map<String, String> comm = new HashMap<String, String>();
                    for(Iterator entries = res.entrySet().iterator(); entries.hasNext(); ){
                        Entry entry=(Entry) entries.next();
                        String cs = (String)entry.getValue();
                        String id = "" + entry.getKey();
                        for (String s : cs.split("-")) {
                            if (null == comm.get(s))
                                comm.put(s, id);
                            else
                                comm.put(s, comm.get(s) + "," + id);
                        }
                    }
                    for (Iterator entries = comm.entrySet().iterator(); entries.hasNext();) {
                        Entry e = (Entry)entries.next();
                        String[] s = {"" + e.getKey(), "" + e.getValue()};
                        dtm_communities.addRow(s);
                    }
                    label_comm_count.setText("" + dtm_communities.getRowCount());
                }
            });
            t.start();
            
        } else if (STR_ALGORITHM_2.equals(algorithm)) {
            //  选择使用第二种算法进行聚类研究
            if (sourceFilePath.isEmpty()) {
                printInfo("文件路径不能为空!");
                return false;
            }
            printInfo("启动数据可视化!");
            printInfo("源文件路径: " + sourceFilePath);
            printInfo("选择使用第二种算法进行聚类研究");
            
            CliquePercolationMethod cpm = null;
            if (0 <= sourceFilePath.indexOf("[Node]"))
                cpm = new CliquePercolationMethod(sourceFilePath, sourceFilePath.replace("[Node]", "[Edge]"), this.textarea_info);
            else
                cpm = new CliquePercolationMethod(sourceFilePath.replace("[Edge]", "[Node]"), sourceFilePath, this.textarea_info);
            double k_clique = Double.parseDouble(tf_param_ov.getText().trim());
            cpm.start((int)k_clique);
            Map<Integer,String> res= cpm.getResult();
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    String filePath;
                    if (0 <= sourceFilePath.indexOf("[Edge]"))
                        filePath = sourceFilePath;
                    else
                        filePath = sourceFilePath.replace("[Node]", "[Edge]");
                    DataVisiualizationHelper.visiualize(filePath, res, textarea_info, new IVisiualized() {
                        @Override
                        public void visiualized(PreviewController pc, PreviewSketch ps) {
                            panel_graph = ps;
                            panel_center.add(panel_graph, BorderLayout.CENTER);
                            printInfo("数据可视化完成!");
                            pc.refreshPreview();
                            main_frame.setVisible(true);
                        }
                        @Override
                        public void gotGraph(Graph g) {
                            dtm_edges.setRowCount(0);
                            for (Edge e : g.getEdges()) {
                                String[] edge = {"" + e.getSource().getId(), "" + e.getTarget().getId()};
                                dtm_edges.addRow(edge);
                            }
                            label_nodes_count.setText("" + g.getNodeCount());
                            label_edges_count.setText("" + g.getEdgeCount());
                        }
                    });
                }
            });
            thread.start();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dtm_communities.setRowCount(0);
                    Map<String, String> comm = new HashMap<String, String>();
                    for(Iterator entries = res.entrySet().iterator(); entries.hasNext(); ){
                        Entry entry=(Entry) entries.next();
                        String cs = (String)entry.getValue();
                        String id = "" + entry.getKey();
                        for (String s : cs.split("-")) {
                            if (null == comm.get(s))
                                comm.put(s, id);
                            else
                                comm.put(s, comm.get(s) + "," + id);
                        }
                    }
                    for (Iterator entries = comm.entrySet().iterator(); entries.hasNext();) {
                        Entry e = (Entry)entries.next();
                        String[] s = {"" + e.getKey(), "" + e.getValue()};
                        dtm_communities.addRow(s);
                    }
                    label_comm_count.setText("" + dtm_communities.getRowCount());
                }
            });
            t.start();
        }
        this.main_frame.setVisible(true);
        return true;
    }

    //  按钮或者菜单项点击的响应函数
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        if (STR_EXIT.equals(cmd)) {
            System.exit(0);
        } else if (STR_OPEN_FILE.equals(cmd)) {
            loadSourceFile();
        } else if (STR_SAVE_FILE.equals(cmd)) {
            saveFile();
        } else if (STR_CLOSE_FILE.equals(cmd)) {
            closeFile();
        } else if (STR_START_PROCESS.equals(cmd)) {
            startProcess();
        } else {
            printInfo(cmd);
        }
    }

    //  显示窗体界面
    public void show() {
        this.main_frame.setVisible(true);
    }

    //  向消息输出框中输出
    public void printInfo(String s) {
        this.textarea_info.append("  " + s + "\r\n");
    }
    
    public static void main(String[] args) {
        SRTP g = SRTP.getInstance();
        g.show();
    }
}
