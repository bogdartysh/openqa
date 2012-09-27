@echo off
rem - Runs OpenEphyra in command line mode.

rem - The '-server' option of the Java VM improves the runtime of Ephyra.
rem - We recommend using 'java -server' if your VM supports this option.

rem java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address="8000" -cp %classpath%;OpenQA-console.jar; -Xms512m -Xmx1024m org.openqa.console.OpenEphyra
java -cp %classpath%;OpenQA-console.jar; -Xms512m -Xmx1024m org.openqa.console.OpenEphyra