package ru.steklopod.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
public class RarProducer {

    private String rarFile = "archive.rar";

    //Метод для теста из IDE, возвращающий .rar-архив из того же каталога, где находится jar-файл
    @GetMapping(value = "/getRar", produces = "application/zip")
    public InputStreamResource getRarFromIntellij() throws FileNotFoundException {
        File file = new File( getClass().getClassLoader().getResource(rarFile).getPath());
        return new InputStreamResource(new FileInputStream(file));
    }

    //Метод, возвращающий .rar-архив из того же каталога, где находится jar-файл
    @GetMapping(value = "/getRar/{fileName}", produces = "application/zip")
    public FileSystemResource getRarFromJar(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        String filenameWhichWillBeSend  = "some_rar_file.rar";
        File file = new File(new ClassPathResource(fileName).getPath());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filenameWhichWillBeSend + "\"");
        return new FileSystemResource(file);
    }
}

