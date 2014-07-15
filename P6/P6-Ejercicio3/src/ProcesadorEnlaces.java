
public interface ProcesadorEnlaces {

    /**
     * Encolar el enlace
     * @param link
     * @throws Exception
     */
    void encolarEnlace(String link) throws Exception;

    /**
     * Devolver el número de enlaces visitados
     * @return
     */
    int cantidad();

    /**
     * comprobar si ya se ha visitado el enlace
     * @param link
     * @return
     */
    boolean visitado(String link);

    /**
     * Marcar el enlace como visitado
     * @param link
     */
    void anadirVisitado(String link);
}
