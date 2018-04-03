package ru.stoloto.service;

import org.springframework.core.io.Resource;

public interface ResourceLoader {
    Resource getResource(String location);
}