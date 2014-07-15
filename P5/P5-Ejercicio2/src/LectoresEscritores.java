public class LectoresEscritores {

	static int numLectores = 4; 
	static int numEscritores = 2; 

	public static void main(String[] argv) {
 
		BaseDatos BD = new BaseDatos();
		ReentranteEscritura monitor = new ReentranteEscritura();

		try {
			for (int i = 1; i <= numLectores; ++i) 
				new LectorReentrante(monitor, BD, "Lector"+i).start();
			for (int i = 1; i <= numEscritores; ++i) 
				new EscritorReentrante(monitor, BD, "Escritor"+i).start();
		} catch (Exception e) {System.out.println("Exception: " + e);}
	}
	
}





