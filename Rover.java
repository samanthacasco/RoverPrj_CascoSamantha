import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Rover {
    private String nombrePropio;
    private String codigoRover;
    private double potenciaInicial;
    private double potenciaDisponible;
    private int posicionInicialX;
    private int posicionInicialY;
    private int posicionActualX;
    private int posicionActualY;
    private int cantidadRecargasRealizadas;
    private int contadorDetecciones;
    private ArrayList<ArrayList<String>> mandatosExitosos;
    private ArrayList<ArrayList<String>> mandatosFallidos;
    private double costoMovimiento;
    private double costoDeteccion;
    private int recargasMaximas;
    private static int cantidadRoversCreados = 0;
    private static ArrayList<Rover> roversCreados = new ArrayList<>();
    
    /**
     * Construye un Rover con el nombre indicado y la potencia por omisión
     * de 100 unidades de aleación.
     *
     * @param nombrePropioP el nombre de pila del Rover.
     */
    public Rover(String nombrePropioP) {
        this(nombrePropioP, 100.0);
    }
    
    /**
     * Construye un Rover con el nombre y la cantidad de potencia indicados.
     * Si la potencia indicada no es mayor que cero, se asigna cero.
     *
     * @param nombrePropioP el nombre de pila del Rover.
     * @param potencia las unidades de aleación con las que inicia el Rover.
     */
    public Rover(String nombrePropioP, double potencia) {
        nombrePropio = nombrePropioP;
        if (potencia > 0) {
            potenciaInicial = potencia;
        } else {
            potenciaInicial = 0;
        }
        potenciaDisponible = potenciaInicial;
        posicionInicialX = 0;
        posicionInicialY = 0;
        posicionActualX = posicionInicialX;
        posicionActualY = posicionInicialY;
        cantidadRecargasRealizadas = 0;
        contadorDetecciones = 0;
        mandatosExitosos = new ArrayList<>();
        mandatosFallidos = new ArrayList<>();
        costoMovimiento = 0.50;
        costoDeteccion = 0.25;
        recargasMaximas = 5;
        cantidadRoversCreados = cantidadRoversCreados + 1;
        codigoRover = "RVR-" + cantidadRoversCreados;
        roversCreados.add(this);
    }
    
    /**
     * Desplaza el Rover una posición hacia arriba, aumentando en 1 la coordenada y.
     */
    public void moverArriba() {
        desplazar(0, 1, "Desplazamiento Arriba");
    }
    
    /**
     * Desplaza el Rover una posición hacia abajo, disminuyendo en 1 la coordenada y.
     */
    public void moverAbajo() {
        desplazar(0, -1, "Desplazamiento Abajo");
    }
    
    /**
     * Desplaza el Rover una posición hacia la derecha, aumentando en 1 la coordenada x.
     */
    public void moverDerecha() {
        desplazar(1, 0, "Desplazamiento Derecha");
    }
    
    /**
     * Desplaza el Rover una posición hacia la izquierda, disminuyendo en 1 la coordenada x.
     */
    public void moverIzquierda() {
        desplazar(-1, 0, "Desplazamiento Izquierda");
    }
    
    private void desplazar(int deltaX, int deltaY, String tipoMandato) {
        if (validarPotenciaActual()) {
            if (!detectarFuga()) {
                posicionActualX = posicionActualX + deltaX;
                posicionActualY = posicionActualY + deltaY;
                potenciaDisponible = potenciaDisponible - costoMovimiento;
                registrarMandato(tipoMandato, "Posible");
            } else {
                registrarMandato(tipoMandato, "No posible: fuga detectada");
            }
        } else {
            registrarMandato(tipoMandato, "No posible: potencia insuficiente");
        }
    }
    
    private boolean detectarFuga() {
        contadorDetecciones = contadorDetecciones + 1;
        potenciaDisponible = potenciaDisponible - costoDeteccion;
        Random aleatorio = new Random();
        return aleatorio.nextDouble() >= 0.5;
    }
    
    private boolean validarPotenciaActual() {
        double costoMinimo = costoMovimiento + costoDeteccion;
        return potenciaDisponible >= costoMinimo;
    }
    
    private boolean validarRecarga() {
        return cantidadRecargasRealizadas < recargasMaximas;
    }
    
    private String determinarFechaHoraActual() {
        Date fecha = new Date(System.currentTimeMillis());
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        return formatoFecha.format(fecha);
    }
    
    private void registrarMandato(String tipoMandato, String estatusMandato) {
        ArrayList<String> mandato = new ArrayList<>();
        mandato.add(determinarFechaHoraActual());
        mandato.add(tipoMandato);
        mandato.add(estatusMandato);
        if (estatusMandato.equals("Posible")) {
            mandatosExitosos.add(mandato);
        } else {
            mandatosFallidos.add(mandato);
        }
    }
    
    /**
     * Consulta la posición actual del Rover en el plano cartesiano.
     *
     * @return la posición actual en formato (x,y).
     */
    public String consultarPosicionActual() {
        return "Posición actual (x,y): " + posicionActualX + ", " + posicionActualY;
    }
    
    /**
     * Consulta la potencia disponible del Rover.
     *
     * @return las unidades de aleación disponibles.
     */
    public double getPotenciaDisponible() {
        return potenciaDisponible;
    }
    
    /**
     * Recarga unidades de potencia al Rover, siempre que no se haya alcanzado
     * el máximo de recargas permitidas.
     *
     * @param potencia las unidades de aleación que se desean recargar.
     */
    public void recargarUnidadesPotencia(double potencia) {
        if (validarRecarga()) {
            potenciaDisponible = potenciaDisponible + potencia;
            cantidadRecargasRealizadas = cantidadRecargasRealizadas + 1;
            registrarMandato("Recarga (" + potencia + ")", "Posible");
        } else {
            registrarMandato("Recarga (" + potencia + ")", "No posible: recargas agotadas");
        }
    }
    
    /**
     * Determinar el estado completo del Rover, incluyendo sus datos generales y el registro
     * de mandatos exitosos y fallidos.
     *
     * @return la ficha completa del Rover.
     */
    @Override
    public String toString() {
        String msg = "";
        msg += "-----Ficha del Rover-----\n";
        msg += "Código: " + codigoRover + "\n";
        msg += "Nombre: " + nombrePropio + "\n";
        msg += "Potencia inicial: " + String.format("%.2f", potenciaInicial) + "\n";
        msg += "Potencia disponible: " + String.format("%.2f", potenciaDisponible) + "\n";
        msg += "Recargas disponibles: " + (recargasMaximas - cantidadRecargasRealizadas) + "\n";
        msg += "Detecciones realizadas: " + contadorDetecciones + "\n";
        msg += "Posición inicial: (" + posicionInicialX + "," + posicionInicialY + ")\n";
        msg += "Posición actual: (" + posicionActualX + "," + posicionActualY + ")\n";
        msg += "--------------------------\n\n";
        msg += "----Mandatos Exitosos----\n";
        msg += construirListaMandatos(mandatosExitosos);
        msg += "\n---- Mandatos Fallidos ----\n";
        msg += construirListaMandatos(mandatosFallidos);
        return msg;
    }
    
    /**
     * Determina la cantidad de Rovers que han sido creados.
     *
     * @return el total de Rovers creados.
     */
    public static int getCantidadRoversCreados() {
        return cantidadRoversCreados;
    }

    /**
     * Determina la información completa de todos los Rovers creados.
     *
     * @return la ficha de cada uno de los Rovers creados.
     */
    public static String consultarTodosLosRovers() {
        String msg = "Cantidad de Rovers creados: " + cantidadRoversCreados + "\n\n";
        for (int i = 0; i < roversCreados.size(); i++) {
            msg += roversCreados.get(i).toString() + "\n";
        }
        return msg;
    }
    
    private String construirListaMandatos(ArrayList<ArrayList<String>> mandatos) {
        String msg = "";
        if (mandatos.isEmpty()) {
            msg += "  (sin registros)\n";
        }
        for (int i = 0; i < mandatos.size(); i++) {
            ArrayList<String> mandato = mandatos.get(i);
            msg += "  " + (i + 1) + ". " + mandato.get(0) + " | " + mandato.get(1)
                    + " | " + mandato.get(2) + "\n";
        }
        return msg;
    }
}
