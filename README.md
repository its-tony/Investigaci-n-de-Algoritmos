# Investigacion de Algoritmos

Este proyecto contiene una implementacion en Rust del algoritmo de Huffman.

La idea principal del algoritmo es contar cuantas veces aparece cada caracter
en un texto y, con esas frecuencias, construir un arbol binario. Los caracteres
que mas se repiten quedan con codigos mas cortos, mientras que los menos
frecuentes reciben codigos mas largos.

## Estructura

- `src/main.rs`: contiene la implementacion, la ejecucion por consola y las pruebas.
- `Cargo.toml`: archivo de configuracion del proyecto Rust.

## Requisitos

- Rust instalado.
- Cargo, que normalmente viene incluido al instalar Rust.

## Compilar

```powershell
cargo build
```

## Ejecutar el programa

```powershell
cargo run
```

Tambien se puede probar con otro texto:

```powershell
cargo run -- "banana bandana"
```

## Ejecutar pruebas

```powershell
cargo test
```

## Pruebas incluidas

- Texto normal con caracteres repetidos.
- Texto con espacios.
- Texto con simbolos y numeros.
- Entrada vacia.
- Entrada con un solo simbolo distinto.
- Deteccion de una cadena de bits incorrecta.
