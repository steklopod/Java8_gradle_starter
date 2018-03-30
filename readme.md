Проект для переноса списка клиентов из реплика БД BetConstruct

#### СБОРКА:
> A. Пароль и логин для БД проиписывается в папке ...\user-rebase\src\main\resources , в файле application.yml.

> Б. Необходимо, чтобы был установлен Gradle, в коммандной строке вводятся команды:

1: 
`cd C:\Users\d.koltovich\Desktop\projects\gamebet\user-rebase
`

2: `gradle bootJar`

3. `cd build/libs`

4. ` java -jar user-rebase-SNAPSHOT.jar`

-------------------------------------
####  ЗАПУСК исполняемого jar:

`java -jar user-rebase-SNAPSHOT.jar`

Запуск с временной зоной:

java -Duser.timezone=UTC  -jar build/libs/user-rebase.jar


