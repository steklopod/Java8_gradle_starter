package ru.steklopod;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.steklopod.entities.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@ExtendWith(RandomBeansExtension.class)
class RandomBeans {

    @Random
    private String anyString;

    @Random(excludes = {"burn", "name"})
    private User partiallyPopulatedDomainObject;

    @Random(type = String.class)
    private List<String> anyList;

    @Random(size = 5, type = String.class)
    private List<String> anyListOfSpecificSize;

    @Random(type = String.class)
    private Stream<String> anyStream;

    @Random(type = String.class)
    private Collection<String> anyCollection;

    @Random(size = 2, type = User.class)
    private List<User> anyFullyPopulatedDomainObjects;

    @Random(size = 2,
            type = User.class,
            excludes = {"burn"})
    private List<User> anyPartiallyPopulatedDomainObjects;


    @Test
    void canInjectAFullyPopulatedRandomObject(@Random User bufferOutTable) {

    }

    @Test
    void canInjectAPartiallyPopulatedRandomObject(
            @Random(excludes = {"burn", "name"})
                    User bufferOutTable) {
        System.err.println(bufferOutTable);
    }

    @Test
    void canInjectARandomListOfDefaultSize(@Random(type = String.class) List<String> anyList) {

    }

    @Test
    void canInjectARandomListOfSpecificSize(@Random(size = 5, type = String.class) List<String> anyListOfSpecificSize) {

    }

    @Test
    void canInjectARandomSet(@Random(type = String.class) Set<String> anySet) {

    }

    @Test
    void canInjectARandomStream(@Random(type = String.class) Stream<String> anyStream) {

    }

    @Test
    void canInjectARandomCollection(@Random(type = String.class) Collection<String> anyCollection) {

    }

    @Test
    void canInjectRandomFullyPopulatedDomainObjects(
            @Random(size = 2, type = User.class)
                    List<User> anyFullyPopulatedDomainObjects) {

    }

    @Test
    void canInjectRandomPartiallyPopulatedDomainObjects(
            @Random(
                    size = 2,
                    type = User.class,
                    excludes = {"burn"}
            )
                    List<User> anyPartiallyPopulatedDomainObjects) {

    }
} 
