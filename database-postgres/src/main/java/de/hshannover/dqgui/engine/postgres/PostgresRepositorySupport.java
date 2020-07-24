package de.hshannover.dqgui.engine.postgres;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.base.Objects;
import de.hshannover.dqgui.execution.database.api.YamlConfigurableRelationalRepositorySupport;
import de.hshannover.dqgui.execution.database.api.Repository.ValidationReport;

public class PostgresRepositorySupport extends YamlConfigurableRelationalRepositorySupport {
    
    // Tests work by comparing type metadata (int flag) with already recorded correct type identifiers.
    // JDBCRepository provides a <String, Integer> with table name and column data type that is compared with a should result.
    
    /* 1111 = JSON
     * 1 = char(n)
     * 4 = int
     * 12 = text
     */
    
    public PostgresRepositorySupport(String yamlRawdataSchema, String yamlRawdataQueries) {
        super(yamlRawdataSchema, yamlRawdataQueries);
    }

    @SuppressWarnings("serial")
    private static Map<String, Integer> INDEX = new HashMap<String, Integer>() {{
        put("project", 4);
        put("guid", 1);
        put("name", 12);
        put("environments", 1111);
    }};
    
    @SuppressWarnings("serial")
    private static Map<String, Integer> RESULTS = new HashMap<String, Integer>() {{
        put("dqgui_meta", 1111);
        put("iqm4hd_report", 1111);
        put("iqm4hd_feedback", 1111);
        put("hash", 1);
        put("project", 4);
    }};
    
    @SuppressWarnings("serial")
    private static Map<String, Integer> REPO = new HashMap<String, Integer>() {{
        put("type", 4);
        put("content", 12);
        put("name", 12);
        put("extra_data", 1111);
        put("project", 4);
    }};
    
    @Override
    public ValidationReport validateRepository(Map<String, Integer> index, Map<String, Integer> repo, Map<String, Integer> results) {
        List<String> errors = new ArrayList<>();
        validateInternal(index, INDEX, "dqgui_index", "index", errors);
        validateInternal(repo, REPO, "dqgui_repo", "repository", errors);
        validateInternal(results, RESULTS, "dqgui_results", "results", errors);
        if(errors.isEmpty())
            return ValidationReport.success();
        return ValidationReport.error(String.join("\n\n", errors));
    }

    private void validateInternal(Map<String, Integer> is, Map<String, Integer> should, String schemaName, String schemaId, List<String> errors) {
        if(!Objects.equal(is, should)) {
            errors.add(String.format("Invalid %s schema!%nIs: %s | Should: %s | Should query: %s", schemaName, is, should, getSchema(schemaId)));
        }
    }
}
