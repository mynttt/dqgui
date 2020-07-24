// Generated from IQM4HDLexer.g4 by ANTLR 4.7.1

	package de.mvise.iqm4hd.dsl.parser.gen;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class IQM4HDLexer extends Lexer {
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
		NATIVE_QUERY=1;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE", "NATIVE_QUERY"
	};

	public static final String[] ruleNames = {
		"DATABASE", "NATIVE", "SOURCE", "CHECK", "ACTION", "EXECUTE", "USING", 
		"ON", "EACH", "FROM", "RESULT", "IF", "IN", "AS", "END", "AND", "TYPE", 
		"WITH", "ERROR", "WARNING", "RECORD", "SELECT", "WHERE", "PATTERN", "RETURN", 
		"NULL", "BOOL", "FOR", "ELSE", "SINGLE", "LIST", "LEFT", "RIGHT", "FULL", 
		"JOIN", "ROLE", "ROLES", "IS", "GROUP", "ORDER", "BY", "HAVING", "ABOVE", 
		"CONST", "OR", "NOT", "TO", "QUERY", "AVG", "MIN", "MAX", "SUM", "COUNT", 
		"ABS", "ARIMA", "LENGTH", "MATCHES", "VAL2BIN", "EVAL", "DOB2AGE", "CUBESCORE", 
		"BINOM", "NVL", "PERCENTAGE", "EVALREC", "RELOP", "Colon", "Comma", "Open", 
		"Close", "Assign", "OpenIdx", "CloseIdx", "Plus", "Minus", "Mul", "Div", 
		"Semicolon", "Equals", "Dot", "OpenBrace", "CloseBrace", "ID", "NUMBER", 
		"STRING", "REGEXP", "ESC", "ID_LETTER", "CHAR", "DIGIT", "WS", "NL", "COMMENT", 
		"END_NATIVE", "QUERYTEXT"
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


	public IQM4HDLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "IQM4HDLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2]\u02bf\b\1\b\1\4"+
		"\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n"+
		"\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t"+
		"=\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4"+
		"I\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\t"+
		"T\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_"+
		"\4`\t`\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\20\3"+
		"\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3"+
		"\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3"+
		"\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3"+
		"\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3"+
		"\33\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u0165\n\34\3\35"+
		"\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3\37\3\37\3\37\3\37"+
		"\3\37\3 \3 \3 \3 \3 \3!\3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3"+
		"#\3#\3$\3$\3$\3$\3$\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3&\3\'\3\'\3\'\3(\3"+
		"(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3*\3*\3*\3+\3+\3+\3+\3+\3+\3+\3,\3,\3"+
		",\3,\3,\3,\3-\3-\3-\3-\3-\3-\3.\3.\3.\3/\3/\3/\3/\3\60\3\60\3\60\3\61"+
		"\3\61\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\64"+
		"\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\66\3\67"+
		"\3\67\3\67\3\67\38\38\38\38\38\38\39\39\39\39\39\39\39\3:\3:\3:\3:\3:"+
		"\3:\3:\3:\3;\3;\3;\3;\3;\3;\3;\3;\3<\3<\3<\3<\3<\3=\3=\3=\3=\3=\3=\3="+
		"\3=\3>\3>\3>\3>\3>\3>\3>\3>\3>\3>\3?\3?\3?\3?\3?\3?\3@\3@\3@\3@\3A\3A"+
		"\3A\3A\3A\3A\3A\3A\3A\3A\3A\3B\3B\3B\3B\3B\3B\3B\3B\3C\3C\3C\3C\3C\3C"+
		"\3C\3C\3C\5C\u0245\nC\3D\3D\3E\3E\3F\3F\3G\3G\3H\3H\3H\3I\3I\3J\3J\3K"+
		"\3K\3L\3L\3M\3M\3N\3N\3O\3O\3P\3P\3Q\3Q\3R\3R\3S\3S\3T\3T\3T\7T\u026b"+
		"\nT\fT\16T\u026e\13T\3U\6U\u0271\nU\rU\16U\u0272\3U\3U\6U\u0277\nU\rU"+
		"\16U\u0278\5U\u027b\nU\3U\3U\6U\u027f\nU\rU\16U\u0280\5U\u0283\nU\3V\3"+
		"V\3V\7V\u0288\nV\fV\16V\u028b\13V\3V\3V\3W\3W\7W\u0291\nW\fW\16W\u0294"+
		"\13W\3W\3W\3X\3X\3X\3Y\3Y\5Y\u029d\nY\3Z\3Z\3[\3[\3\\\3\\\3\\\3\\\3]\5"+
		"]\u02a8\n]\3]\3]\3]\3]\3^\3^\7^\u02b0\n^\f^\16^\u02b3\13^\3^\3^\3_\3_"+
		"\3_\3_\3_\3_\3_\3`\3`\3\u0289\2a\4\3\6\4\b\5\n\6\f\7\16\b\20\t\22\n\24"+
		"\13\26\f\30\r\32\16\34\17\36\20 \21\"\22$\23&\24(\25*\26,\27.\30\60\31"+
		"\62\32\64\33\66\348\35:\36<\37> @!B\"D#F$H%J&L\'N(P)R*T+V,X-Z.\\/^\60"+
		"`\61b\62d\63f\64h\65j\66l\67n8p9r:t;v<x=z>|?~@\u0080A\u0082B\u0084C\u0086"+
		"D\u0088E\u008aF\u008cG\u008eH\u0090I\u0092J\u0094K\u0096L\u0098M\u009a"+
		"N\u009cO\u009eP\u00a0Q\u00a2R\u00a4S\u00a6T\u00a8U\u00aaV\u00acW\u00ae"+
		"X\u00b0\2\u00b2\2\u00b4\2\u00b6\2\u00b8Y\u00baZ\u00bc[\u00be\\\u00c0]"+
		"\4\2\3\t\4\2>>@@\3\2&&\b\2$$^^ddppttvv\4\2C\\c|\3\2\62;\4\2\13\13\"\""+
		"\4\2\f\f\17\17\2\u02cb\2\4\3\2\2\2\2\6\3\2\2\2\2\b\3\2\2\2\2\n\3\2\2\2"+
		"\2\f\3\2\2\2\2\16\3\2\2\2\2\20\3\2\2\2\2\22\3\2\2\2\2\24\3\2\2\2\2\26"+
		"\3\2\2\2\2\30\3\2\2\2\2\32\3\2\2\2\2\34\3\2\2\2\2\36\3\2\2\2\2 \3\2\2"+
		"\2\2\"\3\2\2\2\2$\3\2\2\2\2&\3\2\2\2\2(\3\2\2\2\2*\3\2\2\2\2,\3\2\2\2"+
		"\2.\3\2\2\2\2\60\3\2\2\2\2\62\3\2\2\2\2\64\3\2\2\2\2\66\3\2\2\2\28\3\2"+
		"\2\2\2:\3\2\2\2\2<\3\2\2\2\2>\3\2\2\2\2@\3\2\2\2\2B\3\2\2\2\2D\3\2\2\2"+
		"\2F\3\2\2\2\2H\3\2\2\2\2J\3\2\2\2\2L\3\2\2\2\2N\3\2\2\2\2P\3\2\2\2\2R"+
		"\3\2\2\2\2T\3\2\2\2\2V\3\2\2\2\2X\3\2\2\2\2Z\3\2\2\2\2\\\3\2\2\2\2^\3"+
		"\2\2\2\2`\3\2\2\2\2b\3\2\2\2\2d\3\2\2\2\2f\3\2\2\2\2h\3\2\2\2\2j\3\2\2"+
		"\2\2l\3\2\2\2\2n\3\2\2\2\2p\3\2\2\2\2r\3\2\2\2\2t\3\2\2\2\2v\3\2\2\2\2"+
		"x\3\2\2\2\2z\3\2\2\2\2|\3\2\2\2\2~\3\2\2\2\2\u0080\3\2\2\2\2\u0082\3\2"+
		"\2\2\2\u0084\3\2\2\2\2\u0086\3\2\2\2\2\u0088\3\2\2\2\2\u008a\3\2\2\2\2"+
		"\u008c\3\2\2\2\2\u008e\3\2\2\2\2\u0090\3\2\2\2\2\u0092\3\2\2\2\2\u0094"+
		"\3\2\2\2\2\u0096\3\2\2\2\2\u0098\3\2\2\2\2\u009a\3\2\2\2\2\u009c\3\2\2"+
		"\2\2\u009e\3\2\2\2\2\u00a0\3\2\2\2\2\u00a2\3\2\2\2\2\u00a4\3\2\2\2\2\u00a6"+
		"\3\2\2\2\2\u00a8\3\2\2\2\2\u00aa\3\2\2\2\2\u00ac\3\2\2\2\2\u00ae\3\2\2"+
		"\2\2\u00b8\3\2\2\2\2\u00ba\3\2\2\2\2\u00bc\3\2\2\2\3\u00be\3\2\2\2\3\u00c0"+
		"\3\2\2\2\4\u00c2\3\2\2\2\6\u00cb\3\2\2\2\b\u00d4\3\2\2\2\n\u00db\3\2\2"+
		"\2\f\u00e1\3\2\2\2\16\u00e8\3\2\2\2\20\u00f0\3\2\2\2\22\u00f6\3\2\2\2"+
		"\24\u00f9\3\2\2\2\26\u00fe\3\2\2\2\30\u0103\3\2\2\2\32\u010a\3\2\2\2\34"+
		"\u010d\3\2\2\2\36\u0110\3\2\2\2 \u0113\3\2\2\2\"\u0117\3\2\2\2$\u011b"+
		"\3\2\2\2&\u0120\3\2\2\2(\u0125\3\2\2\2*\u012b\3\2\2\2,\u0133\3\2\2\2."+
		"\u013a\3\2\2\2\60\u0141\3\2\2\2\62\u0147\3\2\2\2\64\u014f\3\2\2\2\66\u0156"+
		"\3\2\2\28\u0164\3\2\2\2:\u0166\3\2\2\2<\u016a\3\2\2\2>\u016f\3\2\2\2@"+
		"\u0176\3\2\2\2B\u017b\3\2\2\2D\u0180\3\2\2\2F\u0186\3\2\2\2H\u018b\3\2"+
		"\2\2J\u0190\3\2\2\2L\u0195\3\2\2\2N\u019b\3\2\2\2P\u019e\3\2\2\2R\u01a4"+
		"\3\2\2\2T\u01aa\3\2\2\2V\u01ad\3\2\2\2X\u01b4\3\2\2\2Z\u01ba\3\2\2\2\\"+
		"\u01c0\3\2\2\2^\u01c3\3\2\2\2`\u01c7\3\2\2\2b\u01ca\3\2\2\2d\u01d0\3\2"+
		"\2\2f\u01d4\3\2\2\2h\u01d8\3\2\2\2j\u01dc\3\2\2\2l\u01e0\3\2\2\2n\u01e6"+
		"\3\2\2\2p\u01ea\3\2\2\2r\u01f0\3\2\2\2t\u01f7\3\2\2\2v\u01ff\3\2\2\2x"+
		"\u0207\3\2\2\2z\u020c\3\2\2\2|\u0214\3\2\2\2~\u021e\3\2\2\2\u0080\u0224"+
		"\3\2\2\2\u0082\u0228\3\2\2\2\u0084\u0233\3\2\2\2\u0086\u0244\3\2\2\2\u0088"+
		"\u0246\3\2\2\2\u008a\u0248\3\2\2\2\u008c\u024a\3\2\2\2\u008e\u024c\3\2"+
		"\2\2\u0090\u024e\3\2\2\2\u0092\u0251\3\2\2\2\u0094\u0253\3\2\2\2\u0096"+
		"\u0255\3\2\2\2\u0098\u0257\3\2\2\2\u009a\u0259\3\2\2\2\u009c\u025b\3\2"+
		"\2\2\u009e\u025d\3\2\2\2\u00a0\u025f\3\2\2\2\u00a2\u0261\3\2\2\2\u00a4"+
		"\u0263\3\2\2\2\u00a6\u0265\3\2\2\2\u00a8\u0267\3\2\2\2\u00aa\u0282\3\2"+
		"\2\2\u00ac\u0284\3\2\2\2\u00ae\u028e\3\2\2\2\u00b0\u0297\3\2\2\2\u00b2"+
		"\u029c\3\2\2\2\u00b4\u029e\3\2\2\2\u00b6\u02a0\3\2\2\2\u00b8\u02a2\3\2"+
		"\2\2\u00ba\u02a7\3\2\2\2\u00bc\u02ad\3\2\2\2\u00be\u02b6\3\2\2\2\u00c0"+
		"\u02bd\3\2\2\2\u00c2\u00c3\7F\2\2\u00c3\u00c4\7C\2\2\u00c4\u00c5\7V\2"+
		"\2\u00c5\u00c6\7C\2\2\u00c6\u00c7\7D\2\2\u00c7\u00c8\7C\2\2\u00c8\u00c9"+
		"\7U\2\2\u00c9\u00ca\7G\2\2\u00ca\5\3\2\2\2\u00cb\u00cc\7P\2\2\u00cc\u00cd"+
		"\7C\2\2\u00cd\u00ce\7V\2\2\u00ce\u00cf\7K\2\2\u00cf\u00d0\7X\2\2\u00d0"+
		"\u00d1\7G\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d3\b\3\2\2\u00d3\7\3\2\2\2"+
		"\u00d4\u00d5\7U\2\2\u00d5\u00d6\7Q\2\2\u00d6\u00d7\7W\2\2\u00d7\u00d8"+
		"\7T\2\2\u00d8\u00d9\7E\2\2\u00d9\u00da\7G\2\2\u00da\t\3\2\2\2\u00db\u00dc"+
		"\7E\2\2\u00dc\u00dd\7J\2\2\u00dd\u00de\7G\2\2\u00de\u00df\7E\2\2\u00df"+
		"\u00e0\7M\2\2\u00e0\13\3\2\2\2\u00e1\u00e2\7C\2\2\u00e2\u00e3\7E\2\2\u00e3"+
		"\u00e4\7V\2\2\u00e4\u00e5\7K\2\2\u00e5\u00e6\7Q\2\2\u00e6\u00e7\7P\2\2"+
		"\u00e7\r\3\2\2\2\u00e8\u00e9\7G\2\2\u00e9\u00ea\7Z\2\2\u00ea\u00eb\7G"+
		"\2\2\u00eb\u00ec\7E\2\2\u00ec\u00ed\7W\2\2\u00ed\u00ee\7V\2\2\u00ee\u00ef"+
		"\7G\2\2\u00ef\17\3\2\2\2\u00f0\u00f1\7W\2\2\u00f1\u00f2\7U\2\2\u00f2\u00f3"+
		"\7K\2\2\u00f3\u00f4\7P\2\2\u00f4\u00f5\7I\2\2\u00f5\21\3\2\2\2\u00f6\u00f7"+
		"\7Q\2\2\u00f7\u00f8\7P\2\2\u00f8\23\3\2\2\2\u00f9\u00fa\7G\2\2\u00fa\u00fb"+
		"\7C\2\2\u00fb\u00fc\7E\2\2\u00fc\u00fd\7J\2\2\u00fd\25\3\2\2\2\u00fe\u00ff"+
		"\7H\2\2\u00ff\u0100\7T\2\2\u0100\u0101\7Q\2\2\u0101\u0102\7O\2\2\u0102"+
		"\27\3\2\2\2\u0103\u0104\7T\2\2\u0104\u0105\7G\2\2\u0105\u0106\7U\2\2\u0106"+
		"\u0107\7W\2\2\u0107\u0108\7N\2\2\u0108\u0109\7V\2\2\u0109\31\3\2\2\2\u010a"+
		"\u010b\7K\2\2\u010b\u010c\7H\2\2\u010c\33\3\2\2\2\u010d\u010e\7K\2\2\u010e"+
		"\u010f\7P\2\2\u010f\35\3\2\2\2\u0110\u0111\7C\2\2\u0111\u0112\7U\2\2\u0112"+
		"\37\3\2\2\2\u0113\u0114\7G\2\2\u0114\u0115\7P\2\2\u0115\u0116\7F\2\2\u0116"+
		"!\3\2\2\2\u0117\u0118\7C\2\2\u0118\u0119\7P\2\2\u0119\u011a\7F\2\2\u011a"+
		"#\3\2\2\2\u011b\u011c\7V\2\2\u011c\u011d\7[\2\2\u011d\u011e\7R\2\2\u011e"+
		"\u011f\7G\2\2\u011f%\3\2\2\2\u0120\u0121\7Y\2\2\u0121\u0122\7K\2\2\u0122"+
		"\u0123\7V\2\2\u0123\u0124\7J\2\2\u0124\'\3\2\2\2\u0125\u0126\7G\2\2\u0126"+
		"\u0127\7T\2\2\u0127\u0128\7T\2\2\u0128\u0129\7Q\2\2\u0129\u012a\7T\2\2"+
		"\u012a)\3\2\2\2\u012b\u012c\7Y\2\2\u012c\u012d\7C\2\2\u012d\u012e\7T\2"+
		"\2\u012e\u012f\7P\2\2\u012f\u0130\7K\2\2\u0130\u0131\7P\2\2\u0131\u0132"+
		"\7I\2\2\u0132+\3\2\2\2\u0133\u0134\7T\2\2\u0134\u0135\7G\2\2\u0135\u0136"+
		"\7E\2\2\u0136\u0137\7Q\2\2\u0137\u0138\7T\2\2\u0138\u0139\7F\2\2\u0139"+
		"-\3\2\2\2\u013a\u013b\7U\2\2\u013b\u013c\7G\2\2\u013c\u013d\7N\2\2\u013d"+
		"\u013e\7G\2\2\u013e\u013f\7E\2\2\u013f\u0140\7V\2\2\u0140/\3\2\2\2\u0141"+
		"\u0142\7Y\2\2\u0142\u0143\7J\2\2\u0143\u0144\7G\2\2\u0144\u0145\7T\2\2"+
		"\u0145\u0146\7G\2\2\u0146\61\3\2\2\2\u0147\u0148\7R\2\2\u0148\u0149\7"+
		"C\2\2\u0149\u014a\7V\2\2\u014a\u014b\7V\2\2\u014b\u014c\7G\2\2\u014c\u014d"+
		"\7T\2\2\u014d\u014e\7P\2\2\u014e\63\3\2\2\2\u014f\u0150\7T\2\2\u0150\u0151"+
		"\7G\2\2\u0151\u0152\7V\2\2\u0152\u0153\7W\2\2\u0153\u0154\7T\2\2\u0154"+
		"\u0155\7P\2\2\u0155\65\3\2\2\2\u0156\u0157\7P\2\2\u0157\u0158\7W\2\2\u0158"+
		"\u0159\7N\2\2\u0159\u015a\7N\2\2\u015a\67\3\2\2\2\u015b\u015c\7V\2\2\u015c"+
		"\u015d\7T\2\2\u015d\u015e\7W\2\2\u015e\u0165\7G\2\2\u015f\u0160\7H\2\2"+
		"\u0160\u0161\7C\2\2\u0161\u0162\7N\2\2\u0162\u0163\7U\2\2\u0163\u0165"+
		"\7G\2\2\u0164\u015b\3\2\2\2\u0164\u015f\3\2\2\2\u01659\3\2\2\2\u0166\u0167"+
		"\7H\2\2\u0167\u0168\7Q\2\2\u0168\u0169\7T\2\2\u0169;\3\2\2\2\u016a\u016b"+
		"\7G\2\2\u016b\u016c\7N\2\2\u016c\u016d\7U\2\2\u016d\u016e\7G\2\2\u016e"+
		"=\3\2\2\2\u016f\u0170\7U\2\2\u0170\u0171\7K\2\2\u0171\u0172\7P\2\2\u0172"+
		"\u0173\7I\2\2\u0173\u0174\7N\2\2\u0174\u0175\7G\2\2\u0175?\3\2\2\2\u0176"+
		"\u0177\7N\2\2\u0177\u0178\7K\2\2\u0178\u0179\7U\2\2\u0179\u017a\7V\2\2"+
		"\u017aA\3\2\2\2\u017b\u017c\7N\2\2\u017c\u017d\7G\2\2\u017d\u017e\7H\2"+
		"\2\u017e\u017f\7V\2\2\u017fC\3\2\2\2\u0180\u0181\7T\2\2\u0181\u0182\7"+
		"K\2\2\u0182\u0183\7I\2\2\u0183\u0184\7J\2\2\u0184\u0185\7V\2\2\u0185E"+
		"\3\2\2\2\u0186\u0187\7H\2\2\u0187\u0188\7W\2\2\u0188\u0189\7N\2\2\u0189"+
		"\u018a\7N\2\2\u018aG\3\2\2\2\u018b\u018c\7L\2\2\u018c\u018d\7Q\2\2\u018d"+
		"\u018e\7K\2\2\u018e\u018f\7P\2\2\u018fI\3\2\2\2\u0190\u0191\7T\2\2\u0191"+
		"\u0192\7Q\2\2\u0192\u0193\7N\2\2\u0193\u0194\7G\2\2\u0194K\3\2\2\2\u0195"+
		"\u0196\7T\2\2\u0196\u0197\7Q\2\2\u0197\u0198\7N\2\2\u0198\u0199\7G\2\2"+
		"\u0199\u019a\7U\2\2\u019aM\3\2\2\2\u019b\u019c\7K\2\2\u019c\u019d\7U\2"+
		"\2\u019dO\3\2\2\2\u019e\u019f\7I\2\2\u019f\u01a0\7T\2\2\u01a0\u01a1\7"+
		"Q\2\2\u01a1\u01a2\7W\2\2\u01a2\u01a3\7R\2\2\u01a3Q\3\2\2\2\u01a4\u01a5"+
		"\7Q\2\2\u01a5\u01a6\7T\2\2\u01a6\u01a7\7F\2\2\u01a7\u01a8\7G\2\2\u01a8"+
		"\u01a9\7T\2\2\u01a9S\3\2\2\2\u01aa\u01ab\7D\2\2\u01ab\u01ac\7[\2\2\u01ac"+
		"U\3\2\2\2\u01ad\u01ae\7J\2\2\u01ae\u01af\7C\2\2\u01af\u01b0\7X\2\2\u01b0"+
		"\u01b1\7K\2\2\u01b1\u01b2\7P\2\2\u01b2\u01b3\7I\2\2\u01b3W\3\2\2\2\u01b4"+
		"\u01b5\7C\2\2\u01b5\u01b6\7D\2\2\u01b6\u01b7\7Q\2\2\u01b7\u01b8\7X\2\2"+
		"\u01b8\u01b9\7G\2\2\u01b9Y\3\2\2\2\u01ba\u01bb\7E\2\2\u01bb\u01bc\7Q\2"+
		"\2\u01bc\u01bd\7P\2\2\u01bd\u01be\7U\2\2\u01be\u01bf\7V\2\2\u01bf[\3\2"+
		"\2\2\u01c0\u01c1\7Q\2\2\u01c1\u01c2\7T\2\2\u01c2]\3\2\2\2\u01c3\u01c4"+
		"\7P\2\2\u01c4\u01c5\7Q\2\2\u01c5\u01c6\7V\2\2\u01c6_\3\2\2\2\u01c7\u01c8"+
		"\7V\2\2\u01c8\u01c9\7Q\2\2\u01c9a\3\2\2\2\u01ca\u01cb\7S\2\2\u01cb\u01cc"+
		"\7W\2\2\u01cc\u01cd\7G\2\2\u01cd\u01ce\7T\2\2\u01ce\u01cf\7[\2\2\u01cf"+
		"c\3\2\2\2\u01d0\u01d1\7C\2\2\u01d1\u01d2\7X\2\2\u01d2\u01d3\7I\2\2\u01d3"+
		"e\3\2\2\2\u01d4\u01d5\7O\2\2\u01d5\u01d6\7K\2\2\u01d6\u01d7\7P\2\2\u01d7"+
		"g\3\2\2\2\u01d8\u01d9\7O\2\2\u01d9\u01da\7C\2\2\u01da\u01db\7Z\2\2\u01db"+
		"i\3\2\2\2\u01dc\u01dd\7U\2\2\u01dd\u01de\7W\2\2\u01de\u01df\7O\2\2\u01df"+
		"k\3\2\2\2\u01e0\u01e1\7E\2\2\u01e1\u01e2\7Q\2\2\u01e2\u01e3\7W\2\2\u01e3"+
		"\u01e4\7P\2\2\u01e4\u01e5\7V\2\2\u01e5m\3\2\2\2\u01e6\u01e7\7C\2\2\u01e7"+
		"\u01e8\7D\2\2\u01e8\u01e9\7U\2\2\u01e9o\3\2\2\2\u01ea\u01eb\7C\2\2\u01eb"+
		"\u01ec\7T\2\2\u01ec\u01ed\7K\2\2\u01ed\u01ee\7O\2\2\u01ee\u01ef\7C\2\2"+
		"\u01efq\3\2\2\2\u01f0\u01f1\7N\2\2\u01f1\u01f2\7G\2\2\u01f2\u01f3\7P\2"+
		"\2\u01f3\u01f4\7I\2\2\u01f4\u01f5\7V\2\2\u01f5\u01f6\7J\2\2\u01f6s\3\2"+
		"\2\2\u01f7\u01f8\7O\2\2\u01f8\u01f9\7C\2\2\u01f9\u01fa\7V\2\2\u01fa\u01fb"+
		"\7E\2\2\u01fb\u01fc\7J\2\2\u01fc\u01fd\7G\2\2\u01fd\u01fe\7U\2\2\u01fe"+
		"u\3\2\2\2\u01ff\u0200\7X\2\2\u0200\u0201\7C\2\2\u0201\u0202\7N\2\2\u0202"+
		"\u0203\7\64\2\2\u0203\u0204\7D\2\2\u0204\u0205\7K\2\2\u0205\u0206\7P\2"+
		"\2\u0206w\3\2\2\2\u0207\u0208\7G\2\2\u0208\u0209\7X\2\2\u0209\u020a\7"+
		"C\2\2\u020a\u020b\7N\2\2\u020by\3\2\2\2\u020c\u020d\7F\2\2\u020d\u020e"+
		"\7Q\2\2\u020e\u020f\7D\2\2\u020f\u0210\7\64\2\2\u0210\u0211\7C\2\2\u0211"+
		"\u0212\7I\2\2\u0212\u0213\7G\2\2\u0213{\3\2\2\2\u0214\u0215\7E\2\2\u0215"+
		"\u0216\7W\2\2\u0216\u0217\7D\2\2\u0217\u0218\7G\2\2\u0218\u0219\7U\2\2"+
		"\u0219\u021a\7E\2\2\u021a\u021b\7Q\2\2\u021b\u021c\7T\2\2\u021c\u021d"+
		"\7G\2\2\u021d}\3\2\2\2\u021e\u021f\7D\2\2\u021f\u0220\7K\2\2\u0220\u0221"+
		"\7P\2\2\u0221\u0222\7Q\2\2\u0222\u0223\7O\2\2\u0223\177\3\2\2\2\u0224"+
		"\u0225\7P\2\2\u0225\u0226\7X\2\2\u0226\u0227\7N\2\2\u0227\u0081\3\2\2"+
		"\2\u0228\u0229\7R\2\2\u0229\u022a\7G\2\2\u022a\u022b\7T\2\2\u022b\u022c"+
		"\7E\2\2\u022c\u022d\7G\2\2\u022d\u022e\7P\2\2\u022e\u022f\7V\2\2\u022f"+
		"\u0230\7C\2\2\u0230\u0231\7I\2\2\u0231\u0232\7G\2\2\u0232\u0083\3\2\2"+
		"\2\u0233\u0234\7G\2\2\u0234\u0235\7X\2\2\u0235\u0236\7C\2\2\u0236\u0237"+
		"\7N\2\2\u0237\u0238\7T\2\2\u0238\u0239\7G\2\2\u0239\u023a\7E\2\2\u023a"+
		"\u0085\3\2\2\2\u023b\u023c\7?\2\2\u023c\u0245\7?\2\2\u023d\u023e\7#\2"+
		"\2\u023e\u0245\7?\2\2\u023f\u0245\t\2\2\2\u0240\u0241\7>\2\2\u0241\u0245"+
		"\7?\2\2\u0242\u0243\7@\2\2\u0243\u0245\7?\2\2\u0244\u023b\3\2\2\2\u0244"+
		"\u023d\3\2\2\2\u0244\u023f\3\2\2\2\u0244\u0240\3\2\2\2\u0244\u0242\3\2"+
		"\2\2\u0245\u0087\3\2\2\2\u0246\u0247\7<\2\2\u0247\u0089\3\2\2\2\u0248"+
		"\u0249\7.\2\2\u0249\u008b\3\2\2\2\u024a\u024b\7*\2\2\u024b\u008d\3\2\2"+
		"\2\u024c\u024d\7+\2\2\u024d\u008f\3\2\2\2\u024e\u024f\7<\2\2\u024f\u0250"+
		"\7?\2\2\u0250\u0091\3\2\2\2\u0251\u0252\7]\2\2\u0252\u0093\3\2\2\2\u0253"+
		"\u0254\7_\2\2\u0254\u0095\3\2\2\2\u0255\u0256\7-\2\2\u0256\u0097\3\2\2"+
		"\2\u0257\u0258\7/\2\2\u0258\u0099\3\2\2\2\u0259\u025a\7,\2\2\u025a\u009b"+
		"\3\2\2\2\u025b\u025c\7\61\2\2\u025c\u009d\3\2\2\2\u025d\u025e\7=\2\2\u025e"+
		"\u009f\3\2\2\2\u025f\u0260\7?\2\2\u0260\u00a1\3\2\2\2\u0261\u0262\7\60"+
		"\2\2\u0262\u00a3\3\2\2\2\u0263\u0264\7}\2\2\u0264\u00a5\3\2\2\2\u0265"+
		"\u0266\7\177\2\2\u0266\u00a7\3\2\2\2\u0267\u026c\5\u00b2Y\2\u0268\u026b"+
		"\5\u00b2Y\2\u0269\u026b\5\u00b6[\2\u026a\u0268\3\2\2\2\u026a\u0269\3\2"+
		"\2\2\u026b\u026e\3\2\2\2\u026c\u026a\3\2\2\2\u026c\u026d\3\2\2\2\u026d"+
		"\u00a9\3\2\2\2\u026e\u026c\3\2\2\2\u026f\u0271\5\u00b6[\2\u0270\u026f"+
		"\3\2\2\2\u0271\u0272\3\2\2\2\u0272\u0270\3\2\2\2\u0272\u0273\3\2\2\2\u0273"+
		"\u027a\3\2\2\2\u0274\u0276\7\60\2\2\u0275\u0277\5\u00b6[\2\u0276\u0275"+
		"\3\2\2\2\u0277\u0278\3\2\2\2\u0278\u0276\3\2\2\2\u0278\u0279\3\2\2\2\u0279"+
		"\u027b\3\2\2\2\u027a\u0274\3\2\2\2\u027a\u027b\3\2\2\2\u027b\u0283\3\2"+
		"\2\2\u027c\u027e\7\60\2\2\u027d\u027f\5\u00b6[\2\u027e\u027d\3\2\2\2\u027f"+
		"\u0280\3\2\2\2\u0280\u027e\3\2\2\2\u0280\u0281\3\2\2\2\u0281\u0283\3\2"+
		"\2\2\u0282\u0270\3\2\2\2\u0282\u027c\3\2\2\2\u0283\u00ab\3\2\2\2\u0284"+
		"\u0289\7$\2\2\u0285\u0288\5\u00b0X\2\u0286\u0288\13\2\2\2\u0287\u0285"+
		"\3\2\2\2\u0287\u0286\3\2\2\2\u0288\u028b\3\2\2\2\u0289\u028a\3\2\2\2\u0289"+
		"\u0287\3\2\2\2\u028a\u028c\3\2\2\2\u028b\u0289\3\2\2\2\u028c\u028d\7$"+
		"\2\2\u028d\u00ad\3\2\2\2\u028e\u0292\7&\2\2\u028f\u0291\n\3\2\2\u0290"+
		"\u028f\3\2\2\2\u0291\u0294\3\2\2\2\u0292\u0290\3\2\2\2\u0292\u0293\3\2"+
		"\2\2\u0293\u0295\3\2\2\2\u0294\u0292\3\2\2\2\u0295\u0296\7&\2\2\u0296"+
		"\u00af\3\2\2\2\u0297\u0298\7^\2\2\u0298\u0299\t\4\2\2\u0299\u00b1\3\2"+
		"\2\2\u029a\u029d\5\u00b4Z\2\u029b\u029d\7a\2\2\u029c\u029a\3\2\2\2\u029c"+
		"\u029b\3\2\2\2\u029d\u00b3\3\2\2\2\u029e\u029f\t\5\2\2\u029f\u00b5\3\2"+
		"\2\2\u02a0\u02a1\t\6\2\2\u02a1\u00b7\3\2\2\2\u02a2\u02a3\t\7\2\2\u02a3"+
		"\u02a4\3\2\2\2\u02a4\u02a5\b\\\3\2\u02a5\u00b9\3\2\2\2\u02a6\u02a8\7\17"+
		"\2\2\u02a7\u02a6\3\2\2\2\u02a7\u02a8\3\2\2\2\u02a8\u02a9\3\2\2\2\u02a9"+
		"\u02aa\7\f\2\2\u02aa\u02ab\3\2\2\2\u02ab\u02ac\b]\3\2\u02ac\u00bb\3\2"+
		"\2\2\u02ad\u02b1\7%\2\2\u02ae\u02b0\n\b\2\2\u02af\u02ae\3\2\2\2\u02b0"+
		"\u02b3\3\2\2\2\u02b1\u02af\3\2\2\2\u02b1\u02b2\3\2\2\2\u02b2\u02b4\3\2"+
		"\2\2\u02b3\u02b1\3\2\2\2\u02b4\u02b5\b^\3\2\u02b5\u00bd\3\2\2\2\u02b6"+
		"\u02b7\7\f\2\2\u02b7\u02b8\7G\2\2\u02b8\u02b9\7P\2\2\u02b9\u02ba\7F\2"+
		"\2\u02ba\u02bb\3\2\2\2\u02bb\u02bc\b_\4\2\u02bc\u00bf\3\2\2\2\u02bd\u02be"+
		"\13\2\2\2\u02be\u00c1\3\2\2\2\23\2\3\u0164\u0244\u026a\u026c\u0272\u0278"+
		"\u027a\u0280\u0282\u0287\u0289\u0292\u029c\u02a7\u02b1\5\4\3\2\2\3\2\4"+
		"\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}