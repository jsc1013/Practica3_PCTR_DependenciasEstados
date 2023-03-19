package src.p03.c01;

import java.util.Enumeration;
import java.util.Hashtable;

public class Parque implements IParque{


	private static final int MAX_PERSONAS = 50;
	private int contadorPersonasTotales;
	private Hashtable<String, Integer> contadoresPersonasPuerta;
	
	
	public Parque() {
		contadorPersonasTotales = 0;
		contadoresPersonasPuerta = new Hashtable<String, Integer>();
	}


	// 
	// Método entrarAlParque
	//
	@Override
	public synchronized void entrarAlParque(String puerta){	
		
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null){
			contadoresPersonasPuerta.put(puerta, 0);
		}
		
		// Antes de dejar entrar hay que hacer la comprobaci�n
		comprobarAntesDeEntrar();	
		
		// Aumentamos el contador total y el individual
		contadorPersonasTotales++;		
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta)+1);
		
		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Entrada");
		
		// Hacemos las comprobaciones del invariante
		checkInvariante();

		// Despertamos el resto de hilos
		this.notifyAll();
	}
	
	// 
	// Método salirDelParque
	//
	@Override
	public synchronized void salirDelParque(String puerta) {
		// Si no hay entradas por esa puerta, inicializamos
		if (contadoresPersonasPuerta.get(puerta) == null) {
			contadoresPersonasPuerta.put(puerta, 0);
		}

		// Antes de dejar salir hay que hacer la comprobación
		comprobarAntesDeSalir();

		// Disminuimos el contador total y el individual
		contadorPersonasTotales--;
		contadoresPersonasPuerta.put(puerta, contadoresPersonasPuerta.get(puerta) - 1);

		// Imprimimos el estado del parque
		imprimirInfo(puerta, "Salida");

		// Hacemos las comprobaciones del invariante
		checkInvariante();
		
		// Despertamos el resto de hilos
		this.notifyAll();
	}
	
	private void imprimirInfo (String puerta, String movimiento){
		System.out.println(movimiento + " por puerta " + puerta);
		System.out.println("--> Personas en el parque " + contadorPersonasTotales); //+ " tiempo medio de estancia: "  + tmedio);
		
		// Iteramos por todas las puertas e imprimimos sus entradas
		for(String p: contadoresPersonasPuerta.keySet()){
			System.out.println("----> Por puerta " + p + " " + contadoresPersonasPuerta.get(p));
		}
		System.out.println(" ");
	}
	
	private int sumarContadoresPuerta() {
		int sumaContadoresPuerta = 0;
			Enumeration<Integer> iterPuertas = contadoresPersonasPuerta.elements();
			while (iterPuertas.hasMoreElements()) {
				sumaContadoresPuerta += iterPuertas.nextElement();
			}
		return sumaContadoresPuerta;
	}
	
	protected void checkInvariante() {
		assert sumarContadoresPuerta() == contadorPersonasTotales : "INV: La suma de contadores de las puertas debe ser igual al valor del contador del parte";
		assert sumarContadoresPuerta() <= MAX_PERSONAS : "Se ha llegado al m�ximo de personas";
		assert sumarContadoresPuerta() >= 0 : "No quedan personas en el parque";
		
		
		
	}

	protected void comprobarAntesDeEntrar(){
		if (contadorPersonasTotales >= MAX_PERSONAS) {
			try {
				this.wait();
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
				ex.printStackTrace();
			}
		}
	}

	protected void comprobarAntesDeSalir(){
		while (contadorPersonasTotales <= 0) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}


}
