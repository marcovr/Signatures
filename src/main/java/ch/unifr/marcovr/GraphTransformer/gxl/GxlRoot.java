//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2018.02.21 um 09:00:49 PM CET 
//


package ch.unifr.marcovr.GraphTransformer.gxl;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="graph"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "graph"
})
@XmlRootElement(name = "gxl")
public class GxlRoot {

    @XmlElement(required = true)
    private Graph graph;

    /**
     * Ruft den Wert der graph-Eigenschaft ab.
     *
     * @return
     *     possible object is
     *     {@link Graph }
     *
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Legt den Wert der graph-Eigenschaft fest.
     *
     * @param value
     *     allowed object is
     *     {@link Graph }
     *
     */
    public void setGraph(Graph value) {
        this.graph = value;
    }

}
