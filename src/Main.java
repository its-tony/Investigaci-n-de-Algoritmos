// Antony Portillo 25615
// UNIVERSIDAD DEL VALLE DE GUATEMALA
// Estructuras de Datos
// Seccion: 10
// Implementacion del algoritmo de Huffman para compresion de texto
// El algoritmo de Huffman es un metodo de compresion sin perdida que asigna codigos de longitud variable a simbolos segun su frecuencia
import java.util.Map;

public class Main {
    private static final String DEFAULT_TEXT = "estructura de datos y algoritmos";

    public static void main(String[] args) {
        // Esta opcion permite correr las pruebas desde el mismo Main
        if (args.length > 0 && "--test".equals(args[0])) {
            HuffmanDemoTests.main(new String[0]);
            return;
        }

        // Si no se manda texto por consola, se usa una frase de ejemplo
        String input = args.length == 0 ? DEFAULT_TEXT : String.join(" ", args);

        // Primero se codifica el texto y luego se decodifica para comprobar el resultado
        HuffmanCoding.EncodedData result = HuffmanCoding.encode(input);
        String decoded = HuffmanCoding.decode(result);

        // Salida principal del programa
        System.out.println("Algoritmo de Huffman");
        System.out.println("--------------------");
        System.out.println("Texto original: " + input);
        System.out.println("Texto codificado: " + result.getEncodedBits());
        System.out.println("Texto decodificado: " + decoded);
        System.out.println();

        // Muestra cuantas veces aparece cada caracter
        System.out.println("Frecuencias:");
        for (Map.Entry<Character, Integer> entry : result.getFrequencies().entrySet()) {
            System.out.printf("  %s -> %d%n", HuffmanCoding.formatSymbol(entry.getKey()), entry.getValue());
        }
        System.out.println();

        // Muestra el codigo binario que Huffman asigno a cada caracter
        System.out.println("Codigos generados:");
        for (Map.Entry<Character, String> entry : result.getCodes().entrySet()) {
            System.out.printf("  %s -> %s%n", HuffmanCoding.formatSymbol(entry.getKey()), entry.getValue());
        }
        System.out.println();

        // Comparacion simple entre el texto original y el codificado
        System.out.println("Estadisticas:");
        System.out.println("  Bits estimados sin comprimir: " + result.getEstimatedOriginalBitCount());
        System.out.println("  Bits codificados: " + result.getEncodedBitCount());
        System.out.printf("  Razon de compresion estimada: %.2f%%%n", result.getCompressionRatio() * 100);
    }
}
