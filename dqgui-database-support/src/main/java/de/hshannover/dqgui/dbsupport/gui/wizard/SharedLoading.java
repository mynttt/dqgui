package de.hshannover.dqgui.dbsupport.gui.wizard;

import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironment;
import de.hshannover.dqgui.dbsupport.dialogs.DatabaseSupportResourceConfiguration;
import de.hshannover.dqgui.dbsupport.gui.AbstractEngineUpdateCreate;
import de.hshannover.dqgui.execution.database.api.DatabaseConnection;
import de.hshannover.dqgui.execution.database.api.DatabaseEngine;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.hshannover.dqgui.framework.FXMLLoadingFactory;
import de.hshannover.dqgui.framework.FXMLLoadingFactory.WindowContent;
import de.hshannover.dqgui.framework.ReflectionUtility;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import de.hshannover.dqgui.framework.wizards.AbstractWizard;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

class SharedLoading {

    static Scene load(DatabaseEngine engine, DialogContext warning, DatabaseConnection con, 
            DatabaseEnvironment env, boolean isUpdate, AbstractWizard<DatabaseConnection> wizard,
            Runnable closeListener) {
        WindowContent obj = FXMLLoadingFactory.load(engine.guiConfiguration().fxmlLocationInClasspath, DatabaseSupportResourceConfiguration.CSS, (controller) -> {
            if(!AbstractEngineUpdateCreate.class.isAssignableFrom(controller))
                throw new IllegalArgumentException("Controller must extend AbstractEngineUpdateCreate! Is " + controller.getCanonicalName());
            try {
                AbstractEngineUpdateCreate instance = (AbstractEngineUpdateCreate) ReflectionUtility.createInstance(controller);
                ReflectionUtility.invokeMethod(instance,
                        ReflectionUtility.getMethod(AbstractEngineUpdateCreate.class, "inject", DatabaseConnection.class, DatabaseEngine.class, DatabaseEnvironment.class),
                        con, engine, env);
                return instance;
            } catch (Exception e) {
                throw ErrorUtility.rethrow(e);
            }
        });
        try {
            ReflectionUtility.invokeMethod((AbstractEngineUpdateCreate) obj.getController(),
                    ReflectionUtility.getMethod(AbstractEngineUpdateCreate.class, "initialize", boolean.class, AbstractWizard.class),
                    isUpdate, wizard);
        } catch (Exception e) {
            throw ErrorUtility.rethrow(e);
        }
        obj.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ESCAPE), closeListener);
        return obj.getScene();
    }

}
