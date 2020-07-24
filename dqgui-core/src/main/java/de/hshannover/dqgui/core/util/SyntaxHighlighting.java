package de.hshannover.dqgui.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.tinylog.Logger;
import de.hshannover.dqgui.framework.ErrorUtility;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser;

public class SyntaxHighlighting {
    private static Pattern PATTERN = null;
    private static List<String> KEYWORDS;
    private static Set<Integer> RULES = new HashSet<>();
    private static String[] LOOKUP;
    
	static {
	    /*
	     * Dynamically load keywords for syntax highlighting from supplied parser.
	     * This works on the following conditions:
	     * - A keyword has the same symbolic and literal name and is upper case
	     * - Access to these data is done via reflection on two private fields of the parser
	     * 
	     * This solution works but might be really fragile. If large changes to the conditions mentioned above are made it has to be updated!
	     * _symbolic has one more item as _literal which is an end symbol. Thus it is important to iterate through _literal to ignore this symbol.
	     */
	    try {
	        /*
	         * Keywords that are supposed to be highlighted in red
	         */
	        List<String> KEYWORDS_RED = Arrays.asList(new String[] {
	                "WARNING", "ERROR"
	        });

	        String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	        String CHARACTER_PATTERN = "\'([^\'\\\\]|\\\\.)*\'";
	        String COMMENT_PATTERN = "^#.*$";
	        
	        //Configuration ends here.
	        
	        List<String> KEYWORDS_DEFAULT = new ArrayList<>();
            Field symbolicF = IQM4HDParser.class.getDeclaredField("_SYMBOLIC_NAMES");
            Field literalsF = IQM4HDParser.class.getDeclaredField("_LITERAL_NAMES");
            symbolicF.setAccessible(true);
            literalsF.setAccessible(true);
            String[] symbolic = (String[]) symbolicF.get(IQM4HDParser.class);
            String[] literals = (String[]) literalsF.get(IQM4HDParser.class);
            
            LOOKUP = new String[literals.length];
            
            for(int i = 0; i < literals.length; i++) {
                if(symbolic[i] == null || literals[i] == null)
                    continue;
                if(symbolic[i].equals(literals[i].substring(1, literals[i].length()-1))) {
                    KEYWORDS_DEFAULT.add(symbolic[i]);
                    RULES.add(i);
                    LOOKUP[i] = symbolic[i];
                }
            }
            
            KEYWORDS = Collections.unmodifiableList(new ArrayList<>(KEYWORDS_DEFAULT));
            
            Logger.info("Loaded {} keywords.", KEYWORDS_DEFAULT.size());
            KEYWORDS_DEFAULT.removeAll(KEYWORDS_RED);
            String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS_DEFAULT) + ")\\b";
            String KEYWORD_PATTERN_RED = "\\b(" + String.join("|", KEYWORDS_RED) + ")\\b";
            PATTERN = Pattern.compile(
                    "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<CHAR>" + CHARACTER_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<KEYWORDRED>" + KEYWORD_PATTERN_RED + ")"
                    , Pattern.MULTILINE
            );
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw ErrorUtility.rethrow(e);
        }
	}
	
	public static List<String> keywords() {
	    return Collections.unmodifiableList(KEYWORDS);
	}
	
	public static Set<Integer> rules() {
	    return Collections.unmodifiableSet(RULES);
	}
	
    public static String keywordLookup(int keyword) {
        return LOOKUP[keyword];
    }

    public static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0; // last keyword end
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("KEYWORDRED") != null ? "keywordred" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("CHAR") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
        	spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
        	spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
        	lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
