package de.hshannover.dqgui.core.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import de.hshannover.dqgui.core.Config;
import de.hshannover.dqgui.framework.ErrorUtility;

public final class HtmlLoader {

    private HtmlLoader() {}
    
    /**
     * Load and prepare an internal (within the JAR) HTML document
     * @param p location of document
     * @param substitution optional, a key -&gt; value pair. Key is what to replace in the document (everything with the {{_KEY_}} syntax) with a value. If empty it will be ignored.
     * @return prepared html with substitutions applied
     */
    public static String loadInternal(String p, String...substitution) {
        InputStream s = HtmlLoader.class.getResourceAsStream(p);
        try(BufferedReader r = new BufferedReader(new InputStreamReader(s, Config.APPLICATION_CHARSET))) {
            String ss;
            StringBuilder sb = new StringBuilder(600);
            while((ss = r.readLine()) != null)
                sb.append(ss);
            return replace(sb.toString(), substitution);
        } catch(Exception e) {
            throw ErrorUtility.rethrow(e);
        }
    }
    
    private static String replace(String html, String[] substitution) {
        Objects.requireNonNull(html);
        if(substitution == null || substitution.length == 0)
            return html;
        if(substitution.length % 2 != 0)
            throw new IllegalArgumentException("substitution must be an even number of arguments");
        for(int i = 0; i < substitution.length; i+=2) {
            String key = Objects.requireNonNull(substitution[i]);
            String value = Objects.requireNonNull(substitution[i+1]);
            key = "{{" + key + "}}";
            html = html.replace(key, value);
        }
        return html;
    }
}
