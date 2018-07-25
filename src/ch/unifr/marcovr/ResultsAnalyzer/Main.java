package ch.unifr.marcovr.ResultsAnalyzer;

import ch.unifr.marcovr.GraphTransformer.gxl.GxlParser;
import ch.unifr.marcovr.ResultsAnalyzer.UI.ResultsGUI;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final int N_USERS = 75;
    private static final int N_SIGS = 30;

    public static void main(String[] args) {
        Path gxlPath;

        if (args.length == 0) {
            gxlPath = Paths.get("D:\\Documents\\BA_offline\\gxl-D25");
        }
        else {
            gxlPath = Paths.get(args[0]);
        }

        if (!Files.isDirectory(gxlPath)) {
            System.err.println("please provide the path to the gxl-MCYT-75 files as first argument");
            System.exit(1);
        }


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        ResultsGUI gui = new ResultsGUI();
        gui.show();

        loadResults("/results_r10_g5.txt", gui);
        loadResults("/results_r5_g10.txt", gui);
        loadFiles(gxlPath, gui);
    }

    private static void loadResults(String file, ResultsGUI gui) {
        try (InputStream in = Main.class.getResourceAsStream(file)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            Stream<String[]> data = reader.lines().skip(2).map(l -> l.split("\t"));
            EER[][] eers = data.map(r -> {
                float original = Float.parseFloat(r[1]);
                return Arrays.stream(r).skip(1).map(x -> {
                    EER e = new EER();
                    e.eer = x;
                    e.status = Float.compare(Float.parseFloat(x), original);
                    return e;
                }).toArray(EER[]::new);
            }).toArray(EER[][]::new);
            gui.addData(eers);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFiles(Path gxlPath, ResultsGUI gui) {
        List<Path> graphFiles = new ArrayList<>();
        if (Files.isDirectory(gxlPath)) {
            try {
                graphFiles = Files.list(gxlPath).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<Path> finalGraphFiles = graphFiles;

        for (int i = 0; i < N_USERS; i++) {
            User user = new User();
            int n = N_SIGS * i;
            user.name = String.format("%04d", i + 1);
            user.signatures = finalGraphFiles.subList(n, n + N_SIGS).stream().map(f -> {
                try {
                    Signature sig = new Signature();
                    sig.name = f.getFileName().toString();
                    sig.gxl = GxlParser.readGXL(f.toAbsolutePath().toString());
                    return sig;
                } catch (JAXBException | XMLStreamException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());

            // genuine first
            List<Signature> forgeries = new ArrayList<>(user.signatures.subList(0, N_SIGS / 2));
            user.signatures.removeAll(forgeries);
            user.signatures.addAll(forgeries);

            gui.addUser(user);
            gui.setProgress(i * 100 / (N_USERS - 1));
        }
    }
}
