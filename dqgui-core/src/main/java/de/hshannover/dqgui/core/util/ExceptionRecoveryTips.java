package de.hshannover.dqgui.core.util;

public enum ExceptionRecoveryTips {
    RSERVE("DQGUI has failed to start the RServe instance.\n" +
            "This might be a misconfiguration on your side.\n" +
            "\n" +
            "Please ensure that your provided shell script:\n" +
            "\n" +
            "- is functional.\n" +
            "- is in the correct place and actually selected.\n" +
            "- has the correct executable bit set.\n" +
            "- has the correct shebang set.\n" +
            "- has the correct owner set."),
    RSERVE_ON_STARTUP("DQGUI has failed to start the RServe instance.\n" +
            "This might be a misconfiguration on your side.\n" +
            "\n" +
            "Please ensure that your provided shell script:\n" +
            "\n" +
            "- is functional.\n" +
            "- is in the correct place and actually selected.\n" +
            "- has the correct executable bit set.\n" +
            "- has the correct shebang set.\n" +
            "- has the correct owner set." +
            "\n" +
            "\n" +
            "Automatic startup for RServe has been disabled."),
    IQM4HD_INVOKE("This might be a misconfiguration on your side.\n" +
            "\n" +
            "Please check if:\n" +
            "- The environment selected contains the database identifier.\n" +
            "- The IQM4HD identifier you're referencing is spelled correctly.\n" +
            "- The IQM4HD identifier you're referencing is case-sensitive.\n" +
            "- The database identifier you're referencing is case-sensitive.\n" +
            "- RServe is running correctly.\n" +
            "- Your referenced DSL files are valid.\n" +
            "- Your referenced database is available."), 
    REMOTE_ERROR("DQGUI Remote Execution Server could not be reached.\n" + 
            "\n" + 
            "Please check if:\n" + 
            "- the server is online and you are connected to the internet.\n" + 
            "- you have valid connection credentials set");

    private String tip;

    ExceptionRecoveryTips(String s) {
        tip = s;
    }

    public String getTip() {
        return tip;
    }
}
