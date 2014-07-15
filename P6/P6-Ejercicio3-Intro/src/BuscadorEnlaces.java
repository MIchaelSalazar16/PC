
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;


public class BuscadorEnlaces implements Runnable {

    private String url;
    private ProcesadorEnlaces procesadorEnlaces;
    /**
     * Usado para las estadisticas
     */
    private static final long t0 = System.nanoTime();

    public BuscadorEnlaces(String url, ProcesadorEnlaces procesador) {
        this.url = url;
        this.procesadorEnlaces = procesador;
    }

    @Override
    public void run() {
        rastrearPagina(url);
    }

    private void rastrearPagina(String url) {
        // si no lo hemos visitado ya
        if (!procesadorEnlaces.visitado(url)) {
            try {
                URL uriLink = new URL(url);
                Parser parser = new Parser(uriLink.openConnection());
                NodeList list = parser.extractAllNodesThatMatch(new NodeClassFilter(LinkTag.class));
                List<String> urls = new ArrayList<String>();

                 for (int i = 0; i < list.size(); i++) {
                    LinkTag extracted = (LinkTag) list.elementAt(i);

                    if (!extracted.getLink().isEmpty()
                            && !procesadorEnlaces.visitado(extracted.getLink())) {

                        urls.add(extracted.getLink());
                    }

                }
                // hemos visitado este URL
                procesadorEnlaces.anadirVisitado(url);

                if (procesadorEnlaces.cantidad() == 1500) {
                    System.out.println("Time to visit 1500 distinct links = " + (System.nanoTime() - t0));                   
                }

                for (String l : urls) {
                    procesadorEnlaces.encolarEnlace(l);
                }

             } catch (Exception e) {
                //ignorar todos los errores de momento
            }
        }
    }
}
