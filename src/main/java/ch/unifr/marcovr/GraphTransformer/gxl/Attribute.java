//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2018.02.23 at 05:10:51 PM CET
//


package ch.unifr.marcovr.GraphTransformer.gxl;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;choice>
 *           &lt;element name="bool" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *           &lt;element name="int" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *           &lt;element name="float" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *           &lt;element name="str" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="kind" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
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
    "bool",
    "_int",
    "_float",
    "str"
})
@XmlRootElement(name = "attr")
public class Attribute {

    @XmlElement(name = "attr")
    private List<Attribute> attributes;
    private Boolean bool;
    @XmlElement(name = "int")
    private Integer _int;
    @XmlElement(name = "float")
    private Float _float;
    private String str;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    private String id;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    private String name;
    @XmlAttribute(name = "kind")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    private String kind;

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
     * Gets the value of the bool property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isBool() {
        return bool;
    }

    /**
     * Sets the value of the bool property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setBool(Boolean value) {
        this.bool = value;
    }

    /**
     * Gets the value of the int property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getInt() {
        return _int;
    }

    /**
     * Sets the value of the int property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setInt(Integer value) {
        this._int = value;
    }

    /**
     * Gets the value of the float property.
     *
     * @return
     *     possible object is
     *     {@link Float }
     *
     */
    public Float getFloat() {
        return _float;
    }

    /**
     * Sets the value of the float property.
     *
     * @param value
     *     allowed object is
     *     {@link Float }
     *
     */
    public void setFloat(Float value) {
        this._float = value;
    }

    /**
     * Gets the value of the str property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getString() {
        return str;
    }

    /**
     * Sets the value of the str property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setString(String value) {
        this.str = value;
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
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the kind property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the value of the kind property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setKind(String value) {
        this.kind = value;
    }

}
