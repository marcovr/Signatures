package ch.unifr.marcovr.GraphTransformer;

import ch.unifr.marcovr.GraphTransformer.gxl.GxlParser;
import ch.unifr.marcovr.GraphTransformer.gxl.GxlRoot;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, XMLStreamException, JAXBException {
        GxlRoot gxl = GxlParser.readGXL("0001_f_001.gxl");
        GxlParser.writeGXL(gxl, "out.gxl");
    }

}
