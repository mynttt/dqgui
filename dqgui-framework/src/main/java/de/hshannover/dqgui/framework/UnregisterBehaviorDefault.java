package de.hshannover.dqgui.framework;

/**
 * Implementation for views not managed by an identifier.
 *
 * @author Marc Herschel
 *
 */
public class UnregisterBehaviorDefault implements UnregisterBehavior {
    private final ApplicationContext context;
    private final AbstractWindowController controller;

    UnregisterBehaviorDefault(ApplicationContext context, AbstractWindowController controller) {
        this.context = context;
        this.controller = controller;
    }

    @Override
    public void unregister() {
        context.unregisterController(controller);
    }

}
