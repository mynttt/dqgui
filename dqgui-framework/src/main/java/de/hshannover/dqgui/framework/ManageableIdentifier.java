package de.hshannover.dqgui.framework;

import de.hshannover.dqgui.framework.api.Manageable;

final class ManageableIdentifier {
    private final Manageable manageable;
    private final String identifier;

    private ManageableIdentifier(Manageable manageable, String identifier) {
        this.manageable = manageable;
        this.identifier = identifier;
    }

    static ManageableIdentifier of(Manageable manageable, String identifier) {
        return new ManageableIdentifier(manageable, identifier);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((manageable == null) ? 0 : manageable.hashCode());
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
        ManageableIdentifier other = (ManageableIdentifier) obj;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (manageable == null) {
            if (other.manageable != null)
                return false;
        } else if (!manageable.equals(other.manageable))
            return false;
        return true;
    }

}
