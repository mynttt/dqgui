package de.hshannover.dqgui.core.rsupport;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class RMetaInformation {
    public String R, PWD, script, args;
    @SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
    public boolean autostart;

    {
        R = PWD = script = "undefined";
        args = "none";
    }
}
