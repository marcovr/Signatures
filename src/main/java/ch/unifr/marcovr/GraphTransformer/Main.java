package ch.unifr.marcovr.GraphTransformer;

import ch.unifr.marcovr.GraphTransformer.gxl.GxlParser;
import ch.unifr.marcovr.GraphTransformer.gxl.GxlRoot;
import ch.unifr.marcovr.GraphTransformer.gxl.GxlVisualizer;
import ch.unifr.marcovr.GraphTransformer.transformation.Transformer;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    /**
     * Main entry point for GraphTransfomer. Handles arguments and executes commands.
     * @param args command line arguments
     */
    public static void main(String[] args) throws JAXBException, IOException, XMLStreamException {
        if (args.length == 0) {
            usage();
            return;
        }

        String command = args[0];
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (command) {
            case "transform":
                transform(subArgs);
                break;
            case "image":
                image(subArgs);
                break;
            case "show":
                show(subArgs);
                break;
            default:
                System.err.println("invalid command: " + command);
                usage();
        }
    }

    private static void usage() {
        System.out.printf("Usage: graphtransform COMMAND ARGS%n%n" +
                "COMMAND: transform, toimage or show%n" +
                "       - transform: applies transformation method on graph%n" +
                "       - toimage:   saves graph as image%n" +
                "       - show:      shows graph in a window%n" +
                "ARGS:    one or more arguments, depends on command%n");
    }

    private static void usageTransform() {
        System.out.printf("Usage: graphtransform transform INPUT OUTPUT KEEPEDGES METHOD K [MERGEMODE]%n%n" +
                "INPUT:     path to the input gxl file%n" +
                "OUTPUT:    path to the output gxl file%n" +
                "KEEPEDGES: keep existing edges: true or false%n" +
                "METHOD:    either knearest or kspan%n" +
                "         - knearest: for each node, edges to the K nearest nodes are added%n" +
                "         - kspan:    for each node, edges are added to K nodes furthest away%n" +
                "                     from the already connected nodes. requires MERGEMODE%n" +
                "K:         number of edges to add per node%n" +
                "MERGEMODE: either minimum or average%n" +
                "         - minimum:  use max of the minimal distances to get the furthest node%n" +
                "         - average:  use max of the average distances to get the furthest node%n");
    }

    private static void usageImage() {
        System.out.printf("Usage: graphtransform image INPUT OUTPUT [WIDTH HEIGHT]%n%n" +
                "INPUT:  path to the input gxl file%n" +
                "OUTPUT: path to the output image file%n" +
                "WIDTH:  image width in pixel%n" +
                "HEIGHT: image height in pixel%n");
    }

    private static void usageShow() {
        System.out.printf("Usage: graphtransform show INPUT%n%n" +
                "INPUT: path to the input gxl file%n");
    }

    private static void transform(String[] args) throws JAXBException, IOException, XMLStreamException {
        if (args.length < 5) {
            usageTransform();
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];
        String keepE = args[2];
        boolean keepEdges;
        switch (keepE) {
            case "true":
                keepEdges = true;
                break;
            case "false":
                keepEdges = false;
                break;
            default:
                System.err.println("invalid boolean value: " + keepE);
                usageTransform();
                return;
        }
        String method = args[3].toLowerCase();
        int k = Integer.parseInt(args[4]);

        Transformer transformer = new Transformer(GxlParser.readGXL(inputPath));
        GxlRoot gxlOut;

        switch (method) {
            case "knearest":
                gxlOut = transformer.kNearest(k, keepEdges);
                break;
            case "kspan":
                if (args.length < 6) {
                    System.err.println("method kspan requires a merge mode argument");
                    usageTransform();
                    return;
                }
                String mode = args[5].toLowerCase();
                int mergeMode;

                switch (mode) {
                    case "minimum":
                        mergeMode = Transformer.MAX_MINIMUM;
                        break;
                    case "average":
                        mergeMode = Transformer.MAX_AVERAGE;
                        break;
                    default:
                        System.err.println("invalid merge mode: " + mode);
                        usageTransform();
                        return;
                }

                gxlOut = transformer.kSpan(k, mergeMode, keepEdges);
                break;
            default:
                System.err.println("invalid transform method: " + method);
                usageTransform();
                return;
        }
        GxlParser.writeGXL(gxlOut, outputPath);
    }

    private static void image(String[] args) throws JAXBException, IOException, XMLStreamException {
        if (args.length < 2) {
            usageImage();
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];
        GxlRoot gxl = GxlParser.readGXL(inputPath);

        if (args.length >= 4) {
            int width = Integer.parseInt(args[2]);
            int height = Integer.parseInt(args[3]);
            GxlVisualizer.toImage(gxl, width, height, outputPath);
        }
        else {
            GxlVisualizer.toImage(gxl, outputPath);
        }
    }

    private static void show(String[] args) throws JAXBException, IOException, XMLStreamException {
        if (args.length == 0) {
            usageShow();
            return;
        }

        String inputPath = args[0];
        GxlRoot gxl = GxlParser.readGXL(inputPath);
        GxlVisualizer.show(gxl, inputPath);
    }

}
