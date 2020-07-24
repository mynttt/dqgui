package de.hshannover.dqgui.core.ui;

import org.controlsfx.control.Notifications;
import de.hshannover.dqgui.core.configuration.Views;
import de.hshannover.dqgui.core.util.ExceptionRecoveryTips;
import de.hshannover.dqgui.core.util.NotificationTools;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData;
import de.hshannover.dqgui.execution.model.Iqm4hdMetaData.Iqm4hdRunnerException;
import de.hshannover.dqgui.framework.ApplicationContext;
import de.hshannover.dqgui.framework.dialogs.DialogContext;
import de.hshannover.dqgui.framework.model.ReconstructedException;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;

public class NotificationService {
    private Pos position;
    private long notificationDurationMillis;
    private DialogContext dialogContext;
    private ApplicationContext context;

    public NotificationService(ApplicationContext context, DialogContext dialogContext, Pos position, long notificationDurationMillis) {
        this.position = position;
        this.notificationDurationMillis = notificationDurationMillis;
        this.dialogContext = dialogContext;
        this.context = context;
    }

    /**
     * @param notificationDurationMillis how long notifications should stay.
     */
    public void setNotificationDurationMillis(long notificationDurationMillis) {
        this.notificationDurationMillis = notificationDurationMillis;
    }

    /**
     * @param position display notifications in that area of the screen.
     */
    public void setNotificationPosition(Pos position) {
        this.position = position;
    }

    /**
     * If no context is set the anonymous context will be used.
     * @param dialogContext to show dialogs in.
     */
    public void setDialogContext(DialogContext dialogContext) {
        this.dialogContext = dialogContext;
    }

    public void notifySuccess(Iqm4hdMetaData r) {
        Notifications n = NotificationTools
                .taskSuccess(r)
                .position(position)
                .hideAfter(Duration.millis(notificationDurationMillis))
                .onAction(e -> context.load(Views.REPORTS, "Reports", r.getIdentifier()));
        Platform.runLater(n::show);
    }

    public void notifyError(Iqm4hdMetaData r) {
        DialogContext c = context == null ? DialogContext.ANONYMOUS_CONTEXT : dialogContext;
        Notifications n = NotificationTools
                .taskError(r)
                .position(position)
                .hideAfter(Duration.millis(notificationDurationMillis))
                .onAction(e -> c.exceptionDialog(from(r.getException().get()), "IQM4HD invocation was not successful!", String.format("IQM4HD invocation for %s @ %s failed.%n%n%s" ,
                        r.getAction(),
                        r.getEnvironment(),
                        ExceptionRecoveryTips.IQM4HD_INVOKE.getTip())));
        Platform.runLater(n::show);
    }
    
    private static ReconstructedException from(Iqm4hdRunnerException ex) {
        return new ReconstructedException(ex.getClassNameSimpleName(), ex.getMessage(), ex.getStacktrace());
    }
}
