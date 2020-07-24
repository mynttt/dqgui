// Generated from IQM4HDParser.g4 by ANTLR 4.7.1

	package de.mvise.iqm4hd.dsl.parser.gen;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link IQM4HDParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface IQM4HDParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(IQM4HDParser.StartContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#part}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPart(IQM4HDParser.PartContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#rolelist}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRolelist(IQM4HDParser.RolelistContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#roledecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoledecl(IQM4HDParser.RoledeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#roleasgn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRoleasgn(IQM4HDParser.RoleasgnContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#srcrole}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSrcrole(IQM4HDParser.SrcroleContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSource(IQM4HDParser.SourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#srcconstlst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSrcconstlst(IQM4HDParser.SrcconstlstContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#srcconstval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSrcconstval(IQM4HDParser.SrcconstvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#srcquery}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSrcquery(IQM4HDParser.SrcqueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#srcquerytext}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSrcquerytext(IQM4HDParser.SrcquerytextContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#check}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheck(IQM4HDParser.CheckContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#formalWithParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalWithParam(IQM4HDParser.FormalWithParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#formalParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParam(IQM4HDParser.FormalParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#checkBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheckBody(IQM4HDParser.CheckBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#checkstmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheckstmt(IQM4HDParser.CheckstmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#checkasgn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheckasgn(IQM4HDParser.CheckasgnContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#checkcall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheckcall(IQM4HDParser.CheckcallContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#actualWithParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActualWithParam(IQM4HDParser.ActualWithParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#actualParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActualParam(IQM4HDParser.ActualParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#checkfor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheckfor(IQM4HDParser.CheckforContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#checkif}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheckif(IQM4HDParser.CheckifContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#checkreturn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheckreturn(IQM4HDParser.CheckreturnContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#checkReturnElement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCheckReturnElement(IQM4HDParser.CheckReturnElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(IQM4HDParser.ActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#actionResult}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionResult(IQM4HDParser.ActionResultContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(IQM4HDParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#expror}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpror(IQM4HDParser.ExprorContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#exprand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprand(IQM4HDParser.ExprandContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#exprrel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprrel(IQM4HDParser.ExprrelContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#exprterm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprterm(IQM4HDParser.ExprtermContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#exprfactor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprfactor(IQM4HDParser.ExprfactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#exprnot}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprnot(IQM4HDParser.ExprnotContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#exprun}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprun(IQM4HDParser.ExprunContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#exprval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprval(IQM4HDParser.ExprvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(IQM4HDParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#varval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarval(IQM4HDParser.VarvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#callval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallval(IQM4HDParser.CallvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#aggval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAggval(IQM4HDParser.AggvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#constval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstval(IQM4HDParser.ConstvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#atomval}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomval(IQM4HDParser.AtomvalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listexpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListexpr(IQM4HDParser.ListexprContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#constlst}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstlst(IQM4HDParser.ConstlstContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listMethod}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListMethod(IQM4HDParser.ListMethodContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listExprSelect}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExprSelect(IQM4HDParser.ListExprSelectContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listExprSelectItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExprSelectItem(IQM4HDParser.ListExprSelectItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listExprFrom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExprFrom(IQM4HDParser.ListExprFromContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listExprFromItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExprFromItem(IQM4HDParser.ListExprFromItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listExprFromSource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExprFromSource(IQM4HDParser.ListExprFromSourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listExprJoin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExprJoin(IQM4HDParser.ListExprJoinContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listExprGroupBy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExprGroupBy(IQM4HDParser.ListExprGroupByContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listExprHaving}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExprHaving(IQM4HDParser.ListExprHavingContext ctx);
	/**
	 * Visit a parse tree produced by {@link IQM4HDParser#listExprOrderBy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExprOrderBy(IQM4HDParser.ListExprOrderByContext ctx);
}