package ch.unifr.marcovr.GraphTransformer.transformation;

import ch.unifr.marcovr.GraphTransformer.gxl.Edge;
import ch.unifr.marcovr.GraphTransformer.gxl.GxlRoot;
import ch.unifr.marcovr.GraphTransformer.gxl.Node;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Provides methods to transform gxl graphs.
 */
public class Transformer {

    /**
     * Use the maximum of the minimal distances to determine the furthest node.
     */
    public static final int MAX_MINIMUM = 0;
    /**
     * Use the maximum of the average distances to determine the furthest node.
     */
    public static final int MAX_AVERAGE = 1;

    private final GxlRoot gxlOriginal;
    private final List<TransformNode> tNodes;
    private final Comparator<Distance> distComp;

    private GxlRoot gxl;

    /**
     * Creates and initializes a new Transformer.
     *
     * @param gxl the gxl object to transform
     */
    public Transformer(GxlRoot gxl) {
        this.gxlOriginal = gxl;
        tNodes = new ArrayList<>();
        distComp = Comparator.comparingDouble(o -> o.distance);

        for (Node gNode : gxl.getGraph().getNodes()) {
            tNodes.add(new TransformNode(gNode));
        }

        calculateDistances();
    }

    /**
     * Calculates and stores distances between each pair of nodes.
     */
    private void calculateDistances() {
        for (int i = 0; i < tNodes.size(); i++) {
            TransformNode tNodeA = tNodes.get(i);
            Point2D p = tNodeA.location;

            // own distance
            tNodeA.distances.add(new Distance(0, tNodeA));

            // other distances
            for (int j = i + 1; j < tNodes.size(); j++) {
                TransformNode tNodeB = tNodes.get(j);
                double dist = p.distance(tNodeB.location);
                tNodeA.distances.add(new Distance(dist, tNodeB));
                tNodeB.distances.add(new Distance(dist, tNodeA));
            }
        }
    }

    /**
     * Transforms the graph. For each node, edges to the k nearest nodes are added.
     *
     * @param k         number of edges to add per node
     * @param keepEdges keep or remove existing edges
     * @return a new, transformed gxl object
     */
    public GxlRoot kNearest(int k, boolean keepEdges) {
        gxl = gxlOriginal.shallowCopy();
        if (!keepEdges) {
            gxl.getGraph().getEdges().clear();
        }
        for (TransformNode tNode : tNodes) {
            // sort by distance
            ArrayList<Distance> distances = new ArrayList<>(tNode.distances);
            distances.sort(distComp);

            // start at index 1, because item 0 is self
            for (int i = 1; i < tNodes.size() && i < k + 1; i++) {
                addEdge(tNode, distances.get(i).tNode);
            }
        }
        return gxl;
    }

    /**
     * Transforms the graph. For each node, edges are added to k nodes furthest away from the already connected nodes.
     *
     * @param k         number of edges to add per node
     * @param mergeMode either {@link Transformer#MAX_AVERAGE} or {@link Transformer#MAX_MINIMUM}. Defines whether to
     *                  use the maximum of the average / minimal distances to determine the furthest node
     * @param keepEdges keep or remove existing edges
     * @return a new, transformed gxl object
     */
    public GxlRoot kSpan(int k, int mergeMode, boolean keepEdges) {
        gxl = gxlOriginal.shallowCopy();
        if (!keepEdges) {
            gxl.getGraph().getEdges().clear();
        }
        for (TransformNode tNode : tNodes) {
            List<Distance> distances = new ArrayList<>(tNode.distances);

            for (int i = 0; i < tNodes.size() && i < k; i++) {
                TransformNode max = Collections.max(distances, distComp).tNode;
                addEdge(tNode, max);
                mergeDistances(distances, max.distances, mergeMode);
            }
        }
        return gxl;
    }

    /**
     * Merges two distance lists into the first (similar to zipWith).
     *
     * @param distancesA the first list - used to store results
     * @param distancesB the second list
     * @param mergeMode  either {@link Transformer#MAX_AVERAGE} or {@link Transformer#MAX_MINIMUM}. Defines whether to
     *                   use the average or minimum to merge distances
     */
    private void mergeDistances(List<Distance> distancesA, List<Distance> distancesB, int mergeMode) {
        for (int j = 0; j < tNodes.size(); j++) {
            Distance a = distancesA.get(j);
            Distance b = distancesB.get(j);

            if (mergeMode == MAX_AVERAGE) {
                // take average but don't choose node twice: distance is 0 -> distance stays 0
                double dist = a.distance == 0 || b.distance == 0 ? 0 : (a.distance + b.distance) / 2;
                distancesA.set(j, new Distance(dist, a.tNode));
            } else {
                // minimal distance
                distancesA.set(j, a.distance > b.distance ? b : a);
            }
        }
    }

    /**
     * Adds a new edge to the graph.
     *
     * @param tNodeFrom start node
     * @param tNodeTo   end node
     */
    private void addEdge(TransformNode tNodeFrom, TransformNode tNodeTo) {
        Edge edge = new Edge();
        edge.setFrom(tNodeFrom.node);
        edge.setTo(tNodeTo.node);
        gxl.getGraph().getEdges().add(edge);
    }

}
