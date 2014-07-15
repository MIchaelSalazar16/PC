
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ForkJoinPool;

public class RastreadorWeb7 implements ProcesadorEnlaces {

	// ORIGINAL:
	private final Collection<String> enlacesVisitados = Collections.synchronizedSet(new HashSet<String>());
    
 	// ALTERNATIVA 1: Synchronized Set
	// TO DO: ver si el rendimiento sería mejor con algo de java.util.concurrent tal como:
	//Set<String> enlacesVisitados = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

 	// ALTERNATIVA 2: Synchronized List
	// Para probar el tiempo que tarda en visitar enlaces sin tomar en cuenta si ya los ha visitado,
	// cambie el "synchronizedSet" por un "synchronizedList" y cambie el método "visitado" para que
	// devuelva siempre falso.
    // private final Collection<String> enlacesVisitados = Collections.synchronizedList(new ArrayList<String>());
    
	// ALTERNATIVA 3: ConcurrentLinkedDeque
	// TO DO: ver si el rendimiento sería mejor con algo de java.util.concurrent tal como ConcurrentLinkedList 
 	// ConcurrentLinkedDeque<String> enlacesVisitados = new ConcurrentLinkedDeque<String>();

    private String url;
    
    //private ExecutorService execService;
    private ForkJoinPool forkJoinPool;
    
    public RastreadorWeb7(String inicioURL, int maxHilos) {
        this.url = inicioURL;
        //execService = Executors.newFixedThreadPool(maxHilos);
        forkJoinPool = new ForkJoinPool(maxHilos);
    }

    @Override @Deprecated
    public void encolarEnlace(String link) throws Exception {
		//execService.execute(new BuscadorEnlaces(link, this));
    }

    @Override
    public int cantidad() {
        return enlacesVisitados.size();
    }

    @Override
    public void anadirVisitado(String s) {
        enlacesVisitados.add(s);
    }

    @Override
    public boolean visitado(String s) {
        return enlacesVisitados.contains(s);
    }

    private void empezarRastreo() throws Exception {
		//execService.execute(new BuscadorEnlaces(this.url, this));
    	forkJoinPool.invoke(new BuscadorEnlacesAction(this.url, this));
    }

    /**
     * @param args los argumentos de la lÃ­nea de comandos (deberÃ­a pasar la URL)
     */
    public static void main(String[] args) throws Exception {
    	System.out.println("> Iniciando Rastreador Web 7 (FORK-JOIN)");
    	new RastreadorWeb7("http://informatica.ucm.es", 16).empezarRastreo();
        //new RastreadorWeb6("/*AQUI UNA URL CON BASTANTES ENLACES*/", 16).empezarRastreo();
    }
}
