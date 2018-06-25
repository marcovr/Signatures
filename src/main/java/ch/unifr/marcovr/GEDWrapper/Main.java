package ch.unifr.marcovr.GEDWrapper;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    private static Path GEDJar;
    private static Path defaultProp;
    private static Path prop;
    private static Path ref;
    private static Path sig;
    private static Path dataDir;
    private static Path res;
    private static Path out;
    private static Path tmpDir;

    private static List<Path> files;
    private static List<Path> references;

    private static boolean debug;
    private static boolean reorder;
    private static boolean verification;

    /**
     * Main entry point for GEDWrapper.
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        List<Integer> refIndexes = null;
        out = Paths.get("out.ged");

        // extract options
        GetOpt g;
        try {
            g = new GetOpt(args, "r:R:o:fdv");
        } catch (GetOpt.OptException e) {
            usage();
            return;
        }

        // handle options
        for (GetOpt.Option option : g.getOpts()) {
            switch (option.opt) {
                case "-r":
                    int n = Integer.parseInt(option.arg);
                    refIndexes = IntStream.range(0, n).boxed().collect(Collectors.toList());
                    break;
                case "-R":
                    refIndexes = IntStream.range(0, option.arg.length())
                            .filter(i -> option.arg.charAt(i) == '1')
                            .boxed().collect(Collectors.toList());
                    break;
                case "-o":
                    out = Paths.get(option.arg);
                    break;
                case "-f":
                    reorder = true;
                    break;
                case "-v":
                    verification = true;
                    break;
                case "-d":
                    debug = true;
                    break;
                default:
                    usage();
                    return;
            }
        }

        if (g.getArgs().isEmpty() || refIndexes == null) {
            usage();
            return;
        }

        init(g.getArgs(), refIndexes);
        if (debug) {
            System.out.println(tmpDir);
            System.out.println(files.stream().map(Util::baseName).collect(Collectors.toList()));
        }

        try {
            run();
        }
        finally {
            if (!debug) {
                Util.deleteDir(tmpDir);
            }
        }
    }

    // example: java -jar gedwrapper.jar -r 10 -f data
    private static void usage() {
        System.out.printf("Usage: gedwrapper (-r INT | -R FLAGS) [OPTIONS] INPUT...%n%n" +
                "-r INT      number indicating amount of reference signatures (0 to N)%n" +
                "-R FLAGS    flags specifying reference signatures (1 = reference, 0 = non r.)%n%n" +
                "OPTIONS:%n" +
                "-f          check file names for flags (f / g) to determine correct order.%n" +
                "            (signatures are rearranged such that genuine ones are first)%n" +
                "-v          verification mode. Faster, skips pure reference distances.%n" +
                "            (reference signatures are not compared against each other)%n%n" +
                "-o FILE     output file path. default is 'out.ged'%n%n" +
                "INPUT       directory containing graph files or list of graph files%n" +
                "   Notice:  all files need to be in the same directory and correctly ordered%n");
    }

    /**
     * Create working directory, build paths and create file lists.
     *
     * @param args arguments representing data source
     * @param refIndexes indexes indicating reference files
     */
    private static void init(List<String> args, List<Integer> refIndexes) throws IOException, URISyntaxException {
        // get location of this jar
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        Path source = Paths.get(new File(path).getParent());

        tmpDir = Files.createTempDirectory("ged");
        GEDJar = source.resolve("GED.jar");
        defaultProp = source.resolve("default.prop");
        prop = tmpDir.resolve("tmp.prop");
        ref = tmpDir.resolve("ref.cxl");
        sig = tmpDir.resolve("sig.cxl");
        res = tmpDir.resolve("res");

        Files.createDirectories(res);

        // get file list
        files = args.stream().map(arg -> Paths.get(arg)).collect(Collectors.toList());
        if (files.size() == 1 && Files.isDirectory(files.get(0))) {
            files = Files.list(files.get(0)).collect(Collectors.toList());
        }

        // rearrange file names such that genuine ones are first
        if (reorder) {
            List<Path> ordered = files.stream()
                    .filter(f -> Util.baseName(f).indexOf('g') >= 0)
                    .collect(Collectors.toList());
            ordered.addAll(files.stream()
                    .filter(f -> Util.baseName(f).indexOf('f') >= 0)
                    .collect(Collectors.toList()));
            if (ordered.size() < files.size()) {
                System.out.println("Not applying filter (-f) since some filenames don't contain a flag");
            }
            else {
                files = ordered;
            }
        }

        // get reference list from indexes
        references = refIndexes.stream().map(files::get).collect(Collectors.toList());

        // skip reference-reference comparisons
        if (verification) {
            files.removeAll(references);
        }

        // get data path
        dataDir = files.get(0).getParent();
        if (dataDir == null) {
            dataDir = Paths.get(".");
        }

        if (!Files.exists(GEDJar)) {
            System.err.println("GED library missing: " + GEDJar.toString());
            System.exit(1);
        }
    }

    /**
     * Set up and run GED.
     */
    private static void run() throws IOException, InterruptedException {
        createPropFile();
        createCxlFile(sig, files);
        createCxlFile(ref, references);

        exec();
        getResult();
    }

    /**
     * Write main configuration file for GED.
     */
    private static void createPropFile() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(defaultProp);
             BufferedWriter writer = Files.newBufferedWriter(prop)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("$PATHS")) {
                    String s = ref.getFileName().toString();
                    String t = sig.getFileName().toString();
                    String p = dataDir.toAbsolutePath().toString() + '/';
                    String r = res.toString() + '/';

                    if (Util.isWindows()) {
                        s = s.replace('\\', '/');
                        t = t.replace('\\', '/');
                        p = p.replace('\\', '/');
                        r = r.replace('\\', '/');
                    }

                    line = String.format("source=%s\ntarget=%s\npath=%s\nresult=%s", s, t, p, r);
                }
                writer.write(line);
                writer.write('\n');
            }
        }
    }

    /**
     * Write cxl configuration file for GED.
     *
     * @param path where to write file to
     * @param files list of files to include in cxl
     */
    private static void createCxlFile(Path path, List<Path> files) throws IOException {
        int count = files.size();
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("<?xml version=\"1.0\"?>\n<GraphCollection>\n<l1 count=\"" + count + "\">\n");
            for (Path file : files) {
                writer.write("<print file=\"" + file.getFileName() + "\" class=\"0001\"/>\n");
            }
            writer.write("</l1>\n</GraphCollection>\n");
        }
    }

    /**
     * Create output directory and move result file.
     */
    private static void getResult() throws IOException {
        Path resFile = res.resolve("tmp_ln.ged");
        if (!Files.exists(resFile)) {
            System.out.println("No output file found");
            return;
        }

        if (out.getParent() != null) {
            Files.createDirectories(out.getParent());
        }
        Files.move(resFile, out, StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Run GED. A new process is created to pass options and set the working directory.
     */
    private static void exec() throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = Paths.get(javaHome, "bin", "java").toString();
        String jarName = GEDJar.toString();
        String className = "algorithms.GraphMatching";
        String propName = prop.getFileName().toString();

        String[] args = new String[]{javaBin, "-Xms4096m", "-Xmx4096m", "-Xss8192k",
                "-XX:ParallelGCThreads=4", "-XX:ConcGCThreads=1", "-cp", jarName, className, propName};
        Util.runProcess(args, tmpDir);
    }

}
