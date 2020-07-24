package de.mvise.iqm4hd.client;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RConn {
    private static RConnection conn;
    
    public static RConnection getConnection() throws RserveException {
        if (conn == null) {
            conn = new RConnection();
            System.out.println("Connected to R ....");
        }
        return conn;
    }
}