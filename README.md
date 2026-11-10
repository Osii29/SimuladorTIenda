# Simulador Discreto de Gestión y Flujo de Caja Registradora

Motor de simulación por eventos discretos (basado en ciclos/*ticks*) desarrollado en **Java** con interfaz gráfica en **Swing**. El sistema modela la llegada estocástica de clientes a un supermercado, la selección de artículos desde el inventario, el balanceo dinámico de colas en múltiples cajas registradoras y el corte financiero y de reabastecimiento al finalizar la jornada.

---

## Características Principales

* **Asignación Dinámica de Cajas (Load Balancing):** Algoritmo que evalúa en cada ciclo la saturación de la fila única de cobro. Abre nuevas cajas automáticamente (hasta un máximo de 4) cuando el promedio de espera supera $\ge 4$ clientes por caja activa, y cierra cajas desocupadas cuando el flujo disminuye a $\le 1$.
* **Estructuras de Datos de Alto Rendimiento:**
  * `HashMap<String, Producto>` para búsqueda y descuento de inventario en tiempo constante $\mathcal{O}(1)$.
  * `Queue<Cliente>` (`LinkedList`) bajo política FIFO (*First-In, First-Out*) con inserciones y extracciones $\mathcal{O}(1)$ para la fila de cobro.
  * `ArrayList` e iteradores seguros para gestionar la concurrencia de clientes seleccionando artículos en tienda.
* **Ejecución Multihilo (Multithreading):** Uso de hilos independientes (`Thread`) y sincronización mediante `SwingUtilities.invokeLater()` para ejecutar tandas de hasta 100 simulaciones consecutivas de alta carga sin bloquear el hilo de eventos de la interfaz gráfica (EDT).
* **Escalado Automático y Tasa Estocástica:** Cálculo adaptativo de la tasa máxima de llegada $k$ según la meta de clientes configurada por el usuario:
  $$k = \left\lceil \frac{\text{MetaClientes}}{24} \times 2 \right\rceil$$
* **Corte de Caja y Auditoría de Reabasto:** Al concluir los 24 ciclos laborales y vaciar las colas remanentes, genera un reporte detallado con ganancias totales, ventas perdidas por quiebre de stock, tiempo real de ejecución en milisegundos ($\text{ms}$) y sugerencias automáticas de reposición por SKU.

---

## Tecnologías y Conceptos Aplicados

* **Lenguaje:** Java (JDK 8+)
* **Interfaz Gráfica:** Java Swing (`JFrame`, `JPanel`, `JTextArea`, `JScrollPane`)
* **Paradigmas y Conceptos:** Programación Orientada a Objetos (POO), Estructuras de Datos Lineales y No Lineales, Concurrencia básica (`Thread` / `SwingUtilities`), Simulación Estocástica.

---

## Estructura del Proyecto

```text
├── VentanaSimulador.java   # Interfaz gráfica en Java Swing, control de parámetros e hilos de ejecución
├── SimuladorTienda.java    # Modelos de dominio (Producto, Cliente, Caja) y motor lógico de la simulación
└── README.md               # Documentación del proyecto
```

---

## Compilación y Ejecución

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Osii29/nombre-de-tu-repo-simulador.git
   cd nombre-de-tu-repo-simulador
   ```

2. **Compilar los archivos fuente:**
   ```bash
   javac SimuladorTienda.java VentanaSimulador.java
   ```

3. **Ejecutar la interfaz del simulador:**
   ```bash
   java VentanaSimulador
   ```

---

## Parámetros de Operación

Desde el panel superior de la ventana puedes configurar:
1. **Cant. Clientes por simulación:** Define la demanda esperada durante la jornada (ej. `100` o `1000` clientes). El stock inicial del inventario escala automáticamente en escenarios de alto tráfico.
2. **Repeticiones:** Permite correr lotes de `1`, `10` o `100` simulaciones continuas para analizar el comportamiento estadístico del sistema.

---

## Autor

**Osiel Carapia Ruiz**  
*Ingeniería en Computación – UNAM (FES Aragón)*  
[LinkedIn](https://www.linkedin.com/in/osielcarapiaruiz) | [GitHub](https://github.com/Osii29)