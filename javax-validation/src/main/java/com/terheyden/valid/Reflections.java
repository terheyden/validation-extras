package com.terheyden.valid;

import java.lang.StackWalker.StackFrame;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Java reflection utils.
 */
final class Reflections {

    private static final Logger LOG = getLogger(Reflections.class);

    private Reflections() {
        // Private since this class shouldn't be instantiated.
    }

    /**
     * Get a particular {@link StackFrame} for inspecting things like the calling class and method.
     * <p>
     * This is using the fastest possible method, see:
     * https://stackoverflow.com/questions/442747/getting-the-name-of-the-currently-executing-method
     *
     * @param offset How many stack frames to skip — you have to kinda tweak this to find the frame you want
     * @return The {@link StackFrame} at the specified offset
     */
    static StackFrame getStackFrame(int offset) {
        return StackWalker.getInstance()
            .walk(stackFrame -> stackFrame.skip(offset).findFirst())
            .orElseThrow(() -> new IllegalArgumentException("Invalid stack frame depth: " + offset));
    }

    /**
     * Use {@link #getStackFrame(int)}, but return the class and method name as a string.
     */
    static String getStackFrameClassAndMethod(int offset) {
        StackFrame enclosingStackFrame = getStackFrame(offset);
        String classAndMethod = enclosingStackFrame.getClassName() + "." + enclosingStackFrame.getMethodName();
        LOG.debug(classAndMethod);
        return classAndMethod;
    }
}
