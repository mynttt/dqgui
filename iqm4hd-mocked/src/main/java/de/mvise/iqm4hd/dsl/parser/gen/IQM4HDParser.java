// Generated from IQM4HDParser.g4 by ANTLR 4.7.1

	package de.mvise.iqm4hd.dsl.parser.gen;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class IQM4HDParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		DATABASE=1, NATIVE=2, SOURCE=3, CHECK=4, ACTION=5, EXECUTE=6, USING=7, 
		ON=8, EACH=9, FROM=10, RESULT=11, IF=12, IN=13, AS=14, END=15, AND=16, 
		TYPE=17, WITH=18, ERROR=19, WARNING=20, RECORD=21, SELECT=22, WHERE=23, 
		PATTERN=24, RETURN=25, NULL=26, BOOL=27, FOR=28, ELSE=29, SINGLE=30, LIST=31, 
		LEFT=32, RIGHT=33, FULL=34, JOIN=35, ROLE=36, ROLES=37, IS=38, GROUP=39, 
		ORDER=40, BY=41, HAVING=42, ABOVE=43, CONST=44, OR=45, NOT=46, TO=47, 
		QUERY=48, AVG=49, MIN=50, MAX=51, SUM=52, COUNT=53, ABS=54, ARIMA=55, 
		LENGTH=56, MATCHES=57, VAL2BIN=58, EVAL=59, DOB2AGE=60, CUBESCORE=61, 
		BINOM=62, NVL=63, PERCENTAGE=64, EVALREC=65, RELOP=66, Colon=67, Comma=68, 
		Open=69, Close=70, Assign=71, OpenIdx=72, CloseIdx=73, Plus=74, Minus=75, 
		Mul=76, Div=77, Semicolon=78, Equals=79, Dot=80, OpenBrace=81, CloseBrace=82, 
		ID=83, NUMBER=84, STRING=85, REGEXP=86, WS=87, NL=88, COMMENT=89, END_NATIVE=90, 
		QUERYTEXT=91;
	public static final int
		RULE_start = 0, RULE_part = 1, RULE_rolelist = 2, RULE_roledecl = 3, RULE_roleasgn = 4, 
		RULE_srcrole = 5, RULE_source = 6, RULE_srcconstlst = 7, RULE_srcconstval = 8, 
		RULE_srcquery = 9, RULE_srcquerytext = 10, RULE_check = 11, RULE_formalWithParam = 12, 
		RULE_formalParam = 13, RULE_checkBody = 14, RULE_checkstmt = 15, RULE_checkasgn = 16, 
		RULE_checkcall = 17, RULE_actualWithParam = 18, RULE_actualParam = 19, 
		RULE_checkfor = 20, RULE_checkif = 21, RULE_checkreturn = 22, RULE_checkReturnElement = 23, 
		RULE_action = 24, RULE_actionResult = 25, RULE_expr = 26, RULE_expror = 27, 
		RULE_exprand = 28, RULE_exprrel = 29, RULE_exprterm = 30, RULE_exprfactor = 31, 
		RULE_exprnot = 32, RULE_exprun = 33, RULE_exprval = 34, RULE_value = 35, 
		RULE_varval = 36, RULE_callval = 37, RULE_aggval = 38, RULE_constval = 39, 
		RULE_atomval = 40, RULE_listexpr = 41, RULE_constlst = 42, RULE_listMethod = 43, 
		RULE_listExprSelect = 44, RULE_listExprSelectItem = 45, RULE_listExprFrom = 46, 
		RULE_listExprFromItem = 47, RULE_listExprFromSource = 48, RULE_listExprJoin = 49, 
		RULE_listExprGroupBy = 50, RULE_listExprHaving = 51, RULE_listExprOrderBy = 52;
	public static final String[] ruleNames = {
		"start", "part", "rolelist", "roledecl", "roleasgn", "srcrole", "source", 
		"srcconstlst", "srcconstval", "srcquery", "srcquerytext", "check", "formalWithParam", 
		"formalParam", "checkBody", "checkstmt", "checkasgn", "checkcall", "actualWithParam", 
		"actualParam", "checkfor", "checkif", "checkreturn", "checkReturnElement", 
		"action", "actionResult", "expr", "expror", "exprand", "exprrel", "exprterm", 
		"exprfactor", "exprnot", "exprun", "exprval", "value", "varval", "callval", 
		"aggval", "constval", "atomval", "listexpr", "constlst", "listMethod", 
		"listExprSelect", "listExprSelectItem", "listExprFrom", "listExprFromItem", 
		"listExprFromSource", "listExprJoin", "listExprGroupBy", "listExprHaving", 
		"listExprOrderBy"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'DATABASE'", "'NATIVE'", "'SOURCE'", "'CHECK'", "'ACTION'", "'EXECUTE'", 
		"'USING'", "'ON'", "'EACH'", "'FROM'", "'RESULT'", "'IF'", "'IN'", "'AS'", 
		"'END'", "'AND'", "'TYPE'", "'WITH'", "'ERROR'", "'WARNING'", "'RECORD'", 
		"'SELECT'", "'WHERE'", "'PATTERN'", "'RETURN'", "'NULL'", null, "'FOR'", 
		"'ELSE'", "'SINGLE'", "'LIST'", "'LEFT'", "'RIGHT'", "'FULL'", "'JOIN'", 
		"'ROLE'", "'ROLES'", "'IS'", "'GROUP'", "'ORDER'", "'BY'", "'HAVING'", 
		"'ABOVE'", "'CONST'", "'OR'", "'NOT'", "'TO'", "'QUERY'", "'AVG'", "'MIN'", 
		"'MAX'", "'SUM'", "'COUNT'", "'ABS'", "'ARIMA'", "'LENGTH'", "'MATCHES'", 
		"'VAL2BIN'", "'EVAL'", "'DOB2AGE'", "'CUBESCORE'", "'BINOM'", "'NVL'", 
		"'PERCENTAGE'", "'EVALREC'", null, "':'", "','", "'('", "')'", "':='", 
		"'['", "']'", "'+'", "'-'", "'*'", "'/'", "';'", "'='", "'.'", "'{'", 
		"'}'", null, null, null, null, null, null, null, "'\nEND'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "DATABASE", "NATIVE", "SOURCE", "CHECK", "ACTION", "EXECUTE", "USING", 
		"ON", "EACH", "FROM", "RESULT", "IF", "IN", "AS", "END", "AND", "TYPE", 
		"WITH", "ERROR", "WARNING", "RECORD", "SELECT", "WHERE", "PATTERN", "RETURN", 
		"NULL", "BOOL", "FOR", "ELSE", "SINGLE", "LIST", "LEFT", "RIGHT", "FULL", 
		"JOIN", "ROLE", "ROLES", "IS", "GROUP", "ORDER", "BY", "HAVING", "ABOVE", 
		"CONST", "OR", "NOT", "TO", "QUERY", "AVG", "MIN", "MAX", "SUM", "COUNT", 
		"ABS", "ARIMA", "LENGTH", "MATCHES", "VAL2BIN", "EVAL", "DOB2AGE", "CUBESCORE", 
		"BINOM", "NVL", "PERCENTAGE", "EVALREC", "RELOP", "Colon", "Comma", "Open", 
		"Close", "Assign", "OpenIdx", "CloseIdx", "Plus", "Minus", "Mul", "Div", 
		"Semicolon", "Equals", "Dot", "OpenBrace", "CloseBrace", "ID", "NUMBER", 
		"STRING", "REGEXP", "WS", "NL", "COMMENT", "END_NATIVE", "QUERYTEXT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "IQM4HDParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public IQM4HDParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class StartContext extends ParserRuleContext {
		public List<PartContext> part() {
			return getRuleContexts(PartContext.class);
		}
		public PartContext part(int i) {
			return getRuleContext(PartContext.class,i);
		}
		public StartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_start; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterStart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitStart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitStart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StartContext start() throws RecognitionException {
		StartContext _localctx = new StartContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_start);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(106);
				part();
				}
				}
				setState(109); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SOURCE) | (1L << CHECK) | (1L << ACTION))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PartContext extends ParserRuleContext {
		public SourceContext source() {
			return getRuleContext(SourceContext.class,0);
		}
		public CheckContext check() {
			return getRuleContext(CheckContext.class,0);
		}
		public ActionContext action() {
			return getRuleContext(ActionContext.class,0);
		}
		public PartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_part; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterPart(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitPart(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitPart(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PartContext part() throws RecognitionException {
		PartContext _localctx = new PartContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_part);
		try {
			setState(114);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SOURCE:
				enterOuterAlt(_localctx, 1);
				{
				setState(111);
				source();
				}
				break;
			case CHECK:
				enterOuterAlt(_localctx, 2);
				{
				setState(112);
				check();
				}
				break;
			case ACTION:
				enterOuterAlt(_localctx, 3);
				{
				setState(113);
				action();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RolelistContext extends ParserRuleContext {
		public Token role;
		public List<TerminalNode> ID() { return getTokens(IQM4HDParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(IQM4HDParser.ID, i);
		}
		public RolelistContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rolelist; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterRolelist(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitRolelist(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitRolelist(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RolelistContext rolelist() throws RecognitionException {
		RolelistContext _localctx = new RolelistContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_rolelist);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			match(Open);
			setState(117);
			((RolelistContext)_localctx).role = match(ID);
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(118);
				match(Comma);
				setState(119);
				((RolelistContext)_localctx).role = match(ID);
				}
				}
				setState(124);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(125);
			match(Close);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RoledeclContext extends ParserRuleContext {
		public List<RoleasgnContext> roleasgn() {
			return getRuleContexts(RoleasgnContext.class);
		}
		public RoleasgnContext roleasgn(int i) {
			return getRuleContext(RoleasgnContext.class,i);
		}
		public RoledeclContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_roledecl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterRoledecl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitRoledecl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitRoledecl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RoledeclContext roledecl() throws RecognitionException {
		RoledeclContext _localctx = new RoledeclContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_roledecl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(Open);
			setState(128);
			roleasgn();
			setState(133);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(129);
				match(Comma);
				setState(130);
				roleasgn();
				}
				}
				setState(135);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(136);
			match(Close);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RoleasgnContext extends ParserRuleContext {
		public Token role;
		public SrcroleContext srcrole() {
			return getRuleContext(SrcroleContext.class,0);
		}
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public RoleasgnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_roleasgn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterRoleasgn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitRoleasgn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitRoleasgn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RoleasgnContext roleasgn() throws RecognitionException {
		RoleasgnContext _localctx = new RoleasgnContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_roleasgn);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(138);
			srcrole();
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Colon) {
				{
				setState(139);
				match(Colon);
				setState(140);
				((RoleasgnContext)_localctx).role = match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SrcroleContext extends ParserRuleContext {
		public Token attrname;
		public List<TerminalNode> ID() { return getTokens(IQM4HDParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(IQM4HDParser.ID, i);
		}
		public SrcroleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_srcrole; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterSrcrole(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitSrcrole(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitSrcrole(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SrcroleContext srcrole() throws RecognitionException {
		SrcroleContext _localctx = new SrcroleContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_srcrole);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			((SrcroleContext)_localctx).attrname = match(ID);
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Dot) {
				{
				{
				setState(144);
				match(Dot);
				setState(145);
				((SrcroleContext)_localctx).attrname = match(ID);
				}
				}
				setState(150);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SourceContext extends ParserRuleContext {
		public Token src;
		public TerminalNode SOURCE() { return getToken(IQM4HDParser.SOURCE, 0); }
		public TerminalNode TYPE() { return getToken(IQM4HDParser.TYPE, 0); }
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public SrcconstlstContext srcconstlst() {
			return getRuleContext(SrcconstlstContext.class,0);
		}
		public SrcconstvalContext srcconstval() {
			return getRuleContext(SrcconstvalContext.class,0);
		}
		public SrcqueryContext srcquery() {
			return getRuleContext(SrcqueryContext.class,0);
		}
		public SourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_source; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterSource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitSource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitSource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SourceContext source() throws RecognitionException {
		SourceContext _localctx = new SourceContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_source);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(151);
			match(SOURCE);
			setState(152);
			((SourceContext)_localctx).src = match(ID);
			setState(153);
			match(TYPE);
			setState(157);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(154);
				srcconstlst();
				}
				break;
			case 2:
				{
				setState(155);
				srcconstval();
				}
				break;
			case 3:
				{
				setState(156);
				srcquery();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SrcconstlstContext extends ParserRuleContext {
		public TerminalNode LIST() { return getToken(IQM4HDParser.LIST, 0); }
		public TerminalNode CONST() { return getToken(IQM4HDParser.CONST, 0); }
		public TerminalNode ROLES() { return getToken(IQM4HDParser.ROLES, 0); }
		public RolelistContext rolelist() {
			return getRuleContext(RolelistContext.class,0);
		}
		public ConstlstContext constlst() {
			return getRuleContext(ConstlstContext.class,0);
		}
		public TerminalNode END() { return getToken(IQM4HDParser.END, 0); }
		public SrcconstlstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_srcconstlst; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterSrcconstlst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitSrcconstlst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitSrcconstlst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SrcconstlstContext srcconstlst() throws RecognitionException {
		SrcconstlstContext _localctx = new SrcconstlstContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_srcconstlst);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(159);
			match(LIST);
			setState(160);
			match(CONST);
			setState(161);
			match(ROLES);
			setState(162);
			rolelist();
			setState(163);
			match(Colon);
			setState(164);
			constlst();
			setState(165);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SrcconstvalContext extends ParserRuleContext {
		public TerminalNode CONST() { return getToken(IQM4HDParser.CONST, 0); }
		public TerminalNode ROLES() { return getToken(IQM4HDParser.ROLES, 0); }
		public RolelistContext rolelist() {
			return getRuleContext(RolelistContext.class,0);
		}
		public TerminalNode END() { return getToken(IQM4HDParser.END, 0); }
		public ConstvalContext constval() {
			return getRuleContext(ConstvalContext.class,0);
		}
		public TerminalNode REGEXP() { return getToken(IQM4HDParser.REGEXP, 0); }
		public SrcconstvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_srcconstval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterSrcconstval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitSrcconstval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitSrcconstval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SrcconstvalContext srcconstval() throws RecognitionException {
		SrcconstvalContext _localctx = new SrcconstvalContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_srcconstval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			match(CONST);
			setState(168);
			match(ROLES);
			setState(169);
			rolelist();
			setState(170);
			match(Colon);
			setState(173);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NULL:
			case BOOL:
			case OpenIdx:
			case Minus:
			case NUMBER:
			case STRING:
				{
				setState(171);
				constval();
				}
				break;
			case REGEXP:
				{
				setState(172);
				match(REGEXP);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(175);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SrcqueryContext extends ParserRuleContext {
		public Token dbase;
		public SrcquerytextContext qtext;
		public TerminalNode LIST() { return getToken(IQM4HDParser.LIST, 0); }
		public TerminalNode QUERY() { return getToken(IQM4HDParser.QUERY, 0); }
		public TerminalNode DATABASE() { return getToken(IQM4HDParser.DATABASE, 0); }
		public TerminalNode NATIVE() { return getToken(IQM4HDParser.NATIVE, 0); }
		public TerminalNode END_NATIVE() { return getToken(IQM4HDParser.END_NATIVE, 0); }
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public SrcquerytextContext srcquerytext() {
			return getRuleContext(SrcquerytextContext.class,0);
		}
		public TerminalNode ROLES() { return getToken(IQM4HDParser.ROLES, 0); }
		public RoledeclContext roledecl() {
			return getRuleContext(RoledeclContext.class,0);
		}
		public SrcqueryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_srcquery; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterSrcquery(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitSrcquery(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitSrcquery(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SrcqueryContext srcquery() throws RecognitionException {
		SrcqueryContext _localctx = new SrcqueryContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_srcquery);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			match(LIST);
			setState(178);
			match(QUERY);
			setState(181);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ROLES) {
				{
				setState(179);
				match(ROLES);
				setState(180);
				roledecl();
				}
			}

			setState(183);
			match(Colon);
			setState(184);
			match(DATABASE);
			setState(185);
			((SrcqueryContext)_localctx).dbase = match(ID);
			setState(186);
			match(NATIVE);
			setState(187);
			((SrcqueryContext)_localctx).qtext = srcquerytext();
			setState(188);
			match(END_NATIVE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SrcquerytextContext extends ParserRuleContext {
		public List<TerminalNode> QUERYTEXT() { return getTokens(IQM4HDParser.QUERYTEXT); }
		public TerminalNode QUERYTEXT(int i) {
			return getToken(IQM4HDParser.QUERYTEXT, i);
		}
		public SrcquerytextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_srcquerytext; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterSrcquerytext(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitSrcquerytext(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitSrcquerytext(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SrcquerytextContext srcquerytext() throws RecognitionException {
		SrcquerytextContext _localctx = new SrcquerytextContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_srcquerytext);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(190);
				match(QUERYTEXT);
				}
				}
				setState(193); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==QUERYTEXT );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CheckContext extends ParserRuleContext {
		public Token chk;
		public FormalParamContext onParam;
		public FormalWithParamContext withParam;
		public TerminalNode CHECK() { return getToken(IQM4HDParser.CHECK, 0); }
		public TerminalNode ON() { return getToken(IQM4HDParser.ON, 0); }
		public CheckBodyContext checkBody() {
			return getRuleContext(CheckBodyContext.class,0);
		}
		public TerminalNode END() { return getToken(IQM4HDParser.END, 0); }
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public FormalParamContext formalParam() {
			return getRuleContext(FormalParamContext.class,0);
		}
		public FormalWithParamContext formalWithParam() {
			return getRuleContext(FormalWithParamContext.class,0);
		}
		public CheckContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_check; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCheck(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCheck(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCheck(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CheckContext check() throws RecognitionException {
		CheckContext _localctx = new CheckContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_check);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(CHECK);
			setState(196);
			((CheckContext)_localctx).chk = match(ID);
			setState(197);
			match(ON);
			setState(198);
			((CheckContext)_localctx).onParam = formalParam();
			setState(200);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WITH) {
				{
				setState(199);
				((CheckContext)_localctx).withParam = formalWithParam();
				}
			}

			setState(202);
			match(Colon);
			setState(203);
			checkBody();
			setState(204);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalWithParamContext extends ParserRuleContext {
		public TerminalNode WITH() { return getToken(IQM4HDParser.WITH, 0); }
		public List<FormalParamContext> formalParam() {
			return getRuleContexts(FormalParamContext.class);
		}
		public FormalParamContext formalParam(int i) {
			return getRuleContext(FormalParamContext.class,i);
		}
		public FormalWithParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalWithParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterFormalWithParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitFormalWithParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitFormalWithParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormalWithParamContext formalWithParam() throws RecognitionException {
		FormalWithParamContext _localctx = new FormalWithParamContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_formalWithParam);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			match(WITH);
			setState(207);
			formalParam();
			setState(212);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(208);
				match(Comma);
				setState(209);
				formalParam();
				}
				}
				setState(214);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FormalParamContext extends ParserRuleContext {
		public Token name;
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public TerminalNode LIST() { return getToken(IQM4HDParser.LIST, 0); }
		public RolelistContext rolelist() {
			return getRuleContext(RolelistContext.class,0);
		}
		public FormalParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_formalParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterFormalParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitFormalParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitFormalParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FormalParamContext formalParam() throws RecognitionException {
		FormalParamContext _localctx = new FormalParamContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_formalParam);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(216);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LIST) {
				{
				setState(215);
				match(LIST);
				}
			}

			setState(218);
			((FormalParamContext)_localctx).name = match(ID);
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Open) {
				{
				setState(219);
				rolelist();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CheckBodyContext extends ParserRuleContext {
		public List<CheckstmtContext> checkstmt() {
			return getRuleContexts(CheckstmtContext.class);
		}
		public CheckstmtContext checkstmt(int i) {
			return getRuleContext(CheckstmtContext.class,i);
		}
		public CheckBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_checkBody; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCheckBody(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCheckBody(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCheckBody(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CheckBodyContext checkBody() throws RecognitionException {
		CheckBodyContext _localctx = new CheckBodyContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_checkBody);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(223); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(222);
				checkstmt();
				}
				}
				setState(225); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EXECUTE) | (1L << IF) | (1L << RETURN) | (1L << FOR) | (1L << LIST))) != 0) || _la==ID );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CheckstmtContext extends ParserRuleContext {
		public CheckasgnContext checkasgn() {
			return getRuleContext(CheckasgnContext.class,0);
		}
		public CheckforContext checkfor() {
			return getRuleContext(CheckforContext.class,0);
		}
		public CheckifContext checkif() {
			return getRuleContext(CheckifContext.class,0);
		}
		public CheckreturnContext checkreturn() {
			return getRuleContext(CheckreturnContext.class,0);
		}
		public CheckcallContext checkcall() {
			return getRuleContext(CheckcallContext.class,0);
		}
		public CheckstmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_checkstmt; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCheckstmt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCheckstmt(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCheckstmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CheckstmtContext checkstmt() throws RecognitionException {
		CheckstmtContext _localctx = new CheckstmtContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_checkstmt);
		try {
			setState(234);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case LIST:
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(227);
				checkasgn();
				}
				break;
			case FOR:
				enterOuterAlt(_localctx, 2);
				{
				setState(228);
				checkfor();
				}
				break;
			case IF:
				enterOuterAlt(_localctx, 3);
				{
				setState(229);
				checkif();
				}
				break;
			case RETURN:
				enterOuterAlt(_localctx, 4);
				{
				setState(230);
				checkreturn();
				}
				break;
			case EXECUTE:
				enterOuterAlt(_localctx, 5);
				{
				setState(231);
				checkcall();
				setState(232);
				match(Semicolon);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CheckasgnContext extends ParserRuleContext {
		public Token newVar;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public TerminalNode LIST() { return getToken(IQM4HDParser.LIST, 0); }
		public ListexprContext listexpr() {
			return getRuleContext(ListexprContext.class,0);
		}
		public TerminalNode EVAL() { return getToken(IQM4HDParser.EVAL, 0); }
		public TerminalNode EVALREC() { return getToken(IQM4HDParser.EVALREC, 0); }
		public CheckasgnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_checkasgn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCheckasgn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCheckasgn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCheckasgn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CheckasgnContext checkasgn() throws RecognitionException {
		CheckasgnContext _localctx = new CheckasgnContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_checkasgn);
		int _la;
		try {
			setState(256);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(236);
				((CheckasgnContext)_localctx).newVar = match(ID);
				setState(237);
				match(Assign);
				setState(238);
				expr();
				setState(239);
				match(Semicolon);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(241);
				match(LIST);
				setState(242);
				((CheckasgnContext)_localctx).newVar = match(ID);
				setState(243);
				match(Assign);
				setState(245);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==EVAL) {
					{
					setState(244);
					match(EVAL);
					}
				}

				setState(247);
				listexpr();
				setState(248);
				match(Semicolon);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(250);
				((CheckasgnContext)_localctx).newVar = match(ID);
				setState(251);
				match(Assign);
				setState(252);
				match(EVALREC);
				setState(253);
				listexpr();
				setState(254);
				match(Semicolon);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CheckcallContext extends ParserRuleContext {
		public Token chk;
		public ActualParamContext onParam;
		public ActualWithParamContext withParam;
		public TerminalNode EXECUTE() { return getToken(IQM4HDParser.EXECUTE, 0); }
		public TerminalNode ON() { return getToken(IQM4HDParser.ON, 0); }
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public ActualParamContext actualParam() {
			return getRuleContext(ActualParamContext.class,0);
		}
		public TerminalNode PERCENTAGE() { return getToken(IQM4HDParser.PERCENTAGE, 0); }
		public TerminalNode EACH() { return getToken(IQM4HDParser.EACH, 0); }
		public TerminalNode IF() { return getToken(IQM4HDParser.IF, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ActualWithParamContext actualWithParam() {
			return getRuleContext(ActualWithParamContext.class,0);
		}
		public CheckcallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_checkcall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCheckcall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCheckcall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCheckcall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CheckcallContext checkcall() throws RecognitionException {
		CheckcallContext _localctx = new CheckcallContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_checkcall);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(258);
			match(EXECUTE);
			setState(260);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PERCENTAGE) {
				{
				setState(259);
				match(PERCENTAGE);
				}
			}

			setState(262);
			((CheckcallContext)_localctx).chk = match(ID);
			setState(263);
			match(ON);
			setState(265);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==EACH) {
				{
				setState(264);
				match(EACH);
				}
			}

			setState(267);
			((CheckcallContext)_localctx).onParam = actualParam();
			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WITH) {
				{
				setState(268);
				((CheckcallContext)_localctx).withParam = actualWithParam();
				}
			}

			setState(273);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IF) {
				{
				setState(271);
				match(IF);
				setState(272);
				expr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActualWithParamContext extends ParserRuleContext {
		public TerminalNode WITH() { return getToken(IQM4HDParser.WITH, 0); }
		public List<ActualParamContext> actualParam() {
			return getRuleContexts(ActualParamContext.class);
		}
		public ActualParamContext actualParam(int i) {
			return getRuleContext(ActualParamContext.class,i);
		}
		public ActualWithParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actualWithParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterActualWithParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitActualWithParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitActualWithParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActualWithParamContext actualWithParam() throws RecognitionException {
		ActualWithParamContext _localctx = new ActualWithParamContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_actualWithParam);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
			match(WITH);
			setState(276);
			actualParam();
			setState(281);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(277);
				match(Comma);
				setState(278);
				actualParam();
				}
				}
				setState(283);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActualParamContext extends ParserRuleContext {
		public Token var;
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public RoledeclContext roledecl() {
			return getRuleContext(RoledeclContext.class,0);
		}
		public ConstvalContext constval() {
			return getRuleContext(ConstvalContext.class,0);
		}
		public ActualParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actualParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterActualParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitActualParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitActualParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActualParamContext actualParam() throws RecognitionException {
		ActualParamContext _localctx = new ActualParamContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_actualParam);
		int _la;
		try {
			setState(289);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(284);
				((ActualParamContext)_localctx).var = match(ID);
				setState(286);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Open) {
					{
					setState(285);
					roledecl();
					}
				}

				}
				break;
			case NULL:
			case BOOL:
			case OpenIdx:
			case Minus:
			case NUMBER:
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(288);
				constval();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CheckforContext extends ParserRuleContext {
		public Token var;
		public ExprContext forStart;
		public ExprContext forEnd;
		public ListexprContext forList;
		public TerminalNode FOR() { return getToken(IQM4HDParser.FOR, 0); }
		public TerminalNode TO() { return getToken(IQM4HDParser.TO, 0); }
		public CheckBodyContext checkBody() {
			return getRuleContext(CheckBodyContext.class,0);
		}
		public TerminalNode END() { return getToken(IQM4HDParser.END, 0); }
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode IN() { return getToken(IQM4HDParser.IN, 0); }
		public ListexprContext listexpr() {
			return getRuleContext(ListexprContext.class,0);
		}
		public CheckforContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_checkfor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCheckfor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCheckfor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCheckfor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CheckforContext checkfor() throws RecognitionException {
		CheckforContext _localctx = new CheckforContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_checkfor);
		try {
			setState(309);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(291);
				match(FOR);
				setState(292);
				((CheckforContext)_localctx).var = match(ID);
				setState(293);
				match(Assign);
				setState(294);
				((CheckforContext)_localctx).forStart = expr();
				setState(295);
				match(TO);
				setState(296);
				((CheckforContext)_localctx).forEnd = expr();
				setState(297);
				match(Colon);
				setState(298);
				checkBody();
				setState(299);
				match(END);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(301);
				match(FOR);
				setState(302);
				((CheckforContext)_localctx).var = match(ID);
				setState(303);
				match(IN);
				setState(304);
				((CheckforContext)_localctx).forList = listexpr();
				setState(305);
				match(Colon);
				setState(306);
				checkBody();
				setState(307);
				match(END);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CheckifContext extends ParserRuleContext {
		public CheckBodyContext truePath;
		public CheckBodyContext falsePath;
		public TerminalNode IF() { return getToken(IQM4HDParser.IF, 0); }
		public TerminalNode END() { return getToken(IQM4HDParser.END, 0); }
		public List<CheckBodyContext> checkBody() {
			return getRuleContexts(CheckBodyContext.class);
		}
		public CheckBodyContext checkBody(int i) {
			return getRuleContext(CheckBodyContext.class,i);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode ELSE() { return getToken(IQM4HDParser.ELSE, 0); }
		public CheckifContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_checkif; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCheckif(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCheckif(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCheckif(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CheckifContext checkif() throws RecognitionException {
		CheckifContext _localctx = new CheckifContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_checkif);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(311);
			match(IF);
			{
			setState(312);
			expr();
			}
			setState(313);
			match(Colon);
			setState(314);
			((CheckifContext)_localctx).truePath = checkBody();
			setState(317);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ELSE) {
				{
				setState(315);
				match(ELSE);
				setState(316);
				((CheckifContext)_localctx).falsePath = checkBody();
				}
			}

			setState(319);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CheckreturnContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(IQM4HDParser.RETURN, 0); }
		public TerminalNode LIST() { return getToken(IQM4HDParser.LIST, 0); }
		public ListexprContext listexpr() {
			return getRuleContext(ListexprContext.class,0);
		}
		public List<CheckReturnElementContext> checkReturnElement() {
			return getRuleContexts(CheckReturnElementContext.class);
		}
		public CheckReturnElementContext checkReturnElement(int i) {
			return getRuleContext(CheckReturnElementContext.class,i);
		}
		public CheckreturnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_checkreturn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCheckreturn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCheckreturn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCheckreturn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CheckreturnContext checkreturn() throws RecognitionException {
		CheckreturnContext _localctx = new CheckreturnContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_checkreturn);
		int _la;
		try {
			setState(337);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(321);
				match(RETURN);
				setState(322);
				match(LIST);
				setState(323);
				listexpr();
				setState(324);
				match(Semicolon);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(326);
				match(RETURN);
				setState(327);
				checkReturnElement();
				setState(332);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Comma) {
					{
					{
					setState(328);
					match(Comma);
					setState(329);
					checkReturnElement();
					}
					}
					setState(334);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(335);
				match(Semicolon);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CheckReturnElementContext extends ParserRuleContext {
		public Token role;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode ROLE() { return getToken(IQM4HDParser.ROLE, 0); }
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public CheckReturnElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_checkReturnElement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCheckReturnElement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCheckReturnElement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCheckReturnElement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CheckReturnElementContext checkReturnElement() throws RecognitionException {
		CheckReturnElementContext _localctx = new CheckReturnElementContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_checkReturnElement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(339);
			expr();
			setState(342);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ROLE) {
				{
				setState(340);
				match(ROLE);
				setState(341);
				((CheckReturnElementContext)_localctx).role = match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActionContext extends ParserRuleContext {
		public Token act;
		public TerminalNode ACTION() { return getToken(IQM4HDParser.ACTION, 0); }
		public CheckcallContext checkcall() {
			return getRuleContext(CheckcallContext.class,0);
		}
		public ActionResultContext actionResult() {
			return getRuleContext(ActionResultContext.class,0);
		}
		public TerminalNode END() { return getToken(IQM4HDParser.END, 0); }
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public ActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterAction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitAction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitAction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionContext action() throws RecognitionException {
		ActionContext _localctx = new ActionContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_action);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(344);
			match(ACTION);
			setState(345);
			((ActionContext)_localctx).act = match(ID);
			setState(346);
			match(Colon);
			setState(347);
			checkcall();
			setState(348);
			actionResult();
			setState(349);
			match(END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActionResultContext extends ParserRuleContext {
		public Token warn;
		public Token error;
		public Token single;
		public Token threshold;
		public TerminalNode RESULT() { return getToken(IQM4HDParser.RESULT, 0); }
		public TerminalNode IN() { return getToken(IQM4HDParser.IN, 0); }
		public TerminalNode WARNING() { return getToken(IQM4HDParser.WARNING, 0); }
		public List<TerminalNode> ABOVE() { return getTokens(IQM4HDParser.ABOVE); }
		public TerminalNode ABOVE(int i) {
			return getToken(IQM4HDParser.ABOVE, i);
		}
		public TerminalNode AND() { return getToken(IQM4HDParser.AND, 0); }
		public TerminalNode ERROR() { return getToken(IQM4HDParser.ERROR, 0); }
		public List<TerminalNode> NUMBER() { return getTokens(IQM4HDParser.NUMBER); }
		public TerminalNode NUMBER(int i) {
			return getToken(IQM4HDParser.NUMBER, i);
		}
		public ActionResultContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actionResult; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterActionResult(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitActionResult(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitActionResult(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionResultContext actionResult() throws RecognitionException {
		ActionResultContext _localctx = new ActionResultContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_actionResult);
		int _la;
		try {
			setState(367);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(351);
				match(RESULT);
				setState(352);
				match(IN);
				setState(353);
				match(WARNING);
				setState(354);
				match(ABOVE);
				setState(355);
				((ActionResultContext)_localctx).warn = match(NUMBER);
				setState(356);
				match(AND);
				setState(357);
				match(ERROR);
				setState(358);
				match(ABOVE);
				setState(359);
				((ActionResultContext)_localctx).error = match(NUMBER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(360);
				match(RESULT);
				setState(361);
				match(IN);
				setState(362);
				((ActionResultContext)_localctx).single = _input.LT(1);
				_la = _input.LA(1);
				if ( !(_la==ERROR || _la==WARNING) ) {
					((ActionResultContext)_localctx).single = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(365);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ABOVE) {
					{
					setState(363);
					match(ABOVE);
					setState(364);
					((ActionResultContext)_localctx).threshold = match(NUMBER);
					}
				}

				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprorContext expror() {
			return getRuleContext(ExprorContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			expror(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprorContext extends ParserRuleContext {
		public ExprorContext left;
		public ExprandContext single;
		public ExprandContext right;
		public ExprandContext exprand() {
			return getRuleContext(ExprandContext.class,0);
		}
		public TerminalNode OR() { return getToken(IQM4HDParser.OR, 0); }
		public ExprorContext expror() {
			return getRuleContext(ExprorContext.class,0);
		}
		public ExprorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expror; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterExpror(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitExpror(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitExpror(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprorContext expror() throws RecognitionException {
		return expror(0);
	}

	private ExprorContext expror(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprorContext _localctx = new ExprorContext(_ctx, _parentState);
		ExprorContext _prevctx = _localctx;
		int _startState = 54;
		enterRecursionRule(_localctx, 54, RULE_expror, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(372);
			((ExprorContext)_localctx).single = exprand(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(379);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprorContext(_parentctx, _parentState);
					_localctx.left = _prevctx;
					_localctx.left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_expror);
					setState(374);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(375);
					match(OR);
					setState(376);
					((ExprorContext)_localctx).right = exprand(0);
					}
					} 
				}
				setState(381);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExprandContext extends ParserRuleContext {
		public ExprandContext left;
		public ExprrelContext single;
		public ExprrelContext right;
		public ExprrelContext exprrel() {
			return getRuleContext(ExprrelContext.class,0);
		}
		public TerminalNode AND() { return getToken(IQM4HDParser.AND, 0); }
		public ExprandContext exprand() {
			return getRuleContext(ExprandContext.class,0);
		}
		public ExprandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterExprand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitExprand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitExprand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprandContext exprand() throws RecognitionException {
		return exprand(0);
	}

	private ExprandContext exprand(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprandContext _localctx = new ExprandContext(_ctx, _parentState);
		ExprandContext _prevctx = _localctx;
		int _startState = 56;
		enterRecursionRule(_localctx, 56, RULE_exprand, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(383);
			((ExprandContext)_localctx).single = exprrel();
			}
			_ctx.stop = _input.LT(-1);
			setState(390);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprandContext(_parentctx, _parentState);
					_localctx.left = _prevctx;
					_localctx.left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_exprand);
					setState(385);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(386);
					match(AND);
					setState(387);
					((ExprandContext)_localctx).right = exprrel();
					}
					} 
				}
				setState(392);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,33,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExprrelContext extends ParserRuleContext {
		public ExprtermContext left;
		public Token op;
		public ExprtermContext right;
		public ExprtermContext single;
		public List<ExprtermContext> exprterm() {
			return getRuleContexts(ExprtermContext.class);
		}
		public ExprtermContext exprterm(int i) {
			return getRuleContext(ExprtermContext.class,i);
		}
		public TerminalNode RELOP() { return getToken(IQM4HDParser.RELOP, 0); }
		public ExprrelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprrel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterExprrel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitExprrel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitExprrel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprrelContext exprrel() throws RecognitionException {
		ExprrelContext _localctx = new ExprrelContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_exprrel);
		try {
			setState(398);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,34,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(393);
				((ExprrelContext)_localctx).left = exprterm(0);
				setState(394);
				((ExprrelContext)_localctx).op = match(RELOP);
				setState(395);
				((ExprrelContext)_localctx).right = exprterm(0);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(397);
				((ExprrelContext)_localctx).single = exprterm(0);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprtermContext extends ParserRuleContext {
		public ExprtermContext left;
		public ExprfactorContext single;
		public Token op;
		public ExprfactorContext right;
		public ExprfactorContext exprfactor() {
			return getRuleContext(ExprfactorContext.class,0);
		}
		public ExprtermContext exprterm() {
			return getRuleContext(ExprtermContext.class,0);
		}
		public ExprtermContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprterm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterExprterm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitExprterm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitExprterm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprtermContext exprterm() throws RecognitionException {
		return exprterm(0);
	}

	private ExprtermContext exprterm(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprtermContext _localctx = new ExprtermContext(_ctx, _parentState);
		ExprtermContext _prevctx = _localctx;
		int _startState = 60;
		enterRecursionRule(_localctx, 60, RULE_exprterm, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(401);
			((ExprtermContext)_localctx).single = exprfactor(0);
			}
			_ctx.stop = _input.LT(-1);
			setState(408);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprtermContext(_parentctx, _parentState);
					_localctx.left = _prevctx;
					_localctx.left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_exprterm);
					setState(403);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(404);
					((ExprtermContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==Plus || _la==Minus) ) {
						((ExprtermContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(405);
					((ExprtermContext)_localctx).right = exprfactor(0);
					}
					} 
				}
				setState(410);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExprfactorContext extends ParserRuleContext {
		public ExprfactorContext left;
		public ExprnotContext single;
		public Token op;
		public ExprnotContext right;
		public ExprnotContext exprnot() {
			return getRuleContext(ExprnotContext.class,0);
		}
		public ExprfactorContext exprfactor() {
			return getRuleContext(ExprfactorContext.class,0);
		}
		public ExprfactorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprfactor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterExprfactor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitExprfactor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitExprfactor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprfactorContext exprfactor() throws RecognitionException {
		return exprfactor(0);
	}

	private ExprfactorContext exprfactor(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprfactorContext _localctx = new ExprfactorContext(_ctx, _parentState);
		ExprfactorContext _prevctx = _localctx;
		int _startState = 62;
		enterRecursionRule(_localctx, 62, RULE_exprfactor, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(412);
			((ExprfactorContext)_localctx).single = exprnot();
			}
			_ctx.stop = _input.LT(-1);
			setState(419);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,36,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprfactorContext(_parentctx, _parentState);
					_localctx.left = _prevctx;
					_localctx.left = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_exprfactor);
					setState(414);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(415);
					((ExprfactorContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==Mul || _la==Div) ) {
						((ExprfactorContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(416);
					((ExprfactorContext)_localctx).right = exprnot();
					}
					} 
				}
				setState(421);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,36,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ExprnotContext extends ParserRuleContext {
		public ExprunContext single;
		public ExprunContext exprun() {
			return getRuleContext(ExprunContext.class,0);
		}
		public TerminalNode NOT() { return getToken(IQM4HDParser.NOT, 0); }
		public ExprnotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprnot; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterExprnot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitExprnot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitExprnot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprnotContext exprnot() throws RecognitionException {
		ExprnotContext _localctx = new ExprnotContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_exprnot);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NOT) {
				{
				setState(422);
				match(NOT);
				}
			}

			setState(425);
			((ExprnotContext)_localctx).single = exprun();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprunContext extends ParserRuleContext {
		public Token minus;
		public ExprvalContext exprval() {
			return getRuleContext(ExprvalContext.class,0);
		}
		public TerminalNode IS() { return getToken(IQM4HDParser.IS, 0); }
		public TerminalNode NULL() { return getToken(IQM4HDParser.NULL, 0); }
		public TerminalNode IN() { return getToken(IQM4HDParser.IN, 0); }
		public ListexprContext listexpr() {
			return getRuleContext(ListexprContext.class,0);
		}
		public TerminalNode NOT() { return getToken(IQM4HDParser.NOT, 0); }
		public ExprunContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprun; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterExprun(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitExprun(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitExprun(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprunContext exprun() throws RecognitionException {
		ExprunContext _localctx = new ExprunContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_exprun);
		int _la;
		try {
			setState(441);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(427);
				exprval();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(428);
				exprval();
				setState(429);
				match(IS);
				setState(430);
				match(NULL);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(432);
				exprval();
				setState(434);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==NOT) {
					{
					setState(433);
					match(NOT);
					}
				}

				setState(436);
				match(IN);
				setState(437);
				listexpr();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(439);
				((ExprunContext)_localctx).minus = match(Minus);
				setState(440);
				exprval();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprvalContext extends ParserRuleContext {
		public ExprContext base;
		public ExprContext index;
		public Token role;
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public ExprvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exprval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterExprval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitExprval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitExprval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprvalContext exprval() throws RecognitionException {
		ExprvalContext _localctx = new ExprvalContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_exprval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(448);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Open:
				{
				setState(443);
				match(Open);
				setState(444);
				((ExprvalContext)_localctx).base = expr();
				setState(445);
				match(Close);
				}
				break;
			case NULL:
			case BOOL:
			case AVG:
			case MIN:
			case MAX:
			case SUM:
			case COUNT:
			case ABS:
			case LENGTH:
			case MATCHES:
			case VAL2BIN:
			case DOB2AGE:
			case BINOM:
			case NVL:
			case OpenIdx:
			case Minus:
			case ID:
			case NUMBER:
			case STRING:
				{
				setState(447);
				value();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(454);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				setState(450);
				match(OpenIdx);
				setState(451);
				((ExprvalContext)_localctx).index = expr();
				setState(452);
				match(CloseIdx);
				}
				break;
			}
			setState(458);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,42,_ctx) ) {
			case 1:
				{
				setState(456);
				match(Dot);
				setState(457);
				((ExprvalContext)_localctx).role = match(ID);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public ConstvalContext constval() {
			return getRuleContext(ConstvalContext.class,0);
		}
		public VarvalContext varval() {
			return getRuleContext(VarvalContext.class,0);
		}
		public CallvalContext callval() {
			return getRuleContext(CallvalContext.class,0);
		}
		public AggvalContext aggval() {
			return getRuleContext(AggvalContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_value);
		try {
			setState(464);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NULL:
			case BOOL:
			case OpenIdx:
			case Minus:
			case NUMBER:
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(460);
				constval();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(461);
				varval();
				}
				break;
			case ABS:
			case LENGTH:
			case MATCHES:
			case VAL2BIN:
			case DOB2AGE:
			case BINOM:
			case NVL:
				enterOuterAlt(_localctx, 3);
				{
				setState(462);
				callval();
				}
				break;
			case AVG:
			case MIN:
			case MAX:
			case SUM:
			case COUNT:
				enterOuterAlt(_localctx, 4);
				{
				setState(463);
				aggval();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarvalContext extends ParserRuleContext {
		public Token var;
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public VarvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_varval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterVarval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitVarval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitVarval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VarvalContext varval() throws RecognitionException {
		VarvalContext _localctx = new VarvalContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_varval);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(466);
			((VarvalContext)_localctx).var = match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CallvalContext extends ParserRuleContext {
		public TerminalNode ABS() { return getToken(IQM4HDParser.ABS, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode NVL() { return getToken(IQM4HDParser.NVL, 0); }
		public TerminalNode BINOM() { return getToken(IQM4HDParser.BINOM, 0); }
		public TerminalNode LENGTH() { return getToken(IQM4HDParser.LENGTH, 0); }
		public ListexprContext listexpr() {
			return getRuleContext(ListexprContext.class,0);
		}
		public TerminalNode MATCHES() { return getToken(IQM4HDParser.MATCHES, 0); }
		public TerminalNode VAL2BIN() { return getToken(IQM4HDParser.VAL2BIN, 0); }
		public TerminalNode DOB2AGE() { return getToken(IQM4HDParser.DOB2AGE, 0); }
		public CallvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_callval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterCallval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitCallval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitCallval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CallvalContext callval() throws RecognitionException {
		CallvalContext _localctx = new CallvalContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_callval);
		try {
			setState(513);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ABS:
				enterOuterAlt(_localctx, 1);
				{
				setState(468);
				match(ABS);
				setState(469);
				match(Open);
				setState(470);
				expr();
				setState(471);
				match(Close);
				}
				break;
			case NVL:
				enterOuterAlt(_localctx, 2);
				{
				setState(473);
				match(NVL);
				setState(474);
				match(Open);
				setState(475);
				expr();
				setState(476);
				match(Comma);
				setState(477);
				expr();
				setState(478);
				match(Close);
				}
				break;
			case BINOM:
				enterOuterAlt(_localctx, 3);
				{
				setState(480);
				match(BINOM);
				setState(481);
				match(Open);
				setState(482);
				expr();
				setState(483);
				match(Comma);
				setState(484);
				expr();
				setState(485);
				match(Comma);
				setState(486);
				expr();
				setState(487);
				match(Close);
				}
				break;
			case LENGTH:
				enterOuterAlt(_localctx, 4);
				{
				setState(489);
				match(LENGTH);
				setState(490);
				match(Open);
				setState(491);
				listexpr();
				setState(492);
				match(Close);
				}
				break;
			case MATCHES:
				enterOuterAlt(_localctx, 5);
				{
				setState(494);
				match(MATCHES);
				setState(495);
				match(Open);
				setState(496);
				expr();
				setState(497);
				match(Comma);
				setState(498);
				expr();
				setState(499);
				match(Close);
				}
				break;
			case VAL2BIN:
				enterOuterAlt(_localctx, 6);
				{
				setState(501);
				match(VAL2BIN);
				setState(502);
				match(Open);
				setState(503);
				expr();
				setState(504);
				match(Comma);
				setState(505);
				listexpr();
				setState(506);
				match(Close);
				}
				break;
			case DOB2AGE:
				enterOuterAlt(_localctx, 7);
				{
				setState(508);
				match(DOB2AGE);
				setState(509);
				match(Open);
				setState(510);
				expr();
				setState(511);
				match(Close);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AggvalContext extends ParserRuleContext {
		public Token agg;
		public Token asterisk;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode AVG() { return getToken(IQM4HDParser.AVG, 0); }
		public TerminalNode MIN() { return getToken(IQM4HDParser.MIN, 0); }
		public TerminalNode MAX() { return getToken(IQM4HDParser.MAX, 0); }
		public TerminalNode SUM() { return getToken(IQM4HDParser.SUM, 0); }
		public TerminalNode COUNT() { return getToken(IQM4HDParser.COUNT, 0); }
		public AggvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_aggval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterAggval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitAggval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitAggval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AggvalContext aggval() throws RecognitionException {
		AggvalContext _localctx = new AggvalContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_aggval);
		int _la;
		try {
			setState(527);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case AVG:
			case MIN:
			case MAX:
			case SUM:
				enterOuterAlt(_localctx, 1);
				{
				setState(515);
				((AggvalContext)_localctx).agg = _input.LT(1);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << AVG) | (1L << MIN) | (1L << MAX) | (1L << SUM))) != 0)) ) {
					((AggvalContext)_localctx).agg = (Token)_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(516);
				match(Open);
				setState(517);
				expr();
				setState(518);
				match(Close);
				}
				break;
			case COUNT:
				enterOuterAlt(_localctx, 2);
				{
				setState(520);
				((AggvalContext)_localctx).agg = match(COUNT);
				setState(521);
				match(Open);
				setState(524);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case Mul:
					{
					setState(522);
					((AggvalContext)_localctx).asterisk = match(Mul);
					}
					break;
				case NULL:
				case BOOL:
				case NOT:
				case AVG:
				case MIN:
				case MAX:
				case SUM:
				case COUNT:
				case ABS:
				case LENGTH:
				case MATCHES:
				case VAL2BIN:
				case DOB2AGE:
				case BINOM:
				case NVL:
				case Open:
				case OpenIdx:
				case Minus:
				case ID:
				case NUMBER:
				case STRING:
					{
					setState(523);
					expr();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(526);
				match(Close);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstvalContext extends ParserRuleContext {
		public List<AtomvalContext> atomval() {
			return getRuleContexts(AtomvalContext.class);
		}
		public AtomvalContext atomval(int i) {
			return getRuleContext(AtomvalContext.class,i);
		}
		public ConstvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterConstval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitConstval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitConstval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstvalContext constval() throws RecognitionException {
		ConstvalContext _localctx = new ConstvalContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_constval);
		int _la;
		try {
			setState(541);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case OpenIdx:
				enterOuterAlt(_localctx, 1);
				{
				setState(529);
				match(OpenIdx);
				setState(530);
				atomval();
				setState(535);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Comma) {
					{
					{
					setState(531);
					match(Comma);
					setState(532);
					atomval();
					}
					}
					setState(537);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(538);
				match(CloseIdx);
				}
				break;
			case NULL:
			case BOOL:
			case Minus:
			case NUMBER:
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(540);
				atomval();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomvalContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(IQM4HDParser.STRING, 0); }
		public TerminalNode NUMBER() { return getToken(IQM4HDParser.NUMBER, 0); }
		public TerminalNode BOOL() { return getToken(IQM4HDParser.BOOL, 0); }
		public TerminalNode NULL() { return getToken(IQM4HDParser.NULL, 0); }
		public AtomvalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomval; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterAtomval(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitAtomval(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitAtomval(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomvalContext atomval() throws RecognitionException {
		AtomvalContext _localctx = new AtomvalContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_atomval);
		int _la;
		try {
			setState(550);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(543);
				match(STRING);
				}
				break;
			case Minus:
			case NUMBER:
				enterOuterAlt(_localctx, 2);
				{
				setState(545);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Minus) {
					{
					setState(544);
					match(Minus);
					}
				}

				setState(547);
				match(NUMBER);
				}
				break;
			case BOOL:
				enterOuterAlt(_localctx, 3);
				{
				setState(548);
				match(BOOL);
				}
				break;
			case NULL:
				enterOuterAlt(_localctx, 4);
				{
				setState(549);
				match(NULL);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListexprContext extends ParserRuleContext {
		public ExprContext where;
		public Token var;
		public TerminalNode SELECT() { return getToken(IQM4HDParser.SELECT, 0); }
		public ListExprSelectContext listExprSelect() {
			return getRuleContext(ListExprSelectContext.class,0);
		}
		public TerminalNode FROM() { return getToken(IQM4HDParser.FROM, 0); }
		public ListExprFromContext listExprFrom() {
			return getRuleContext(ListExprFromContext.class,0);
		}
		public TerminalNode WHERE() { return getToken(IQM4HDParser.WHERE, 0); }
		public ListExprGroupByContext listExprGroupBy() {
			return getRuleContext(ListExprGroupByContext.class,0);
		}
		public ListExprOrderByContext listExprOrderBy() {
			return getRuleContext(ListExprOrderByContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ListMethodContext listMethod() {
			return getRuleContext(ListMethodContext.class,0);
		}
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public ConstlstContext constlst() {
			return getRuleContext(ConstlstContext.class,0);
		}
		public ListexprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listexpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListexpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListexpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListexpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListexprContext listexpr() throws RecognitionException {
		ListexprContext _localctx = new ListexprContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_listexpr);
		try {
			setState(572);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SELECT:
				enterOuterAlt(_localctx, 1);
				{
				setState(552);
				match(SELECT);
				setState(553);
				listExprSelect();
				setState(554);
				match(FROM);
				setState(555);
				listExprFrom();
				setState(558);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,51,_ctx) ) {
				case 1:
					{
					setState(556);
					match(WHERE);
					setState(557);
					((ListexprContext)_localctx).where = expr();
					}
					break;
				}
				setState(561);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,52,_ctx) ) {
				case 1:
					{
					setState(560);
					listExprGroupBy();
					}
					break;
				}
				setState(564);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,53,_ctx) ) {
				case 1:
					{
					setState(563);
					listExprOrderBy();
					}
					break;
				}
				}
				break;
			case ARIMA:
			case CUBESCORE:
				enterOuterAlt(_localctx, 2);
				{
				setState(566);
				listMethod();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(567);
				((ListexprContext)_localctx).var = match(ID);
				}
				break;
			case OpenBrace:
				enterOuterAlt(_localctx, 4);
				{
				setState(568);
				match(OpenBrace);
				setState(569);
				constlst();
				setState(570);
				match(CloseBrace);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstlstContext extends ParserRuleContext {
		public List<ConstvalContext> constval() {
			return getRuleContexts(ConstvalContext.class);
		}
		public ConstvalContext constval(int i) {
			return getRuleContext(ConstvalContext.class,i);
		}
		public ConstlstContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constlst; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterConstlst(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitConstlst(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitConstlst(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstlstContext constlst() throws RecognitionException {
		ConstlstContext _localctx = new ConstlstContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_constlst);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(574);
			constval();
			setState(579);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Comma) {
				{
				{
				setState(575);
				match(Comma);
				setState(576);
				constval();
				}
				}
				setState(581);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListMethodContext extends ParserRuleContext {
		public TerminalNode ARIMA() { return getToken(IQM4HDParser.ARIMA, 0); }
		public List<ListexprContext> listexpr() {
			return getRuleContexts(ListexprContext.class);
		}
		public ListexprContext listexpr(int i) {
			return getRuleContext(ListexprContext.class,i);
		}
		public TerminalNode CUBESCORE() { return getToken(IQM4HDParser.CUBESCORE, 0); }
		public ListMethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listMethod; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListMethod(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListMethod(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListMethodContext listMethod() throws RecognitionException {
		ListMethodContext _localctx = new ListMethodContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_listMethod);
		try {
			setState(596);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ARIMA:
				enterOuterAlt(_localctx, 1);
				{
				setState(582);
				match(ARIMA);
				setState(583);
				match(Open);
				setState(584);
				listexpr();
				setState(585);
				match(Comma);
				setState(586);
				listexpr();
				setState(587);
				match(Close);
				}
				break;
			case CUBESCORE:
				enterOuterAlt(_localctx, 2);
				{
				setState(589);
				match(CUBESCORE);
				setState(590);
				match(Open);
				setState(591);
				listexpr();
				setState(592);
				match(Comma);
				setState(593);
				listexpr();
				setState(594);
				match(Close);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListExprSelectContext extends ParserRuleContext {
		public Token asterisk;
		public List<ListExprSelectItemContext> listExprSelectItem() {
			return getRuleContexts(ListExprSelectItemContext.class);
		}
		public ListExprSelectItemContext listExprSelectItem(int i) {
			return getRuleContext(ListExprSelectItemContext.class,i);
		}
		public ListExprSelectContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExprSelect; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListExprSelect(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListExprSelect(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListExprSelect(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprSelectContext listExprSelect() throws RecognitionException {
		ListExprSelectContext _localctx = new ListExprSelectContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_listExprSelect);
		int _la;
		try {
			setState(607);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case NULL:
			case BOOL:
			case NOT:
			case AVG:
			case MIN:
			case MAX:
			case SUM:
			case COUNT:
			case ABS:
			case LENGTH:
			case MATCHES:
			case VAL2BIN:
			case DOB2AGE:
			case BINOM:
			case NVL:
			case Open:
			case OpenIdx:
			case Minus:
			case ID:
			case NUMBER:
			case STRING:
				enterOuterAlt(_localctx, 1);
				{
				setState(598);
				listExprSelectItem();
				setState(603);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Comma) {
					{
					{
					setState(599);
					match(Comma);
					setState(600);
					listExprSelectItem();
					}
					}
					setState(605);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case Mul:
				enterOuterAlt(_localctx, 2);
				{
				setState(606);
				((ListExprSelectContext)_localctx).asterisk = match(Mul);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListExprSelectItemContext extends ParserRuleContext {
		public Token role;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode ROLE() { return getToken(IQM4HDParser.ROLE, 0); }
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public ListExprSelectItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExprSelectItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListExprSelectItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListExprSelectItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListExprSelectItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprSelectItemContext listExprSelectItem() throws RecognitionException {
		ListExprSelectItemContext _localctx = new ListExprSelectItemContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_listExprSelectItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(609);
			expr();
			setState(612);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ROLE) {
				{
				setState(610);
				match(ROLE);
				setState(611);
				((ListExprSelectItemContext)_localctx).role = match(ID);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListExprFromContext extends ParserRuleContext {
		public ListExprFromSourceContext listExprFromSource() {
			return getRuleContext(ListExprFromSourceContext.class,0);
		}
		public List<ListExprFromItemContext> listExprFromItem() {
			return getRuleContexts(ListExprFromItemContext.class);
		}
		public ListExprFromItemContext listExprFromItem(int i) {
			return getRuleContext(ListExprFromItemContext.class,i);
		}
		public ListExprFromContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExprFrom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListExprFrom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListExprFrom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListExprFrom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprFromContext listExprFrom() throws RecognitionException {
		ListExprFromContext _localctx = new ListExprFromContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_listExprFrom);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(614);
			listExprFromSource();
			setState(618);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,60,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(615);
					listExprFromItem();
					}
					} 
				}
				setState(620);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,60,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListExprFromItemContext extends ParserRuleContext {
		public Token left;
		public Token right;
		public ListExprJoinContext listExprJoin() {
			return getRuleContext(ListExprJoinContext.class,0);
		}
		public ListExprFromSourceContext listExprFromSource() {
			return getRuleContext(ListExprFromSourceContext.class,0);
		}
		public TerminalNode ON() { return getToken(IQM4HDParser.ON, 0); }
		public List<TerminalNode> ID() { return getTokens(IQM4HDParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(IQM4HDParser.ID, i);
		}
		public ListExprFromItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExprFromItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListExprFromItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListExprFromItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListExprFromItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprFromItemContext listExprFromItem() throws RecognitionException {
		ListExprFromItemContext _localctx = new ListExprFromItemContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_listExprFromItem);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(621);
			listExprJoin();
			setState(622);
			listExprFromSource();
			setState(627);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,61,_ctx) ) {
			case 1:
				{
				setState(623);
				match(ON);
				setState(624);
				((ListExprFromItemContext)_localctx).left = match(ID);
				setState(625);
				match(Equals);
				setState(626);
				((ListExprFromItemContext)_localctx).right = match(ID);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListExprFromSourceContext extends ParserRuleContext {
		public Token src;
		public TerminalNode ID() { return getToken(IQM4HDParser.ID, 0); }
		public ListexprContext listexpr() {
			return getRuleContext(ListexprContext.class,0);
		}
		public ListExprFromSourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExprFromSource; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListExprFromSource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListExprFromSource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListExprFromSource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprFromSourceContext listExprFromSource() throws RecognitionException {
		ListExprFromSourceContext _localctx = new ListExprFromSourceContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_listExprFromSource);
		try {
			setState(634);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(629);
				((ListExprFromSourceContext)_localctx).src = match(ID);
				}
				break;
			case Open:
				enterOuterAlt(_localctx, 2);
				{
				setState(630);
				match(Open);
				setState(631);
				listexpr();
				setState(632);
				match(Close);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListExprJoinContext extends ParserRuleContext {
		public TerminalNode JOIN() { return getToken(IQM4HDParser.JOIN, 0); }
		public TerminalNode LEFT() { return getToken(IQM4HDParser.LEFT, 0); }
		public TerminalNode RIGHT() { return getToken(IQM4HDParser.RIGHT, 0); }
		public TerminalNode FULL() { return getToken(IQM4HDParser.FULL, 0); }
		public ListExprJoinContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExprJoin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListExprJoin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListExprJoin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListExprJoin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprJoinContext listExprJoin() throws RecognitionException {
		ListExprJoinContext _localctx = new ListExprJoinContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_listExprJoin);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(637);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LEFT) | (1L << RIGHT) | (1L << FULL))) != 0)) {
				{
				setState(636);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LEFT) | (1L << RIGHT) | (1L << FULL))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
			}

			setState(639);
			match(JOIN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListExprGroupByContext extends ParserRuleContext {
		public TerminalNode GROUP() { return getToken(IQM4HDParser.GROUP, 0); }
		public TerminalNode BY() { return getToken(IQM4HDParser.BY, 0); }
		public List<TerminalNode> ID() { return getTokens(IQM4HDParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(IQM4HDParser.ID, i);
		}
		public ListExprHavingContext listExprHaving() {
			return getRuleContext(ListExprHavingContext.class,0);
		}
		public ListExprGroupByContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExprGroupBy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListExprGroupBy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListExprGroupBy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListExprGroupBy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprGroupByContext listExprGroupBy() throws RecognitionException {
		ListExprGroupByContext _localctx = new ListExprGroupByContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_listExprGroupBy);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(641);
			match(GROUP);
			setState(642);
			match(BY);
			setState(643);
			match(ID);
			setState(648);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(644);
					match(Comma);
					setState(645);
					match(ID);
					}
					} 
				}
				setState(650);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,64,_ctx);
			}
			setState(652);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,65,_ctx) ) {
			case 1:
				{
				setState(651);
				listExprHaving();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListExprHavingContext extends ParserRuleContext {
		public TerminalNode HAVING() { return getToken(IQM4HDParser.HAVING, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ListExprHavingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExprHaving; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListExprHaving(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListExprHaving(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListExprHaving(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprHavingContext listExprHaving() throws RecognitionException {
		ListExprHavingContext _localctx = new ListExprHavingContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_listExprHaving);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(654);
			match(HAVING);
			setState(655);
			expr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListExprOrderByContext extends ParserRuleContext {
		public TerminalNode ORDER() { return getToken(IQM4HDParser.ORDER, 0); }
		public TerminalNode BY() { return getToken(IQM4HDParser.BY, 0); }
		public List<TerminalNode> ID() { return getTokens(IQM4HDParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(IQM4HDParser.ID, i);
		}
		public ListExprOrderByContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExprOrderBy; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).enterListExprOrderBy(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof IQM4HDParserListener ) ((IQM4HDParserListener)listener).exitListExprOrderBy(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof IQM4HDParserVisitor ) return ((IQM4HDParserVisitor<? extends T>)visitor).visitListExprOrderBy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprOrderByContext listExprOrderBy() throws RecognitionException {
		ListExprOrderByContext _localctx = new ListExprOrderByContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_listExprOrderBy);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(657);
			match(ORDER);
			setState(658);
			match(BY);
			setState(659);
			match(ID);
			setState(664);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(660);
					match(Comma);
					setState(661);
					match(ID);
					}
					} 
				}
				setState(666);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,66,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 27:
			return expror_sempred((ExprorContext)_localctx, predIndex);
		case 28:
			return exprand_sempred((ExprandContext)_localctx, predIndex);
		case 30:
			return exprterm_sempred((ExprtermContext)_localctx, predIndex);
		case 31:
			return exprfactor_sempred((ExprfactorContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expror_sempred(ExprorContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean exprand_sempred(ExprandContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean exprterm_sempred(ExprtermContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean exprfactor_sempred(ExprfactorContext _localctx, int predIndex) {
		switch (predIndex) {
		case 3:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3]\u029e\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\3\2\6\2n\n\2\r\2\16\2o\3\3\3\3\3\3\5\3u\n\3\3"+
		"\4\3\4\3\4\3\4\7\4{\n\4\f\4\16\4~\13\4\3\4\3\4\3\5\3\5\3\5\3\5\7\5\u0086"+
		"\n\5\f\5\16\5\u0089\13\5\3\5\3\5\3\6\3\6\3\6\5\6\u0090\n\6\3\7\3\7\3\7"+
		"\7\7\u0095\n\7\f\7\16\7\u0098\13\7\3\b\3\b\3\b\3\b\3\b\3\b\5\b\u00a0\n"+
		"\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00b0\n"+
		"\n\3\n\3\n\3\13\3\13\3\13\3\13\5\13\u00b8\n\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\f\6\f\u00c2\n\f\r\f\16\f\u00c3\3\r\3\r\3\r\3\r\3\r\5\r\u00cb"+
		"\n\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\7\16\u00d5\n\16\f\16\16\16\u00d8"+
		"\13\16\3\17\5\17\u00db\n\17\3\17\3\17\5\17\u00df\n\17\3\20\6\20\u00e2"+
		"\n\20\r\20\16\20\u00e3\3\21\3\21\3\21\3\21\3\21\3\21\3\21\5\21\u00ed\n"+
		"\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u00f8\n\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u0103\n\22\3\23\3\23\5\23"+
		"\u0107\n\23\3\23\3\23\3\23\5\23\u010c\n\23\3\23\3\23\5\23\u0110\n\23\3"+
		"\23\3\23\5\23\u0114\n\23\3\24\3\24\3\24\3\24\7\24\u011a\n\24\f\24\16\24"+
		"\u011d\13\24\3\25\3\25\5\25\u0121\n\25\3\25\5\25\u0124\n\25\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\5\26\u0138\n\26\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u0140\n"+
		"\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\7\30\u014d"+
		"\n\30\f\30\16\30\u0150\13\30\3\30\3\30\5\30\u0154\n\30\3\31\3\31\3\31"+
		"\5\31\u0159\n\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u0170\n\33\5\33"+
		"\u0172\n\33\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\7\35\u017c\n\35\f"+
		"\35\16\35\u017f\13\35\3\36\3\36\3\36\3\36\3\36\3\36\7\36\u0187\n\36\f"+
		"\36\16\36\u018a\13\36\3\37\3\37\3\37\3\37\3\37\5\37\u0191\n\37\3 \3 \3"+
		" \3 \3 \3 \7 \u0199\n \f \16 \u019c\13 \3!\3!\3!\3!\3!\3!\7!\u01a4\n!"+
		"\f!\16!\u01a7\13!\3\"\5\"\u01aa\n\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#\5#\u01b5"+
		"\n#\3#\3#\3#\3#\3#\5#\u01bc\n#\3$\3$\3$\3$\3$\5$\u01c3\n$\3$\3$\3$\3$"+
		"\5$\u01c9\n$\3$\3$\5$\u01cd\n$\3%\3%\3%\3%\5%\u01d3\n%\3&\3&\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'"+
		"\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\5\'\u0204\n\'\3(\3(\3(\3(\3(\3(\3(\3(\3"+
		"(\5(\u020f\n(\3(\5(\u0212\n(\3)\3)\3)\3)\7)\u0218\n)\f)\16)\u021b\13)"+
		"\3)\3)\3)\5)\u0220\n)\3*\3*\5*\u0224\n*\3*\3*\3*\5*\u0229\n*\3+\3+\3+"+
		"\3+\3+\3+\5+\u0231\n+\3+\5+\u0234\n+\3+\5+\u0237\n+\3+\3+\3+\3+\3+\3+"+
		"\5+\u023f\n+\3,\3,\3,\7,\u0244\n,\f,\16,\u0247\13,\3-\3-\3-\3-\3-\3-\3"+
		"-\3-\3-\3-\3-\3-\3-\3-\5-\u0257\n-\3.\3.\3.\7.\u025c\n.\f.\16.\u025f\13"+
		".\3.\5.\u0262\n.\3/\3/\3/\5/\u0267\n/\3\60\3\60\7\60\u026b\n\60\f\60\16"+
		"\60\u026e\13\60\3\61\3\61\3\61\3\61\3\61\3\61\5\61\u0276\n\61\3\62\3\62"+
		"\3\62\3\62\3\62\5\62\u027d\n\62\3\63\5\63\u0280\n\63\3\63\3\63\3\64\3"+
		"\64\3\64\3\64\3\64\7\64\u0289\n\64\f\64\16\64\u028c\13\64\3\64\5\64\u028f"+
		"\n\64\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\7\66\u0299\n\66\f\66\16"+
		"\66\u029c\13\66\3\66\2\68:>@\67\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36"+
		" \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVXZ\\^`bdfhj\2\7\3\2\25\26\3\2LM\3"+
		"\2NO\3\2\63\66\3\2\"$\2\u02be\2m\3\2\2\2\4t\3\2\2\2\6v\3\2\2\2\b\u0081"+
		"\3\2\2\2\n\u008c\3\2\2\2\f\u0091\3\2\2\2\16\u0099\3\2\2\2\20\u00a1\3\2"+
		"\2\2\22\u00a9\3\2\2\2\24\u00b3\3\2\2\2\26\u00c1\3\2\2\2\30\u00c5\3\2\2"+
		"\2\32\u00d0\3\2\2\2\34\u00da\3\2\2\2\36\u00e1\3\2\2\2 \u00ec\3\2\2\2\""+
		"\u0102\3\2\2\2$\u0104\3\2\2\2&\u0115\3\2\2\2(\u0123\3\2\2\2*\u0137\3\2"+
		"\2\2,\u0139\3\2\2\2.\u0153\3\2\2\2\60\u0155\3\2\2\2\62\u015a\3\2\2\2\64"+
		"\u0171\3\2\2\2\66\u0173\3\2\2\28\u0175\3\2\2\2:\u0180\3\2\2\2<\u0190\3"+
		"\2\2\2>\u0192\3\2\2\2@\u019d\3\2\2\2B\u01a9\3\2\2\2D\u01bb\3\2\2\2F\u01c2"+
		"\3\2\2\2H\u01d2\3\2\2\2J\u01d4\3\2\2\2L\u0203\3\2\2\2N\u0211\3\2\2\2P"+
		"\u021f\3\2\2\2R\u0228\3\2\2\2T\u023e\3\2\2\2V\u0240\3\2\2\2X\u0256\3\2"+
		"\2\2Z\u0261\3\2\2\2\\\u0263\3\2\2\2^\u0268\3\2\2\2`\u026f\3\2\2\2b\u027c"+
		"\3\2\2\2d\u027f\3\2\2\2f\u0283\3\2\2\2h\u0290\3\2\2\2j\u0293\3\2\2\2l"+
		"n\5\4\3\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3\2\2\2p\3\3\2\2\2qu\5\16\b"+
		"\2ru\5\30\r\2su\5\62\32\2tq\3\2\2\2tr\3\2\2\2ts\3\2\2\2u\5\3\2\2\2vw\7"+
		"G\2\2w|\7U\2\2xy\7F\2\2y{\7U\2\2zx\3\2\2\2{~\3\2\2\2|z\3\2\2\2|}\3\2\2"+
		"\2}\177\3\2\2\2~|\3\2\2\2\177\u0080\7H\2\2\u0080\7\3\2\2\2\u0081\u0082"+
		"\7G\2\2\u0082\u0087\5\n\6\2\u0083\u0084\7F\2\2\u0084\u0086\5\n\6\2\u0085"+
		"\u0083\3\2\2\2\u0086\u0089\3\2\2\2\u0087\u0085\3\2\2\2\u0087\u0088\3\2"+
		"\2\2\u0088\u008a\3\2\2\2\u0089\u0087\3\2\2\2\u008a\u008b\7H\2\2\u008b"+
		"\t\3\2\2\2\u008c\u008f\5\f\7\2\u008d\u008e\7E\2\2\u008e\u0090\7U\2\2\u008f"+
		"\u008d\3\2\2\2\u008f\u0090\3\2\2\2\u0090\13\3\2\2\2\u0091\u0096\7U\2\2"+
		"\u0092\u0093\7R\2\2\u0093\u0095\7U\2\2\u0094\u0092\3\2\2\2\u0095\u0098"+
		"\3\2\2\2\u0096\u0094\3\2\2\2\u0096\u0097\3\2\2\2\u0097\r\3\2\2\2\u0098"+
		"\u0096\3\2\2\2\u0099\u009a\7\5\2\2\u009a\u009b\7U\2\2\u009b\u009f\7\23"+
		"\2\2\u009c\u00a0\5\20\t\2\u009d\u00a0\5\22\n\2\u009e\u00a0\5\24\13\2\u009f"+
		"\u009c\3\2\2\2\u009f\u009d\3\2\2\2\u009f\u009e\3\2\2\2\u00a0\17\3\2\2"+
		"\2\u00a1\u00a2\7!\2\2\u00a2\u00a3\7.\2\2\u00a3\u00a4\7\'\2\2\u00a4\u00a5"+
		"\5\6\4\2\u00a5\u00a6\7E\2\2\u00a6\u00a7\5V,\2\u00a7\u00a8\7\21\2\2\u00a8"+
		"\21\3\2\2\2\u00a9\u00aa\7.\2\2\u00aa\u00ab\7\'\2\2\u00ab\u00ac\5\6\4\2"+
		"\u00ac\u00af\7E\2\2\u00ad\u00b0\5P)\2\u00ae\u00b0\7X\2\2\u00af\u00ad\3"+
		"\2\2\2\u00af\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2\7\21\2\2\u00b2"+
		"\23\3\2\2\2\u00b3\u00b4\7!\2\2\u00b4\u00b7\7\62\2\2\u00b5\u00b6\7\'\2"+
		"\2\u00b6\u00b8\5\b\5\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b9"+
		"\3\2\2\2\u00b9\u00ba\7E\2\2\u00ba\u00bb\7\3\2\2\u00bb\u00bc\7U\2\2\u00bc"+
		"\u00bd\7\4\2\2\u00bd\u00be\5\26\f\2\u00be\u00bf\7\\\2\2\u00bf\25\3\2\2"+
		"\2\u00c0\u00c2\7]\2\2\u00c1\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c1"+
		"\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\27\3\2\2\2\u00c5\u00c6\7\6\2\2\u00c6"+
		"\u00c7\7U\2\2\u00c7\u00c8\7\n\2\2\u00c8\u00ca\5\34\17\2\u00c9\u00cb\5"+
		"\32\16\2\u00ca\u00c9\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc"+
		"\u00cd\7E\2\2\u00cd\u00ce\5\36\20\2\u00ce\u00cf\7\21\2\2\u00cf\31\3\2"+
		"\2\2\u00d0\u00d1\7\24\2\2\u00d1\u00d6\5\34\17\2\u00d2\u00d3\7F\2\2\u00d3"+
		"\u00d5\5\34\17\2\u00d4\u00d2\3\2\2\2\u00d5\u00d8\3\2\2\2\u00d6\u00d4\3"+
		"\2\2\2\u00d6\u00d7\3\2\2\2\u00d7\33\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d9"+
		"\u00db\7!\2\2\u00da\u00d9\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00dc\3\2"+
		"\2\2\u00dc\u00de\7U\2\2\u00dd\u00df\5\6\4\2\u00de\u00dd\3\2\2\2\u00de"+
		"\u00df\3\2\2\2\u00df\35\3\2\2\2\u00e0\u00e2\5 \21\2\u00e1\u00e0\3\2\2"+
		"\2\u00e2\u00e3\3\2\2\2\u00e3\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\37"+
		"\3\2\2\2\u00e5\u00ed\5\"\22\2\u00e6\u00ed\5*\26\2\u00e7\u00ed\5,\27\2"+
		"\u00e8\u00ed\5.\30\2\u00e9\u00ea\5$\23\2\u00ea\u00eb\7P\2\2\u00eb\u00ed"+
		"\3\2\2\2\u00ec\u00e5\3\2\2\2\u00ec\u00e6\3\2\2\2\u00ec\u00e7\3\2\2\2\u00ec"+
		"\u00e8\3\2\2\2\u00ec\u00e9\3\2\2\2\u00ed!\3\2\2\2\u00ee\u00ef\7U\2\2\u00ef"+
		"\u00f0\7I\2\2\u00f0\u00f1\5\66\34\2\u00f1\u00f2\7P\2\2\u00f2\u0103\3\2"+
		"\2\2\u00f3\u00f4\7!\2\2\u00f4\u00f5\7U\2\2\u00f5\u00f7\7I\2\2\u00f6\u00f8"+
		"\7=\2\2\u00f7\u00f6\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8\u00f9\3\2\2\2\u00f9"+
		"\u00fa\5T+\2\u00fa\u00fb\7P\2\2\u00fb\u0103\3\2\2\2\u00fc\u00fd\7U\2\2"+
		"\u00fd\u00fe\7I\2\2\u00fe\u00ff\7C\2\2\u00ff\u0100\5T+\2\u0100\u0101\7"+
		"P\2\2\u0101\u0103\3\2\2\2\u0102\u00ee\3\2\2\2\u0102\u00f3\3\2\2\2\u0102"+
		"\u00fc\3\2\2\2\u0103#\3\2\2\2\u0104\u0106\7\b\2\2\u0105\u0107\7B\2\2\u0106"+
		"\u0105\3\2\2\2\u0106\u0107\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u0109\7U"+
		"\2\2\u0109\u010b\7\n\2\2\u010a\u010c\7\13\2\2\u010b\u010a\3\2\2\2\u010b"+
		"\u010c\3\2\2\2\u010c\u010d\3\2\2\2\u010d\u010f\5(\25\2\u010e\u0110\5&"+
		"\24\2\u010f\u010e\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u0113\3\2\2\2\u0111"+
		"\u0112\7\16\2\2\u0112\u0114\5\66\34\2\u0113\u0111\3\2\2\2\u0113\u0114"+
		"\3\2\2\2\u0114%\3\2\2\2\u0115\u0116\7\24\2\2\u0116\u011b\5(\25\2\u0117"+
		"\u0118\7F\2\2\u0118\u011a\5(\25\2\u0119\u0117\3\2\2\2\u011a\u011d\3\2"+
		"\2\2\u011b\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011c\'\3\2\2\2\u011d\u011b"+
		"\3\2\2\2\u011e\u0120\7U\2\2\u011f\u0121\5\b\5\2\u0120\u011f\3\2\2\2\u0120"+
		"\u0121\3\2\2\2\u0121\u0124\3\2\2\2\u0122\u0124\5P)\2\u0123\u011e\3\2\2"+
		"\2\u0123\u0122\3\2\2\2\u0124)\3\2\2\2\u0125\u0126\7\36\2\2\u0126\u0127"+
		"\7U\2\2\u0127\u0128\7I\2\2\u0128\u0129\5\66\34\2\u0129\u012a\7\61\2\2"+
		"\u012a\u012b\5\66\34\2\u012b\u012c\7E\2\2\u012c\u012d\5\36\20\2\u012d"+
		"\u012e\7\21\2\2\u012e\u0138\3\2\2\2\u012f\u0130\7\36\2\2\u0130\u0131\7"+
		"U\2\2\u0131\u0132\7\17\2\2\u0132\u0133\5T+\2\u0133\u0134\7E\2\2\u0134"+
		"\u0135\5\36\20\2\u0135\u0136\7\21\2\2\u0136\u0138\3\2\2\2\u0137\u0125"+
		"\3\2\2\2\u0137\u012f\3\2\2\2\u0138+\3\2\2\2\u0139\u013a\7\16\2\2\u013a"+
		"\u013b\5\66\34\2\u013b\u013c\7E\2\2\u013c\u013f\5\36\20\2\u013d\u013e"+
		"\7\37\2\2\u013e\u0140\5\36\20\2\u013f\u013d\3\2\2\2\u013f\u0140\3\2\2"+
		"\2\u0140\u0141\3\2\2\2\u0141\u0142\7\21\2\2\u0142-\3\2\2\2\u0143\u0144"+
		"\7\33\2\2\u0144\u0145\7!\2\2\u0145\u0146\5T+\2\u0146\u0147\7P\2\2\u0147"+
		"\u0154\3\2\2\2\u0148\u0149\7\33\2\2\u0149\u014e\5\60\31\2\u014a\u014b"+
		"\7F\2\2\u014b\u014d\5\60\31\2\u014c\u014a\3\2\2\2\u014d\u0150\3\2\2\2"+
		"\u014e\u014c\3\2\2\2\u014e\u014f\3\2\2\2\u014f\u0151\3\2\2\2\u0150\u014e"+
		"\3\2\2\2\u0151\u0152\7P\2\2\u0152\u0154\3\2\2\2\u0153\u0143\3\2\2\2\u0153"+
		"\u0148\3\2\2\2\u0154/\3\2\2\2\u0155\u0158\5\66\34\2\u0156\u0157\7&\2\2"+
		"\u0157\u0159\7U\2\2\u0158\u0156\3\2\2\2\u0158\u0159\3\2\2\2\u0159\61\3"+
		"\2\2\2\u015a\u015b\7\7\2\2\u015b\u015c\7U\2\2\u015c\u015d\7E\2\2\u015d"+
		"\u015e\5$\23\2\u015e\u015f\5\64\33\2\u015f\u0160\7\21\2\2\u0160\63\3\2"+
		"\2\2\u0161\u0162\7\r\2\2\u0162\u0163\7\17\2\2\u0163\u0164\7\26\2\2\u0164"+
		"\u0165\7-\2\2\u0165\u0166\7V\2\2\u0166\u0167\7\22\2\2\u0167\u0168\7\25"+
		"\2\2\u0168\u0169\7-\2\2\u0169\u0172\7V\2\2\u016a\u016b\7\r\2\2\u016b\u016c"+
		"\7\17\2\2\u016c\u016f\t\2\2\2\u016d\u016e\7-\2\2\u016e\u0170\7V\2\2\u016f"+
		"\u016d\3\2\2\2\u016f\u0170\3\2\2\2\u0170\u0172\3\2\2\2\u0171\u0161\3\2"+
		"\2\2\u0171\u016a\3\2\2\2\u0172\65\3\2\2\2\u0173\u0174\58\35\2\u0174\67"+
		"\3\2\2\2\u0175\u0176\b\35\1\2\u0176\u0177\5:\36\2\u0177\u017d\3\2\2\2"+
		"\u0178\u0179\f\3\2\2\u0179\u017a\7/\2\2\u017a\u017c\5:\36\2\u017b\u0178"+
		"\3\2\2\2\u017c\u017f\3\2\2\2\u017d\u017b\3\2\2\2\u017d\u017e\3\2\2\2\u017e"+
		"9\3\2\2\2\u017f\u017d\3\2\2\2\u0180\u0181\b\36\1\2\u0181\u0182\5<\37\2"+
		"\u0182\u0188\3\2\2\2\u0183\u0184\f\4\2\2\u0184\u0185\7\22\2\2\u0185\u0187"+
		"\5<\37\2\u0186\u0183\3\2\2\2\u0187\u018a\3\2\2\2\u0188\u0186\3\2\2\2\u0188"+
		"\u0189\3\2\2\2\u0189;\3\2\2\2\u018a\u0188\3\2\2\2\u018b\u018c\5> \2\u018c"+
		"\u018d\7D\2\2\u018d\u018e\5> \2\u018e\u0191\3\2\2\2\u018f\u0191\5> \2"+
		"\u0190\u018b\3\2\2\2\u0190\u018f\3\2\2\2\u0191=\3\2\2\2\u0192\u0193\b"+
		" \1\2\u0193\u0194\5@!\2\u0194\u019a\3\2\2\2\u0195\u0196\f\4\2\2\u0196"+
		"\u0197\t\3\2\2\u0197\u0199\5@!\2\u0198\u0195\3\2\2\2\u0199\u019c\3\2\2"+
		"\2\u019a\u0198\3\2\2\2\u019a\u019b\3\2\2\2\u019b?\3\2\2\2\u019c\u019a"+
		"\3\2\2\2\u019d\u019e\b!\1\2\u019e\u019f\5B\"\2\u019f\u01a5\3\2\2\2\u01a0"+
		"\u01a1\f\4\2\2\u01a1\u01a2\t\4\2\2\u01a2\u01a4\5B\"\2\u01a3\u01a0\3\2"+
		"\2\2\u01a4\u01a7\3\2\2\2\u01a5\u01a3\3\2\2\2\u01a5\u01a6\3\2\2\2\u01a6"+
		"A\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a8\u01aa\7\60\2\2\u01a9\u01a8\3\2\2\2"+
		"\u01a9\u01aa\3\2\2\2\u01aa\u01ab\3\2\2\2\u01ab\u01ac\5D#\2\u01acC\3\2"+
		"\2\2\u01ad\u01bc\5F$\2\u01ae\u01af\5F$\2\u01af\u01b0\7(\2\2\u01b0\u01b1"+
		"\7\34\2\2\u01b1\u01bc\3\2\2\2\u01b2\u01b4\5F$\2\u01b3\u01b5\7\60\2\2\u01b4"+
		"\u01b3\3\2\2\2\u01b4\u01b5\3\2\2\2\u01b5\u01b6\3\2\2\2\u01b6\u01b7\7\17"+
		"\2\2\u01b7\u01b8\5T+\2\u01b8\u01bc\3\2\2\2\u01b9\u01ba\7M\2\2\u01ba\u01bc"+
		"\5F$\2\u01bb\u01ad\3\2\2\2\u01bb\u01ae\3\2\2\2\u01bb\u01b2\3\2\2\2\u01bb"+
		"\u01b9\3\2\2\2\u01bcE\3\2\2\2\u01bd\u01be\7G\2\2\u01be\u01bf\5\66\34\2"+
		"\u01bf\u01c0\7H\2\2\u01c0\u01c3\3\2\2\2\u01c1\u01c3\5H%\2\u01c2\u01bd"+
		"\3\2\2\2\u01c2\u01c1\3\2\2\2\u01c3\u01c8\3\2\2\2\u01c4\u01c5\7J\2\2\u01c5"+
		"\u01c6\5\66\34\2\u01c6\u01c7\7K\2\2\u01c7\u01c9\3\2\2\2\u01c8\u01c4\3"+
		"\2\2\2\u01c8\u01c9\3\2\2\2\u01c9\u01cc\3\2\2\2\u01ca\u01cb\7R\2\2\u01cb"+
		"\u01cd\7U\2\2\u01cc\u01ca\3\2\2\2\u01cc\u01cd\3\2\2\2\u01cdG\3\2\2\2\u01ce"+
		"\u01d3\5P)\2\u01cf\u01d3\5J&\2\u01d0\u01d3\5L\'\2\u01d1\u01d3\5N(\2\u01d2"+
		"\u01ce\3\2\2\2\u01d2\u01cf\3\2\2\2\u01d2\u01d0\3\2\2\2\u01d2\u01d1\3\2"+
		"\2\2\u01d3I\3\2\2\2\u01d4\u01d5\7U\2\2\u01d5K\3\2\2\2\u01d6\u01d7\78\2"+
		"\2\u01d7\u01d8\7G\2\2\u01d8\u01d9\5\66\34\2\u01d9\u01da\7H\2\2\u01da\u0204"+
		"\3\2\2\2\u01db\u01dc\7A\2\2\u01dc\u01dd\7G\2\2\u01dd\u01de\5\66\34\2\u01de"+
		"\u01df\7F\2\2\u01df\u01e0\5\66\34\2\u01e0\u01e1\7H\2\2\u01e1\u0204\3\2"+
		"\2\2\u01e2\u01e3\7@\2\2\u01e3\u01e4\7G\2\2\u01e4\u01e5\5\66\34\2\u01e5"+
		"\u01e6\7F\2\2\u01e6\u01e7\5\66\34\2\u01e7\u01e8\7F\2\2\u01e8\u01e9\5\66"+
		"\34\2\u01e9\u01ea\7H\2\2\u01ea\u0204\3\2\2\2\u01eb\u01ec\7:\2\2\u01ec"+
		"\u01ed\7G\2\2\u01ed\u01ee\5T+\2\u01ee\u01ef\7H\2\2\u01ef\u0204\3\2\2\2"+
		"\u01f0\u01f1\7;\2\2\u01f1\u01f2\7G\2\2\u01f2\u01f3\5\66\34\2\u01f3\u01f4"+
		"\7F\2\2\u01f4\u01f5\5\66\34\2\u01f5\u01f6\7H\2\2\u01f6\u0204\3\2\2\2\u01f7"+
		"\u01f8\7<\2\2\u01f8\u01f9\7G\2\2\u01f9\u01fa\5\66\34\2\u01fa\u01fb\7F"+
		"\2\2\u01fb\u01fc\5T+\2\u01fc\u01fd\7H\2\2\u01fd\u0204\3\2\2\2\u01fe\u01ff"+
		"\7>\2\2\u01ff\u0200\7G\2\2\u0200\u0201\5\66\34\2\u0201\u0202\7H\2\2\u0202"+
		"\u0204\3\2\2\2\u0203\u01d6\3\2\2\2\u0203\u01db\3\2\2\2\u0203\u01e2\3\2"+
		"\2\2\u0203\u01eb\3\2\2\2\u0203\u01f0\3\2\2\2\u0203\u01f7\3\2\2\2\u0203"+
		"\u01fe\3\2\2\2\u0204M\3\2\2\2\u0205\u0206\t\5\2\2\u0206\u0207\7G\2\2\u0207"+
		"\u0208\5\66\34\2\u0208\u0209\7H\2\2\u0209\u0212\3\2\2\2\u020a\u020b\7"+
		"\67\2\2\u020b\u020e\7G\2\2\u020c\u020f\7N\2\2\u020d\u020f\5\66\34\2\u020e"+
		"\u020c\3\2\2\2\u020e\u020d\3\2\2\2\u020f\u0210\3\2\2\2\u0210\u0212\7H"+
		"\2\2\u0211\u0205\3\2\2\2\u0211\u020a\3\2\2\2\u0212O\3\2\2\2\u0213\u0214"+
		"\7J\2\2\u0214\u0219\5R*\2\u0215\u0216\7F\2\2\u0216\u0218\5R*\2\u0217\u0215"+
		"\3\2\2\2\u0218\u021b\3\2\2\2\u0219\u0217\3\2\2\2\u0219\u021a\3\2\2\2\u021a"+
		"\u021c\3\2\2\2\u021b\u0219\3\2\2\2\u021c\u021d\7K\2\2\u021d\u0220\3\2"+
		"\2\2\u021e\u0220\5R*\2\u021f\u0213\3\2\2\2\u021f\u021e\3\2\2\2\u0220Q"+
		"\3\2\2\2\u0221\u0229\7W\2\2\u0222\u0224\7M\2\2\u0223\u0222\3\2\2\2\u0223"+
		"\u0224\3\2\2\2\u0224\u0225\3\2\2\2\u0225\u0229\7V\2\2\u0226\u0229\7\35"+
		"\2\2\u0227\u0229\7\34\2\2\u0228\u0221\3\2\2\2\u0228\u0223\3\2\2\2\u0228"+
		"\u0226\3\2\2\2\u0228\u0227\3\2\2\2\u0229S\3\2\2\2\u022a\u022b\7\30\2\2"+
		"\u022b\u022c\5Z.\2\u022c\u022d\7\f\2\2\u022d\u0230\5^\60\2\u022e\u022f"+
		"\7\31\2\2\u022f\u0231\5\66\34\2\u0230\u022e\3\2\2\2\u0230\u0231\3\2\2"+
		"\2\u0231\u0233\3\2\2\2\u0232\u0234\5f\64\2\u0233\u0232\3\2\2\2\u0233\u0234"+
		"\3\2\2\2\u0234\u0236\3\2\2\2\u0235\u0237\5j\66\2\u0236\u0235\3\2\2\2\u0236"+
		"\u0237\3\2\2\2\u0237\u023f\3\2\2\2\u0238\u023f\5X-\2\u0239\u023f\7U\2"+
		"\2\u023a\u023b\7S\2\2\u023b\u023c\5V,\2\u023c\u023d\7T\2\2\u023d\u023f"+
		"\3\2\2\2\u023e\u022a\3\2\2\2\u023e\u0238\3\2\2\2\u023e\u0239\3\2\2\2\u023e"+
		"\u023a\3\2\2\2\u023fU\3\2\2\2\u0240\u0245\5P)\2\u0241\u0242\7F\2\2\u0242"+
		"\u0244\5P)\2\u0243\u0241\3\2\2\2\u0244\u0247\3\2\2\2\u0245\u0243\3\2\2"+
		"\2\u0245\u0246\3\2\2\2\u0246W\3\2\2\2\u0247\u0245\3\2\2\2\u0248\u0249"+
		"\79\2\2\u0249\u024a\7G\2\2\u024a\u024b\5T+\2\u024b\u024c\7F\2\2\u024c"+
		"\u024d\5T+\2\u024d\u024e\7H\2\2\u024e\u0257\3\2\2\2\u024f\u0250\7?\2\2"+
		"\u0250\u0251\7G\2\2\u0251\u0252\5T+\2\u0252\u0253\7F\2\2\u0253\u0254\5"+
		"T+\2\u0254\u0255\7H\2\2\u0255\u0257\3\2\2\2\u0256\u0248\3\2\2\2\u0256"+
		"\u024f\3\2\2\2\u0257Y\3\2\2\2\u0258\u025d\5\\/\2\u0259\u025a\7F\2\2\u025a"+
		"\u025c\5\\/\2\u025b\u0259\3\2\2\2\u025c\u025f\3\2\2\2\u025d\u025b\3\2"+
		"\2\2\u025d\u025e\3\2\2\2\u025e\u0262\3\2\2\2\u025f\u025d\3\2\2\2\u0260"+
		"\u0262\7N\2\2\u0261\u0258\3\2\2\2\u0261\u0260\3\2\2\2\u0262[\3\2\2\2\u0263"+
		"\u0266\5\66\34\2\u0264\u0265\7&\2\2\u0265\u0267\7U\2\2\u0266\u0264\3\2"+
		"\2\2\u0266\u0267\3\2\2\2\u0267]\3\2\2\2\u0268\u026c\5b\62\2\u0269\u026b"+
		"\5`\61\2\u026a\u0269\3\2\2\2\u026b\u026e\3\2\2\2\u026c\u026a\3\2\2\2\u026c"+
		"\u026d\3\2\2\2\u026d_\3\2\2\2\u026e\u026c\3\2\2\2\u026f\u0270\5d\63\2"+
		"\u0270\u0275\5b\62\2\u0271\u0272\7\n\2\2\u0272\u0273\7U\2\2\u0273\u0274"+
		"\7Q\2\2\u0274\u0276\7U\2\2\u0275\u0271\3\2\2\2\u0275\u0276\3\2\2\2\u0276"+
		"a\3\2\2\2\u0277\u027d\7U\2\2\u0278\u0279\7G\2\2\u0279\u027a\5T+\2\u027a"+
		"\u027b\7H\2\2\u027b\u027d\3\2\2\2\u027c\u0277\3\2\2\2\u027c\u0278\3\2"+
		"\2\2\u027dc\3\2\2\2\u027e\u0280\t\6\2\2\u027f\u027e\3\2\2\2\u027f\u0280"+
		"\3\2\2\2\u0280\u0281\3\2\2\2\u0281\u0282\7%\2\2\u0282e\3\2\2\2\u0283\u0284"+
		"\7)\2\2\u0284\u0285\7+\2\2\u0285\u028a\7U\2\2\u0286\u0287\7F\2\2\u0287"+
		"\u0289\7U\2\2\u0288\u0286\3\2\2\2\u0289\u028c\3\2\2\2\u028a\u0288\3\2"+
		"\2\2\u028a\u028b\3\2\2\2\u028b\u028e\3\2\2\2\u028c\u028a\3\2\2\2\u028d"+
		"\u028f\5h\65\2\u028e\u028d\3\2\2\2\u028e\u028f\3\2\2\2\u028fg\3\2\2\2"+
		"\u0290\u0291\7,\2\2\u0291\u0292\5\66\34\2\u0292i\3\2\2\2\u0293\u0294\7"+
		"*\2\2\u0294\u0295\7+\2\2\u0295\u029a\7U\2\2\u0296\u0297\7F\2\2\u0297\u0299"+
		"\7U\2\2\u0298\u0296\3\2\2\2\u0299\u029c\3\2\2\2\u029a\u0298\3\2\2\2\u029a"+
		"\u029b\3\2\2\2\u029bk\3\2\2\2\u029c\u029a\3\2\2\2Eot|\u0087\u008f\u0096"+
		"\u009f\u00af\u00b7\u00c3\u00ca\u00d6\u00da\u00de\u00e3\u00ec\u00f7\u0102"+
		"\u0106\u010b\u010f\u0113\u011b\u0120\u0123\u0137\u013f\u014e\u0153\u0158"+
		"\u016f\u0171\u017d\u0188\u0190\u019a\u01a5\u01a9\u01b4\u01bb\u01c2\u01c8"+
		"\u01cc\u01d2\u0203\u020e\u0211\u0219\u021f\u0223\u0228\u0230\u0233\u0236"+
		"\u023e\u0245\u0256\u025d\u0261\u0266\u026c\u0275\u027c\u027f\u028a\u028e"+
		"\u029a";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}