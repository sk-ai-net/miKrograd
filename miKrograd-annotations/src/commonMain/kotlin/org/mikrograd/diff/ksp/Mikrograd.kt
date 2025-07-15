package org.mikrograd.diff.ksp

/**
 * Computation mode for the Mikrograd annotation.
 * This determines whether to use ForwardValue (INFERENCE) or BackwardValue (TRAINING).
 */
enum class ComputationMode {
    /**
     * Inference mode uses ForwardValue which doesn't track gradients.
     * This is more memory-efficient when only forward pass is needed.
     */
    INFERENCE,

    /**
     * Training mode uses BackwardValue which tracks gradients for backpropagation.
     * This is necessary when gradient computation is needed.
     */
    TRAINING
}

/**
 * Annotation for functions that should be processed by the Mikrograd KSP processor.
 * The processor will generate optimized code for the function based on the computation mode.
 * 
 * @param mode The computation mode to use (INFERENCE or TRAINING)
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Mikrograd(val mode: ComputationMode = ComputationMode.INFERENCE)
