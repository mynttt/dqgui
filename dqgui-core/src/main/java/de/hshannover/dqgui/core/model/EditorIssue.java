package de.hshannover.dqgui.core.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import org.antlr.v4.runtime.Token;
import de.hshannover.dqgui.core.Config;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class EditorIssue {
    public static final Comparator<EditorIssue> COMPARATOR = (c1, c2) -> Integer.compare(c2.getType().priority, c1.getType().priority);
    
    public enum WarningType {
        ERROR(1, "Error", 
                new Image(WarningType.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+"error.png"), 16, 16, true, true),
                "syntaxError"), 
        WARNING(5, "Warning", new Image(WarningType.class.getResourceAsStream(Config.APPLICATION_PATH_ASSETS_IMAGE_UI+"warning.png"), 16, 16, true, true),
                "editorWarning");
        
        public final int priority;
        private final Collection<String> style;
        private final Image icon;
        
        WarningType(int priority, String display, Image icon, String styleClass) {
            this.priority = priority;
            this.icon = icon;
            this.style = Collections.singleton(styleClass);
        }
        
        public ImageView icon() {
            return new ImageView(icon);
        }

        public Collection<String> getStyle() {
            return style;
        }
    }
    
    private final WarningType type;
    private final String description, category, linePre;
    private final int startIndex, stopIndex, line, lineIndex;
    
    public EditorIssue(WarningType type, Token token, String category, String description) {
        boolean swapped = token.getStartIndex() < token.getStopIndex();
        if(token.getStartIndex() == token.getStopIndex() && token.getStartIndex() > 0) {
            this.startIndex = token.getStartIndex() - 1;
            this.stopIndex = token.getStopIndex();
        } else {
            this.startIndex = token.getStopIndex() < 0 ? 0 : Math.min(token.getStartIndex(), token.getStopIndex());
            this.stopIndex = Math.max(token.getStartIndex(), token.getStopIndex()) + (swapped ? 1 : 0);
        }
        this.line = token.getLine();
        this.lineIndex = token.getCharPositionInLine();
        this.type = type;
        this.category = category;
        this.description = description;
        this.linePre = "Line: " + line + ", Pos: " + lineIndex;
    }
    
    public WarningType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLineDescription() {
        return linePre;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getStopIndex() {
        return stopIndex;
    }

    public int getLine() {
        return line;
    }

    public int getLineIndex() {
        return lineIndex;
    }
}
