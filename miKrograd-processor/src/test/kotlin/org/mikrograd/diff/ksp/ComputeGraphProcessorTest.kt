package org.mikrograd.diff.ksp

import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertTrue

class ComputeGraphProcessorTest {

    @Test
    fun testProcessorGeneratesCodeWithDefaultMode() {
        // Create a test Kotlin source file with a function annotated with @Mikrograd
        val sourceCode = """
            @org.mikrograd.diff.ksp.Mikrograd
            fun testExpr() {
                3.0 * 4.0 + (7.0 + 3.0)
            }
        """
        val source = SourceFile.kotlin("test/TestFile.kt", sourceCode)

        // Capture the output
        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        val originalOut = System.out
        System.setOut(printStream)

        try {
            // Compile the source file with the ComputeGraphProcessor
            val compilation = KotlinCompilation().apply {
                sources = listOf(source)
                symbolProcessorProviders = listOf(ComputeGraphProcessorProvider())
                inheritClassPath = true
                messageOutputStream = printStream
            }

            // Run the compilation
            compilation.compile()

            // Get the output
            val output = outputStream.toString()

            // Print the output for debugging
            System.setOut(originalOut)
            println("[DEBUG_LOG] Compilation output:")
            println(output)

            // Check that the KSP processor found and processed the function
            assertTrue(output.contains("Found 1 symbols with @Mikrograd annotation"), 
                "KSP processor should find the annotated function")
            assertTrue(output.contains("Processing function: testExpr"), 
                "KSP processor should process the testExpr function")
            assertTrue(output.contains("Generating code for function: testExpr"), 
                "KSP processor should generate code for the testExpr function")
            assertTrue(output.contains("Computation mode: INFERENCE"), 
                "KSP processor should use INFERENCE mode by default")
            assertTrue(output.contains("Code generation completed for testExpr"), 
                "KSP processor should complete code generation for the testExpr function")
        } finally {
            // Restore the original output stream
            System.setOut(originalOut)
        }
    }

    @Test
    fun testProcessorGeneratesCodeWithTrainingMode() {
        // Create a test Kotlin source file with a function annotated with @Mikrograd(mode = ComputationMode.TRAINING)
        val sourceCode = """
            @org.mikrograd.diff.ksp.Mikrograd(mode = org.mikrograd.diff.ksp.ComputationMode.TRAINING)
            fun testExprTraining() {
                3.0 * 4.0 + (7.0 + 3.0)
            }
        """
        val source = SourceFile.kotlin("test/TestFileTraining.kt", sourceCode)

        // Capture the output
        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)
        val originalOut = System.out
        System.setOut(printStream)

        try {
            // Compile the source file with the ComputeGraphProcessor
            val compilation = KotlinCompilation().apply {
                sources = listOf(source)
                symbolProcessorProviders = listOf(ComputeGraphProcessorProvider())
                inheritClassPath = true
                messageOutputStream = printStream
            }

            // Run the compilation
            compilation.compile()

            // Get the output
            val output = outputStream.toString()

            // Print the output for debugging
            System.setOut(originalOut)
            println("[DEBUG_LOG] Compilation output:")
            println(output)

            // Check that the KSP processor found and processed the function
            assertTrue(output.contains("Found 1 symbols with @Mikrograd annotation"), 
                "KSP processor should find the annotated function")
            assertTrue(output.contains("Processing function: testExprTraining"), 
                "KSP processor should process the testExprTraining function")
            assertTrue(output.contains("Generating code for function: testExprTraining"), 
                "KSP processor should generate code for the testExprTraining function")
            assertTrue(output.contains("Computation mode: TRAINING"), 
                "KSP processor should use TRAINING mode as specified")
            assertTrue(output.contains("Code generation completed for testExprTraining"), 
                "KSP processor should complete code generation for the testExprTraining function")
        } finally {
            // Restore the original output stream
            System.setOut(originalOut)
        }
    }
}
