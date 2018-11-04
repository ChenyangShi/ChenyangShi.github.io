/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srtp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.gephi.filters.api.FilterController;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.preview.api.G2DTarget;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.types.DependantColor;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 *
 * @author traitor
 */
public class DataVisiualizationHelper {
//    public static final JPanel visiualize(String csvFilePath, JTextArea infoTA) {
    public static final PreviewSketch visiualize(String csvFilePath, Map<Integer, String> res, JTextArea infoTA, IVisiualized v) {
        //初始化一个project，并获取workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        //获取graphModel和需要用到的controller
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        FilterController filterController = Lookup.getDefault().lookup(FilterController.class);

        //输入文件，暂时仅支持csv文件，并将输入的文件和之前获取的workspace关联
        Container container;
        try {
            File file = new File(csvFilePath);
            container = importController.importFile(file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        importController.process(container, new DefaultProcessor(), workspace);
        
        //根据输入文件生成无向图，并输出该图的点数和边数
        UndirectedGraph graph = graphModel.getUndirectedGraph();
        if (null != res) {
            Map<String, Color> colors = new HashMap<String, Color>();
            for (Node n : graph.getNodes()) {
                double nodeID = Double.parseDouble("" + n.getId());
                Random rand = new Random();
                float r = 0, g = 0, b = 0;
                String nodeComm = res.get((int)nodeID);
                if (null == nodeComm)
                    infoTA.append("No Label For " + nodeID + "\r\n");
                else
                    n.setLabel(nodeComm);
                //  取出多个社区
                for (String sC: nodeComm.split("-")) {
                    if (null == colors.get(sC))
                        colors.put(sC, new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
                    r += (colors.get(sC).getRed() / 256.0);
                    g += (colors.get(sC).getGreen() / 256.0);
                    b += (colors.get(sC).getBlue() / 256.0);
                }
                n.setColor(new Color(r - (int)r, g - (int)g, b - (int)b));
            }
        }
        if (null != infoTA) {
            infoTA.append("  节点数量: " + graph.getNodeCount() + "\r\n");
            infoTA.append("  边数量: " + graph.getEdgeCount() + "\r\n");
            infoTA.setCaretPosition(infoTA.getText().length());
        }
        
        //Fruchterman布局设置，可将联通的子图分离开来
        FruchtermanReingold myLayout = new FruchtermanReingold(null);
        myLayout.setGraphModel(graphModel);
        myLayout.setArea(10000.0f);
        myLayout.setGravity(10.0);
        myLayout.setSpeed(500.0);
      
        myLayout.initAlgo();
        for (int i = 0; i < 20000 && myLayout.canAlgo(); i++) {
            myLayout.goAlgo();
        }
        myLayout.endAlgo();
        
        //graph的点属性，边属性
        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        
        previewModel.getProperties().putValue(PreviewProperty.ARROW_SIZE, 0);
        previewModel.getProperties().putValue(PreviewProperty.DIRECTED, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.MARGIN, 10f); //图距离frame边的距离
        previewModel.getProperties().putValue(PreviewProperty.MOVING, Boolean.FALSE);//移动画布？true时边和顶点id都没了
        previewModel.getProperties().putValue(PreviewProperty.VISIBILITY_RATIO, 1f);//显示的比例，1f为100%，点超级多的话可以显示一部分
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);
        
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);//显式点的id与否
        previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 0.0f);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.BLACK));//点标签颜色
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_MAX_CHAR, 10);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, Boolean.TRUE);     
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_SHOW_BOX, Boolean.FALSE);//false，不好看
        previewModel.getProperties().putValue(PreviewProperty.NODE_OPACITY, 100f);//点的透明度LIGHT_GRAY

        previewModel.getProperties().putValue(PreviewProperty.SHOW_EDGE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.SHOW_EDGES, Boolean.TRUE);//显不显示边
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.TRUE);//边是否弯曲，弯的无向，直的有向
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 100);//边的透明度
        previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 0f);//箭头到点的间隔
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(Color.BLACK));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_THICKNESS, 0.1f);    
        
        //显示JFrame
        G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);
        PreviewSketch previewSketch = new PreviewSketch(target);
        previewController.refreshPreview();
        
        if (null != v) {
            v.gotGraph(graph);
            v.visiualized(previewController, previewSketch);
        }

        return previewSketch;
    }
}
