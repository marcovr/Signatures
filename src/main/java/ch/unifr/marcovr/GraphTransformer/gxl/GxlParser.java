package ch.unifr.marcovr.GraphTransformer.gxl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Provides methods to read and write .gxl files.
 */
public class GxlParser {

    /**
     * Reads a .gxl file and creates a GxlRoot object from it.
     *
     * @param filePath .gxl file to read
     * @return gxl object
     */
    public static GxlRoot readGXL(String filePath) throws JAXBException, IOException, XMLStreamException {
        try (FileInputStream gxlFile = new FileInputStream(filePath)) {

            // create reader which ignores DTD
            XMLInputFactory factory = XMLInputFactory.newFactory();
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            XMLStreamReader reader = factory.createXMLStreamReader(gxlFile);

            // create Unmarshaller and unmarshal .gxl file
            JAXBContext jaxb = JAXBContext.newInstance(GxlRoot.class);
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            return (GxlRoot) unmarshaller.unmarshal(reader);
        }
    }

    /**
     * Writes a GxlRoot object into a .gxl file.
     *
     * @param gxlRoot gxl object to write
     * @param filePath .gxl file to write to
     */
    public static void writeGXL(GxlRoot gxlRoot, String filePath) throws IOException, JAXBException {
        try (FileOutputStream gxlFile = new FileOutputStream(filePath)) {

            JAXBContext ctx = JAXBContext.newInstance(GxlRoot.class);
            Marshaller marshaller = ctx.createMarshaller();

            // set options to create formatted output & set custom xml header
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<!DOCTYPE gxl SYSTEM \"http://www.gupro.de/GXL/gxl-1.0.dtd\">");

            marshaller.marshal(gxlRoot, gxlFile);
        }
    }
}
