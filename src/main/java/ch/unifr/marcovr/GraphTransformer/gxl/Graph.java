//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2018.02.23 at 05:10:51 PM CET
//


package ch.unifr.marcovr.GraphTransformer.gxl;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="attr" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="node" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="edge" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="edgeids" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="edgemode" default="directed">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="directed"/>
 *             &lt;enumeration value="undirected"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "attributes",
        "nodes",
        "edges"
})
@XmlRootElement(name = "graph")
public class Graph {

    @XmlElement(name = "attr")
    private List<Attribute> attributes;
    @XmlElement(name = "node")
    private List<Node> nodes;
    @XmlElement(name = "edge")
    private List<Edge> edges;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;
    @XmlAttribute(name = "edgeids")
    private Boolean edgeids;
    @XmlAttribute(name = "edgemode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String edgemode;

    /**
     * Gets the value of the attributes property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributes property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributes().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     *
     *
     */
    public List<Attribute> getAttributes() {
        if (attributes == null) {
            attributes = new ArrayList<Attribute>();
        }
        return this.attributes;
    }

    /**
     * Gets the value of the nodes property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodes property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodes().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Node }
     *
     *
     */
    public List<Node> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<Node>();
        }
        return this.nodes;
    }

    /**
     * Gets the value of the edges property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the edges property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEdges().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Edge }
     *
     *
     */
    public List<Edge> getEdges() {
        if (edges == null) {
            edges = new ArrayList<Edge>();
        }
        return this.edges;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the edgeids property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean hasEdgeids() {
        if (edgeids == null) {
            return false;
        } else {
            return edgeids;
        }
    }

    /**
     * Sets the value of the edgeids property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setEdgeids(Boolean value) {
        this.edgeids = value;
    }

    /**
     * Gets the value of the edgemode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEdgemode() {
        if (edgemode == null) {
            return "directed";
        } else {
            return edgemode;
        }
    }

    /**
     * Sets the value of the edgemode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEdgemode(String value) {
        this.edgemode = value;
    }

    /**
     * Performs a shallow copy of the graph object.
     * Attributes, Nodes and Edges are only copied by reference.
     *
     * @return the copied {@link Graph}
     */
    public Graph shallowCopy() {
        Graph graph = new Graph();
        graph.id = id;
        graph.edgeids = edgeids;
        graph.edgemode = edgemode;
        graph.attributes = new ArrayList<>(attributes);
        graph.nodes = new ArrayList<>(nodes);
        graph.edges = new ArrayList<>(edges);

        return graph;
    }
}
