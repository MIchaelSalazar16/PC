class ComidaFilosofos {

   public static void main(String[] args) {

   // n�mero de fil�sofos por defecto
   int numFilosofos = 5;

   // parsing de los argumentos de la l�nea de comandos
   if (args.length > 1)
	System.out.println("Uso: ComidaFilosofos <numero_de_filosofos>");
   else if (args.length == 1)
	try{
	   numFilosofos = Integer.parseInt(args[0]);
	} catch(NumberFormatException e){ // El par�metro no es un entero
	   System.out.println("Uso: ComidaFilosofos <numero_de_filosofos>");
	   System.exit(0);
	}
   
   // crear el objeto MonitorPhils
   MonitorFilosofos monitor = new MonitorFilosofos(numFilosofos);
	
   // crear los fil�sofos y arrancar sus hilos
   for (int i = 0; i < numFilosofos; i++)
      new Filosofo(i, monitor).start();
   System.out.println("Todos los hilos han sido arrancados");

   }

}