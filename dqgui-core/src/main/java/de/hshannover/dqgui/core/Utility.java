package de.hshannover.dqgui.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Stack;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.Config.OS;
import de.hshannover.dqgui.execution.model.Project;
import de.hshannover.dqgui.execution.model.RepoType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Utility class. Add globally referenced Utility methods here.
 *
 * @author Marc Herschel
 *
 */
public final class Utility {
    private final static String[] SUFIXES = { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
    private final static String[] FOLDERS =  {"actions", "sources", "checks", "results/feedbacks", "results/logs", "results/reports"};
    
    public final static DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(TimeZone.getDefault().toZoneId());
    public final static DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(TimeZone.getDefault().toZoneId());
    
    private Utility() {}
    
    public static String formatHost(String host) {
        if(host.endsWith("/"))
            host = host.substring(0, host.length()-1);
        if(InetAddressValidator.getInstance().isValidInet4Address(host) ||
                InetAddressValidator.getInstance().isValidInet6Address(host))
            return "http://" + host;
        if(host.startsWith("https://") || host.startsWith("http://"))
            return host;
        return "http://" + host;
    }
    
    public static String toString(Object obj) {
        if(obj instanceof Double && (Double) obj % 1 == 0)
            return Long.toString(((Double) obj).longValue());
        if(obj != null && obj.getClass().isArray())
            return Arrays.deepToString((Object[]) obj);
        return Objects.toString(obj).trim();
    }

    public static void requireNonNull(Object...objects) {
        if(objects == null)
            throw new NullPointerException("Input is supposed to be not null");
        int nulls = 0;
        for(Object o : objects) {
            if(o == null)
                nulls++;
        }
        if(nulls != 0)
            throw new NullPointerException("Input is supposed to be not null - got " + nulls + " null input(s).");
    }

    public static Optional<Integer> integer(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch(NumberFormatException e) {
            return Optional.empty();
        }
    }
    
    public static String ordinal(int i) {
        switch (i % 100) {
        case 11:
        case 12:
        case 13:
            return i + "th";
        default:
            return i + SUFIXES[i % 10];

        }
    }
    
    public static void prepareFilesystemProject(Project prj) throws Exception {
        if(prj.getType() != RepoType.FILE_SYSTEM)
            throw new IllegalArgumentException("RepoType != FILE_SYSTEM");
        Path p = Paths.get(prj.getIdentifier());
        for(String s : FOLDERS) {
            Path t = p.resolve(s);
            if(!Files.isDirectory(t))
                Files.createDirectories(t);
        }
        Path guidLocation = p.resolve("guid");
        String guid;
        if(!Files.exists(guidLocation)) {
            guid = UUID.randomUUID().toString();
            Files.write(guidLocation, guid.getBytes(StandardCharsets.UTF_8));
        } else {
            guid = new String(Files.readAllBytes(guidLocation), StandardCharsets.UTF_8);
        }
        // Reflection hack to inject GUID after construction
        Field f = Project.class.getDeclaredField("guid");
        f.setAccessible(true);
        f.set(prj, guid);
    }

    /**
     * Convert call with checked exception to unchecked exception.<br>
     * Useful in streams.
     * @param <T> type of call
     * @param callable to convert
     * @return callable result
     */
    public static <T> T uncheckCall(Callable<T> callable) {
        try { return callable.call(); }
        catch (RuntimeException e) { throw e; }
        catch (Exception e) { throw new RuntimeException(e); }
      }

    /**
     * Convert a duration into a format like this 5m1s.
     * @param duration to convert
     * @return human readable format
     */
    public static String humanReadableFormat(Duration duration) {
        return duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    /**
     * Might have issues with other JDKs than Oracle/OpenJDK.<br>
     * We're on the mercy of the bean here lol
     * @return pid of current JVM or -1 if impossible to determine
     */
    static long getJvmPid() {
        try {
            RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
            String jvmName = bean.getName();
            return Long.parseLong(jvmName.split("@")[0]);
        } catch(Exception e) {
            return -1;
        }
    }

    /**
     * Kills everything attached to this process.
     */
    public static void killChildProcessTree() {
        if(!Config.PROCESS_HAVE_PID) {
            Logger.warn(String.format("!!! %s PID unknown. Initiating hard coded fallback for %s !!!", Config.APPLICATION_NAME, Config.PROCESS_HOST));
            if(Config.PROCESS_HOST == OS.NIX)
                fallbackUnix();
            if(Config.PROCESS_HOST == OS.WIN)
                fallbackWindows();
            return;
        }
        if(Config.PROCESS_HOST == OS.NIX)
            killChildrenOnUnix();
        if(Config.PROCESS_HOST == OS.WIN)
            killChildrenOnWindows();
    }

    private static void killChildrenOnWindows() {
        Logger.info(String.format("Retrieving child processes for %d @ %s...", Config.PROCESS_OWN_PID, Config.PROCESS_HOST));
        Stack<Long> pids = new Stack<>();
        try {
            getWindowsChildren(Config.PROCESS_OWN_PID, pids);
        } catch (IOException e) { Logger.error(e); }
        while(!pids.isEmpty()) {
            String query = String.format("taskkill /F /PID %d", pids.pop());
            new Thread(() -> {
                try {
                    Process p = Runtime.getRuntime().exec(query);
                    p.waitFor(1000, TimeUnit.MILLISECONDS);
                    Logger.info("{} returned {}", query, p.exitValue());
                } catch(IOException | InterruptedException e) {
                    Logger.error("Failed to run taskkill");
                    Logger.error(e);
                }
            }, "winkiller").start();
        }
    }

    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    private static void getWindowsChildren(long ppid, Stack<Long> pids) throws IOException {
        String line;
        String query = String.format("wmic process where (ParentProcessId=%d) get Caption,ProcessId /FORMAT:CSV", ppid);
        Process p = Runtime.getRuntime().exec(query);
        BufferedReader list = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while((line = list.readLine()) != null) {
            try {
                if(line.isEmpty())
                    continue;
                if(line.startsWith("Node"))
                    continue;
                if(line.contains("WMIC.exe"))
                    continue;
                String[] csv = line.trim().split(",");
                String caption = csv[1];
                long pid = Long.parseLong(csv[2]);
                Logger.info(String.format("Found process: %s @ %d", caption, pid));
                pids.push(pid);
                getWindowsChildren(pid, pids);
            } catch(NumberFormatException e) {}
        }
        list.close();
    }

    private static void killChildrenOnUnix() {
        //Method does nothing as Unix Systems do not spawn a nested process tree like in windows.
        //If this ever changes it can be dealt with here.
    }

    private static void fallbackWindows() {
        Logger.info("Windows fallback. Killing for {}. All processes will be affected.", Arrays.toString(Config.PROCESS_KILL_FALLBACK_WINDOWS));
        for(String process : Config.PROCESS_KILL_FALLBACK_WINDOWS) {
            try {
                killOnWindowsByName(process);
            } catch (IOException | InterruptedException e) {
                Logger.error(String.format("Fallback: Winkill failed for %s", process));
                Logger.error(e);
            }
        }
    }

    private static void fallbackUnix() {
        Logger.info("Linux fallback. Killing for {}. All processes will be affected.", Arrays.toString(Config.PROCESS_KILL_FALLBACK_LINUX));
        for(String process : Config.PROCESS_KILL_FALLBACK_LINUX) {
            killOnUnixByName(process);
        }
    }

    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    private static void killOnWindowsByName(String process) throws IOException, InterruptedException {
        String line;
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(String.format("tasklist /fo csv /FI \"IMAGENAME eq %s\"", process));
        BufferedReader list = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = list.readLine()) != null) {
            try {
                String csv = line.split(",")[1];
                long pid = Long.parseLong(csv.substring(1, csv.length()-1));
                Process k = rt.exec(String.format("taskkill /F /pid %d", pid));
                Logger.info(String.format("Killing %s with PID: %d", process, pid));
                k.waitFor(1000, TimeUnit.MILLISECONDS);
                Logger.info("taskkill returned {}", k.exitValue());
            } catch(NumberFormatException e) {}
        }
        list.close();
    }

    private static void killOnUnixByName(String process) {
        try {
            Process p = Runtime.getRuntime().exec(String.format("killall %s", process));
            Logger.info(String.format("Killing all for %s.", process));
            p.waitFor(1000, TimeUnit.MILLISECONDS);
            Logger.info(String.format("killall returned %d", p.exitValue()));
        } catch (InterruptedException | IOException e) {
            Logger.error(String.format("Fallback: Killall failed for %s", process));
            Logger.error(e);
        }
    }

    @SuppressFBWarnings("DE_MIGHT_IGNORE")
    static String generateUserIdentifier() {
        String hostname = "unknown";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {}
        return String.format("%s @ %s", System.getProperty("user.name"), hostname);
    }

    public static void deleteFolderRecursively(Path p) throws IOException {
        if(!Files.exists(p)) return;
        try (Stream<Path> walk = Files.walk(p)) {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }
}
