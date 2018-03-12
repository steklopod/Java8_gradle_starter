package ru.steklopod.java8.mockito;

import java.util.UUID;

class MyMockClassForTest {

   public int getUniqueId() {
       String uniqueID = UUID.randomUUID().toString();
       int id = Integer.parseInt(uniqueID);
       return id;
   }

    public void testing(int i) {
        System.out.println("Вызвали метод testing(), " + i);
    }

    public void someMethod(String toPrint) {
        System.out.println("Вызвали метод someMethod(), " + toPrint);
    }
}
