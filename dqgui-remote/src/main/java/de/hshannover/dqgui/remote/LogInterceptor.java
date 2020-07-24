package de.hshannover.dqgui.remote;

import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class LogInterceptor extends PrintStream {
    private static boolean INTERCEPTED = false;
    private static final ConcurrentMap<Thread, StringBuilder> FILTER = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Thread> FILTER_MAP = new ConcurrentHashMap<>();
    
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    private LogInterceptor(PrintStream out) {
        super(out);
    }
    
    public static void init() {
        if(INTERCEPTED)
            return;
        System.setOut(new LogInterceptor(System.out));
        System.setErr(new LogInterceptor(System.err));
        INTERCEPTED = true;
    }

    public static void begin(Thread thread, String jobId) {
        FILTER.put(thread, new StringBuilder(200));
        FILTER_MAP.put(jobId, thread);
    }
    
    public static String end(Thread thread) {
        FILTER_MAP.values().removeIf(v -> v.equals(thread));
        return FILTER.remove(thread).toString();
    }

    public static String request(String jobId) {
        StringBuilder sb = FILTER.get(FILTER_MAP.get(jobId));
        return sb != null ? sb.toString() : null;
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
