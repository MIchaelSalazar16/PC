@echo off

echo Iniciando P7-JavaRMI : Ejercicio 1 ...

echo Preparando directorios ...
erase C:\JavaRMI\bin
xcopy C:\hlocal\PC\P7-JavaRMI\P7-Ejercicio1\chats C:\JavaRMI\chats	/Y /R /Q

echo Compilando proyecto ...
cd C:\JavaRMI\chats
javac -d C:\JavaRMI\bin ChatBasicoServidor.java Servidor.java ChatBasicoCliente.java Cliente.java

echo Iniciando ejecucion ... 
cd C:\JavaRMI\bin

echo Iniciando Registro RMI ...
start rmiregistry

echo Iniciando Servidor de chats ...
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Servidor
pause

echo Iniciando 2 clientes de chat ...
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Cliente
start java -classpath C:\JavaRMI\bin -Djava.rmi.server.codebase=file:C:\JavaRMI\bin/ chats.Cliente

