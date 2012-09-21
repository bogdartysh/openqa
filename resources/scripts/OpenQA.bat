@echo off
rem - Runs OpenEphyra in command line mode.

rem - The '-server' option of the Java VM improves the runtime of Ephyra.
rem - We recommend using 'java -server' if your VM supports this option.

java -cp %classpath%;OpenQa.jar; -Xms512m -Xmx1024m info.ephyra.OpenEphyra
