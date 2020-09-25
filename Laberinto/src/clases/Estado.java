package clases;

/**
 * Clase que representa los puntos del laberinto.
 * @author Jonathan
 *
 */
public class Estado {
	private int x, y; // Variables de instancia
	private Integer profundidad;
	private Estado padre;
	private Integer distancia_manhattan;

	/**
	 * Constructor de coordenadas, profundidad y padre
	 * @param x - Coordenada X en el laberinto
	 * @param y - Coordenada Y en el laberinto
	 * @param prof - Profundidad a la que se encuentra
	 * @param padre - Estado padre 
	 */

	public Estado(int x, int y, Integer prof, Estado padre) { // Constructor coordenadas, profundidad y padre
		this.x = x;
		this.y = y;
		this.profundidad = prof;
		this.padre = padre;
	}

	/**
	 * Constructor de Estado + Heuristica
	 * @param p - Estado a partir del cual vamos a construir
	 * @param distancia_manhattan - La distancia entre dos puntos es la suma de las diferencias absolutas entre sus coordenadas
	 */

	public Estado(Estado p, Integer distancia_manhattan) { // Constructor Estado mas heuristica
		this.x = p.getX();
		this.y = p.getY();
		this.profundidad = p.getProf();
		this.padre = p.getPadre();
		this.distancia_manhattan = distancia_manhattan;
	}

	/**
	 * Metodo que nos da la distancia Manhattan
	 * @return
	 */
	public Integer getDistanciaManhattan() {
		return distancia_manhattan;
	}

	/**
	 * Metodo que nos devuelve el estado padre
	 * @return
	 */
	public Estado getPadre() {
		return padre;
	}

	/**
	 * Metodo para modificar el padre
	 * @param padre - Nuevo Padre
	 */
	public void setPadre(Estado padre) {
		this.padre = padre;
	}

	/**
	 * Metodo que nos da la coordenada X
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Metodo para modificar la coordenada X
	 * @param x - Nueva coordenada X en el laberinto
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Metodo que nos da la coordenada Y
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * Metodo para modificar la coordenada Y
	 * @param y - Nueva coordenada Y en el laberinto
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Metodo para obtener la profundidad
	 * @return
	 */
	public Integer getProf() {
		return profundidad;
	}

	/**
	 * Metodo para modificar la profundidad
	 * @param prof - Nueva profundidad
	 */
	public void setProf(Integer prof) {
		this.profundidad = prof;
	}

	/**
	 * Metodo para crear distancia Manhattan
	 * @param fin - Estado final para calcular distancia Manhattan respecto del estado this en cuestion
	 * @return
	 */
	public Estado addHeuristica(Estado fin) {
		Integer manhattan = Math.abs(fin.getX() - this.getX()) + Math.abs(fin.getY() - this.getY());
		return new Estado(this, manhattan);
	}

	/**
	 * Metodo que nos devuelve la posicion derecha del punto desde el cual lo llamamos
	 * @return
	 */
	public Estado derecha() {
		return new Estado(this.getX(), this.getY() + 1, this.getProf() + 1, this); //Sumo 1 posicion a las columnas, sumo 1 a profundidad, y asigno this como padre
	}

	/**
	 * Metodo que nos devuelve la posicion izquierda del punto desde el cual lo llamamos
	 * @return
	 */
	public Estado izquierda() {
		return new Estado(this.getX(), this.getY() - 1, this.getProf() + 1, this); //Resto 1 posicion a las columnas, sumo 1 a profundidad, y asigno this como padre
	}

	/**
	 * Metodo que nos devuelve la posicion arriba del punto desde el cual lo llamamos
	 * @return
	 */
	public Estado arriba() {
		return new Estado(this.getX() - 1, this.getY(), this.getProf() + 1, this); //Resto 1 posicion a las filas, sumo 1 a profundidad, y asigno this como padre
	}

	/**
	 * Metodo que nos devuelve la posicion abajo del punto desde el cual lo llamamos
	 * @return
	 */
	public Estado abajo() {
		return new Estado(this.getX() + 1, this.getY(), this.getProf() + 1, this); //Sumo 1 posicion a las filas, sumo 1 a profundidad, y asigno this como padre
	}

	@Override
	public String toString() {
		return "Fila " + (x) + ", Columna " + (y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Estado other = (Estado) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

}
