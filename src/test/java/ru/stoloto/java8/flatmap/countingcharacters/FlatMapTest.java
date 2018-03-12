package ru.stoloto.java8.flatmap.countingcharacters;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@RunWith(JUnitPlatform.class)
@Disabled
class FlatMapTest {

    private static String localPath = "src/test/java/ru/stdpr/fc/java8/flatmap/countingcharacters";
    private String fileName = "TomSawyer_";
    private int countOfCharactersInWord = 2;

    @Test
    void makeFlatMap() throws IOException {
        // http://introcs.cs.princeton.edu/java/data/TomSawyer.txt
        String path = getPath();

        Stream<String> stream1 = Files.lines(Paths.get(path + fileName + "01.txt"));
        Stream<String> stream2 = Files.lines(Paths.get(path + fileName + "02.txt"));
        Stream<String> stream3 = Files.lines(Paths.get(path + fileName + "03.txt"));
        Stream<String> stream4 = Files.lines(Paths.get(path + fileName + "04.txt"));

//        System.out.println("Stream 1 : " + stream1.count());
//        System.out.println("Stream 2 : " + stream2.count());
//        System.out.println("Stream 3 : " + stream3.count());
//        System.out.println("Stream 4 : " + stream4.count());

        Stream<Stream<String>> streamOfStreams =
                Stream.of(stream1, stream2, stream3, stream4);

//        System.out.println("# Кол-во стримов: " + streamOfStreams.count());

        Stream<String> streamOfLines =
                streamOfStreams.flatMap(Function.identity());

//        System.out.println("# Общее кол-во строк " + streamOfLines.count());

        Function<String, Stream<String>> lineSplitter =
                line -> Pattern.compile(" ").splitAsStream(line);

        Stream<String> streamOfWords =
                streamOfLines.flatMap(lineSplitter)
                        .map(word -> word.toLowerCase())
                        .filter(word -> word.length() == countOfCharactersInWord)
                        .distinct();

        System.err.println("# Кол-во слов, состоящих из "
                + countOfCharactersInWord + "-х букв = "
                + streamOfWords.count());
    }

    static String getPath() throws IOException {
        File resourcesDirectory = new File(localPath);
        @SuppressWarnings("На линукс другой разделитель")
        String absolutePath = resourcesDirectory.getAbsolutePath() + "\\";
        System.out.println("Текущая папка: \n" + absolutePath);
        return absolutePath;
    }


    @Disabled
    @Test
    void getBasePackagePath() throws IOException {
        File currentDirectory = new File(new File(".").getAbsolutePath());
        System.out.println(currentDirectory.getCanonicalPath());
        System.out.println(currentDirectory.getAbsolutePath());
    }

    @Disabled
    @Test
    void testIfPathIsWrite_then_OK() throws IOException {
        String path = getPath();
        System.out.println(path);
        assertNotNull(path);
        System.out.println((path + fileName + "01.txt"));
        assertNotNull(Paths.get(path + fileName + "01.txt"));
    }
}
