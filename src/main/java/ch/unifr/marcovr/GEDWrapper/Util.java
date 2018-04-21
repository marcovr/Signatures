package ch.unifr.marcovr.GEDWrapper;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

class Util {

    /**
     * Recursively delete directory.
     *
     * @param dir directory to delete
     */
    static void deleteDir(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Check whether JVM runs on Windows.
     *
     * @return true if Windows, false otherwise
     */
    static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Create and run a new process.
     *
     * @param args executable and list of arguments to it
     * @param workingDir Path to working directory
     */
    static void runProcess(String[] args, Path workingDir) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(args);

        builder.directory(workingDir.toFile());
        builder.inheritIO();

        Process process = builder.start();
        process.waitFor();
    }

}
