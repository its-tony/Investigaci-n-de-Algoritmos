# Investigacion de Algoritmos

Este proyecto contiene una implementacion en Java del algoritmo de Huffman.

La idea principal del algoritmo es contar cuantas veces aparece cada caracter
en un texto y, con esas frecuencias, construir un arbol binario. Los caracteres
que mas se repiten quedan con codigos mas cortos, mientras que los menos
frecuentes reciben codigos mas largos.

## Estructura

- `src/HuffmanCoding.java`: contiene la logica del algoritmo.
- `src/Main.java`: sirve para ejecutar una demostracion desde consola.
- `src/HuffmanDemoTests.java`: tiene pruebas sencillas del programa.

## Requisitos

- Java JDK 17 o superior.

## Compilar

```powershell
javac -d out src\*.java
```

## Ejecutar el programa

```powershell
java -cp out Main
```

Tambien se puede probar con otro texto:

```powershell
java -cp out Main "banana bandana"
```

## Ejecutar pruebas

```powershell
java -cp out HuffmanDemoTests
```

O desde el programa principal:

```powershell
java -cp out Main --test
```

## Pruebas incluidas

- Texto normal con caracteres repetidos.
- Texto con espacios.
- Entrada vacia.
- Entrada con un solo simbolo distinto.
- Deteccion de una cadena de bits incorrecta.
