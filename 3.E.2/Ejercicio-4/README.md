# Ejercicio 4: Generadores de Facturas Comerciales

Unidad: **3.E.2**

Patrón: **Factory Method**

## Objetivo

Separar la creación del producto de su uso mediante una interfaz común y creadores concretos. El cliente trabaja con el producto o creador abstracto, sin instanciar directamente las implementaciones concretas.

## Implementación

La solución está en `src\Ejercicio04.java`. Incluye los productos, sus creadores especializados y un `main` que demuestra el flujo del ejercicio. Los servicios externos se simulan cuando corresponde; consulta el README de la raíz para el alcance didáctico.

## Compilar y ejecutar

Desde esta carpeta, con JDK 17 o posterior:

~~~powershell
javac --release 17 -encoding UTF-8 -d bin src\Ejercicio04.java
java -cp bin Ejercicio04
~~~

## Captura de ejecución

![Captura de la ejecución del ejercicio](captura/ejecucion.png)