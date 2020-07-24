package de.hshannover.dqgui.core.util;

import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import de.hshannover.dqgui.execution.DSLService;
import de.hshannover.dqgui.execution.DSLServiceException;
import de.hshannover.dqgui.execution.model.DSLComponent;
import de.hshannover.dqgui.execution.model.DSLComponentType;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDLexer;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.ConstvalContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParser.SrcqueryContext;
import de.mvise.iqm4hd.dsl.parser.gen.IQM4HDParserBaseListener;

public final class ExtraData {

    private ExtraData() {}
    
    private static IQM4HDParser createParser(String source) {
        IQM4HDLexer lex = new IQM4HDLexer(CharStreams.fromString(source));
        lex.removeErrorListeners();
        IQM4HDParser pars = new IQM4HDParser(new CommonTokenStream(lex));
        pars.removeErrorListeners();
        return pars;
    }
    
    /**
     * Populate extra data fields of components. This is a first time process for previously unprocessed components.
     * @param service to update with.
     * @param toProcess data to process.
     * @throws DSLServiceException in case the service is unreachable
     */
    public static void firstLoadProcessing(DSLService service, List<DSLComponent> toProcess) throws DSLServiceException {
        for(DSLComponent c : toProcess) {
            switch(c.getType()) {
            case ACTION:
                break;
            case CHECK:
                break;
            case SOURCE:
                if(!c.getExtraData().containsKey("S_IS_CONST_QUERY")) {
                    decideConstOrQuery(service.read(c), c);
                    service.updateExtraDataOnly(c);
                }
                break;
            default:
                throw new AssertionError("unsupported component: " + c);
            }
        }
    }
    
    /**
     * Decides if a component is CONST or QUERY.<br>
     * Extra data fields:
     * - S_IS_CONST_QUERY
     * - S_CONST_QUERY
     * 
     * @author myntt
     *
     */
    private static class ConstCheckListener extends IQM4HDParserBaseListener {
        private final DSLComponent c;
        private boolean update = false;

        private ConstCheckListener(DSLComponent component) {
            c = component;
            update = true;
            c.getExtraData().remove("S_IS_CONST_QUERY");
            c.getExtraData().remove("S_CONST_QUERY");
        }
        
        @Override
        public void enterConstval(ConstvalContext ctx) {
            super.enterConstval(ctx);
            update = true;
            c.getExtraData().put("S_IS_CONST_QUERY", "");
            c.getExtraData().put("S_CONST_QUERY", "CONST");
        }

        @Override
        public void enterSrcquery(SrcqueryContext ctx) {
            super.enterSrcquery(ctx);
            update = true;
            c.getExtraData().put("S_IS_CONST_QUERY", "");
            c.getExtraData().put("S_CONST_QUERY", "QUERY");
        }
    }
    
    public static boolean decideConstOrQuery(String source, DSLComponent component) {
        if(component.getType() != DSLComponentType.SOURCE) return false;
        ConstCheckListener l = new ConstCheckListener(component);
        ParseTreeWalker.DEFAULT.walk(l, createParser(source).start());
        return l.update;
    }
}
