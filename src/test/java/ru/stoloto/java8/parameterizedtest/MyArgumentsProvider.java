package ru.stoloto.java8.parameterizedtest;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

class MyArgumentsProvider implements ArgumentsProvider {

   @Override
   public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
       return Stream.of("foo", "bar").map(Arguments::of);
   }
}