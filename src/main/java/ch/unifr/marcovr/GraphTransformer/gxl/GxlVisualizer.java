package ch.unifr.marcovr.GraphTransformer.gxl;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Provides methods to visualize gxl graphs.
 */
public class GxlVisualizer {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 400;

    private static final double PADDING_PERCENT = 0.05;
    private static final double RADIUS_PERCENT = 0.004;

    private final Graph graph;
    private int radius = 3;
    private double scale, offsetX, offsetY;

    private GxlVisualizer(GxlRoot gxl) {
        graph = gxl.getGraph();
    }

    /**
     * Shows a graph in a resizeable JFrame.
     *
     * @param gxl gxl object to visualize
     */
    public static void visualize(GxlRoot gxl) {
        GxlVisualizer visualizer = new GxlVisualizer(gxl);

        // create dynamically redrawing panel
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                visualizer.paintImage((Graphics2D) g, getWidth(), getHeight());
            }
        };
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // show window
        JFrame frame = new JFrame("Graph Visualizer");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Writes a graph into an image file.
     *
     * @param gxl gxl object to visualize
     * @param filePath image path to write to
     */
    public static void visualize(GxlRoot gxl, String filePath) throws IOException {
        visualize(gxl, WIDTH, HEIGHT, filePath);
    }

    /**
     * Writes a graph into an image file.
     *
     * @param gxl gxl object to visualize
     * @param width size of image
     * @param height size of image
     * @param filePath image path to write to
     */
    public static void visualize(GxlRoot gxl, int width, int height, String filePath) throws IOException {
        GxlVisualizer visualizer = new GxlVisualizer(gxl);

        // create image
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        visualizer.paintImage((Graphics2D) img.getGraphics(), img.getWidth(), img.getHeight());

        // write image to file - determine format from path
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        ImageIO.write(img, extension, new File(filePath));
    }

    /**
     * Paints the stored graph using the provided graphics object.
     *
     * @param g graphics object to paint with
     * @param width size of image
     * @param height size of image
     */
    private void paintImage(Graphics2D g, int width, int height) {
        calculateNormalisation(width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);

        // draw nodes
        for (Node node : graph.getNodes()) {
            Point p = transform(node.getLocation());
            g.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
        }

        // draw edges
        for (Edge edge : graph.getEdges()) {
            Point from = transform(edge.getFrom().getLocation());
            Point to = transform(edge.getTo().getLocation());
            g.drawLine(from.x, from.y, to.x, to.y);
        }

    }

    /**
     * Calculates radius, scale and offsetX/Y to paint the stored graph in a reasonable way.
     *
     * @param width size of image
     * @param height size of image
     */
    private void calculateNormalisation(int width, int height) {
        double minX, minY, maxX, maxY;

        // initialize values
        Point2D first = graph.getNodes().get(0).getLocation();
        minX = maxX = first.getX();
        minY = maxY = first.getY();

        // get min/max values
        for (Node node : graph.getNodes()) {
            Point2D p = node.getLocation();
            minX = Math.min(p.getX(), minX);
            minY = Math.min(p.getY(), minY);
            maxX = Math.max(p.getX(), maxX);
            maxY = Math.max(p.getY(), maxY);
        }

        // determine reasonable padding/radius values
        double ratio = (maxX - minX) / (maxY - minY);
        int padding = (int) (Math.min(width, ratio * height) * PADDING_PERCENT);
        radius = (int) (Math.min(width, ratio * height) * RADIUS_PERCENT);

        // width/height without padding
        int innerWidth = width - 2 * padding;
        int innerHeight = height - 2 * padding;

        // scale graph such that it fills the image
        scale = innerWidth / (maxX - minX);
        scale = Math.min(innerHeight / (maxY - minY), scale);

        // center the scaled graph
        offsetX = padding - scale * minX + (innerWidth - scale * maxX + scale * minX) / 2;
        offsetY = padding - scale * minY + (innerHeight - scale * maxY + scale * minY) / 2;
    }

    /**
     * Transforms a Point2D into a Point and applies normalisation factors.
     *
     * @param p Point2D to transform
     * @return transformed Point
     */
    private Point transform(Point2D p) {
        int x = (int) (p.getX() * scale + offsetX);
        int y = (int) (p.getY() * scale + offsetY);
        return new Point(x, y);
    }

}
