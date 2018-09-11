package ru.steklopod.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
public class MockService {

    private String filename = "archive.rar";

    @GetMapping(value = "/getArchive")
    public InputStreamResource index() throws FileNotFoundException {
        File file = new File(filename);
        return new InputStreamResource(new FileInputStream(file));
    }

}
