package de.hshannover.dqgui.core.util;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.hshannover.dqgui.core.Executors;
import de.hshannover.dqgui.core.Utility;
import de.hshannover.dqgui.core.util.RepositorySearch.SearchOptions.PatternInterpretation;
import de.hshannover.dqgui.execution.ComponentSplitter;
import de.hshannover.dqgui.execution.ComponentSplitter.ComponentSourceMapping;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.ErrorUtility;

/**
 * Global repository text search
 * 
 * @author Marc Herschel
 *
 */
public class RepositorySearch {
    private static final String ESCAPE = "\\.[]{}()<>*+-=!?^$|";
    private static final Map<Character, String> ESCAPED = new HashMap<>();
    
    static {
        for(int i = 0; i < ESCAPE.length(); i++) {
            char c = ESCAPE.charAt(i);
            ESCAPED.put(c, "\\"+c);
        }
    }
    
    public static class SearchOptions {
        
        public enum PatternInterpretation {
            AS_VALUE, CASE_INSENSITIVE, REGEX, REGEX_INSENSITIVE;
        }
        
        private final String pattern;
        private final PatternInterpretation interpretation;
        private final List<DSLComponentType> types;
        
        public SearchOptions(String pattern, PatternInterpretation interpretation, List<DSLComponentType> types) {
            this.pattern = pattern;
            this.types = types;
            this.interpretation = interpretation;
        }

        public String getPattern() {
            return pattern;
        }
        
        public List<DSLComponentType> getTypes() {
            return types;
        }

        public PatternInterpretation getInterpretation() {
            return interpretation;
        }
    }
    
    public static class SearchHit {
        private final int start, end;
        
        public SearchHit(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }
    
    public static class RepositorySearchResult {
        private final DSLComponent searchedComponent;
        private final String searchedText;
        private final List<SearchHit> hits;
        
        private RepositorySearchResult(DSLComponent searchedComponent, String searchedText, List<SearchHit> hits) {
            this.searchedComponent = searchedComponent;
            this.searchedText = searchedText;
            this.hits = hits;
        }

        public DSLComponent getSearchedComponent() {
            return searchedComponent;
        }

        public String getSearchedText() {
            return searchedText;
        }

        public List<SearchHit> getHits() {
            return hits;
        }
    }

    public static class RepositorySearchResults {
        private int occurenceCount;
        private final SearchOptions options;
        private final List<RepositorySearchResult> results = new ArrayList<>();
        private String stats;
        
        private RepositorySearchResults(SearchOptions options) {
            this.options = options;
        }
        
        private synchronized void addHits(List<RepositorySearchResult> results)  {
            this.results.addAll(results);
            occurenceCount += results.stream().mapToInt(r -> r.getHits().size()).sum();
        }

        public int getOccurenceCount() {
            return occurenceCount;
        }

        public SearchOptions getOptions() {
            return options;
        }

        public List<RepositorySearchResult> getResults() {
            return results;
        }

        public String getStatistics() {
            return stats;
        }

        private RepositorySearchResults complete(String string) {
            this.stats = string;
            return this;
        }
    }
    
    private static class SearchTask implements Supplier<List<RepositorySearchResult>> {
        private final Iterator<ComponentSourceMapping> it;
        private final List<RepositorySearchResult> results = new ArrayList<>();
        private final Pattern p;
        private final AtomicInteger i;
        
        private SearchTask(Iterator<ComponentSourceMapping> it, Pattern p, AtomicInteger counter) {
            this.it = it;
            this.p = p;
            this.i = counter;
        }

        @Override
        public List<RepositorySearchResult> get() {
            while(it.hasNext()) {
                i.getAndIncrement();
                ComponentSourceMapping next = it.next();
                List<SearchHit> hits = new ArrayList<>(4);
                Matcher m = p.matcher(next.source);
                while(m.find())
                    hits.add(new SearchHit(m.start(), m.end()));
                if(!hits.isEmpty())
                    results.add(new RepositorySearchResult(next.component, next.source, hits));
            }
            return results;
        }
        
    }
    
    public static RepositorySearchResults search(SearchOptions options, ComponentSplitter splitter) {
        Instant s = Instant.now();
        RepositorySearchResults results = new RepositorySearchResults(options);
        Pattern p = compile(options);
        AtomicInteger counter = new AtomicInteger();
        List<CompletableFuture<Void>> futures = new ArrayList<>(4);
        for(Iterator<ComponentSourceMapping> it : splitter.splitBy(4)) {
            futures.add(CompletableFuture.supplyAsync(new SearchTask(it, p, counter), Executors.SERVICE)
                .thenAccept(results::addHits));
        }
        for(int i = 0; i < futures.size(); i++) {
            try {
                futures.get(i).get();
            } catch (InterruptedException | ExecutionException e) {
                throw ErrorUtility.rethrow(e);
            }
        }
        Instant end = Instant.now();
        return results.complete("Processed " + counter.get() + (counter.get() == 1 ? " item" : " items") + " @ " + Utility.humanReadableFormat(Duration.between(s, end)));
    }

    private static Pattern compile(SearchOptions options) {
        switch(options.interpretation) {
        case REGEX:
            return Pattern.compile(options.pattern);
        case REGEX_INSENSITIVE:
            return Pattern.compile(options.pattern, Pattern.CASE_INSENSITIVE);
        case AS_VALUE:
        case CASE_INSENSITIVE:
            StringBuilder sb = new StringBuilder(options.pattern.length() + 20);
            for(int i = 0; i < options.pattern.length(); i++) {
                char c = options.pattern.charAt(i);
                String s = ESCAPED.get(c);
                if(s == null) {
                    sb.append(c);
                } else {
                    sb.append(s);
                }
            }
            return options.interpretation == PatternInterpretation.AS_VALUE ? Pattern.compile(sb.toString()) : Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE);
        default:
            throw new AssertionError("unknown case: " + options.interpretation);
        }
    }
}
