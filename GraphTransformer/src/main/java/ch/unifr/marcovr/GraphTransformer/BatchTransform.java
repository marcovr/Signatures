package ch.unifr.marcovr.GraphTransformer;

import ch.unifr.marcovr.GraphTransformer.gxl.GxlParser;
import ch.unifr.marcovr.GraphTransformer.gxl.GxlRoot;
import ch.unifr.marcovr.GraphTransformer.transformation.Transformer;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

public class BatchTransform {

    /**
     * Transforms all files from source directory with all kinds of transformations into the target directory.
     *
     * @param args [source, target]
     */
    public static void transform(String[] args) throws IOException, XMLStreamException, JAXBException {
        if (args.length < 2) {
            usage();
            return;
        }

        String sourcePath = args[0];
        String targetPath = args[1];

        File[] files = new File(sourcePath).listFiles();
        if (files == null) {
            System.err.println("source directory not found: " + sourcePath);
            usage();
            return;
        }

        for (File file : files) {
            applyAll(file, targetPath);
        }
    }

    private static void usage() {
        System.out.printf("Usage: graphtransform batchtransform INPUT OUTPUT%n%n" +
                "INPUT:     path to the source directory. All its files will be transformed%n" +
                "OUTPUT:    path to the target directory%n");
    }

    private static void applyAll(File file, String target) throws JAXBException, IOException, XMLStreamException {
        GxlRoot gxl = GxlParser.readGXL(file.getAbsolutePath());
        Transformer transformer = new Transformer(gxl);
        String fileName = file.getName();

        System.out.println("processing " + fileName);

        for (int k = 1; k < 9; k++) {
            applyKNearest(transformer, fileName, target, k, true);
            applyKNearest(transformer, fileName, target, k, false);
            applyKSpan(transformer, fileName, target, k, Transformer.MAX_MINIMUM, true);
            applyKSpan(transformer, fileName, target, k, Transformer.MAX_AVERAGE, true);
            applyKSpan(transformer, fileName, target, k, Transformer.MAX_MINIMUM, false);
            applyKSpan(transformer, fileName, target, k, Transformer.MAX_AVERAGE, false);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void applyKNearest(Transformer t, String file, String target, int k, boolean keepEdges) throws IOException, JAXBException {
        String edges = keepEdges ? "keepEdges" : "removeEdges";
        String path = String.format("%s/kNearest_%s/%s/", target, edges, k);
        new File(path).mkdirs();
        GxlParser.writeGXL(t.kNearest(k, keepEdges), path + file);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void applyKSpan(Transformer t, String file, String target, int k, int mode, boolean keepEdges) throws IOException, JAXBException {
        String edges = keepEdges ? "keepEdges" : "removeEdges";
        String modeText = mode == Transformer.MAX_MINIMUM ? "minimum" : "average";
        String path = String.format("%s/kSpan_%s_%s/%s/", target, modeText ,edges, k);
        new File(path).mkdirs();
        GxlParser.writeGXL(t.kSpan(k, mode, keepEdges), path + file);
    }

}
