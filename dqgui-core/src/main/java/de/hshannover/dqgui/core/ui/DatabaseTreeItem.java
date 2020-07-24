package de.hshannover.dqgui.core.ui;

import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.framework.control.SpecificTreeItem;

public final class DatabaseTreeItem extends SpecificTreeItem {
    private final DatabaseConnection con;
    private final DatabaseEnvironment env;

    public DatabaseTreeItem(DatabaseConnection con, DatabaseEnvironment env) {
        super(con.getIdentifier());
        this.con = con;
        this.env = env;
    }

    public DatabaseTreeItem(DatabaseEnvironment env) {
        super(env.getIdentifier());
        this.env = env;
        this.con = null;
    }

    public DatabaseConnection getCon() {
        return con;
    }

    public DatabaseEnvironment getEnv() {
        return env;
    }

    @Override
    public String stringConversion() {
        if(con == null)
            return env.getIdentifier();
        return con.getIdentifier();
    }

}
