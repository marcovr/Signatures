package ch.unifr.marcovr.ResultsAnalyzer.UI;

import ch.unifr.marcovr.GraphTransformer.gxl.GxlRoot;
import ch.unifr.marcovr.GraphTransformer.gxl.GxlVisualizer;
import ch.unifr.marcovr.GraphTransformer.transformation.Transformer;

import javax.swing.*;
import java.awt.*;

public class GraphPanel extends JPanel {
    private GxlRoot gxl_original;
    private GxlRoot gxl;
    private GxlVisualizer visualizer = new GxlVisualizer();

    private String transform = "none";
    private boolean keepEdges = true;
    private int k = 1;

    public void setTransform(String transform) {
        this.transform = transform;
        setGxl(gxl_original);
    }

    public void setKeepEdges(boolean keepEdges) {
        this.keepEdges = keepEdges;
        setGxl(gxl_original);
    }

    public void setK(int k) {
        this.k = k;
        setGxl(gxl_original);
    }

    public void setGxl(GxlRoot gxl) {
        gxl_original = gxl;

        if (gxl == null) {
            this.gxl = null;
            repaint();
            return;
        }

        Transformer transformer = new Transformer(gxl);

        switch (transform) {
            case "knearest":
                this.gxl = transformer.kNearest(k, keepEdges);
                break;
            case "kspan_avg":
                this.gxl = transformer.kSpan(k, Transformer.MAX_AVERAGE, keepEdges);
                break;
            case "kspan_min":
                this.gxl = transformer.kSpan(k, Transformer.MAX_MINIMUM, keepEdges);
                break;
            default:
                this.gxl = gxl;
                break;
        }

        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (gxl != null) {
            visualizer.paintImage(gxl, (Graphics2D) g, getWidth(), getHeight());
        }
    }
}
