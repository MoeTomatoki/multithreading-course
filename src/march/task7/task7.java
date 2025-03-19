package march.tasks7;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class task7 {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Использование: java MultiThreadedCopy <исходный каталог> <целевой каталог>");
            System.exit(1);
        }

        Path sourceDir = Paths.get(args[0]);
        Path targetDir = Paths.get(args[1]);

        if (!Files.exists(sourceDir)) {
            System.err.println("Исходный каталог не существует: " + sourceDir);
            System.exit(1);
        }

        try {
            copyDirectory(sourceDir, targetDir);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static void copyDirectory(Path sourceDir, Path targetDir) throws IOException {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetPath = targetDir.resolve(sourceDir.relativize(dir));
                if (!Files.exists(targetPath)) {
                    Files.createDirectory(targetPath);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = targetDir.resolve(sourceDir.relativize(file));
                executor.submit(() -> copyFile(file, targetFile));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void copyFile(Path sourceFile, Path targetFile) {
        try {
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Скопирован файл: " + sourceFile + " -> " + targetFile);
        } catch (IOException e) {
            System.err.println("Ошибка при копировании файла: " + sourceFile);
            e.printStackTrace();
        }
    }
}
