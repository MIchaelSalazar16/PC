@echo off

echo Iniciando P7-JavaRMI : Ejercicio 5 ...

echo Preparando directorios ...
erase C:\JavaRMI\bin
xcopy C:\hlocal\PC\P7-JavaRMI\P7-Ejercicio5\src\chats C:\JavaRMI\chats	/Y /R /Q

echo Compilando proyecto ...
cd C:\JavaRMI\chats
javac -d C:\JavaRMI\bin ChatBasicoServidor.java Servidor.java ChatBasicoCliente.java Cliente.java Logger.java

echo Iniciando ejecucion ... 
cd C:\JavaRMI\bin

echo Iniciando Registro RMI...
start rmiregistry

echo Iniciando Servidor de chats...
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Servidor
pause

echo Iniciando Logger de chat...
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Logger
pause

echo Iniciando 5 clientes de chat automaticos...
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Cliente 1
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Cliente 2
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Cliente 3
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Cliente 4
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Cliente 5
