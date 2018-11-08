/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package srtp;

import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.NodeIterable;
import org.gephi.preview.api.PreviewController;

/**
 *
 * @author traitor
 */
public interface IVisiualized {
    public abstract void gotGraph(Graph g);
    public abstract void visiualized(PreviewController pc, PreviewSketch ps);
}
