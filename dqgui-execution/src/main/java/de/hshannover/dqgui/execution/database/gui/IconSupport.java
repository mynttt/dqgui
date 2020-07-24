package de.hshannover.dqgui.execution.database.gui;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import javafx.scene.image.Image;

/**
 * Wrapper class to not introduce a hard javafx dependency on the remote execution server.
 * 
 * @author myntt
 *
 */
public class IconSupport {
    private static final HashMap<GuiConfiguration, Image> WIZARD_ICON = new HashMap<>();
    private static final HashMap<GuiConfiguration, Image> LIST_ICON = new HashMap<>();
    
    public static Image requestWizardIcon(GuiConfiguration conf) {
        Image img = WIZARD_ICON.get(conf);
        if(img != null)
            return img;
        img = new Image(new ByteArrayInputStream(conf.iconWizard.buffer));
        WIZARD_ICON.put(conf, img);
        return img;
    }
    
    public static Image requestListIcon(GuiConfiguration conf) {
        Image img = LIST_ICON.get(conf);
        if(img != null)
            return img;
        img = new Image(new ByteArrayInputStream(conf.iconList.buffer));
        LIST_ICON.put(conf, img);
        return img;
    }
}
