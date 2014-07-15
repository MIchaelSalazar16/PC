class MonitorFilosofos {

   private int numFils = 0;
   private int[] estado = null;
   private static final int
      THINKING = 0, HUNGRY = 1, EATING = 2 , STARVING = 3;
 
   private class Notificacion{}
   
   private Notificacion[] notas = null;

   public MonitorFilosofos(int numFils) {
      this.numFils = numFils;
      estado = new int[numFils];
      notas = new Notificacion[numFils];
      for (int i = 0; i < numFils; i++){
    	  estado[i] = THINKING;
    	  notas[i]= new Notificacion();
      }
   }

   private final int izquierda(int i) {
	return (numFils + i - 1) % numFils;
   }

   private final int derecha(int i) {
	return (i + 1) % numFils;
   }

   private void prueba(int k, boolean devolver) { 
	   
	if ( (estado[izquierda(k)]== HUNGRY || estado[izquierda(k)]==THINKING) && (estado[derecha(k)]==THINKING || estado[derecha(k)]==HUNGRY) && (estado[k]==HUNGRY || estado[k]==STARVING))
         estado[k] = EATING;
	if (devolver && (estado[derecha(k)]==EATING || estado[izquierda(k)]==EATING) && estado[k]==HUNGRY){
		estado[k]=STARVING;
		System.out.println("El filosofo: " + k + " entra en STARVING");
	}
   }

   public void takeForks(int i) { //Cambiar el bloque sincronize para que no se pueda ejecutar el prueba ya que puede provocar problemas.
      estado[i] = HUNGRY;
      prueba(i,false);
      if (estado[i] != EATING){
    	  try{
    		  synchronized (notas[i]) { notas[i].wait(); }
    	  } catch (InterruptedException e) {}
      }
   }

   public void putForks(int i) {
      estado[i] = THINKING;
      prueba(izquierda(i),true);
      prueba(derecha(i),true);
      synchronized (notas[izquierda(i)]){
    	  if (estado[izquierda(i)]== EATING)
    		  notas[izquierda(i)].notify();}
      synchronized (notas[derecha(i)]){
    	  if (estado[derecha(i)] == EATING)
    		  notas[derecha(i)].notify();}
   }
}