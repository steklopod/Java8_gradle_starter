### Проект-стартер для Java 8 проекта. 

**5 ветвей** с разными настройками, в том числе конфигурация подключения к 2-ум БД (`MSSQL`, `MariaDB`).

В одном из бранчей находится пример работы с 2-мя БД: в качестве системы отправителя используется `MSSQL` server, а в 
качестве системы получателя приведен пример работы с БД типом `MariaDB`. Унифицировать работу с данными позволяет 
[Object-Relationa (ORM)](https://ru.wikipedia.org/wiki/ORM), а точнее его реализация [Java Persistence (JPA)](https://ru.wikipedia.org/wiki/Java_Persistence_API) 
от компании `Pivotal`, одной из ее дочерних компаний - [Spring](http://projects.spring.io/spring-data/). 

В данном проекте находятся скелетоны для быстрого стартк на Spring Data/Web.

---

### REST-сервер, отправляющий .rar-архив из папки, где находится jar

> Сборка

* Пароль/логин/port и др. конфигурации проиписываются в файле `application.yml`, который находится в папке `...src\main\resources`.
[Подробнее о формате YAML](https://ru.wikipedia.org/wiki/YAML)

* Необходимо, чтобы был установлен [Gradle](https://gradle.org/install/), в коммандной строке вводится:

```jshelllanguage
     cd C:\Users\...\Java8_gradle_starter
    
     gradle bootJar
    
```

После построения jar-файла по пути `build/libs` будет создан исполняемый jar-файл `server-1.0.jar`.

> Запуск

Необходимо через командную строку зайти в каталог с jar-файлом (`build/libs`), поместить в этот же каталог `.rar-архив` с именем

`archive.rar` и выполнить след. команду:

```jshelllanguage

    cd build/libs
        
    java -jar server-1.0.jar
```

Чтобы скачать архив необходимо через браузер зайти на адрес `localhost:8085/getArchive`

___

#### Gradle Versions Plugin - Актуальные версии библиотек

Чтобы узнать актуальность версий с помощью плагина [Gradle Versions Plugin](https://github.com/ben-manes/gradle-versions-plugin) 
библиотек необходимо запустить таску gradle: `help` -> `dependencyUpdates`

#### Чтобы убить процесс на порту 8080 (Windows):
```
1. netstat -ano | findstr 8085
2. taskkill /pid @НОМЕР_ПОРТА@ /F
``` 

