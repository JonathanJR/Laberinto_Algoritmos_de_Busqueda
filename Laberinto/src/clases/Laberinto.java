
package clases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Clase para obtener la solucion a un Laberinto en formato .txt mediante 4 tipos de algoritmos de busqueda:
 * - Busqueda en anchura, Busqueda en profundidad limitada, Busqueda Greedy Best First, Busqueda A*
 * @author Jonathan
 */

public class Laberinto {

	private Estado estado_inicial, estado_final;
	private char[][] matriz;
	private ArrayList<Estado> abiertos, cerrados, hijos;
	public int trata_repe, estados_visitados, estados_abiertos, estados_cerrados, pasos, iteraciones;
	private static BufferedReader b, bf;
	private char camino, muro;
	public boolean haySolucion = true;
	private Comparator<Estado> comp;

	/**
	 * Constructor de la clase
	 * @param f - Fichero del archivo .txt con el laberinto
	 * @param muro - Caracter de tipo char que nos indica los muros
	 * @param solucion - Caracter de tipo char para pintar la solucion
	 */

	public Laberinto(File f, char muro, char solucion) {
		this.matriz = ReadMatrizXY(f);
		this.estado_inicial = getEstadoInicial(matriz);
		this.estado_final = getEstadoFinal(matriz);
		this.abiertos = new ArrayList<>(); // Nodos abiertos
		this.cerrados = new ArrayList<>(); // Nodos ya visitados
		this.muro = muro;
		this.camino = solucion;
	}

	/**
	 * Metodo Busqueda en anchura. Cola FIFO
	 * Se elige un estado inicial y se exploran todos los vecinos de este nodo. 
	 * A continuación para cada uno de los vecinos se exploran sus respectivos vecinos 
	 * adyacentes, y así hasta que se recorra todo el árbol.
	 * @param estado_inicial - Objeto tipo Estado que nos indica el punto de partida 
	 * @param estado_final - Objeto tipo Estado que nos indica el punto final 
	 */

	public void busqueda_anchura(Estado estado_inicial, Estado estado_final) {
		abiertos.add(estado_inicial); // Añado como nodo abierto el punto de partida del laberinto
		Estado actual = abiertos.get(0); // Selecciono como punto actual el primero de los nodos abiertos (el punto de partida)
		trata_repe = 1; // Variable para indicar al switch como tiene que tratar los repetidos en el switch del metodo tratar_repetidos
		while (actual != estado_final && !abiertos.isEmpty()) { // Mientras que actual no sea el punto final y haya nodos abiertos
			iteraciones++; // Contador de iteraciones del bucle while
			abiertos.remove(0); // Elimino el nodo actual de la lista de abiertos
			cerrados.add(actual); // Y lo añado a nodos cerrados			
			estados_cerrados = cerrados.size(); // Contador para estados cerrados

			hijos = generar_sucesores(actual); // Genero los hijos del punto actual (Limpio de muros o punto de inicio)
			hijos = tratar_repetidos(cerrados, abiertos, hijos); // Trato los repetidos
			insertar(hijos); // Acolo los hijos en la lista de abiertos
			estados_visitados += hijos.size(); // Contador para estados visitados

			actual = abiertos.get(0); // Selecciono como actual el primero en la cola de abiertos

			if (actual.equals(estado_final)) { //Compruebo si estamos en el estado final
				mostrarcamino(actual, estado_final); // Muestro el camino solucion
				break; //Salgo del bucle while
			}
		}
	}

	/**
	 * Metodo Busqueda en profundidad. Pila LIFO
	 * A partir del nodo actual voy abriendo último elemento generado, y cuando se alcanza 
	 * un nodo cuyos vecinos han sido marcados, se retrocede al nodo anterior y se avanza desde este.
	 * @param estado_inicial - Objeto tipo Estado que nos indica el punto de partida 
	 * @param estado_final - Objeto tipo Estado que nos indica el punto final 
	 * @param limite - Nos indica hasta que profundidad limite podemos explorar en la busqueda 
	 */

	public void busqueda_profundidad(Estado estado_inicial, Estado estado_final, int limite) {
		abiertos.add(estado_inicial); // Añado como nodo abierto el punto de partida del laberinto
		Estado actual = abiertos.get(0); // Selecciono como punto actual el primero de los nodos abiertos (el punto de partida)
		trata_repe = 2; // Variable para indicar al switch como tiene que tratar los repetidos en el switch del metodo tratar_repetidos
		while (actual != estado_final && !abiertos.isEmpty()) { // Mientras que actual no sea el punto final y haya nodos abiertos
			iteraciones++; // Contador de iteraciones del bucle while
			abiertos.remove(actual); // Elimino el nodo actual de la lista de abiertos

			if (!cerrados.contains(actual)) // Compruebo que actual no este ya en la lista de nodos cerrados
				cerrados.add(actual); // Y lo añado a nodos cerrados

			estados_cerrados = cerrados.size(); // Contador para estados cerrados

			if (actual.getProf() <= limite) {
				hijos = generar_sucesores(actual); // Genero los hijos del punto actual (Limpio de muros o punto de inicio)
				hijos = tratar_repetidos(cerrados, abiertos, hijos); // Trato los repetidos
				insertar(hijos); // Acolo los hijos en la lista de abiertos
				estados_visitados += hijos.size(); // Contador para estados visitados
			}
			if (abiertos.size() != 0) {
				actual = abiertos.get(abiertos.size() - 1); // Selecciono como actual el primero de la pila
			} else {
				haySolucion = false; // Le indico que no hay solucion para ese limite
				break; // Salgo del bucle while
			}
			if (actual.equals(estado_final)) { //Compruebo si estamos en el estado final
				mostrarcamino(actual, estado_final); // Muestro el camino solucion
				break; //Salgo del bucle while
			}
		}
	}

	/**
	 * Metodo Busqueda Greedy Best First con Heuristica Manhattan.
	 * En cada iteración se escoge el nodo mas cercano a la solucion (el primero de la cola), 
	 * esto provoca que no se garantice la solucion optima
	 * @param estado_inicial - Objeto tipo Estado que nos indica el punto de partida 
	 * @param estado_final - Objeto tipo Estado que nos indica el punto final 
	 */

	public void busqueda_greedy(Estado estado_inicial, Estado estado_final) {
		abiertos.add(estado_inicial); // Añado como nodo abierto el punto de partida del laberinto
		Estado actual = abiertos.get(0); // Selecciono como punto actual el primero de los nodos abiertos (el punto de partida)
		trata_repe = 1; // Variable para indicar al switch como tiene que tratar los repetidos en el switch del metodo tratar_repetidos
		while (actual != estado_final && !abiertos.isEmpty()) { // Mientras que actual no sea el punto final y haya nodos abiertos
			iteraciones++; // Contador de iteraciones del bucle while
			abiertos.remove(0); // Elimino el nodo actual de la lista de abiertos
			cerrados.add(actual); // Y lo añado a nodos cerrados			
			estados_cerrados = cerrados.size(); // Contador para estados cerrados

			hijos = generar_sucesores(actual); // Genero los hijos del punto actual (Limpio de muros o punto de inicio)
			hijos = tratar_repetidos(cerrados, abiertos, hijos); // Trato los repetidos
			insertar(hijos); // Acolo los hijos en la lista de abiertos
			estados_visitados += hijos.size(); // Contador para estados visitados

			Collections.sort(abiertos, getCompHeuristica()); // Ordeno por Heurisitca Manhattan la cola de abiertos

			actual = abiertos.get(0); // Selecciono como actual el primero en la cola de abiertos

			if (actual.equals(estado_final)) { //Compruebo si estamos en el estado final
				mostrarcamino(actual, estado_final); // Muestro el camino solucion
				break; //Salgo del bucle while
			}
		}
	}

	/**
	 * Metodo Busqueda A* con Heuristica (Manhattan + Profundidad).
	 * Ordenamos la cola por la funcion heuristica distancia Manhattan mas profundidad de los nodos
	 * Garantizamos la solucion optima.
	 * @param estado_inicial - Objeto tipo Estado que nos indica el punto de partida 
	 * @param estado_final - Objeto tipo Estado que nos indica el punto final 
	 */

	public void busqueda_Aestrella(Estado estado_inicial, Estado estado_final) {
		abiertos.add(estado_inicial); // Añado como nodo abierto el punto de partida del laberinto
		Estado actual = abiertos.get(0); // Selecciono como punto actual el primero de los nodos abiertos (el punto de partida)
		trata_repe = 1; // Variable para indicar al switch como tiene que tratar los repetidos en el switch del metodo tratar_repetidos
		while (actual != estado_final && !abiertos.isEmpty()) { // Mientras que actual no sea el punto final y haya nodos abiertos
			iteraciones++; // Contador de iteraciones del bucle while
			abiertos.remove(0); // Elimino el nodo actual de la lista de abiertos
			cerrados.add(actual); // Y lo añado a nodos cerrados			
			estados_cerrados = cerrados.size(); // Contador para estados cerrados

			hijos = generar_sucesores(actual); // Genero los hijos del punto actual (Limpio de muros o punto de inicio)
			hijos = tratar_repetidos(cerrados, abiertos, hijos); // Trato los repetidos
			insertar(hijos); // Acolo los hijos en la lista de abiertos
			estados_visitados += hijos.size(); // Contador para estados visitados

			Collections.sort(abiertos, getCompHeuristicaMasProf()); // Ordeno por heuristica Manhattan + Profundidad la cola de abiertos

			actual = abiertos.get(0); // Selecciono como actual el primero en la cola de abiertos

			if (actual.equals(estado_final)) { //Compruebo si estamos en el estado final
				mostrarcamino(actual, estado_final); // Muestro el camino solucion
				break; //Salgo del bucle while
			}
		}
	}

	/**
	 * Metodo que nos sirve para tratar repetidos, incluye un switch para evaluar el algoritmo que lo llama y aplicar un tratamiento u otro
	 * @param cerrados - Lista de estados ya cerrados
	 * @param abiertos - Lista de estados abiertos
	 * @param hijos - Lista de hijos generados
	 * @return
	 */

	private ArrayList<Estado> tratar_repetidos(ArrayList<Estado> cerrados, ArrayList<Estado> abiertos,
			ArrayList<Estado> hijos) {

		ArrayList<Estado> aux_hijos = new ArrayList<>(); // Para evitar error de concurrencia creo una lista auxiliar de nodos hijos
		for (int i = 0; i < hijos.size(); i++) {
			aux_hijos.add(hijos.get(i));
		}
		switch (trata_repe) {
		case 1:
			for (Estado h : aux_hijos) { // Recorro todos los nodos hijos
				if (cerrados.contains(h) || abiertos.contains(h)) // Si el hijo esta en la cola de abiertos o en cerrados
					hijos.remove(h); // Lo elimino
			}
			break;
		case 2:
			for (Estado h : aux_hijos) { // Recorro todos los nodos hijos
				if (abiertos.contains(h)) // Si el hijo esta en la pila de abiertos
					hijos.remove(h); // Lo elimino puesto que no nos interesa ya que tendrá una profundidad mayor
				else if (cerrados.contains(h)) // Si el hijo esta en cerrados
					if (h.getProf() >= cerrados.get(cerrados.indexOf(h)).getProf()) // Compruebo si la profundidad es >= 
						hijos.remove(h); // Lo elimino porque solo nos interesan los de menor profundidad
			}
			break;
		default:
			break;
		}
		return hijos;
	}

	/**
	 * Metodo para mostrar el camino solucion
	 * @param act - Estado desde el cual partimos para encontrar la solucion
	 * @param fin - Estado hasta el cual el metodo tiene que llegar pintando el camino 
	 */
	private void mostrarcamino(Estado act, Estado fin) {
		pasos = act.getProf(); // Variable para guardar la profundidad a la que se encuentra la solucion del laberinto
		while (true) { // Con este bucle voy pintando el camino en la matriz con todos los padres de actual hasta que padre==null (estado_inicial)
			matriz[act.getX() - 1][act.getY() - 1] = camino;
			act = act.getPadre();
			if (act.getPadre() == null) {
				break;
			}
		}
	}

	/**
	 * Metodo para insertar los hijos en la lista de abiertos
	 * @param hijos - Lista de hijos generados
	 */

	private void insertar(ArrayList<Estado> hijos) {
		for (Estado p : hijos) { // Añado hijos a nodos abiertos
			abiertos.add(p);
			if (abiertos.size() > estados_abiertos)
				estados_abiertos = abiertos.size(); // Variable para contar el numero maximo de estados abiertos
		}
	}

	/**
	 * Metodo para devolver los hijos del estado actual
	 * @param actual - Estado actual desde el cual genero los hijos
	 * @return
	 */

	private ArrayList<Estado> generar_sucesores(Estado actual) {
		ArrayList<Estado> sucesores = new ArrayList<>();
		sucesores.add(actual.derecha().addHeuristica(estado_final)); //Añado los puntos hijos del estado actual
		sucesores.add(actual.izquierda().addHeuristica(estado_final));
		sucesores.add(actual.arriba().addHeuristica(estado_final));
		sucesores.add(actual.abajo().addHeuristica(estado_final));

		if (sucesores.contains(estado_inicial)) { //Limpio posible estado inicial
			sucesores.remove(estado_inicial);
		}
		ArrayList<Estado> aux = new ArrayList<>(); // Para evitar error de concurrencia creo una lista auxiliar con los sucesores
		for (int i = 0; i < sucesores.size(); i++) {
			aux.add(sucesores.get(i));
		}
		for (Estado p : aux) { //Limpio muros
			if (matriz[p.getX() - 1][p.getY() - 1] == muro) {
				sucesores.remove(p);
			}
		}
		return sucesores;
	}

	/**
	 * Metodo para obtener el estado inicial, osea el punto de partida
	 * @param matriz - Array bidimensional de chars con el laberinto
	 * @return
	 */

	public static Estado getEstadoInicial(char[][] matriz) {
		Estado pinicio = null;
		int x = 0;
		for (char[] m : matriz) {
			x++;
			for (int y = 0; y < m.length; y++) {
				if (m[y] == '>') { // Asigno el punto de partida a una variable tipo Estado(x,y)
					pinicio = new Estado(x, y + 1, 0, null);
				}
			}
		}
		return pinicio;
	}

	/**
	 * Metodo para obtener el estado final, osea el punto final
	 * @param matriz - Array bidimensional de chars con el laberinto
	 * @return
	 */

	public static Estado getEstadoFinal(char[][] matriz) { //Metodo para obtener el estado final
		Estado pfin = null;
		int x = 0;
		for (char[] m : matriz) {
			x++;
			for (int y = 0; y < m.length; y++) {
				if (m[y] == '*') { // Asigno el punto de partida a una variable tipo Estado(x,y)
					pfin = new Estado(x, y + 1, 0, null);
				}
			}
		}
		return pfin;
	}

	/**
	 * Metodo para leer matriz y devolverla como un array bidimensional de char [][]
	 * @param f - Fichero con el laberinto en formato .txt
	 * @return
	 */

	private static char[][] ReadMatrizXY(File f) {
		try {
			if (f.exists()) {
				int x = 0; // Variable para dimension (Numero de filas)
				int y = 0; // Variable para dimension (Numero de columnas)
				b = new BufferedReader(new FileReader(f));
				bf = new BufferedReader(new FileReader(f));

				while ((b.readLine()) != null) { // Mientras que haya lineas no vacias
					x++; // Calculo el numero de filas
				}

				String Linea = bf.readLine();
				y = Linea.length(); // Calculo el numero de columnas
				char[][] Matriz = new char[x][y]; // Ya puedo crear matriz de X filas e Y columnas

				int fila = 0;

				while (Linea != null && fila < x) { // Mientras que haya filas no vacias
					String[] linea = Linea.split(""); // Introduczo caracteres en un array de string
					for (int columna = 0; columna < y; columna++) { // Bucle para construir matriz recorriendo chars
						char c = linea[columna].charAt(0);
						Matriz[fila][columna] = c;
					}
					fila++;
					Linea = bf.readLine();
				}
				return Matriz;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Metodo que devuelve la matriz
	 * @return
	 */
	public char[][] getMatriz() {
		return matriz;
	}

	/**
	 * Metodo que nos da el estado inicial
	 * @return
	 */

	public Estado getEstado_inicial() {
		return estado_inicial;
	}

	/**
	 * Metodo que nos da el estado final
	 * @return
	 */

	public Estado getEstado_final() {
		return estado_final;
	}

	/**
	 * Metodo comparador por distancia Manhattan
	 * f (n) = g(n) --> Funcion estimacion = distancia Manhattan
	 * En esta métrica, la distancia entre dos puntos es la suma de las 
	 * diferencias absolutas entre sus coordenadas
	 * @return
	 */

	public Comparator<Estado> getCompHeuristica() {
		comp = new Comparator<Estado>() {
			@Override
			public int compare(Estado e1, Estado e2) {
				return new Integer(e1.getDistanciaManhattan().compareTo(e2.getDistanciaManhattan()));
			}
		};
		return comp;
	}

	/**
	 * Metodo comparador por distancia Manhattan + Profundidad
	 * f (n) = g(n) + h(n) --> Funcion estimacion = distancia Manhattan + Profundidad
	 * La preferencia es siempre del nodo con menor f , en caso de empate, la
	 * preferencia es del nodo con menor h, en nuestro caso con menor profundidad.
	 * @return
	 */

	public Comparator<Estado> getCompHeuristicaMasProf() {
		comp = new Comparator<Estado>() {
			@Override
			public int compare(Estado e1, Estado e2) {
				Integer es1 = e1.getDistanciaManhattan() + e1.getProf();
				Integer es2 = e2.getDistanciaManhattan() + e2.getProf();
				if (es1 == es2)
					return new Integer(e1.getProf().compareTo(e2.getProf())); // En caso de empate comparo por profundidad
				return new Integer(es1.compareTo(es2));
			}
		};
		return comp;
	}
}
