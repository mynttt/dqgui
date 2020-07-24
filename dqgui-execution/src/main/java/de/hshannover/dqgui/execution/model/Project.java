package de.hshannover.dqgui.execution.model;

import java.util.Objects;

public class Project {
    private final String identifier, name, guid;
    private final RepoType type;
    
    public Project(String identifier, String name, RepoType type, String guid) {
        this.identifier = identifier;
        this.name = name;
        this.type = type;
        this.guid = guid;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }

    public RepoType getType() {
        return type;
    }
    
    public String getGuid() {
        return guid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Project other = (Project) obj;
        return Objects.equals(identifier, other.identifier) && Objects.equals(name, other.name)
                && type == other.type;
    }

    @Override
    public String toString() {
        return "Project [identifier=" + identifier + ", name=" + name + ", type=" + type + ", guid=" + guid + "]";
    }
}
