use std::cmp::Ordering;
use std::collections::{BTreeMap, BinaryHeap};
use std::env;

// Antony Portillo 25615
// Universidad del Valle de Guatemala
// Estructuras de Datos
// Seccion: 10
// Implementacion del algoritmo de Huffman para compresion de texto
// El algoritmo de Huffman asigna codigos mas cortos a los caracteres que mas se repiten

const DEFAULT_TEXT: &str = "estructura de datos y algoritmos";

fn main() {
    let args: Vec<String> = env::args().skip(1).collect();

    // Si no se manda texto por consola, se usa una frase de ejemplo
    let input = if args.is_empty() {
        DEFAULT_TEXT.to_string()
    } else {
        args.join(" ")
    };

    // Primero se codifica y luego se decodifica para comprobar que no se perdio informacion
    let result = HuffmanCoding::encode(&input);
    let decoded = HuffmanCoding::decode(&result).expect("No se pudo decodificar el texto");

    // Salida principal del programa
    println!("Algoritmo de Huffman");
    println!("--------------------");
    println!("Texto original: {}", input);
    println!("Texto codificado: {}", result.encoded_bits());
    println!("Texto decodificado: {}", decoded);
    println!();

    // Muestra cuantas veces aparece cada caracter
    println!("Frecuencias:");
    for (symbol, frequency) in result.frequencies() {
        println!("  {} -> {}", format_symbol(*symbol), frequency);
    }
    println!();

    // Muestra el codigo binario que Huffman asigno a cada caracter
    println!("Codigos generados:");
    for (symbol, code) in result.codes() {
        println!("  {} -> {}", format_symbol(*symbol), code);
    }
    println!();

    // Comparacion simple entre el texto original y el codificado
    println!("Estadisticas:");
    println!(
        "  Bits estimados sin comprimir: {}",
        result.estimated_original_bits()
    );
    println!("  Bits codificados: {}", result.encoded_bit_count());
    println!(
        "  Razon de compresion estimada: {:.2}%",
        result.compression_ratio() * 100.0
    );
}

#[derive(Debug)]
struct HuffmanCoding;

#[derive(Debug)]
struct EncodedData {
    encoded_bits: String,
    frequencies: BTreeMap<char, usize>,
    codes: BTreeMap<char, String>,
    root: Option<Box<Node>>,
    original_len: usize,
}

impl EncodedData {
    fn encoded_bits(&self) -> &str {
        &self.encoded_bits
    }

    fn frequencies(&self) -> &BTreeMap<char, usize> {
        &self.frequencies
    }

    fn codes(&self) -> &BTreeMap<char, String> {
        &self.codes
    }

    fn encoded_bit_count(&self) -> usize {
        self.encoded_bits.len()
    }

    fn estimated_original_bits(&self) -> usize {
        self.original_len * 8
    }

    fn compression_ratio(&self) -> f64 {
        let original_bits = self.estimated_original_bits();
        if original_bits == 0 {
            0.0
        } else {
            self.encoded_bit_count() as f64 / original_bits as f64
        }
    }
}

impl HuffmanCoding {
    fn encode(text: &str) -> EncodedData {
        let frequencies = count_frequencies(text);

        if text.is_empty() {
            return EncodedData {
                encoded_bits: String::new(),
                frequencies,
                codes: BTreeMap::new(),
                root: None,
                original_len: 0,
            };
        }

        let root = build_tree(&frequencies);
        let mut codes = BTreeMap::new();
        build_codes(root.as_ref().unwrap(), String::new(), &mut codes);

        let mut encoded_bits = String::new();
        for symbol in text.chars() {
            if let Some(code) = codes.get(&symbol) {
                encoded_bits.push_str(code);
            }
        }

        EncodedData {
            encoded_bits,
            frequencies,
            codes,
            root,
            original_len: text.chars().count(),
        }
    }

    fn decode(data: &EncodedData) -> Result<String, String> {
        decode_bits(data.encoded_bits(), data.root.as_deref())
    }

    #[cfg(test)]
    fn decode_with_model(bits: &str, data: &EncodedData) -> Result<String, String> {
        decode_bits(bits, data.root.as_deref())
    }
}

#[derive(Debug)]
struct Node {
    symbol: Option<char>,
    frequency: usize,
    left: Option<Box<Node>>,
    right: Option<Box<Node>>,
    smallest_symbol: char,
    order: usize,
}

impl Node {
    fn leaf(symbol: char, frequency: usize, order: usize) -> Self {
        Self {
            symbol: Some(symbol),
            frequency,
            left: None,
            right: None,
            smallest_symbol: symbol,
            order,
        }
    }

    fn internal(left: Box<Node>, right: Box<Node>, order: usize) -> Self {
        Self {
            symbol: None,
            frequency: left.frequency + right.frequency,
            smallest_symbol: left.smallest_symbol.min(right.smallest_symbol),
            left: Some(left),
            right: Some(right),
            order,
        }
    }

    fn is_leaf(&self) -> bool {
        self.left.is_none() && self.right.is_none()
    }
}

#[derive(Debug)]
struct HeapItem {
    frequency: usize,
    smallest_symbol: char,
    order: usize,
    node: Box<Node>,
}

impl HeapItem {
    fn new(node: Box<Node>) -> Self {
        Self {
            frequency: node.frequency,
            smallest_symbol: node.smallest_symbol,
            order: node.order,
            node,
        }
    }
}

impl Eq for HeapItem {}

impl PartialEq for HeapItem {
    fn eq(&self, other: &Self) -> bool {
        self.frequency == other.frequency
            && self.smallest_symbol == other.smallest_symbol
            && self.order == other.order
    }
}

impl Ord for HeapItem {
    fn cmp(&self, other: &Self) -> Ordering {
        other
            .frequency
            .cmp(&self.frequency)
            .then_with(|| other.smallest_symbol.cmp(&self.smallest_symbol))
            .then_with(|| other.order.cmp(&self.order))
    }
}

impl PartialOrd for HeapItem {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

fn count_frequencies(text: &str) -> BTreeMap<char, usize> {
    // Se cuentan las apariciones de cada caracter del texto
    let mut frequencies = BTreeMap::new();

    for symbol in text.chars() {
        *frequencies.entry(symbol).or_insert(0) += 1;
    }

    frequencies
}

fn build_tree(frequencies: &BTreeMap<char, usize>) -> Option<Box<Node>> {
    // La cola deja sacar siempre los dos nodos menos frecuentes
    let mut queue = BinaryHeap::new();
    let mut order = 0;

    for (symbol, frequency) in frequencies {
        queue.push(HeapItem::new(Box::new(Node::leaf(
            *symbol, *frequency, order,
        ))));
        order += 1;
    }

    while queue.len() > 1 {
        let left = queue.pop().unwrap().node;
        let right = queue.pop().unwrap().node;
        queue.push(HeapItem::new(Box::new(Node::internal(left, right, order))));
        order += 1;
    }

    queue.pop().map(|item| item.node)
}

fn build_codes(node: &Node, prefix: String, codes: &mut BTreeMap<char, String>) {
    // Al bajar por el arbol: izquierda agrega 0 y derecha agrega 1
    if node.is_leaf() {
        if let Some(symbol) = node.symbol {
            let code = if prefix.is_empty() {
                "0".to_string()
            } else {
                prefix
            };
            codes.insert(symbol, code);
        }
        return;
    }

    if let Some(left) = node.left.as_deref() {
        build_codes(left, format!("{}0", prefix), codes);
    }

    if let Some(right) = node.right.as_deref() {
        build_codes(right, format!("{}1", prefix), codes);
    }
}

fn decode_bits(bits: &str, root: Option<&Node>) -> Result<String, String> {
    let Some(root) = root else {
        if bits.is_empty() {
            return Ok(String::new());
        }
        return Err("No se puede decodificar usando un arbol vacio".to_string());
    };

    let mut decoded = String::new();

    if root.is_leaf() {
        for bit in bits.chars() {
            if bit != '0' {
                return Err("Para un solo simbolo, solo se esperan ceros".to_string());
            }
            decoded.push(root.symbol.unwrap());
        }
        return Ok(decoded);
    }

    let mut current = root;
    for bit in bits.chars() {
        // Se avanza por el arbol hasta llegar a una hoja
        current = match bit {
            '0' => current
                .left
                .as_deref()
                .ok_or_else(|| "La cadena de bits no coincide con este arbol".to_string())?,
            '1' => current
                .right
                .as_deref()
                .ok_or_else(|| "La cadena de bits no coincide con este arbol".to_string())?,
            _ => return Err("El texto codificado solo puede tener 0 y 1".to_string()),
        };

        if current.is_leaf() {
            decoded.push(current.symbol.unwrap());
            current = root;
        }
    }

    if !std::ptr::eq(current, root) {
        return Err("La cadena termino antes de completar un codigo".to_string());
    }

    Ok(decoded)
}

fn format_symbol(symbol: char) -> String {
    match symbol {
        ' ' => "' ' (espacio)".to_string(),
        '\n' => "\\n".to_string(),
        '\t' => "\\t".to_string(),
        '\r' => "\\r".to_string(),
        _ => symbol.to_string(),
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn codifica_y_decodifica_texto_con_repetidos() {
        assert_round_trip("banana bandana");
    }

    #[test]
    fn codifica_y_decodifica_texto_con_espacios() {
        assert_round_trip("algoritmos y estructuras de datos");
    }

    #[test]
    fn codifica_y_decodifica_texto_con_simbolos() {
        assert_round_trip("rust! rust! huffman 2026");
    }

    #[test]
    fn maneja_entrada_vacia() {
        let data = HuffmanCoding::encode("");

        assert_eq!("", data.encoded_bits());
        assert_eq!("", HuffmanCoding::decode(&data).unwrap());
    }

    #[test]
    fn maneja_un_solo_simbolo() {
        let data = HuffmanCoding::encode("aaaaaa");

        assert_eq!("000000", data.encoded_bits());
        assert_eq!("aaaaaa", HuffmanCoding::decode(&data).unwrap());
    }

    #[test]
    fn rechaza_bits_invalidos() {
        let data = HuffmanCoding::encode("aaaaaa");

        assert!(HuffmanCoding::decode_with_model("00101", &data).is_err());
    }

    fn assert_round_trip(input: &str) {
        let data = HuffmanCoding::encode(input);
        let decoded = HuffmanCoding::decode(&data).unwrap();

        assert_eq!(input, decoded);
    }
}
