### Проект-стартер для Java 8 проекта. 

4 ветви с разными настройками, в том числе конфигурация подключения к 2-ум БД (MSSQL, MariaDB).

В одном из бранчей находится пример работы с 2-мя БД: в качестве системы отправителя используется MSSQL server, а в качестве системы получателя приведен пример работы с БД типом MariaDB. Унифицировать работу с данными позволяет Object Relationship Mapping, а точнее его реализация JPA от компании Pivotal, а точнее одной из ее дочерних компаний Spring. 

В данном примере показана работа с фреймворком Spring Data.

+ Gradle

+ Postgres

+ Spring Boot 2.0

---

#### СБОРКА:
> A. Пароль и логин для БД проиписывается в папке ...\user-rebase\src\main\resources ,
в файле application.yml.

> Б. Необходимо, чтобы был установлен Gradle, в коммандной строке вводятся команды:

```groovy
1. `cd C:\Users\steklopod\...\user-app`

2. `gradle bootJar`

3. `cd build/libs`

4. `java -jar user-app-1.0.jar`
```

В папке `..\starter-post\src\test\resources` находятся **настройки Idea** `intellij_settings.jar` и используемые плагины.
[Инструкцйия по установке плагинов](https://github.com/shiraji/plugin-importer-exporter) `plugins.json`.

// TODO - Чтобы узнать актуальные версии библиотек необходимо запустить таску gradle: ...
