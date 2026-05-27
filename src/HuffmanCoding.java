import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.TreeMap;

// Antony Portillo 25615
// UNIVERSIDAD DEL VALLE DE GUATEMALA
// Estructuras de Datos
// Seccion: 10
// Implementacion del algoritmo de Huffman para compresion de texto
// El algoritmo de Huffman es un metodo de compresion sin perdida que asigna codigos de longitud variable a simbolos segun su frecuencia
public final class HuffmanCoding {
    private HuffmanCoding() {
    }

    public static final class EncodedData {
        private final String encodedBits;
        private final Map<Character, Integer> frequencies;
        private final Map<Character, String> codes;
        private final Node root;
        private final int originalLength;

        private EncodedData(
                String encodedBits,
                Map<Character, Integer> frequencies,
                Map<Character, String> codes,
                Node root,
                int originalLength
        ) {
            this.encodedBits = encodedBits;
            this.frequencies = Collections.unmodifiableMap(frequencies);
            this.codes = Collections.unmodifiableMap(codes);
            this.root = root;
            this.originalLength = originalLength;
        }

        public String getEncodedBits() {
            return encodedBits;
        }

        public Map<Character, Integer> getFrequencies() {
            return frequencies;
        }

        public Map<Character, String> getCodes() {
            return codes;
        }

        public int getOriginalLength() {
            return originalLength;
        }

        public int getEncodedBitCount() {
            return encodedBits.length();
        }

        public int getEstimatedOriginalBitCount() {
            return originalLength * Byte.SIZE;
        }

        public double getCompressionRatio() {
            int originalBits = getEstimatedOriginalBitCount();
            if (originalBits == 0) {
                return 0.0;
            }
            return (double) getEncodedBitCount() / originalBits;
        }
    }

    public static EncodedData encode(String text) {
        Objects.requireNonNull(text, "El texto no puede ser null.");

        Map<Character, Integer> frequencies = countFrequencies(text);
        if (text.isEmpty()) {
            return new EncodedData("", frequencies, new LinkedHashMap<>(), null, 0);
        }

        Node root = buildTree(frequencies);
        Map<Character, String> codes = new LinkedHashMap<>();
        buildCodes(root, "", codes);

        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            encoded.append(codes.get(text.charAt(i)));
        }

        return new EncodedData(encoded.toString(), frequencies, codes, root, text.length());
    }

    public static String decode(EncodedData data) {
        Objects.requireNonNull(data, "Los datos codificados no pueden ser null.");
        return decode(data.encodedBits, data.root);
    }

    public static String decode(String bits, EncodedData model) {
        Objects.requireNonNull(model, "El modelo no puede ser null.");
        return decode(bits, model.root);
    }

    public static String formatSymbol(char symbol) {
        return switch (symbol) {
            case ' ' -> "' ' (space)";
            case '\n' -> "\\n";
            case '\t' -> "\\t";
            case '\r' -> "\\r";
            default -> Character.toString(symbol);
        };
    }

    private static Map<Character, Integer> countFrequencies(String text) {
        // Se cuentan las apariciones de cada caracter del texto
        Map<Character, Integer> frequencies = new TreeMap<>();
        for (int i = 0; i < text.length(); i++) {
            char symbol = text.charAt(i);
            frequencies.put(symbol, frequencies.getOrDefault(symbol, 0) + 1);
        }
        return new LinkedHashMap<>(frequencies);
    }

    private static Node buildTree(Map<Character, Integer> frequencies) {
        // La cola deja sacar siempre los dos nodos menos frecuentes
        PriorityQueue<Node> queue = new PriorityQueue<>();
        int order = 0;

        for (Map.Entry<Character, Integer> entry : frequencies.entrySet()) {
            queue.add(Node.leaf(entry.getKey(), entry.getValue(), order));
            order++;
        }

        while (queue.size() > 1) {
            Node left = queue.remove();
            Node right = queue.remove();
            queue.add(Node.internal(left, right, order));
            order++;
        }

        return queue.remove();
    }

    private static void buildCodes(Node node, String prefix, Map<Character, String> codes) {
        // Al bajar por el arbol: izquierda agrega 0 y derecha agrega 1
        if (node.isLeaf()) {
            codes.put(node.symbol, prefix.isEmpty() ? "0" : prefix);
            return;
        }

        buildCodes(node.left, prefix + "0", codes);
        buildCodes(node.right, prefix + "1", codes);
    }

    private static String decode(String bits, Node root) {
        Objects.requireNonNull(bits, "La cadena de bits no puede ser null.");

        if (root == null) {
            if (bits.isEmpty()) {
                return "";
            }
            throw new IllegalArgumentException("No se puede decodificar usando un arbol vacio.");
        }

        StringBuilder decoded = new StringBuilder();

        if (root.isLeaf()) {
            for (int i = 0; i < bits.length(); i++) {
                if (bits.charAt(i) != '0') {
                    throw new IllegalArgumentException("Para un solo simbolo, solo se esperan ceros.");
                }
                decoded.append(root.symbol);
            }
            return decoded.toString();
        }

        Node current = root;
        for (int i = 0; i < bits.length(); i++) {
            // Se avanza por el arbol hasta llegar a una hoja
            char bit = bits.charAt(i);
            if (bit == '0') {
                current = current.left;
            } else if (bit == '1') {
                current = current.right;
            } else {
                throw new IllegalArgumentException("El texto codificado solo puede tener 0 y 1.");
            }

            if (current == null) {
                throw new IllegalArgumentException("La cadena de bits no coincide con este arbol.");
            }

            if (current.isLeaf()) {
                decoded.append(current.symbol);
                current = root;
            }
        }

        if (current != root) {
            throw new IllegalArgumentException("La cadena termino antes de completar un codigo.");
        }

        return decoded.toString();
    }

    private static final class Node implements Comparable<Node> {
        private final Character symbol;
        private final int frequency;
        private final Node left;
        private final Node right;
        private final char smallestSymbol;
        private final int order;

        private Node(Character symbol, int frequency, Node left, Node right, char smallestSymbol, int order) {
            this.symbol = symbol;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
            this.smallestSymbol = smallestSymbol;
            this.order = order;
        }

        private static Node leaf(char symbol, int frequency, int order) {
            return new Node(symbol, frequency, null, null, symbol, order);
        }

        private static Node internal(Node left, Node right, int order) {
            char smallest = (char) Math.min(left.smallestSymbol, right.smallestSymbol);
            return new Node(null, left.frequency + right.frequency, left, right, smallest, order);
        }

        private boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(Node other) {
            int byFrequency = Integer.compare(this.frequency, other.frequency);
            if (byFrequency != 0) {
                return byFrequency;
            }

            int bySymbol = Character.compare(this.smallestSymbol, other.smallestSymbol);
            if (bySymbol != 0) {
                return bySymbol;
            }

            return Integer.compare(this.order, other.order);
        }
    }
}
