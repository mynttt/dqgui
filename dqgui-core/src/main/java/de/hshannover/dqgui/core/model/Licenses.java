package de.hshannover.dqgui.core.model;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import de.hshannover.dqgui.core.Config;

public class Licenses {
    public final static Licenses INSTANCE = load();
    
    private final Map<CategoryType, Map<LicenseFormat, List<License>>> data = new HashMap<>();
    
    public enum CategoryType {
        LIB("External Libraries"), ICON("Icons"), FONT("Fonts");
        public final String desc;

        private CategoryType(String desc) {
            this.desc = desc;
        }
    }
    
    public enum LicenseFormat {
        TEXT, IMAGE;
    }
    
    public static class License {
        public final String content= null, description = null, url = null;

        @Override
        public String toString() {
            return "License [content=" + content + ", description=" + description + ", url=" + url + "]";
        }
    }
    
    private static Licenses load() {
        return new Gson().fromJson(new InputStreamReader(Licenses.class.getResourceAsStream("/dqgui/core/licenses.json"), Config.APPLICATION_CHARSET), Licenses.class);
    }

    public Map<CategoryType, Map<LicenseFormat, List<License>>> getData() {
        return Collections.unmodifiableMap(data);
    }

    @Override
    public String toString() {
        return "Licenses [data=" + data + "]";
    }
}
