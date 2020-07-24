package de.hshannover.dqgui.core.util;

import java.time.Duration;
import org.controlsfx.control.Notifications;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.core.configuration.Notification;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.framework.FXMLLoadingFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class NotificationTools {

    private NotificationTools() {}

    public static Notifications success(String title, String header, String body) {
        return loadCommon(title, header, body, Notification.SUCCESS);
    }
    
    public static Notifications warning(String title, String header, String body) {
        return loadCommon(title, header, body, Notification.WARNING);
    }
    
    public static Notifications error(String title, String header, String body) {
        return loadCommon(title, header, body, Notification.ERROR);
    } 
    
    private static Notifications loadCommon(String title, String header, String body, Notification n) {
        BorderPane b = (BorderPane) FXMLLoadingFactory.load(n).getScene().getRoot();
        VBox box = (VBox) b.getCenter();
        ((Label) box.getChildren().get(0)).setText(title);
        ((Label) box.getChildren().get(1)).setText(header);
        ((Label) box.getChildren().get(2)).setText(body);
        return Notifications.create().graphic(b);
    }
    
    public static Notifications taskError(Iqm4hdMetaData m) {
        String header = String.format("Failed due to %s", m.getException().get().getClassNameSimpleName());
        return workerNotificationHelper(Notification.TASK_ERROR, header, m.getDuration());
    }

    public static Notifications taskSuccess(Iqm4hdMetaData m) {
        String header = String.format("%s @ %s", m.getAction(), m.getEnvironment());
        return workerNotificationHelper(Notification.TASK_SUCCESS, header, m.getDuration());
    }

    private static Notifications workerNotificationHelper(Notification view, String header, Duration duration) {
        BorderPane b = (BorderPane) FXMLLoadingFactory.load(view).getScene().getRoot();
        VBox box = (VBox) b.getCenter();
        ((Label) box.getChildren().get(1)).setText(header);
        ((Label) ((HBox) box.getChildren().get(2)).getChildren().get(1)).setText(Utility.humanReadableFormat(duration));
        return Notifications.create().graphic(b);
    }
}