package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class ScannerTest {
    @Test
    void scansSimpleOperatorsAndPunctuation() {
        String source = "+ - * / ( ) { } , . ;";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        TokenType[] expectedTypes = {
            TokenType.PLUS, TokenType.MINUS, TokenType.STAR, TokenType.SLASH,
            TokenType.LEFT_PAREN, TokenType.RIGHT_PAREN, TokenType.LEFT_BRACE, TokenType.RIGHT_BRACE,
            TokenType.COMMA, TokenType.DOT, TokenType.SEMICOLON, TokenType.EOF
        };

        assertEquals(expectedTypes.length, tokens.size());
        for (int i = 0; i < expectedTypes.length; i++) {
            assertEquals(expectedTypes[i], tokens.get(i).type, "token " + i + " type mismatch");
        }
    }

    @Test
    void scansNumbersAndLiterals() {
        String source = "123 3.14159 0 42.0";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals("123", tokens.get(0).lexeme);
        assertEquals(123.0, ((Number) tokens.get(0).literal).doubleValue(), 1e-9);

        assertEquals(TokenType.NUMBER, tokens.get(1).type);
        assertEquals("3.14159", tokens.get(1).lexeme);
        assertEquals(3.14159, ((Number) tokens.get(1).literal).doubleValue(), 1e-9);

        assertEquals(TokenType.NUMBER, tokens.get(2).type);
        assertEquals("0", tokens.get(2).lexeme);
        assertEquals(0.0, ((Number) tokens.get(2).literal).doubleValue(), 1e-9);

        assertEquals(TokenType.NUMBER, tokens.get(3).type);
        assertEquals("42.0", tokens.get(3).lexeme);
        assertEquals(42.0, ((Number) tokens.get(3).literal).doubleValue(), 1e-9);

        assertEquals(TokenType.EOF, tokens.get(tokens.size() - 1).type);
    }

    @Test
    void scansStringsRespectingQuotesAndLiteralValue() {
        String source = "\"hello world\" \"\" \"line\nbreak\"";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        assertEquals(TokenType.STRING, tokens.get(0).type);
        assertEquals("\"hello world\"", tokens.get(0).lexeme);
        assertEquals("hello world", tokens.get(0).literal);

        assertEquals(TokenType.STRING, tokens.get(1).type);
        assertEquals("\"\"", tokens.get(1).lexeme);
        assertEquals("", tokens.get(1).literal);

        assertEquals(TokenType.STRING, tokens.get(2).type);
        assertEquals("\"line\nbreak\"", tokens.get(2).lexeme);
        assertEquals("line\nbreak", tokens.get(2).literal);
    }

    @Test
    void scansIdentifiersAndKeywords() {
        String source = "and class var this identifier fooBar _underscore";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        assertEquals(TokenType.AND, tokens.get(0).type);
        assertEquals(TokenType.CLASS, tokens.get(1).type);
        assertEquals(TokenType.VAR, tokens.get(2).type);
        assertEquals(TokenType.THIS, tokens.get(3).type);

        assertEquals(TokenType.IDENTIFIER, tokens.get(4).type);
        assertEquals("identifier", tokens.get(4).lexeme);

        assertEquals(TokenType.IDENTIFIER, tokens.get(5).type);
        assertEquals("fooBar", tokens.get(5).lexeme);

        assertEquals(TokenType.IDENTIFIER, tokens.get(6).type);
        assertEquals("_underscore", tokens.get(6).lexeme);
    }

    @Test
    void ignoresCommentsAndHandlesLineNumbers() {
        String source = "var a = 1; // this is a comment\nvar b = 2;\n// full line comment\nvar c = 3;";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        // Find tokens for the three var declarations and check their line numbers roughly
        // var a = 1 ;  => tokens starting at index 0: VAR, IDENTIFIER, EQUAL, NUMBER, SEMICOLON
        assertEquals(TokenType.VAR, tokens.get(0).type);
        assertEquals(1, tokens.get(0).line);

        // skip ahead to second var declaration (after first semicolon)
        int secondVarIndex = 5; // depends on tokenization; if mismatch adjust upon review
        assertEquals(TokenType.VAR, tokens.get(secondVarIndex).type);
        assertEquals(2, tokens.get(secondVarIndex).line);

        // third var should be on line 4 (because of blank or comment line)
        int thirdVarIndex = secondVarIndex + 5; // approximate; adjust if tokens differ
        assertEquals(TokenType.VAR, tokens.get(thirdVarIndex).type);
        assertEquals(4, tokens.get(thirdVarIndex).line);
    }

    @Test
    void reportsTwoCharacterOperators() {
        String source = "! != = == < <= > >= ";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        TokenType[] expected = {
            TokenType.BANG, TokenType.BANG_EQUAL,
            TokenType.EQUAL, TokenType.EQUAL_EQUAL,
            TokenType.LESS, TokenType.LESS_EQUAL,
            TokenType.GREATER, TokenType.GREATER_EQUAL,
            TokenType.EOF
        };

        assertEquals(expected.length, tokens.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).type, "token " + i + " mismatch");
        }
    }
}
