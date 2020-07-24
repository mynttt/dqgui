package de.hshannover.dqgui.core.serialization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.serialization.JSONOperations;
import de.mvise.iqm4hd.api.ExecutionReport;

/**
 * Serialize/deserialize ExecutionReports
 *
 * @author Marc Herschel
 *
 */
public final class ExecutionReportSerialization {

    private ExecutionReportSerialization() {}

    /**
     * Dump a report to disk.
     * @param report to dump
     * @param p path of report
     */
    public static void dump(ExecutionReport report, Path p) {
        try {
            JSONOperations.toJSON(report, p);
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    /**
     * Recover a saved report from disk.
     * @param p path to recover from
     * @return recovered report
     */
    public static ExecutionReport recover(Path p) {
        try {
            return (ExecutionReport) JSONOperations.fromJSON(ExecutionReport.class, p);
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }
    }

    /**
     * Dump a raw report (json) to disk.
     * @param json raw report to dump
     * @param p path of report
     */
    public static void dumpRaw(String json, Path p) {
        try {
            Files.write(p, json.getBytes(Config.APPLICATION_CHARSET));
        } catch (IOException e) {
            throw ErrorUtility.rethrow(e);
        }
    }
}
