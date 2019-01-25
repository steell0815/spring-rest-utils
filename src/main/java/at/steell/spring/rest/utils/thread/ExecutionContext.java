package at.steell.spring.rest.utils.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A helper class to track the execution context of a REST invocation. This utility is needed because REST calls are
 * executed in different threads. Before a new thread is started by the thread pool, the current stack trace is recorded
 * in order to also log the caller reference to see where a REST call is originally issued.
 *
 * @author Markus Jessenitschnig (XJM)
 */
public class ExecutionContext
{
    private List<StackTraceElement> stacktrace = new ArrayList<>();
    private static ThreadLocal<ExecutionContext> contexts = new ThreadLocal<>();

    /**
     * Clears the execution context from the current thread
     */
    public static void clear()
    {
        contexts.remove();
    }

    /**
     * Starts a {@link ExecutionContext} to track information about REST invocations. If an {@link ExecutionContext} is
     * already assigned to the current thread, this {@link ExecutionContext} is used
     *
     * @return The started {@link ExecutionContext}
     */
    public static ExecutionContext start()
    {
        ExecutionContext context = contexts.get();
        if (context == null)
        {
            context = new ExecutionContext();
            contexts.set(context);
        }
        return context;
    }

    /**
     * @return The {@link ExecutionContext} which is currently assigned with the current thread ot null.
     */
    public static ExecutionContext get()
    {
        return contexts.get();
    }

    /**
     * Sets the {@link ExecutionContext} for the current thread
     *
     * @param context The {@link ExecutionContext} to be used for the current thread
     */
    public static void set(final ExecutionContext context)
    {
        contexts.set(context);
    }

    /**
     * Records the current stack trace
     */
    public void recordStackTrace()
    {
        stacktrace.addAll(computeStackTrace());
    }

    /**
     * Prints the recorded stack trance
     *
     * @return The printed stack trance
     */
    public String printStackTrace()
    {
        List<StackTraceElement> toPrint = new ArrayList<>(stacktrace);
        if (!toPrint.isEmpty())
        {
            return toPrint.stream().map(element ->
            {
                String trace = String.format("%s.%s", element.getClassName(), element.getMethodName());
                if (element.getLineNumber() > 0)
                {
                    trace = String.format("%s(%s:%d)", trace, element.getFileName(), element.getLineNumber());
                }
                else
                {
                    trace = trace + "(?:?)";
                }
                return trace;
            }).collect(Collectors.joining(" <- "));
        }
        return null;

    }

    /**
     * Compute a reasonable stack trace for logging
     *
     * @return The string representing containing a reasonable stack trace for logging
     */
    private List<StackTraceElement> computeStackTrace()
    {
        return Arrays.asList(Thread.currentThread().getStackTrace()).stream().filter(element ->
        {
            /* exclude some framework packages to so the real usage */
            return !element.getClassName().startsWith("at.steell.spring.rest.utils")
                && !element.getMethodName().contains("$")
                && !element.getClassName().contains("$")
                && !element.getClassName().contains(".aspects.");
        }).collect(Collectors.toList());
    }
}
