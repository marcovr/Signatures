package ch.unifr.marcovr.GraphTransformer;

import ch.unifr.marcovr.GraphTransformer.gxl.GxlParser;
import ch.unifr.marcovr.GraphTransformer.gxl.GxlRoot;
import ch.unifr.marcovr.GraphTransformer.gxl.GxlVisualizer;
import ch.unifr.marcovr.GraphTransformer.transformation.Transformer;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, XMLStreamException, JAXBException {
        GxlRoot gxl = GxlParser.readGXL("0001_f_001.gxl");
        Transformer g = new Transformer(gxl);
        GxlVisualizer.show(g.kNearest(2, false), "kNearest");
        GxlVisualizer.show(g.kSpan(2, Transformer.MAX_MINIMUM, false), "kSpan");
        GxlVisualizer.show(gxl, "Original");
        //GxlParser.writeGXL(gxl, "out.gxl");
    }

}
