package de.hshannover.dqgui.execution.database.gui;

import java.io.IOException;
import java.io.InputStream;
import com.google.common.io.ByteStreams;
import de.hshannover.dqgui.execution.Rethrow;

/**
 * The icon class exists to decouple JavaFX and parts of the database abstraction layer that don't rely on JavaFX.
 * 
 * @author myntt
 *
 */
public class Icon {
    final byte[] buffer;
    
    public static Icon of(String classpathResource) {
        return new Icon(Icon.class.getResourceAsStream(classpathResource));
    }
    
    public static Icon of(InputStream in) {
        return new Icon(in);
    }
    
    private Icon(InputStream in) {
        try {
            buffer = ByteStreams.toByteArray(in);
        } catch (IOException e) {
            throw Rethrow.rethrow(e);
        }
    }
}
