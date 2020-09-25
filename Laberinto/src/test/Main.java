package test;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;

import clases.Laberinto;

import java.awt.Color;
import java.awt.Font;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.Checkbox;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.FlowLayout;
import javax.swing.JRadioButton;

/**
 * Clase Main que representa en un JFrame toda la informacion referente a la resolucion de laberintos usando distintos metodos de busqueda
 * @author Jonathan
 *
 */
@SuppressWarnings("serial")
public class Main extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table, labe;
	private static long tinicio, tfin, tiempo;
	long t = 6000;
	private Laberinto laberinto, laberinto_anchura, laberinto_profundidad, laberinto_greedy, laberinto_estrella;
	private File fichero;
	private String temp = "mili";
	private int fila, limit;
	private JScrollPane scrollPane;
	private char solucion = '·';
	private char muro; // Muro del archivo .txt

	/**
	 * Lanzar la aplicación
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor del JFrame con todos parametros asociados a la ventana
	 */
	public Main() {

		// Concerniente al JPanel 
		setTitle("Practica 1 - Inteligencia Artificial - Jonathan Jimenez Reina 2016/17");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 951, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), null));
		contentPane.setBackground(new Color(240, 240, 240));
		contentPane.setLayout(null);
		setContentPane(contentPane);

		// Campo para mostrar la ubicacion del archivo elegido
		textField = new JTextField();
		textField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
		textField.setToolTipText("Inserta la ruta del fichero");
		textField.setBounds(52, 45, 209, 20);
		contentPane.add(textField);
		textField.setColumns(10);

		// Etiqueta Laberinto
		JLabel labelLaberinto = new JLabel();
		labelLaberinto.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
		labelLaberinto.setText("Laberinto:");
		labelLaberinto.setBounds(680, 30, 100, 23);
		contentPane.add(labelLaberinto);

		// Boton seleccionar laberinto para buscar en el pc
		JButton btnSeleccionar = new JButton("Seleccionar laberinto");
		btnSeleccionar.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
		btnSeleccionar.setBounds(271, 44, 155, 23);
		contentPane.add(btnSeleccionar);

		// Boton para lanzar algoritmo en anchura
		JButton anchura = new JButton("Anchura");
		anchura.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
		anchura.setBounds(52, 90, 140, 23);
		contentPane.add(anchura);

		// Boton para lanzar algoritmo en profundidad
		JButton profundidad = new JButton("Profundidad");
		profundidad.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
		profundidad.setBounds(200, 90, 140, 23);
		contentPane.add(profundidad);

		// Boton para lanzar algoritmo greedy best first
		JButton greedy = new JButton("Greedy Best First");
		greedy.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
		greedy.setBounds(350, 90, 140, 23);
		contentPane.add(greedy);

		// Boton para lanzar algoritmo A*
		JButton aestrella = new JButton("A*");
		aestrella.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
		aestrella.setBounds(500, 90, 140, 23);
		contentPane.add(aestrella);

		// Boton para mostrar el algoritmo sin resolver, hace funcion de RESET
		JButton mostrar = new JButton("Mostrar Laberinto / Reset");
		mostrar.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
		mostrar.setBounds(436, 44, 204, 23);
		contentPane.add(mostrar);

		// Campo de texto para introducir el limite para el algoritmo en profundidad
		JTextField limite = new JTextField();
		limite.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
		limite.setHorizontalAlignment(SwingConstants.CENTER);
		limite.setText("0");
		limite.setBounds(300, 124, 34, 23);
		contentPane.add(limite);

		// Etiqueta profundidad
		JLabel profundida = new JLabel();
		profundida.setText("Introduzca l\u00EDmite:");
		profundida.setBounds(201, 124, 100, 23);
		contentPane.add(profundida);

		// Desactivacion de componentes
		anchura.setEnabled(false);
		profundidad.setEnabled(false);
		greedy.setEnabled(false);
		aestrella.setEnabled(false);
		mostrar.setEnabled(false);

		// Inicializo la tabla resultados
		resultados_inicio();

		// Etiqueta Resultados:
		JLabel lblResultados = new JLabel("RESULTADOS DE EJECUCI\u00D3N : ");
		lblResultados.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
		lblResultados.setBounds(52, 214, 282, 34);
		contentPane.add(lblResultados);

		// Etiqueta Resuelte tu laberinto
		JLabel lblPractica = new JLabel("Resuelve tu laberinto! :)");
		lblPractica.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
		lblPractica.setBounds(52, 14, 418, 14);
		contentPane.add(lblPractica);

		// Etiqueta mostrada cuando profundidad no encuentra solucion
		JLabel labeLimite = new JLabel();
		labeLimite.setText("Con 0 no encuentro solución");
		labeLimite.setBounds(200, 150, 226, 20);
		contentPane.add(labeLimite);

		// JPanel para indicar en que unidades mostramos el tiempo de ejecucion de los algoritmos
		JPanel panelTiempo = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelTiempo.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panelTiempo.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Mostrar tiempo en:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelTiempo.setBounds(436, 124, 161, 112);
		contentPane.add(panelTiempo);

		// Checkbox para mostrar en milisegundos
		Checkbox mili = new Checkbox("Milisegundos");
		panelTiempo.add(mili);
		mili.setState(true);

		// Checkbox para mostrar en nanosegundos
		Checkbox nano = new Checkbox("Nanosegundos");
		panelTiempo.add(nano);

		// Checkbox para mostrar en nanosegundos / 10.000 para mayor comodidad
		Checkbox nanoplus = new Checkbox("Nano / 10.000");
		panelTiempo.add(nanoplus);

		// Funciones de los checkboxs relativos al tiempo de ejecucion
		nano.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (nano.getState()) {
					mili.setState(false);
					nanoplus.setState(false);
					temp = "nano";
				}
			}
		});

		mili.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (mili.getState()) {
					nano.setState(false);
					nanoplus.setState(false);
					temp = "mili";
				}
			}
		});

		nanoplus.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (nanoplus.getState()) {
					nano.setState(false);
					mili.setState(false);
					temp = "nanop";
				}
			}
		});

		// Desactivacion de componentes
		labeLimite.setVisible(false);
		mili.setEnabled(false);
		nano.setEnabled(false);
		nanoplus.setEnabled(false);

		// JPanel para seleccionar como quieres que se pinte la solucion
		JPanel panelChars = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelChars.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panelChars.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Dibujar camino:",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelChars.setBounds(62, 124, 113, 87);
		contentPane.add(panelChars);

		// Boton para mostrar el camino con ·
		JRadioButton puntomedio = new JRadioButton("   \u00B7");
		puntomedio.setEnabled(false);
		panelChars.add(puntomedio);
		puntomedio.setSelected(true);

		// Boton para mostrar el camino con .
		JRadioButton puntobajo = new JRadioButton("   .");
		puntobajo.setEnabled(false);
		panelChars.add(puntobajo);

		// Boton para mostrar el camino con *
		JRadioButton asterisco = new JRadioButton("   *");
		asterisco.setEnabled(false);
		panelChars.add(asterisco);

		// Boton para mostrar el camino con X
		JRadioButton equis = new JRadioButton("   X");
		equis.setEnabled(false);
		panelChars.add(equis);

		// Para hacer efectivas las funciones de los Radiobuttons
		puntomedio.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (puntomedio.isSelected()) {
					puntobajo.setSelected(false);
					asterisco.setSelected(false);
					equis.setSelected(false);
					solucion = '·';
				}
			}
		});
		puntobajo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (puntobajo.isSelected()) {
					puntomedio.setSelected(false);
					asterisco.setSelected(false);
					equis.setSelected(false);
					solucion = '.';
				}
			}
		});
		asterisco.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (asterisco.isSelected()) {
					puntomedio.setSelected(false);
					puntobajo.setSelected(false);
					equis.setSelected(false);
					solucion = '*';
				}
			}
		});
		equis.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (equis.isSelected()) {
					puntobajo.setSelected(false);
					asterisco.setSelected(false);
					puntomedio.setSelected(false);
					solucion = 'X';
				}
			}
		});

		// Funciones del boton mostrar
		mostrar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Activacion y desactivacion de componentes
				puntomedio.setEnabled(false);
				puntobajo.setEnabled(false);
				asterisco.setEnabled(false);
				equis.setEnabled(false);
				mili.setEnabled(true);
				nano.setEnabled(true);
				nanoplus.setEnabled(true);
				anchura.setEnabled(true);
				profundidad.setEnabled(true);
				greedy.setEnabled(true);
				aestrella.setEnabled(true);
				mostrar.setEnabled(true);
				labeLimite.setVisible(false);

				textField.setText(fichero.getAbsolutePath());
				limite.setText("0");
				resultados_inicio();

				// Inicializo variables para los diferentes laberintos
				try (FileReader fr = new FileReader(fichero)) {
					muro = '#';
					laberinto = new Laberinto(fichero, muro, solucion);
					laberinto_anchura = new Laberinto(fichero, muro, solucion);
					laberinto_profundidad = new Laberinto(fichero, muro, solucion);
					laberinto_greedy = new Laberinto(fichero, muro, solucion);
					laberinto_estrella = new Laberinto(fichero, muro, solucion);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				muestra(laberinto);
			}
		});

		// Funciones del boton anchura
		anchura.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				anchura.setEnabled(false); // Desactivo boton hasta nuevo reseteo
				fila = 0; // Indico la fila en la que se mostraran los datos en la tabla de resultados
				calculaTiempoEjecucion(laberinto_anchura); // Calculo el tiempo de ejecucion con la ejecucion del algorimo
				muestra(laberinto_anchura); // Dibujo matriz solucion
				modificoresultados(laberinto_anchura, fila, tiempo); // Inserto los datos de la ejecucion en la tabla resultados
				// Desactivo los checkboxs de modificacion del tiempo
				mili.setEnabled(false);
				nano.setEnabled(false);
				nanoplus.setEnabled(false);
			}
		});

		// Funciones del boton profundidad
		profundidad.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				profundidad.setEnabled(false); // Desactivo boton hasta nuevo reseteo
				limit = Integer.parseInt(limite.getText()); // Asigno el limite introducido por el usuario
				fila = 1; // Indico la fila en la que se mostraran los datos en la tabla de resultados
				calculaTiempoEjecucion(laberinto_profundidad); // Calculo el tiempo de ejecucion con la ejecucion del algorimo
				if (laberinto_profundidad.haySolucion) { // Si hay solucion 
					muestra(laberinto_profundidad); // Dibujo matriz solucion
					modificoresultados(laberinto_profundidad, fila, tiempo); // Inserto los datos de la ejecucion en la tabla resultados
				} else {
					labeLimite.setText("Con " + Integer.parseInt(limite.getText()) + " no encuentro solución"); // Sino hay solucion muestro mensaje
					labeLimite.setVisible(true);
				}
				// Desactivo los checkboxs de modificacion del tiempo
				mili.setEnabled(false);
				nano.setEnabled(false);
				nanoplus.setEnabled(false);
			}
		});

		// Funciones del boton greedy
		greedy.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				greedy.setEnabled(false); // Desactivo boton hasta nuevo reseteo
				fila = 2; // Indico la fila en la que se mostraran los datos en la tabla de resultados
				calculaTiempoEjecucion(laberinto_greedy); // Calculo el tiempo de ejecucion con la ejecucion del algorimo
				muestra(laberinto_greedy); // Dibujo matriz solucion
				modificoresultados(laberinto_greedy, fila, tiempo); // Inserto los datos de la ejecucion en la tabla resultados
				// Desactivo los checkboxs de modificacion del tiempo
				mili.setEnabled(false);
				nano.setEnabled(false);
				nanoplus.setEnabled(false);
			}
		});

		// Funciones del boton A*
		aestrella.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				aestrella.setEnabled(false); // Desactivo boton hasta nuevo reseteo
				fila = 3; // Indico la fila en la que se mostraran los datos en la tabla de resultados
				calculaTiempoEjecucion(laberinto_estrella); // Calculo el tiempo de ejecucion con la ejecucion del algorimo
				muestra(laberinto_estrella); // Dibujo matriz solucion
				modificoresultados(laberinto_estrella, fila, tiempo); // Inserto los datos de la ejecucion en la tabla resultados
				// Desactivo los checkboxs de modificacion del tiempo
				mili.setEnabled(false);
				nano.setEnabled(false);
				nanoplus.setEnabled(false);
			}
		});

		// Funciones del boton Seleccionar
		btnSeleccionar.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(); // Creamos el objeto JFileChooser
				FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.TXT", "txt"); //Filtro solo texto
				fc.setFileFilter(filtro); // Le paso el filtro al FileChooser
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

				int seleccion = fc.showOpenDialog(contentPane); // Guardo la opcion seleccionada

				if (seleccion == JFileChooser.APPROVE_OPTION) { // Si el usuario, pincha en aceptar
					fichero = fc.getSelectedFile(); // Seleccionamos el fichero
					textField.setText(fichero.getAbsolutePath()); // Ecribe la ruta del fichero seleccionado en el campo de texto

					// Activacion de componentes
					mostrar.setEnabled(true);
					puntomedio.setEnabled(true);
					puntobajo.setEnabled(true);
					asterisco.setEnabled(true);
					equis.setEnabled(true);
					mili.setEnabled(true);
					nano.setEnabled(true);
					nanoplus.setEnabled(true);
					pintarJtable();
				}
			}
		});
	}

	/**
	 * Metodos auxiliares.
	 */

	protected void pintarJtable() {
		Object[][] filas = { { "" } };
		String[] columnas = { "" };
		labe = new JTable(filas, columnas);
		contentPane.add(labe);
		labe.setBounds(680, 60, 400, 340);
		labe.setShowGrid(false);
		labe.setShowHorizontalLines(false);
		labe.setShowVerticalLines(false);
		labe.setBackground(contentPane.getBackground());
	}

	/**
	 * Metodo para mostrar el laberinto 
	 * @param lab - Laberinto que queremos dibujar
	 */
	public void muestra(Laberinto lab) {
		Object[][] filas = new Object[lab.getMatriz().length][lab.getMatriz()[0].length];
		String[] columnas = new String[lab.getMatriz()[0].length];
		int x = 0;

		for (char[] m : lab.getMatriz()) {
			x++;
			for (int y = 0; y < m.length; y++) {
				filas[x - 1][y] = "" + m[y];
				columnas[y] = "";
			}
		}

		Double f = 15.8 * lab.getMatriz().length;
		Double c = 11.3 * lab.getMatriz()[0].length;

		labe = new JTable(filas, columnas);
		labe.setBounds(680, 60, c.intValue(), f.intValue());
		labe.setShowGrid(false);
		labe.setShowHorizontalLines(false);
		labe.setShowVerticalLines(false);
		contentPane.add(labe);
		for (int i = 0; i < columnas.length; i++) {
			labe.getColumnModel().getColumn(i).setMinWidth(4);
			labe.getColumnModel().getColumn(i).setPreferredWidth(4);
		}
		contentPane.add(labe);
	}

	/**
	 * Metodo para mostrar los resultados en la tabla de resultados
	 * @param lab - Laberinto del cual vamos a mostrar los resultados
	 * @param f - Fila en la cual mostraremos los datos (Segun algoritmo escogido)
	 * @param tiempo - Tiempo de ejecucion del algoritmo ejecutado
	 */
	protected void modificoresultados(Laberinto lab, int f, long tiempo) {
		table.setValueAt(lab.iteraciones, f, 1);
		table.setValueAt(lab.pasos, f, 2);
		table.setValueAt(lab.estados_visitados, f, 3);
		table.setValueAt(lab.estados_cerrados, f, 4);
		table.setValueAt(lab.estados_abiertos, f, 5);
		table.setValueAt(tiempo, f, 6);

	}

	/**
	 * Metodo para inicializar la tabla a valores por defecto (ceros)
	 */
	protected void resultados_inicio() {
		Object[][] filas = { { "Anchura", 0, 0, 0, 0, 0, 0 }, { "Profundidad", 0, 0, 0, 0, 0, 0 },
				{ "Greedy", 0, 0, 0, 0, 0, 0 }, { "A*", 0, 0, 0, 0, 0, 0 } };
		String[] columnas = { "Algoritmo", "Iteraciones", "Pasos", "Visitados", "Cerrados", "Abiertos", "Tiempo" };
		table = new JTable(filas, columnas);
		table.setEnabled(false);
		table.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 13));
		scrollPane = new JScrollPane(table);
		scrollPane.setBounds(52, 250, 600, 87);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
		tcr.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < columnas.length; i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(tcr);
		}
	}

	/**
	 * Metodo para obtener el tiempo actual en milisegundos, nanosegundos o nanosegundos / 10.000
	 * @return
	 */
	public long currentTime() {
		if (temp == "mili")
			return System.currentTimeMillis();
		else if (temp == "nano")
			return System.nanoTime();
		return System.nanoTime() / 10000;
	}

	/**
	 * Calculo el tiempo de ejecucion restanto el tiempo de inicio con el de fin
	 * @param l - Laberinto para el que tenemos que calcular el tiempo de ejecucion
	 * @return
	 */
	public long calculaTiempoEjecucion(Laberinto l) {
		switch (fila) {
		case 0:
			tinicio = currentTime(); // Guardo el tiempo actual
			l.busqueda_anchura(l.getEstado_inicial(), l.getEstado_final()); // Metodo busqueda de solucion														
			tfin = currentTime(); // Guardo el tiempo actual en milisegundos
			tiempo = (tfin - tinicio); // Resto para conseguir tiempo de ejecucion
			break;
		case 1:
			tinicio = currentTime(); // Guardo el tiempo actual
			l.busqueda_profundidad(l.getEstado_inicial(), l.getEstado_final(), limit); // Metodo busqueda de solucion														
			tfin = currentTime(); // Guardo el tiempo actual en milisegundos
			tiempo = (tfin - tinicio); // Resto para conseguir tiempo de ejecucion
			break;
		case 2:
			tinicio = currentTime(); // Guardo el tiempo actual
			l.busqueda_greedy(l.getEstado_inicial(), l.getEstado_final()); // Metodo busqueda de solucion														
			tfin = currentTime(); // Guardo el tiempo actual en milisegundos
			tiempo = (tfin - tinicio); // Resto para conseguir tiempo de ejecucion
			break;
		case 3:
			tinicio = currentTime(); // Guardo el tiempo actual
			l.busqueda_Aestrella(l.getEstado_inicial(), l.getEstado_final()); // Metodo busqueda de solucion														
			tfin = currentTime(); // Guardo el tiempo actual en milisegundos
			tiempo = (tfin - tinicio); // Resto para conseguir tiempo de ejecucion
			break;
		default:
			break;
		}
		return tiempo;
	}
}