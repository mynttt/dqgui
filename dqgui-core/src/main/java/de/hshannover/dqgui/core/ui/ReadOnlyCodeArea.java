package de.hshannover.dqgui.core.ui;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import de.hshannover.dqgui.core.util.SyntaxHighlighting;
import de.hshannover.dqgui.core.util.RepositorySearch.SearchHit;
import javafx.application.Platform;

public class ReadOnlyCodeArea extends CodeArea {
    private final static Collection<String> CL = Collections.singleton("search-highlight"), CLS = Collections.singleton("search-highlight-selected");

    public ReadOnlyCodeArea() {
        super();
        setWrapText(true);
        setEditable(false);
        setParagraphGraphicFactory(LineNumberFactory.get(this));
    }
    
    public void setText(String text, List<SearchHit> hits) {
        setText(text, hits, null);
    }
    
    public void setText(String text, List<SearchHit> hits, SearchHit hit) {
        replaceText(text);
        computeHighlighting(hits, hit);
    }
    
    private void computeHighlighting(List<SearchHit> hits, SearchHit highlight) {
        Platform.runLater(() -> {
            setStyleSpans(0, SyntaxHighlighting.computeHighlighting(getText()));
            for(SearchHit h : hits) {
                setStyle(h.getStart(), h.getEnd(), CL);
            }
            if(highlight != null)
                setStyle(highlight.getStart(), highlight.getEnd(), CLS);
        });
    }
    
}