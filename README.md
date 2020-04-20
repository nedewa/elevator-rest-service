# Getting Started

## How To Buid & Run

* Install & configure JDK 8
* Install Maven
```sh
> git clone https://path_of_this_project
> cd elevator-rest-service/
> ./mvnw 
> ./mvnw spring-boot:run
Tip: When run mvnw and see the similar issue: 
Error: Could not find or load main class org.apache.maven.wrapper.MavenWrapperMain
Please run the command: mvn -N io.takari:maven:wrapper
You can refer the following blog for details, I can`t use VPN today, 
so can`t not search with Google. But this Chinese blog can help to fix it as well.
(https://blog.csdn.net/blueheart20/article/details/51601441?utm_source=blogxgwz0)
```
* You can also import the code straight into your IDE:
```
Spring Tool Suite (STS)
IntelliJ IDEA
```
## Other Tips
* I mainly use MacOS, if you are using Windows, please contact with me. The environment is quite different.
* When you run mvnm the first time, it will download all the dependencies, so please wait. Today I have to use my personal Windows system. It`s weird in Windows to run mvnw.cmd, there is no any response and message for 1 minutes at least.
