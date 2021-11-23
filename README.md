# Project generation
- Generated with spring initializer https://start.spring.io/
  - Project: Maven
  - Language: Java
  - Spring Boot: 2.5.6
  - Project Metadata  
    - Group: com.crowdstreet.parker
    - Artifact: backend
    - Name: backend
    - Description: CrowdStreet Test - Back End
    - Package Name: com.crowdstreet.parker.backend
  - Dependencies
    - H2 Database
    - Rest Repositories

- Based on guide: https://spring.io/guides/gs/rest-service/#scratch

# Compile
- java.exe -Dmaven.multiModuleProjectDirectory=C:\Users\steph\IdeaProjects\backend "-Dmaven.home=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.1.2\plugins\maven\lib\maven3" "-Dclassworlds.conf=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.1.2\plugins\maven\lib\maven3\bin\m2.conf" "-Dmaven.ext.class.path=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.1.2\plugins\maven\lib\maven-event-listener.jar" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.1.2\lib\idea_rt.jar=61559:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.1.2\bin" -Dfile.encoding=UTF-8 -classpath "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.1.2\plugins\maven\lib\maven3\boot\plexus-classworlds-2.6.0.jar;C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2021.1.2\plugins\maven\lib\maven3\boot\plexus-classworlds.license" org.codehaus.classworlds.Launcher -Didea.version=2021.1.2 install


# Project Execution  
- Execute with parameters: spring-boot:run