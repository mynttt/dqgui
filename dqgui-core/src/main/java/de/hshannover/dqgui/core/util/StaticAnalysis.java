package de.hshannover.dqgui.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.tinylog.Logger;
import de.hshannover.dqgui.core.model.DSLComponentCollection;
import de.hshannover.dqgui.core.model.EditorIssue;
import de.hshannover.dqgui.core.model.IntervalMap;
import de.hshannover.dqgui.core.model.EditorIssue.WarningType;
import de.hshannover.dqgui.core.util.StaticAnalysis.StaticAnalysisResult.SourceType;
import de.hshannover.dqgui.dbsupport.api.DatabaseEnvironments;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.hshannover.dqgui.framework.model.Pointer;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDLexer;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.ActionContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.ActualParamContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.CheckContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.CheckcallContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.ConstvalContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.SourceContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.SrcqueryContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParserBaseListener;

public class StaticAnalysis {
    private static DSLComponentCollection DSL_LOOKUP;
    private static Pointer<DatabaseEnvironments> DB_ENV;
    
    public static class StaticAnalysisResult {
        
        public enum SourceType {
            IS_NO_SOURCE, UNDEFINED, CONST, QUERY
        }
        
        private final List<EditorIssue> warnings;
        private final IntervalMap<EditorLink> links = new IntervalMap<>();
        private SourceType sourceType = SourceType.IS_NO_SOURCE;
        private final DSLComponent c;
        
        private StaticAnalysisResult(List<EditorIssue> warnings, DSLComponent component) {
            this.warnings = warnings;
            this.c = component;
        }

        public List<EditorIssue> getWarnings() {
            return Collections.unmodifiableList(warnings);
        }

        public IntervalMap<EditorLink> getLinks() {
            return links;
        }

        public SourceType getSourceType() {
            return sourceType;
        }

        public DSLComponent getComponent() {
            return c;
        }
    }
    
    public abstract static class EditorLink {
        public final int start, stop;
        private String stringRepresentation;

        private EditorLink(int start, int stop) {
            this.start = start;
            this.stop = stop + 1;
        }
        
        protected void setStringPresentation() {
            this.stringRepresentation = generateStringRepresentation();
        }
        
        protected abstract String generateStringRepresentation();
        
        public String stringRepresentation() {
            return stringRepresentation;
        }
    }
    
    public static class ComponentLink extends EditorLink {
        public final DSLComponent c;

        private ComponentLink(DSLComponent c, int start, int stop) {
            super(start, stop);
            this.c = c;
        }

        @Override
        protected String generateStringRepresentation() {
            if(!DSL_LOOKUP.exists(c))
                return c.getType() + " : " + c.getIdentifier() + " does not exist yet.";
            String base = c.getType() + " : " + c.getIdentifier();
            if(c.getType() == DSLComponentType.SOURCE)
                base += " -> " + DSL_LOOKUP.requestExtradata(c).map(m -> m.getOrDefault("S_CONST_QUERY", "UNDEFINED")).orElse("");
            return base;
        }
    }
    
    private static class StaticAnalysisListener extends IQM4HDParserBaseListener {
        private final List<EditorIssue> warnings;
        private final DSLComponent component;
        private final StaticAnalysisResult res;

        protected StaticAnalysisListener(List<EditorIssue> warnings, DSLComponent component) {
            this.warnings = warnings;
            this.component = component;
            res = new StaticAnalysisResult(warnings, component);
        }

        @Override
        public void exitSrcquery(SrcqueryContext ctx) {
            super.exitSrcquery(ctx);
            if(ctx.dbase == null) return;
            databaseExists(ctx.dbase.getText(), ctx.dbase);
            res.sourceType = SourceType.QUERY;
        }
        
        @Override
        public void enterConstval(ConstvalContext ctx) {
            super.enterConstval(ctx);
            res.sourceType = SourceType.CONST;
        }

        @Override
        public void enterSource(SourceContext ctx) {
            super.enterSource(ctx);
            res.sourceType = SourceType.UNDEFINED;
            setAndValidateType(DSLComponentType.SOURCE, ctx.getStart());
        }

        @Override
        public void enterAction(ActionContext ctx) {
            super.enterAction(ctx);
            setAndValidateType(DSLComponentType.ACTION, ctx.getStart());
        }

        @Override
        public void enterCheck(CheckContext ctx) {
            super.enterCheck(ctx);
            setAndValidateType(DSLComponentType.CHECK, ctx.getStart());
        }

        @Override
        public void exitSource(SourceContext ctx) {
            super.exitSource(ctx);
            if(ctx.src == null) return;
            ownNameValidation(ctx.src.getText(), ctx.src);
        }
        
        @Override
        public void exitAction(ActionContext ctx) {
            super.exitAction(ctx);
            if(ctx.act == null) return;
            ownNameValidation(ctx.act.getText(), ctx.act);
        }

        @Override
        public void exitCheck(CheckContext ctx) {
            super.exitCheck(ctx);
            if(ctx.chk == null) return;
            ownNameValidation(ctx.chk.getText(), ctx.chk);
        }
        
        @Override
        public void exitActualParam(ActualParamContext ctx) {
            super.exitActualParam(ctx);
            if(ctx.var == null) return;
            externalComponentExists(ctx.var.getText(), DSLComponentType.SOURCE, ctx.var);
        }
        
        @Override
        public void exitCheckcall(CheckcallContext ctx) {
            super.exitCheckcall(ctx);
            if(ctx.chk == null) return;
            externalComponentExists(ctx.chk.getText(), DSLComponentType.CHECK, ctx.chk);
        }
        
        private void databaseExists(String db, Token token) {
            if(db == null) return;
            if(!DB_ENV.unsafeGet().getAllContainedConnectionIdentifiers().contains(db)) {
                warnings.add(new EditorIssue(WarningType.WARNING, token, "Missing connection",
                        "Referenced database connection with identifier '" + db + "' was not found in any environment. Database identifiers are case-sensitive."));
            }
        }
        
        private void externalComponentExists(String text, DSLComponentType type, Token start) {
            if(start.getText() == null)
                return;
            res.links.put(start.getStartIndex(), start.getStopIndex(), new ComponentLink(DSLComponent.of(text, type, false), start.getStartIndex(), start.getStopIndex()));
            DSLComponent c = DSLComponent.of(start.getText(), type, false);
            if(DSL_LOOKUP.existsCaseInsensitive(c) && !DSL_LOOKUP.exists(c)) {
                warnings.add(new EditorIssue(WarningType.WARNING, start, "Capitalization", 
                        "Referenced " + c.getType() + " " + c.getIdentifier() + " only exists if treated case-insensitive. Some repository treat identifiers case-sensitive."));
                return;
            }
            if(!DSL_LOOKUP.exists(c)) {
                warnings.add(new EditorIssue(WarningType.WARNING, start, "Missing reference",
                        "Referenced " + c.getType() + " " + c.getIdentifier() + " does not exist within this repository. Create or rename the referenced identifier."));
            }
        }

        private void ownNameValidation(String name, Token start) {
            if(!Objects.equals(name, component.getIdentifier())) {
                if(name != null && Objects.equals(name.toLowerCase(), component.getIdentifier().toLowerCase())) {
                    warnings.add(new EditorIssue(WarningType.WARNING, start, "Capitalization",
                            name + " != " + component.getIdentifier() + ". Could cause 'component not found' errors if the repository treats identifiers case-sensitive."));
                    return;
                }
                warnings.add(new EditorIssue(WarningType.WARNING, start, "Identifier mismatch",
                        name + " != " + component.getIdentifier() + ". Component identifier and identifier declaration should match."));
            }
        }
        
        private void setAndValidateType(DSLComponentType type, Token start) {
            if(type != component.getType())
                warnings.add(new EditorIssue(WarningType.WARNING, start, "Component mismatch",
                        "Categorized as " + component.getType() + " but is declared as " + type.toString()));
        }
    }

    public static StaticAnalysisResult run(String sourceCode, DSLComponent component, Consumer<StaticAnalysisResult> warningConsumer) {
        List<EditorIssue> warnings = new ArrayList<>();
        IQM4HDLexer lex = new IQM4HDLexer(CharStreams.fromString(sourceCode));
        lex.removeErrorListeners();
        CommonTokenStream tokens = new CommonTokenStream(lex);
        IQM4HDParser parser = new IQM4HDParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                int charPositionInLine, String msg, RecognitionException e) {
              CommonToken symbol = (CommonToken) offendingSymbol;
              msg = msg == null ? "Unspecified error" : msg.replaceAll("[\n\r]", "");
              
              Logger.trace("Error: Line {} | Position {} | Start {} | Stop {} | {}", 
                      symbol.getLine(),
                      symbol.getCharPositionInLine(),
                      symbol.getStartIndex(),
                      symbol.getStopIndex(),
                      msg);
              
              warnings.add(new EditorIssue(WarningType.ERROR,  symbol, "Syntax Error", msg));
            }
          });
        StaticAnalysisListener lis = new StaticAnalysisListener(warnings, component);
        parser.addParseListener(lis);
        parser.start();
        lis.res.links.getValues().forEach(EditorLink::setStringPresentation);
        Collections.sort(warnings, EditorIssue.COMPARATOR);
        warningConsumer.accept(lis.res);
        return lis.res;
    }

    public static void registerComponentLookup(DSLComponentCollection components) {
        DSL_LOOKUP = components;
    }
    
    public static void registerDatabaseEnvironments(Pointer<DatabaseEnvironments> pointer) {
        DB_ENV = pointer;
    }
}
