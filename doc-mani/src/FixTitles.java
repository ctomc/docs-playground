/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc., and individual contributors as indicated
 * by the @authors tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * @author Tomaz Cerar (c) 2017 Red Hat Inc.
 */
public class FixTitles {
    public static void main(String[] args) throws Exception {

        Files.walkFileTree(Paths.get(""), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.toString().startsWith(".")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!file.toString().endsWith(".adoc")) {
                    return FileVisitResult.CONTINUE;
                }
                //System.out.println("doc = " + file);
                List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                String title = lines.get(0).trim();
                String titleMarker = lines.get(1);
                //String title = Files.lines(doc, StandardCharsets.UTF_8).filter(s -> s.startsWith("== ")).findFirst().orElse("not found");
                if (title.startsWith("=")) {
                    title = title.substring(title.indexOf(" ") + 1);
                    System.out.println("corrected title: " + title);
                }
                if (titleMarker.startsWith("===")) {
                    System.out.println("Removed marker = " + title);
                    lines.remove(1);
                }
                lines.remove(0);
                lines.add(0, "= " + title);
                System.out.println("lines = " + lines.subList(0, 2));
                Files.write(file, lines, StandardCharsets.UTF_8);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
