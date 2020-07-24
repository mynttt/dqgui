package de.hshannover.dqgui.core.concurrency;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import de.hshannover.dqgui.core.model.ProjectHandle;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * PrintStream Proxy that allows to filter stdout, stderr for certain Threads if they are registered with the filter.<br>
 * This Proxy only catches println(), println(String), print(String). All other PrintStream operations are not filtered.
 *
 * @author Marc Herschel
 *
 */
public final class InterceptablePrintStream extends PrintStream {
    private static boolean INTERCEPTED = false;
    private static final ConcurrentMap<Thread, StringBuilder> FILTER = new ConcurrentHashMap<>(20);

    /**
     * Set up PrintStream interception.
     * This operation is irreversible.
     */
    public static void intercept() {
        if(INTERCEPTED)
            return;
        System.setOut(new InterceptablePrintStream(System.out));
        System.setErr(new InterceptablePrintStream(System.err));
        INTERCEPTED = true;
    }

    /**
     * Register a thread with the filter.
     * @param t to filter for.
     * @throws IllegalArgumentException if the Thread to filter for is null.
     * @throws IllegalStateException if the Thread to filter is already started.
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    static void register(Thread t) {
        if(t == null)
            throw new IllegalArgumentException("Thread must not be null!");
        if(FILTER.containsKey(t))
            return;
        FILTER.put(t, new StringBuilder(2000));
    }

    /**
     * Unregister a Thread with the filter and write the output into the provided log file.
     * @param t to unregister with the filter.
     * @param handle handle to supply
     * @param meta to write to
     * @throws IllegalArgumentException if the Thread is not registered with the filter.
     */
    static void unregister(Thread t, ProjectHandle handle, Iqm4hdMetaData meta) {
        if(!FILTER.containsKey(t))
            throw new IllegalArgumentException(String.format("Thread: %s is not registered with filter.", t.getName()));
        handle.dumpLog(FILTER.remove(t).toString(), meta);
    }

    /**
     * Check if a Thread is registered with the filter.
     * @param t to check for.
     * @return true if registered with filter.
     */
    static boolean registered(Thread t) {
        return FILTER.containsKey(t);
    }

    /**
     * Preview the stdout, stderr output of the Thread.
     * @param t to preview for.
     * @return stderr, stdout for the time of calling.
     * @throws IllegalArgumentException if the Thread is not registered with the filter.
     */
    static String preview(Thread t) {
        if(!FILTER.containsKey(t))
            throw new IllegalArgumentException(String.format("Thread: %s is not registered with filter.", t.getName()));
        return FILTER.get(t).toString();
    }

    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    private InterceptablePrintStream(PrintStream out) {
        super(out);
    }

    @Override
    public void println(String s) {
        StringBuilder sb = FILTER.get(Thread.currentThread());
        if(sb != null) {
            sb.append(s).append('\n');
        } else {
            super.println(s);
        }
    }

    @Override
    public void println() {
        StringBuilder sb = FILTER.get(Thread.currentThread());
        if(sb != null) {
            sb.append('\n');
        } else {
            super.println();
        }
    }

    @Override
    public void print(String s) {
        StringBuilder sb = FILTER.get(Thread.currentThread());
        if(sb != null) {
            sb.append(s);
        } else {
            super.print(s);
        }
    }

}
