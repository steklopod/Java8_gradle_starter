Проект для переноса списка клиентов из реплика БД BetConstruct

#### СБОРКА:
> A. Пароль и логин для БД проиписывается в папке ...\user-rebase\src\main\resources , в файле application.yml.

> Б. Необходимо, чтобы был установлен Gradle, в коммандной строке вводятся команды:

1. `cd C:\Users\steklopod\...\user-app`

2. `gradle bootJar`

3. `cd build/libs`

4. `java -jar user-app-1.0.jar`

-------------------------------------
####  ЗАПУСК исполняемого jar:

`java -jar user-app-1.0.jar`


-Xms512m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=128m


