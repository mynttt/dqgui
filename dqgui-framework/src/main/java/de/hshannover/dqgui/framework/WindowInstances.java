package de.hshannover.dqgui.framework;

/**
 * Amount of window instances that can exist at the time of a view managed by the {@link ApplicationContext}.<br>
 * Managed by identifier means that there can be n instances but they correspond to an identifier so calling them again will force the window
 * in the foreground if a window with the called identifier is already loaded.
 *
 * @author Marc Herschel
 *
 */
public final class WindowInstances {

    /**
     * An unlimited amount of windows with the view can exist.<br>
     * These are not managed by an identifier.
     */
    public static final WindowInstances UNLIMITED = new WindowInstances(-1, false);

    /**
     * An unlimited amount of windows with the view can exist.<br>
     * These are managed by an identifier.
     */
    public static final WindowInstances UNLIMITED_MANAGED_BY_IDENTIFIER = new WindowInstances(-1, true);

    /**
     * Only one window with the view can exist at the time.<br>
     * If the {@link ApplicationContext} receives a load call the window referencing the view will be forced in the foreground.<br>
     * These are not managed by an identifier.
     */
    public static final WindowInstances ONCE_AT_A_TIME = new WindowInstances(1, false);

    private final int possibleInstances;
    private final boolean identifierManaged;

    private WindowInstances(int possibleInstances, boolean identifierManaged) {
        this.possibleInstances = possibleInstances;
        this.identifierManaged = identifierManaged;
    }

    /**
     * Create a WindowInstances object that is not managed by an identifier.<br>
     * -1 means unlimited instances in this context.
     * @param possibleInstances number of possible instances.
     * @return possible window instances object.
     * @throws IllegalArgumentException if not -1 or &gt; 0
     */
    public static WindowInstances of(int possibleInstances) {
        if(possibleInstances == -1)
            return UNLIMITED;
        if(possibleInstances == 1)
            return ONCE_AT_A_TIME;
        if(possibleInstances == 0 || possibleInstances < -1)
            throw new IllegalArgumentException(String.format("Invalid argument %d. Must be (-1 or >0)", possibleInstances));
        return new WindowInstances(possibleInstances, false);
    }

    /**
     * Create a WindowInstances object that is managed by an identifier.<br>
     * -1 means unlimited instances in this context.
     * @param possibleInstances number of possible instances.
     * @return possible window instances object.
     * @throws IllegalArgumentException if not -1 or &gt; 1
     */
    public static WindowInstances ofIdentifierManaged(int possibleInstances) {
        if(possibleInstances == -1)
            return UNLIMITED_MANAGED_BY_IDENTIFIER;
        if(possibleInstances == 1)
            throw new IllegalArgumentException("possibleInstances of 1 cannot be managed by an identifier.");
        if(possibleInstances == 0 || possibleInstances < -1)
            throw new IllegalArgumentException(String.format("Invalid argument %d. Must be (-1 or >0)", possibleInstances));
        return new WindowInstances(possibleInstances, true);
    }


    /**
     * @return amount of possible instances. <br>-1 means unlimited
     */
    public int getPossibleInstances() {
        return possibleInstances;
    }

    /**
     * @return true if managed by an identifier
     */
    public boolean isIdentifierManaged() {
        return identifierManaged;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (identifierManaged ? 1231 : 1237);
        result = prime * result + possibleInstances;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WindowInstances other = (WindowInstances) obj;
        if (identifierManaged != other.identifierManaged)
            return false;
        if (possibleInstances != other.possibleInstances)
            return false;
        return true;
    }
}
