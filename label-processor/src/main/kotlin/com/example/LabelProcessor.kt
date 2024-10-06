package com.example

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

class LabelProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return LabelProcessor(environment)
    }
}

class LabelProcessor(
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // Collect variables anew each time process() is called
        val symbols = resolver.getSymbolsWithAnnotation(Label::class.qualifiedName!!)
    val variables = symbols.filterIsInstance<KSPropertyDeclaration>().toList()

    if (variables.isNotEmpty()) {
        generateLabelSetter(variables)
    }

        return emptyList()
    }

    private fun generateLabelSetter(variables: List<KSPropertyDeclaration>) {
        val packageName = variables.first().packageName.asString()
        val fileName = "LabelSetter"

        // Collect originating files
        val originatingFiles = variables.mapNotNull { it.containingFile }.distinct().toTypedArray()

        val file = codeGenerator.createNewFile(
            Dependencies(aggregating = true, *originatingFiles),
            packageName,
            fileName,
            extensionName = "kt",
            // Set overwrite to true to handle existing files
            //overwrite = true
        )

        val codeBuilder = StringBuilder()
        codeBuilder.appendLine("package $packageName")
        codeBuilder.appendLine()
        codeBuilder.appendLine("fun setAllLabels() {")

        variables.forEach { property ->
            val variableName = property.simpleName.asString()
            codeBuilder.appendLine("    $variableName.label = \"$variableName\"")
        }

        codeBuilder.appendLine("}")

        file.bufferedWriter().use { it.write(codeBuilder.toString()) }
    }
}
