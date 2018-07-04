package ch.unifr.marcovr.GraphTransformer.transformation;

import ch.unifr.marcovr.GraphTransformer.gxl.Node;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple class representing a node, its location and its distances to other nodes.
 */
public class TransformNode {

    public final Node node;
    public final Point2D location;
    public final List<Distance> distances;

    /**
     * Creates a new TransformNode from a gxl node. Creates an empty distances list.
     *
     * @param node the gxl node to represent
     */
    public TransformNode(Node node) {
        this.node = node;
        location = node.getLocation();
        distances = new ArrayList<>();
    }

}
