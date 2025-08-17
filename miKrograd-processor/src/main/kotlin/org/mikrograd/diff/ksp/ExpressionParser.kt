package org.mikrograd.diff.ksp

import com.squareup.kotlinpoet.CodeBlock

/**
 * Parser for mathematical expressions.
 * This class parses a simple expression and builds a compute graph using the visitor pattern.
 */
class ExpressionParser {
    /**
     * Parse an expression and generate code for the compute graph.
     * @param expression The expression to parse
     * @param visitor The visitor to use for code generation (defaults to CodeGeneratingVisitor)
     * @return The code block representing the compute graph
     */
    fun parseExpression(expression: String, visitor: ComputeNodeVisitor<Double> = CodeGeneratingVisitor()): CodeBlock {
        val tokens = tokenize(expression)
        val ast = buildAST(tokens)
        return generateCode(ast, visitor)
    }

    /**
     * Tokenize an expression into a list of tokens.
     * @param expression The expression to tokenize
     * @return The list of tokens
     */
    private fun tokenize(expression: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        while (i < expression.length) {
            val c = expression[i]
            when {
                c.isDigit() || c == '.' -> {
                    var j = i
                    while (j < expression.length && (expression[j].isDigit() || expression[j] == '.')) {
                        j++
                    }
                    tokens.add(Token.Number(expression.substring(i, j).toDouble()))
                    i = j
                }
                c == '+' -> {
                    tokens.add(Token.Plus)
                    i++
                }
                c == '*' -> {
                    tokens.add(Token.Times)
                    i++
                }
                c == '(' -> {
                    tokens.add(Token.LeftParen)
                    i++
                }
                c == ')' -> {
                    tokens.add(Token.RightParen)
                    i++
                }
                c.isWhitespace() -> {
                    i++
                }
                else -> {
                    throw IllegalArgumentException("Unexpected character: $c")
                }
            }
        }
        return tokens
    }

    /**
     * Build an abstract syntax tree (AST) from a list of tokens.
     * @param tokens The list of tokens
     * @return The root node of the AST
     */
    private fun buildAST(tokens: List<Token>): ASTNode {
        // This is a simple recursive descent parser for expressions
        // It handles the following grammar:
        // expr = term { "+" term }
        // term = factor { "*" factor }
        // factor = number | "(" expr ")"

        var pos = 0

        // Forward declarations
        lateinit var parseExpr: () -> ASTNode
        lateinit var parseTerm: () -> ASTNode
        lateinit var parseFactor: () -> ASTNode

        // Implementation
        parseExpr = {
            var left = parseTerm()
            while (pos < tokens.size && tokens[pos] == Token.Plus) {
                pos++
                val right = parseTerm()
                left = ASTNode.Add(left, right)
            }
            left
        }

        parseTerm = {
            var left = parseFactor()
            while (pos < tokens.size && tokens[pos] == Token.Times) {
                pos++
                val right = parseFactor()
                left = ASTNode.Multiply(left, right)
            }
            left
        }

        parseFactor = {
            when (val token = tokens[pos++]) {
                is Token.Number -> ASTNode.Value(token.value)
                Token.LeftParen -> {
                    val expr = parseExpr()
                    if (pos < tokens.size && tokens[pos] == Token.RightParen) {
                        pos++
                        expr
                    } else {
                        throw IllegalArgumentException("Expected closing parenthesis")
                    }
                }
                else -> throw IllegalArgumentException("Unexpected token: $token")
            }
        }

        return parseExpr()
    }

    /**
     * Generate code for an AST using a visitor.
     * @param ast The AST to generate code for
     * @param visitor The visitor to use
     * @return The generated code
     */
    private fun generateCode(ast: ASTNode, visitor: ComputeNodeVisitor<Double>): CodeBlock {
        return when (ast) {
            is ASTNode.Value -> visitor.visitValueNode(ast.value, "const_${ast.value}")
            is ASTNode.Add -> visitor.visitAddNode(
                generateCode(ast.left, visitor),
                generateCode(ast.right, visitor),
                "add_${ast.left}_${ast.right}"
            )
            is ASTNode.Multiply -> visitor.visitMultiplyNode(
                generateCode(ast.left, visitor),
                generateCode(ast.right, visitor),
                "multiply_${ast.left}_${ast.right}"
            )
        }
    }

    /**
     * Token types for the tokenizer.
     */
    sealed class Token {
        data class Number(val value: Double) : Token()
        object Plus : Token()
        object Times : Token()
        object LeftParen : Token()
        object RightParen : Token()
    }

    /**
     * AST node types for the parser.
     */
    sealed class ASTNode {
        data class Value(val value: Double) : ASTNode()
        data class Add(val left: ASTNode, val right: ASTNode) : ASTNode()
        data class Multiply(val left: ASTNode, val right: ASTNode) : ASTNode()
    }
}
