class ComidaFilosofos {

   public static void main(String[] args) {

   // n�mero de fil�sofos por defecto
   int numFilosofos = 5;

   MonitorFilosofos monitor = null;
   
   // parsing de los argumentos de la l�nea de comandos
   switch (args.length){
    			
	   case 2: 	if (args[1].equals("equ")){
			   		monitor = new MonitorFilosofos(numFilosofos);
					System.out.println("-> Opcion elegida: Equitativa");
					}
					else if (args[1].equals("efi")){
							monitor = new MonitorFilosofosEfi(numFilosofos);
						System.out.println("-> Opcion elegida: Equitativa y Eficiente");
					}
				else {
					System.out.println("Uso: ComidaFilosofos <numero_de_filosofos> <equ|efi>");
					System.exit(0);
				}
					
		  		try{
				   numFilosofos = Integer.parseInt(args[0]);
				} catch(NumberFormatException e){ // El par�metro no es un entero
				   System.out.println("Uso: ComidaFilosofos <numero_de_filosofos> <equ|efi>");
				   System.exit(0);
				}
				
		  		break;
		  		
		default: System.out.println("Uso: ComidaFilosofos <numero_de_filosofos> <equ|efi>");
				break;
		   
   }
   
   System.out.println("-> Filosofos involucrados: "+ numFilosofos);
   
   // crear los fil�sofos y arrancar sus hilos
   for (int i = 0; i < numFilosofos; i++)
      new Filosofo(i, monitor).start();
   System.out.println("Todos los hilos han sido arrancados");

   }

}