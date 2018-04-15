package ch.unifr.marcovr.GEDWrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static Path jar;
    private static Path defaultProp;
    private static Path prop;
    private static Path ref;
    private static Path sig;
    private static Path data;
    private static Path res;
    private static Path out;
    private static Path tmp;

    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        if (args.length < 3) {
            usage();
            return;
        }

        init(args[0], args[1]);
        try {
            List<String > references = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));
            run(references);
        }
        finally {
            Util.deleteDir(tmp);
        }
    }

    private static void usage() {
        System.out.printf("Usage: gedwrapper INPUT OUTPUT REF...%n%n" +
                "INPUT:  directory containing graphs%n" +
                "OUTPUT: output file%n" +
                "REF:    reference graphs%n");
    }

    private static void init(String input, String output) throws IOException, URISyntaxException {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        Path source = Paths.get(new File(path).getParent());

        tmp = Files.createTempDirectory("ged");
        jar = source.resolve("GED.jar");
        defaultProp = source.resolve("default.prop");
        prop = tmp.resolve("tmp.prop");
        ref = tmp.resolve("ref.cxl");
        sig = tmp.resolve("sig.cxl");
        data = Paths.get(input);
        res = tmp.resolve("res");
        out = Paths.get(output);

        Files.createDirectories(res);
    }

    private static void run(List<String> references) throws IOException, InterruptedException {
        Stream<String> fs = Files.list(data).map(p -> p.getFileName().toString());
        List<String> files = fs.collect(Collectors.toList());

        createPropFile();
        createConfFile(sig, files);
        createConfFile(ref, references);

        exec();
        getResult();
    }

    private static void createPropFile() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(defaultProp);
             BufferedWriter writer = Files.newBufferedWriter(prop)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals("$PATHS")) {
                    String s = ref.getFileName().toString();
                    String t = sig.getFileName().toString();
                    String p = data.toAbsolutePath().toString() + '/';
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

    private static void createConfFile(Path which, List<String> files) throws IOException {
        int count = files.size();
        try (BufferedWriter writer = Files.newBufferedWriter(which)) {
            writer.write("<?xml version=\"1.0\"?>\n<GraphCollection>\n<l1 count=\"" + count + "\">\n");
            for (String file : files) {
                file = new File(file).getName();
                writer.write("<print file=\"" + file + "\" class=\"0001\"/>\n");
            }
            writer.write("</l1>\n</GraphCollection>\n");
        }
    }

    private static void getResult() throws IOException {
        if (out.getParent() != null) {
            Files.createDirectories(out.getParent());
        }
        Path resFile = res.resolve("tmp_ln.ged");
        Files.move(resFile, out, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void exec() throws IOException, InterruptedException {
        String javaHome = System.getProperty("java.home");
        String javaBin = Paths.get(javaHome, "bin", "java").toString();
        String jarName = jar.toString();
        String className = "algorithms.GraphMatching";
        String propName = prop.getFileName().toString();

        String[] args = new String[]{javaBin, "-Xms4096m", "-Xmx4096m", "-Xss8192k", "-XX:ParallelGCThreads=4", "-XX:ConcGCThreads=1", "-cp", jarName, className, propName};
        Util.runProcess(args, tmp);
    }

}
