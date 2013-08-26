set CP=./;lib/log4j-1.2.16.jar;lib/mina-core-2.0.4.jar;lib/slf4j-api-1.7.5.jar;libslf4j-log4j12-1.7.5.jar;
%JAVA_HOME%\bin\java -Xms128m -Xmx256m -Dfile.encoding=UTF-8 -classpath %CP% ClientModule