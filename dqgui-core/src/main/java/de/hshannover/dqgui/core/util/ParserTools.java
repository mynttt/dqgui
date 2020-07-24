package de.hshannover.dqgui.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.mvise.iqm4hd.api.RuleService;
import de.mvise.iqm4hd.core.exception.types.Iqm4hdException;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDLexer;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.ActualParamContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.CheckcallContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.RoleasgnContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParserBaseListener;

public class ParserTools {
    
    /**
     * Extract 'value' roles from action
     * @param actionSourcecode to extract from
     * @return list of extracted 'value' roles
     */
    public static List<String> extractActionValues(String actionSourcecode) {
        TargetExtractor t = new TargetExtractor();
        getParser(actionSourcecode, t).start();
        return t.identifiers;
    }
    
    /**
     * extract identifiers from all sources linked from the action
     * @param actionSourcecode source of action
     * @param service service to lookup sources
     * @return list of extracted identifiers from all sources
     * @throws Iqm4hdException if identifier could not be resolved
     */
    public static List<String> extractIdentifier(String actionSourcecode, RuleService service) throws Iqm4hdException {
        List<String> identifiers = new ArrayList<>();
        DependencyExtractor exs = new DependencyExtractor();
        getParser(actionSourcecode, exs).start();
        for(String source : exs.source) {
            IdentifierExtractor ex = new IdentifierExtractor();
            getParser(service.getSourceStatementByName(source), ex).start();
            identifiers.addAll(ex.identifiers);
        }
        return identifiers;
    }
    
    /**
     * Extract dependencies from action for remote jobs
     * @param actionName name of root action
     * @param actionSource source of root action
     * @param service to use for lookup
     * @return mapping type : [ identifier : source [
     * @throws Iqm4hdException if identifier could not be resolved
     */
    public static Map<DSLComponentType, Map<String, String>> extractDependencies(String actionName, String actionSource, RuleService service) throws Iqm4hdException {
        Map<DSLComponentType, Map<String, String>> map = new HashMap<>();
        for(DSLComponentType t : DSLComponentType.values())
            map.put(t, new HashMap<>());
        map.get(DSLComponentType.ACTION).put(actionName, actionSource);
        DependencyExtractor d = new DependencyExtractor();
        getParser(actionSource, d).start();
        for(String s : d.checks)
            map.get(DSLComponentType.CHECK).put(s, service.getCheckStatementByName(s));
        for(String s : d.source)
            map.get(DSLComponentType.SOURCE).put(s, service.getSourceStatementByName(s));
        return map;
    }
    
    private static IQM4HDParser getParser(String source, ParseTreeListener listener) {
        IQM4HDLexer lex = new IQM4HDLexer(CharStreams.fromString(source));
        lex.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lex);
        IQM4HDParser parser = new IQM4HDParser(tokens);
        parser.removeErrorListeners();
        parser.addParseListener(listener);
        return parser;
    }
    
    private static class TargetExtractor extends IQM4HDParserBaseListener {
        private final List<String> identifiers = new ArrayList<>();

        @Override
        public void exitRoleasgn(RoleasgnContext ctx) {
            super.enterRoleasgn(ctx);
            if(ctx.getText() == null || ctx.getText().trim().isEmpty()) return;
            String[] s = ctx.getText().split(":");
            if(s.length != 2) return;
            if(!s[1].equals("value")) return;
            identifiers.add(s[0]);
        }
    }
    
    private static class DependencyExtractor extends IQM4HDParserBaseListener {
        private final List<String> source = new ArrayList<>();
        private final List<String> checks = new ArrayList<>();
        
        @Override
        public void exitActualParam(ActualParamContext ctx) {
            super.exitActualParam(ctx);
            if(ctx.var == null) return;
            source.add(ctx.var.getText());
        }
        
        @Override
        public void exitCheckcall(CheckcallContext ctx) {
            super.exitCheckcall(ctx);
            if(ctx.chk == null) return;
            checks.add(ctx.chk.getText());
        }
    }
    
    private static class IdentifierExtractor extends IQM4HDParserBaseListener {
        private final List<String> identifiers = new ArrayList<>();
        
        @Override
        public void exitRoleasgn(RoleasgnContext ctx) {
            super.enterRoleasgn(ctx);
            if(ctx.getText() == null) return;
            String[] s = ctx.getText().split(":");
            if(s.length != 2 && !s[1].equals("IDENTIFIER")) return;
            identifiers.add(s[0]);
        }
    }
}
