package org.mikrograd.diff.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.writeTo
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File

// KSP Processor
class ComputeGraphProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Mikrograd::class.qualifiedName!!)
        logger.info("Found ${symbols.count()} symbols with @Mikrograd annotation")
        val invalidSymbols = symbols.filter { !it.validate() }.toList()
        logger.info("Found ${invalidSymbols.size} invalid symbols")

        symbols.filter { it is KSFunctionDeclaration && it.validate() }
            .forEach { symbol ->
                val function = symbol as KSFunctionDeclaration
                logger.info("Processing function: ${function.simpleName.asString()}")
                logger.info("  - Package: ${function.packageName.asString()}")
                logger.info("  - File: ${function.containingFile?.fileName}")
                logger.info("  - Parameters: ${function.parameters.map { it.name?.asString() to it.type.resolve().declaration.qualifiedName?.asString() }}")
                logger.info("  - Return type: ${function.returnType?.resolve()?.declaration?.qualifiedName?.asString()}")

                // Extract the computation mode from the annotation
                val annotation = function.annotations.find {
                    it.shortName.asString() == "Mikrograd"
                }

                // Default to INFERENCE if the mode argument is not specified
                val modeArgument = annotation?.arguments?.find { it.name?.asString() == "mode" }
                val modeValue = modeArgument?.value?.toString() ?: "INFERENCE"

                // Extract just the enum constant name (INFERENCE or TRAINING) from the fully qualified name
                val enumConstantName = modeValue.substringAfterLast('.', modeValue)
                val mode = ComputationMode.valueOf(enumConstantName)

                logger.info("  - Computation mode: $mode")

                try {
                    generateComputeGraphCode(function, mode)
                } catch (e: Exception) {
                    logger.error("Failed to process function ${function.simpleName.asString()}: ${e.message}", symbol)
                }
            }

        return invalidSymbols
    }

    private fun generateComputeGraphCode(function: KSFunctionDeclaration, mode: ComputationMode) {
        val packageName = function.packageName.asString()
        val fileName = "${function.simpleName.asString()}Generated"
        logger.info("Generating code for function: ${function.simpleName.asString()}")
        logger.info("  - Output file: $packageName.$fileName")
        logger.info("  - Computation mode: $mode")

        // Log AST details
        logger.info("  - AST details:")
        logger.info("    - Modifiers: ${function.modifiers.map { it.name }}")
        logger.info("    - Documentation: ${function.docString}")
        logger.info("    - Location: ${function.location}")

        // Extract the function body as a string
        val functionBody = extractFunctionBody(function)
        // If we couldn't extract the function body, use a default expression
        val expressionString = functionBody ?: "3.0 * 8.0 + (7.0 + 3.0)"
        logger.info("  - Extracted expression: $expressionString")

        // Parse the expression and generate code
        val parser = ExpressionParser()

        // Use the appropriate visitor based on the mode
        val visitor = DifferentiationVisitor(mode)
        val codeBlock = parser.parseExpression(expressionString, visitor)

        // Get the last variable name from the code block
        val lastVarName = extractLastVarName(codeBlock.toString())
        logger.info("  - Last variable name: $lastVarName")

        val fileSpec = FileSpec.builder(packageName, fileName)

        // Build the function using KotlinPoet, wrapping the entire code in a single context block
        val funSpec = FunSpec.builder(function.simpleName.asString() + "Generated")
            .returns(ClassName("org.mikrograd.diff", if (mode == ComputationMode.INFERENCE) "AutoDiffNode" else "BackpropNode"))
            .addCode(codeBlock)
            .addStatement("return $lastVarName")
            .build()

        logger.info("  - Function spec created: ${funSpec.name}")

        // Add imports based on the computation mode
        val imports = mutableListOf(
            "org.mikrograd.core.ComputeNode",
            "org.mikrograd.core.ValueNode",
            "org.mikrograd.core.MultiplyNode",
            "org.mikrograd.core.AddNode",
            "org.mikrograd.diff.ForwardPassNode"
        )

        // Add mode-specific imports
        if (mode == ComputationMode.INFERENCE) {
            imports.add("org.mikrograd.diff.ForwardPassNode")
        } else {
            imports.add("org.mikrograd.diff.BackpropNode")
        }

        // Add ValueInterface import
        imports.add("org.mikrograd.diff.AutoDiffNode")

        // Write the file with imports
        fileSpec.addFileComment("Generated by ComputeGraphProcessor")
            .addFileComment(" - Mode: $mode")

        // Add imports
        imports.forEach { importPath ->
            val lastDot = importPath.lastIndexOf('.')
            val packageName = importPath.substring(0, lastDot)
            val className = importPath.substring(lastDot + 1)
            fileSpec.addImport(packageName, className)
        }

        fileSpec.addFunction(funSpec)
            .build()
            .writeTo(codeGenerator, Dependencies(false, function.containingFile!!))

        logger.info("  - Code generation completed for ${function.simpleName.asString()}")
    }

    /**
     * Extract the variable name from the last statement in a code block.
     * This is a simplistic implementation that assumes the last statement
     * in the code block is a variable declaration.
     * @param codeBlock The code block to extract from
     * @return The variable name
     */
    private fun extractLastVarName(codeBlock: String): String {
        // Find the last variable declaration in the code block
        val statements = codeBlock.trim().split("\n")
        for (i in statements.indices.reversed()) {
            val statement = statements[i]
            val match = Regex("val (\\w+)").find(statement)
            if (match != null) {
                return match.groupValues[1]
            }
        }

        // If no variable declaration is found, return a default name
        return "resultNode"
    }

    /**
     * Extract the function body as a string from a KSFunctionDeclaration.
     * This method reads the source file directly and extracts the function body
     * based on the function's location in the file.
     * @param function The function declaration
     * @return The function body as a string, or null if it couldn't be extracted
     */
    private fun extractFunctionBody(function: KSFunctionDeclaration): String? {
        try {
            // Get the file path from the containing file
            val filePath = function.containingFile?.filePath ?: return null
            logger.info("  - Source file path: $filePath")

            // Read the file content
            val fileContent = File(filePath).readText()
            logger.info("  - File content length: ${fileContent.length}")

            // Get the function's location in the file
            val location = function.location
            logger.info("  - Function location: $location")

            // Extract the function body by finding the opening and closing braces
            // or by finding the expression body after the equals sign
            val functionName = function.simpleName.asString()

            // First try to match a function with a body enclosed in braces
            val blockBodyPattern =
                Regex("fun\\s+$functionName\\s*\\([^)]*\\)\\s*\\{([\\s\\S]*?)\\}", RegexOption.DOT_MATCHES_ALL)
            val blockBodyMatch = blockBodyPattern.find(fileContent)

            if (blockBodyMatch != null && blockBodyMatch.groupValues.size > 1) {
                val functionBody = blockBodyMatch.groupValues[1].trim()
                logger.info("  - Extracted block body: $functionBody")
                return functionBody
            }

            // If that fails, try to match a function with an expression body
            val exprBodyPattern = Regex(
                "fun\\s+$functionName\\s*\\([^)]*\\)(?:\\s*:\\s*[^=]+)?\\s*=\\s*([^{]+)\\{([\\s\\S]*?)\\}",
                RegexOption.DOT_MATCHES_ALL
            )
            val exprBodyMatch = exprBodyPattern.find(fileContent)

            if (exprBodyMatch != null && exprBodyMatch.groupValues.size > 2) {
                // For expression bodies, we're interested in the content inside the curly braces
                val contextFunction = exprBodyMatch.groupValues[1].trim()
                val functionBody = exprBodyMatch.groupValues[2].trim()
                logger.info("  - Extracted expression body with context function $contextFunction: $functionBody")
                return functionBody
            }

            // Log the function declaration for debugging
            logger.error("Function declaration not matched by regex patterns")
            val functionDeclarationPattern = Regex("fun\\s+$functionName[^{]*", RegexOption.DOT_MATCHES_ALL)
            val functionDeclarationMatch = functionDeclarationPattern.find(fileContent)
            if (functionDeclarationMatch != null) {
                logger.error("Function declaration: ${functionDeclarationMatch.value}")
            }

            logger.error("Failed to extract function body for ${function.simpleName.asString()}")
            return null
        } catch (e: Exception) {
            logger.error("Error extracting function body: ${e.message}")
            return null
        }
    }

    companion object {
        private val DOUBLE = ClassName("kotlin", "Double")
    }
}

class ComputeGraphProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ComputeGraphProcessor(environment.codeGenerator, environment.logger)
    }
}
