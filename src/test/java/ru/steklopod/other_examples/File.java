package ru.steklopod.other_examples;

import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SpringBootTest
@RunWith(JUnitPlatform.class)
@ExtendWith({SpringExtension.class, RandomBeansExtension.class})
class File {

    @Test
    void file() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(""))) {
            String joined = stream
                    .map(String::valueOf)
                    .filter(path -> !path.startsWith("."))
                    .sorted()
                    .collect(Collectors.joining("; "));
            System.err.println("Файлы: " + joined);
        }
    }

    @Test
    @Disabled
    void fileWhenJarBuild_Outside() {
        String filename = "file";
        Resource resource = new FileSystemResource(filename);
        try (InputStream is = resource.getInputStream()) {
            new BufferedReader(new InputStreamReader(is, "UTF-8")).lines()

//        File file = new File(MethodHandles
//                .lookup()
//                .lookupClass()
//                .getClassLoader().getResource(filename).getFile());
//        try (Stream<String> stream = Files.lines(file.toPath())) {
//            stream
                    .skip(1)
                    .forEach(System.err::println);

        } catch (NullPointerException | IOException e) {
            throw new RuntimeException("Ooops... Can't read " + filename);
        }
    }



}
