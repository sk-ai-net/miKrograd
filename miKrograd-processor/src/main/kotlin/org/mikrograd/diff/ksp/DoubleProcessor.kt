package org.mikrograd.diff.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName


class DoubleToValueProcessor(env: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator = env.codeGenerator
    private val logger = env.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(Mikrograd::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()

        val invalidSymbols = symbols.filterNot { it.validate() }.toList()
        val validSymbols = symbols.filter { it.validate() }

        validSymbols.forEach { function ->
            processFunction(function)
        }

        return invalidSymbols
    }

    private fun processFunction(function: KSFunctionDeclaration) {
        val packageName = function.packageName.asString()
        val className = function.parentDeclaration?.simpleName?.asString() ?: "Generated"

        val fileSpecBuilder = FileSpec.builder(packageName, "${className}_MicrogradConversions")

        val transformedFunction = transformFunction(function)
        fileSpecBuilder.addFunction(transformedFunction)

        val fileSpec = fileSpecBuilder.build()
        codeGenerator.createNewFile(
            Dependencies(true, function.containingFile!!),
            packageName,
            "${className}_MicrogradConversions"
        ).use { outputStream ->
            outputStream.writer().use { writer ->
                fileSpec.writeTo(writer)
            }
        }
    }

    private fun transformFunction(function: KSFunctionDeclaration): FunSpec {
        val functionBuilder = FunSpec.builder(function.simpleName.asString() + "_MicrogradConversions")
            .addModifiers(KModifier.PUBLIC)

        function.parameters.forEach { parameter ->
            val name = parameter.name?.asString() ?: return@forEach
            val type = parameter.type.resolve()
            if (type.toClassName() == Double::class.asClassName()) {
                val valueType = ClassName("com.example", "Value")
                functionBuilder.addParameter(name, valueType)
            } else {
                functionBuilder.addParameter(name, type.toClassName())
            }
        }

        // Add the function body (not transformed for simplicity in this example)
        functionBuilder.addCode("// Original body not transformed for simplicity\n")

        return functionBuilder.build()
    }
}

