import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

//La clase hereda de JFrame, que es una ventana estandar en Java Swing
public class VentanaSimulador extends JFrame {

    //Componentes de la interfaz
    private JTextArea areaReporte; //El area blanca grande para texto
    private JButton btnIniciar;    //El boton
    private JTextField inputClientes; //Campo para escribir numero
    private JComboBox<Integer> comboRepeticiones; //Selector de repeticiones

    public VentanaSimulador() {
        //Configuracion basica de la ventana
        setTitle("Simulador de Tienda - Tarea");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Cierra la app al cerrar ventana
        setLocationRelativeTo(null); //Centra la ventana en pantalla
        setLayout(new BorderLayout(10, 10)); //Diseño de bordes

        //Crear el panel de control
        JPanel panelControl = new JPanel();
        panelControl.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelControl.setBorder(BorderFactory.createTitledBorder("Configuración de la Corrida"));

        //Inputs
        panelControl.add(new JLabel("Cant. Clientes por simulación:"));
        inputClientes = new JTextField("100", 5); //Valor por defecto 100
        panelControl.add(inputClientes);

        panelControl.add(new JLabel(" Repeticiones:"));
        //Combo box para elegir 1 o 10 repeticiones facilmente
        comboRepeticiones = new JComboBox<>(new Integer[]{1, 10, 100});
        comboRepeticiones.setSelectedItem(10); //Por defecto 10, como pide la tarea
        panelControl.add(comboRepeticiones);

        //Boton de inicio
        btnIniciar = new JButton("▶ INICIAR SIMULACIONES");
        //Cambiamos color para que resalte
        btnIniciar.setBackground(new Color(70, 130, 180));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);
        
        //Agregar accion al boton: Que pasa cuando se hace click
        btnIniciar.addActionListener(this::accionBotonIniciar);
        panelControl.add(btnIniciar);

        //Agregar el panel de control arriba de la ventana
        add(panelControl, BorderLayout.NORTH);

        //Area de Reportes (Centro)
        areaReporte = new JTextArea();
        areaReporte.setEditable(false); //Que el usuario no pueda borrar el texto
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 12)); //Fuente tipo consola
        areaReporte.setText("Bienvenido al Simulador.\nConfigure los parámetros arriba y presione Iniciar.\n\n");
        
        //Ponemos el area de texto dentro de un ScrollPane para tener barras de desplazamiento
        JScrollPane scrollPane = new JScrollPane(areaReporte);
        //Agregamos al centro
        add(scrollPane, BorderLayout.CENTER);
    }

    // Metodo que se ejecuta al presionar el boton
    private void accionBotonIniciar(ActionEvent e) {
        int numClientes;
        int repeticiones = (int) comboRepeticiones.getSelectedItem();
        try {
            numClientes = Integer.parseInt(inputClientes.getText());
            if (numClientes <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido de clientes > 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        btnIniciar.setEnabled(false); //Desactivar boton para no darle doble click
        btnIniciar.setText("Ejecutando...");
        areaReporte.setText(""); //Limpiar pantalla anterior
        areaReporte.append(">>> INICIANDO TANDA DE " + repeticiones + " SIMULACIONES PARA " + numClientes + " CLIENTES <<<\n\n");

        //Si corremos la simulacion en el hilo principal, la ventana se congela
        //Usamos un hilo nuevo para el trabajo pesado
        new Thread(() -> {
            for (int i = 1; i <= repeticiones; i++) {
                areaReporte.append("----------------------------------------\n");
                areaReporte.append("🔴 EJECUCIÓN " + i + " DE " + repeticiones + "\n"); 
                
                //Creamos la instancia de la logica pasandole cuantos clientes queremos
                //y la referencia a "areaReporte" para que escriba ahi
                SimuladorTienda.LogicaSimulacion sim = new SimuladorTienda.LogicaSimulacion(numClientes, areaReporte);
                sim.ejecutarSimulacion();
                
                areaReporte.append("----------------------------------------\n\n");
                
                // Pequeña pausa para no saturar la vista
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }

            //Cuando termina el bucle, reactivamos el boton (usando invokeLater para seguridad de hilos Swing)
            SwingUtilities.invokeLater(() -> {
                areaReporte.append(">>> TANDA FINALIZADA <<<");
                btnIniciar.setText("▶ INICIAR SIMULACIONES");
                btnIniciar.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Ejecución completa. Revise el área de texto.");
            });

        }).start();//Arranca el hilo
    }

    public static void main(String[] args) {
        //Swing debe iniciarse en su propio hilo de eventos por seguridad
        SwingUtilities.invokeLater(() -> {
            VentanaSimulador ventana = new VentanaSimulador();
            ventana.setVisible(true);
        });
    }
}