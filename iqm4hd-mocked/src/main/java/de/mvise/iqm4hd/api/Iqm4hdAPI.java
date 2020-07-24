package de.mvise.iqm4hd.api;

import java.util.HashMap;
import java.util.Random;

public class Iqm4hdAPI {

    public static String compile(String action, RuleService rules) {
        return "DUMMY IMPLEMENTATION";
    }

    public static ExecutionReport execute(String compile, DatabaseService service, RService rService, RuleService rules, boolean optimize) {
        ExecutionReport mocked = new ExecutionReport();
        
        try {
            Thread.sleep(1250);
        } catch (InterruptedException e) {}
        
        if(new Random().nextBoolean()) {
            ExecutionIssue issue = new ExecutionIssue(0, .0);
            issue.setIdentifier(new String[]{"MOCKED", "ISSUE"});
            issue.setInfo(new HashMap<>());
            issue.setSeverity("ERROR");
            mocked.getExecutionResults().add(issue);
        }
        
        return mocked;
    }
}
