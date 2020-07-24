package de.hshannover.dqgui.framework;

/**
 * Implementation for views managed by an identifier.
 *
 * @author Marc Herschel
 *
 */
final class UnregisterBehaviorIdentifier implements UnregisterBehavior {
    private final ApplicationContext context;
    private final AbstractWindowController controller;
    private final String identifier;

    UnregisterBehaviorIdentifier(ApplicationContext context, AbstractWindowController controller, String identifier) {
        this.context = context;
        this.controller = controller;
        this.identifier = identifier;
    }

    @Override
    public void unregister() {
        context.unregisterController(controller, identifier);
    }

}
