package de.hshannover.dqgui.execution.database.api;

import java.util.HashMap;
import java.util.Objects;

/**
 * An engine language uniquely identifies the language the database system uses.
 * 
 * @author myntt
 *
 */
public final class EngineLanguage {
    private static HashMap<String, EngineLanguage> CACHE = new HashMap<>();
    private final String language;
    
    private EngineLanguage(String language) {
        this.language = language;
    }
    
    static EngineLanguage of(String language) {
        Objects.requireNonNull(language, "language must not be null.");
        if(language.trim().isEmpty())
            throw new IllegalArgumentException("Language must not be blank.");
        language = language.toUpperCase();
        EngineLanguage cached = CACHE.get(language);
        if(cached != null)
            return cached;
        cached = new EngineLanguage(language);
        CACHE.put(language, cached);
        return cached;
    }

    @Override
    public String toString() {
        return language;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(language);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EngineLanguage other = (EngineLanguage) obj;
        return Objects.equals(language, other.language);
    }
}
