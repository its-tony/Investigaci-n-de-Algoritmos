public class HuffmanDemoTests {
    public static void main(String[] args) {
        testRoundTripWithRepeatedCharacters();
        testRoundTripWithSpaces();
        testEmptyInput();
        testSingleSymbolInput();
        testInvalidBitString();
        System.out.println("Todas las pruebas pasaron correctamente.");
    }

    private static void testRoundTripWithRepeatedCharacters() {
        assertRoundTrip("banana bandana");
    }

    private static void testRoundTripWithSpaces() {
        assertRoundTrip("algoritmos y estructuras de datos");
    }

    private static void testEmptyInput() {
        HuffmanCoding.EncodedData data = HuffmanCoding.encode("");
        assertEquals("", data.getEncodedBits(), "La cadena vacia debe producir codificacion vacia.");
        assertEquals("", HuffmanCoding.decode(data), "La cadena vacia debe decodificarse como vacia.");
    }

    private static void testSingleSymbolInput() {
        HuffmanCoding.EncodedData data = HuffmanCoding.encode("aaaaaa");
        assertEquals("000000", data.getEncodedBits(), "Un solo simbolo debe codificarse con ceros.");
        assertEquals("aaaaaa", HuffmanCoding.decode(data), "La decodificacion de un solo simbolo fallo.");
    }

    private static void testInvalidBitString() {
        HuffmanCoding.EncodedData data = HuffmanCoding.encode("aaaaaa");
        try {
            HuffmanCoding.decode("00101", data);
            throw new AssertionError("Se esperaba una excepcion por bits invalidos.");
        } catch (IllegalArgumentException expected) {
        // Si llega aqui, la prueba salio bien :D
        }
    }

    private static void assertRoundTrip(String input) {
        HuffmanCoding.EncodedData data = HuffmanCoding.encode(input);
        String decoded = HuffmanCoding.decode(data);
        assertEquals(input, decoded, "El texto decodificado no coincide con el original.");
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " Esperado: " + expected + " Actual: " + actual);
        }
    }
}
