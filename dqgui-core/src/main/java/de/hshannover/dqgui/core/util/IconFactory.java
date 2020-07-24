package de.hshannover.dqgui.core.util;

import java.util.HashMap;
import java.util.NoSuchElementException;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.core.concurrency.Iqm4hdTask;
import de.hshannover.dqgui.core.concurrency.Iqm4hdTask.State;
import de.hshannover.dqgui.core.model.DSLExtra;
import de.hshannover.dqgui.core.model.Iqm4hdFeedback.FeedbackState;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData.Iqm4hdReturnCode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Factory to create ImageViews for icons.
 *
 * @author Marc Herschel
 *
 */
public final class IconFactory {
    private static final HashMap<Enum<?>, Image> ICONS = new HashMap<>();

    private IconFactory() {}

    /*
     * Cache your icons here.
     */
    static {
        for(State s : Iqm4hdTask.State.values())
            ICONS.put(s, new Image(IconFactory.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_THREADING+s.name()+".png")));
        for(Iqm4hdReturnCode s : Iqm4hdMetaData.Iqm4hdReturnCode.values())
            ICONS.put(s, new Image(IconFactory.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_THREADING+s.name()+".png")));
        for(DSLExtra s : DSLExtra.values())
            ICONS.put(s,  new Image(IconFactory.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+s.name()+".png"), 16, 16, true, true));
        for(DSLComponentType s : DSLComponentType.values())
            ICONS.put(s, new Image(IconFactory.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+s.name()+".png"), 16, 16, true, true));
        ICONS.put(FeedbackState.CORRECT, ICONS.get(Iqm4hdReturnCode.PASS));
        ICONS.put(FeedbackState.NOT_EVALUATED, new Image(IconFactory.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+"undefined.png"))); 
        ICONS.put(FeedbackState.FALSE_POSITIVE, ICONS.get(Iqm4hdReturnCode.ISSUES));
    }

    /**
     * Request a view for a registered icon
     * @param key to query for
     * @return default image view for icon
     */
    public static ImageView of(Enum<?> key) {
        Image img = ICONS.get(key);
        if(img == null)
            throw new NoSuchElementException(key.getClass().getName() + " " + key + " is not mapped to an image.");
        return new ImageView(img);
    }

}
