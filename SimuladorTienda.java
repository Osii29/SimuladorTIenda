import java.util.*;
import javax.swing.JTextArea;

public class SimuladorTienda {

  
    //Clases del modelo (entidades)
    static class Producto {
        String sku;
        String nombre;
        double precio;
        int stock;
        int stockInicial;

        public Producto(String sku, String nombre, double precio, int stock) {
            this.sku = sku;
            this.nombre = nombre;
            this.precio = precio;
            this.stock = stock;
            this.stockInicial = stock;
        }
    }

    static class Cliente {
        int id;
        // Map para manejar cantidad por producto (p.ej. 2 leches) si quisieras, 
        // pero la lista simple cumple con "elegir entre 1 y 5 productos".
        List<Producto> carrito = new ArrayList<>(); 
        int tiempoCompraRestante; //Ciclos simulando la selección de productos

        public Cliente(int id) {
            this.id = id;
        }
    }

    static class Caja {
        int id;
        boolean abierta;
        Cliente clienteActual;
        int tiempoServicioRestante;

        public Caja(int id) {
            this.id = id;
            this.abierta = false;
        }
    }


    //Logica Central
    public static class LogicaSimulacion {
        private JTextArea areaSalida;

        //Estructuras de Datos Eficientes
        Map<String, Producto> inventario = new HashMap<>(); //Búsqueda O(1)
        List<Cliente> clientesComprando = new ArrayList<>(); //Lista dinámica
        Queue<Cliente> colaCobro = new LinkedList<>(); //FIFO O(1)
        List<Caja> cajas = new ArrayList<>(); //Acceso por índice O(1)
        
        //Variables de Estadísticas
        double gananciasTotales = 0;
        int clientesAtendidos = 0;
        int productosNoVendidosPorStock = 0; //Metrica extra de calidad
        int maxClientesObjetivo; //Meta de clientes a generar en 24 ticks

        public LogicaSimulacion(int maxClientes, JTextArea areaSalida) {
            this.maxClientesObjetivo = maxClientes;
            this.areaSalida = areaSalida;
            inicializarInventario(maxClientes); //Stock escala segun la demanda esperada
            
            // Inicializar 4 cajas
            for (int i = 0; i < 4; i++) cajas.add(new Caja(i + 1));
            cajas.get(0).abierta = true; //Siempre debe haber mínimo 1 caja
        }

        private void log(String mensaje) {
            if (areaSalida != null) {
                areaSalida.append(mensaje + "\n");
                areaSalida.setCaretPosition(areaSalida.getDocument().getLength());
            } else {
                System.out.println(mensaje);
            }
        }

        //Inicializamos inventario con suficiente stock base, pero escalable
        //para que en la simulación de 1000 clientes haya riesgo de agotarse
        private void inicializarInventario(int escalaClientes) {
            int multiplicador = (escalaClientes > 100) ? 5 : 1; 
            
            inventario.put("A01", new Producto("A01", "Leche", 25.0, 100 * multiplicador));
            inventario.put("B02", new Producto("B02", "Pan", 12.5, 80 * multiplicador));
            inventario.put("C03", new Producto("C03", "Huevo", 40.0, 60 * multiplicador));
            inventario.put("D04", new Producto("D04", "Refresco", 18.0, 150 * multiplicador));
            inventario.put("E05", new Producto("E05", "Sabritas", 15.0, 100 * multiplicador));
           
        }

        //Metodo principal de la simulacion
        public void ejecutarSimulacion() {
            int tick = 1;
            int clientesGenerados = 0;
            Random random = new Random();
            long tiempoInicio = System.nanoTime();

            // Calculo de "k" (tasa de llegada máxima) para cumplir la meta en 24 ticks
            // Si meta es 1000: 1000/24 = 41.6 avg. K debe ser aprox 83 para que el avg sea 41.
            int k = (int) Math.ceil((double) maxClientesObjetivo / 24.0 * 2.0);
            if (k < 2) k = 2; // Mínimo para simulaciones pequeñas

            log(">>> Configuración: Meta " + maxClientesObjetivo + " clientes en 24 ticks (Tasa llegada k=0.." + k + ") <<<");

            //Bucle Principal
            //Mientras sea horario laboral (tick <=24) O quede gente en la tienda
            while (tick <= 24 || !clientesComprando.isEmpty() || !colaCobro.isEmpty() || hayCajasOcupadas()) {
                
                //Llegada de clientes
                if (tick <= 24 && clientesGenerados < maxClientesObjetivo) {
                    int nuevosClientes = random.nextInt(k + 1); // 0..k
                    
                    for (int i = 0; i < nuevosClientes; i++) {
                        if (clientesGenerados >= maxClientesObjetivo) break;
        
                        Cliente c = new Cliente(++clientesGenerados);
                        
                        //Elegir 1 a 5 productos
                        int numProductos = 1 + random.nextInt(5);
                        c.tiempoCompraRestante = numProductos; //1 ciclo por producto seleccionando
                        
                        List<String> keys = new ArrayList<>(inventario.keySet());
                        for (int p = 0; p < numProductos; p++) {
                            c.carrito.add(inventario.get(keys.get(random.nextInt(keys.size()))));
                        }
                        clientesComprando.add(c);
                    }
                }

                //Proceso d seleccion, (clientes comprando)
                Iterator<Cliente> it = clientesComprando.iterator();
                while (it.hasNext()) {
                    Cliente c = it.next();
                    c.tiempoCompraRestante--;
                    if (c.tiempoCompraRestante <= 0) {
                        colaCobro.add(c); //Pasa a la cola
                        it.remove();
                    }
                }

                //Gestion de cajas
                gestionarAperturaCajas();

                //Atencion en cajas
                procesarCajas();

                tick++;
                //Freno de seguridad por si el bucle se vuelve infinito (stock agotado y colas atoradas)
                if(tick > 50000) { log("!!! CORTE FORZADO (Seguridad) !!!"); break; }
            }

            generarReporteFinal(System.nanoTime() - tiempoInicio, tick - 1);
        }

        private void gestionarAperturaCajas() {
            int cajasAbiertas = 0;
            for(Caja c : cajas) if(c.abierta) cajasAbiertas++;

            //Evitar division por cero
            double promedioCola = (cajasAbiertas > 0) ? (double) colaCobro.size() / cajasAbiertas : colaCobro.size();

            //Abrir caja si promedio >= 4
            if (promedioCola >= 4 && cajasAbiertas < 4) {
                for (Caja c : cajas) {
                    if (!c.abierta) { c.abierta = true; break; }
                }
            }
            //Cerrar caja si promedio <= 1 y hay > 1 abierta
            else if (promedioCola <= 1 && cajasAbiertas > 1) {
                //Buscamos cerrar una caja que este vacia (sin atender a nadie en este instante)
                for (int i = 3; i >= 0; i--) {
                    if (cajas.get(i).abierta && cajas.get(i).clienteActual == null) {
                        cajas.get(i).abierta = false;
                        break;
                    }
                }
            }
        }

        private void procesarCajas() {
            for (Caja caja : cajas) {
                if (!caja.abierta) continue;

                //Jalar cliente si caja esta libre
                if (caja.clienteActual == null && !colaCobro.isEmpty()) {
                    caja.clienteActual = colaCobro.poll();
                    //1 ciclo por producto de servicio
                    caja.tiempoServicioRestante = caja.clienteActual.carrito.size();
                }

                //Atender cliente
                if (caja.clienteActual != null) {
                    caja.tiempoServicioRestante--;

                    //Si termino
                    if (caja.tiempoServicioRestante <= 0) {
                        cobrarCliente(caja.clienteActual); //Aqui se descuenta stock
                        caja.clienteActual = null;
                        clientesAtendidos++;
                    }
                }
            }
        }

        private void cobrarCliente(Cliente c) {
            //El stock se descuenta al cobrar
            for (Producto p : c.carrito) {
                //Si hay stock, vender. Si no, rechazar excedente.
                if (p.stock > 0) {
                    p.stock--;
                    gananciasTotales += p.precio;
                } else {
                    productosNoVendidosPorStock++;
                }
            }
        }

        private boolean hayCajasOcupadas() {
            for (Caja c : cajas) if (c.clienteActual != null) return true;
            return false;
        }

        //Control de cierre / corte
        private void generarReporteFinal(long nanos, int ticksTotales) {
            log("\n=== CORTE DEL DÍA (Tick final: " + ticksTotales + ") ===");
            log("Clientes Atendidos: " + clientesAtendidos);
            log("Ganancias Totales: $" + String.format("%.2f", gananciasTotales));
            log("Prod. No Vendidos (Stock Agotado): " + productosNoVendidosPorStock);
            log("Tiempo Ejecución Real: " + (nanos / 1_000_000) + "ms");
            
            log("\n--- INFORME DE INVENTARIO Y REABASTO ---");
            log(String.format("%-6s %-12s %-8s %-15s", "SKU", "Nombre", "Stock", "Acción"));
            log("-----------------------------------------------");
            
            for (Producto p : inventario.values()) {
                String sugerencia = "OK";
                //Informe si stock <= umbral (ej. 10)
                if (p.stock <= 10) { 
                    int cantidadReponer = p.stockInicial - p.stock;
                    sugerencia = "IMPORTANTE: +" + cantidadReponer;
                }
                log(String.format("%-6s %-12s %-8d %-15s", p.sku, p.nombre, p.stock, sugerencia));
            }
            log("===============================================\n");
        }
    }
}