package ch.unifr.marcovr.GraphTransformer.transformation;

/**
 * Simple class representing a distance to a node.
 */
public class Distance {

    public double distance;
    public TransformNode tNode;

    /**
     * Creates a new Distance.
     *
     * @param distance the distance to {@code tNode}
     * @param tNode    the "target" of the distance
     */
    public Distance(double distance, TransformNode tNode) {
        this.distance = distance;
        this.tNode = tNode;
    }

}
