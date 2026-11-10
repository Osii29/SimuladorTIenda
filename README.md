# Nabi Store – Plataforma Web de Comercio Electrónico

Interfaz web *frontend* de comercio electrónico diseñada para **Nabi Store**, una tienda especializada en botanas, ramen y bebidas importadas de Asia. El proyecto implementa un catálogo interactivo con selección multinivel de variantes, carrito de compras deslizable en tiempo real y flujo de confirmación de pedido utilizando **HTML5, CSS3 y JavaScript Vanilla**.

---

##  Características Principales

* **Modal Dinámico con Selección Multinivel:** Sistema inteligente en JavaScript (`openProductModal`) que detecta automáticamente la estructura de datos de cada producto:
  * **Arreglos simples:** Despliega un selector directo de sabores (ej. *Buldak Ramen*, *Soju*, *Pocky*).
  * **Objetos anidados:** Activa un submenú dinámico de marcas (ej. *Coca-Cola*, *Fanta*, *Pepsi*, *Marcas Varias*) que actualiza en cascada las opciones de sabor disponibles (`updateFlavorOptions`).
* **Carrito de Compras Interactivo (*Off-Canvas Sidebar*):** Panel lateral animado con transiciones `cubic-bezier` que permite agregar productos personalizados, eliminar artículos individuales, vaciar la cesta y calcular el monto total acumulado mediante funciones de orden superior (`Array.prototype.reduce`).
* **Diseño Responsivo y Moderno:** Maquetación adaptable a dispositivos móviles y escritorio utilizando **CSS Grid** (`auto-fit` / `minmax`), **Flexbox**, tipografía fluida con `clamp()` y variables nativas de CSS (`:root`) para mantener la consistencia de la paleta de colores.
* **Simulación de Checkout y Contacto:** Validación de formularios de dirección de envío (código postal, calle, colonia), selección de método de pago y formulario de atención al cliente.

---

##  Tecnologías Utilizadas

* **Estructura:** HTML5 Semántico
* **Estilos y Diseño:** CSS3 (Custom Properties, Flexbox, CSS Grid, Media Queries implícitas)
* **Interactividad y Lógica:** JavaScript ES6+ (Manipulación del DOM, Eventos, Manejo de Estado del Carrito)

---

##  Estructura del Proyecto

```text
├── index.html                   # Estructura principal, catálogo de productos, modal y sidebar del carrito
├── styles.css                   # Variables de diseño, layout responsivo, animaciones y estilos de componentes
├── script.js                    # Lógica del carrito de compras, modales dinámicos y validación de pago
├── imagen_logo_nabistore.png    # Recursos gráficos (Logo, banner y fotografías de productos)
└── README.md                    # Documentación del proyecto
```

---

##  Instalación y Uso Local

No requiere dependencias externas ni servidores de compilación.

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/Osii29/nombre-de-tu-repo-nabistore.git
   ```
2. **Ejecutar el proyecto:**
   Abre el archivo `index.html` directamente en cualquier navegador web moderno o utiliza la extensión **Live Server** en Visual Studio Code.

---

##  Autor

**Osiel Carapia Ruiz**  
*Ingeniería en Computación – UNAM (FES Aragón)*  
[LinkedIn](https://www.linkedin.com/in/osielcarapiaruiz) | [GitHub](https://github.com/Osii29)
