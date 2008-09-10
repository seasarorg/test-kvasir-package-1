package org.seasar.kvasir.util.parametrized;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ScriptExpression
{
    private static final int        OP_NONE = 0;
    private static final int        OP_EQ_N = 1;
    private static final int        OP_NE_N = 2;
    private static final int        OP_LT_N = 3;
    private static final int        OP_LE_N = 4;
    private static final int        OP_GT_N = 5;
    private static final int        OP_GE_N = 6;
    private static final int        OP_CMP_N =7;
    private static final int        OP_EQ_S = 8;
    private static final int        OP_NE_S = 9;
    private static final int        OP_LT_S = 10;
    private static final int        OP_LE_S = 11;
    private static final int        OP_GT_S = 12;
    private static final int        OP_GE_S = 13;
    private static final int        OP_CMP_S = 14;
    private static final int        OP_IS = 15;

    private String      expression_;
    private Map         param_;
    private Function    function_;
    private boolean     ignoreCase_;
    private int         len_;
    private int         pos_;


    /*
     * constuctors
     */

    public ScriptExpression()
    {
        this(false);
    }


    public ScriptExpression(boolean ignoreCase)
    {
        ignoreCase_ = ignoreCase;
    }


    /*
     * static methods
     */

    public static String toString(Object value)
    {
        if (value == null) {
            return "";
        } else if (value instanceof Boolean) {
            return (((Boolean)value).booleanValue() ? "1" : "0");
        } else {
            return String.valueOf(value);
        }
    }


    public static int toNumber(Object value)
    {
        return toNumber(value, 0);
    }


    public static int toNumber(Object value, int def)
    {
        if (value == null) {
            return 0;
        } else if (value instanceof Number) {
            return ((Number)value).intValue();
        } else if (value instanceof Boolean) {
            return toNumber(((Boolean)value).booleanValue());
        } else {
            try {
                return Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException ex) {
                return def;
            }
        }
    }


    public static int toNumber(boolean value)
    {
        if (value) {
            return 1;
        } else {
            return 0;
        }
    }


    public static boolean toBoolean(Object value)
    {
        if (value == null) {
            return false;
        } else if (value instanceof Number) {
            return (((Number)value).intValue() != 0);
        } else if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue();
        } else {
            String valueStr = String.valueOf(value);
            if (valueStr.equalsIgnoreCase("true")
            || valueStr.equalsIgnoreCase("yes")
            || valueStr.equalsIgnoreCase("on")) {
                return true;
            } else if (valueStr.equalsIgnoreCase("false")
            || valueStr.equalsIgnoreCase("no")
            || valueStr.equalsIgnoreCase("off")) {
                return false;
            } else {
                try {
                    return (Integer.parseInt(String.valueOf(value)) != 0);
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
        }
    }


    /*
     * public scope methods
     */

    public Object evaluate(String expression)
        throws IllegalArgumentException
    {
        return evaluate(expression, null, null);
    }


    public Object evaluate(String expression, Map param)
        throws IllegalArgumentException
    {
        return evaluate(expression, param, null);
    }


    public Object evaluate(String expression, Map param, Function function)
        throws IllegalArgumentException
    {
        expression_ = expression;
        len_ = expression_.length();
        pos_ = 0;
        param_ = param;
        function_ = function;

        Object value = evaluateExpression();
        if (getNotSpaceChar() != -1) {
            throw new IllegalArgumentException(
                "Syntax error: " + getCurrentExpression());
        }

        return value;
    }


    public Object evaluateSafely(String expression, Map param,
        Function function, Object def)
    {
        Object value = def;
        try {
            value = evaluate(expression, param, function);
        } catch (Throwable t) {
            ;
        }
        return value;
    }


    public boolean evaluateIf(String expression)
        throws IllegalArgumentException
    {
        return evaluateIf(expression, null, null);
    }


    public boolean evaluateIf(String expression, Map param)
        throws IllegalArgumentException
    {
        return evaluateIf(expression, param, null);
    }


    public boolean evaluateIf(String expression, Map param, Function function)
        throws IllegalArgumentException
    {
        return toBoolean(evaluate(expression, param, function));
    }


    public boolean evaluateIfSafely(String expression, boolean def)
    {
        return evaluateIfSafely(expression, null, null, def);
    }


    public boolean evaluateIfSafely(String expression, Map param, boolean def)
    {
        return evaluateIfSafely(expression, param, null, def);
    }


    public boolean evaluateIfSafely(String expression, Map param,
        Function function, boolean def)
    {
        boolean value = def;
        try {
            value = evaluateIf(expression, param, function);
        } catch (Throwable t) {
            ;
        }
        return value;
    }


    public Object execute(String name, Object[] args)
        throws IllegalArgumentException, NoSuchMethodException
    {
        if (name.equals("endsWith")) {
            if (args.length != 2) {
                throw new IllegalArgumentException("Argument count mismatch: "
                    + args.length);
            }
            String str = toString(args[0]);
            String arg = toString(args[1]);
            try {
                return Boolean.valueOf(str.endsWith(arg));
            } catch (Throwable t) {
                IllegalArgumentException ex = new IllegalArgumentException();
                ex.initCause(t);
                throw ex;
            }
        } else if (name.equals("indexOf")) {
            if (args.length != 2) {
                throw new IllegalArgumentException("Argument count mismatch: "
                    + args.length);
            }
            String str = toString(args[0]);
            String arg = toString(args[1]);
            try {
                return new Integer(str.indexOf(arg));
            } catch (Throwable t) {
                IllegalArgumentException ex = new IllegalArgumentException();
                ex.initCause(t);
                throw ex;
            }
        } else if (name.equals("startsWith")) {
            if (args.length != 2) {
                throw new IllegalArgumentException("Argument count mismatch: "
                    + args.length);
            }
            String str = toString(args[0]);
            String arg = toString(args[1]);
            try {
                return Boolean.valueOf(str.startsWith(arg));
            } catch (Throwable t) {
                IllegalArgumentException ex = new IllegalArgumentException();
                ex.initCause(t);
                throw ex;
            }
        } else if (name.equals("substring")) {
            if (args.length < 2 || args.length > 3) {
                throw new IllegalArgumentException("Argument count mismatch: "
                    + args.length);
            }
            String str = toString(args[0]);
            int begin = toNumber(args[1]);
            int end = (args.length == 3 ? toNumber(args[2]) : str.length());
            if (begin > end) {
                begin = end;
            }
            try {
                return str.substring(begin, end);
            } catch (Throwable t) {
                IllegalArgumentException ex = new IllegalArgumentException();
                ex.initCause(t);
                throw ex;
            }
        } else if (name.equals("toBoolean")) {
            if (args.length != 1) {
                throw new IllegalArgumentException("Argument count mismatch: "
                    + args.length);
            }
            try {
                return Boolean.valueOf(toBoolean(args[0]));
            } catch (Throwable t) {
                IllegalArgumentException ex = new IllegalArgumentException();
                ex.initCause(t);
                throw ex;
            }
        } else if (name.equals("toNumber")) {
            if (args.length < 1 || args.length > 2) {
                throw new IllegalArgumentException("Argument count mismatch: "
                    + args.length);
            }
            int def = (args.length == 2 ? toNumber(args[1]) : 0);
            try {
                return new Integer(toNumber(args[0], def));
            } catch (Throwable t) {
                IllegalArgumentException ex = new IllegalArgumentException();
                ex.initCause(t);
                throw ex;
            }
        } else if (name.equals("toString")) {
            if (args.length != 1) {
                throw new IllegalArgumentException("Argument count mismatch: "
                    + args.length);
            }
            try {
                return toString(args[0]);
            } catch (Throwable t) {
                IllegalArgumentException ex = new IllegalArgumentException();
                ex.initCause(t);
                throw ex;
            }
        } else {
            throw new NoSuchMethodException(name);
        }
    }


    /*
     * protected scope methods
     */

    protected Object evaluateNumber()
        throws IllegalArgumentException
    {
//      System.out.println("number: " + getCurrentExpression());

        int ch = getNotSpaceChar();
        if (ch < '0' || ch > '9') {
            throw new IllegalArgumentException(
                "Syntax error: " + getCurrentExpression());
        }

        StringBuffer sb = new StringBuffer();
        sb.append((char)ch);
        while ((ch = getChar()) != -1) {
            if (ch >= '0' && ch <= '9') {
                sb.append((char)ch);
            } else {
                back();
                break;
            }
        }

        return new Integer(sb.toString());
    }


    protected Object evaluateString()
        throws IllegalArgumentException
    {
//      System.out.println("string: " + getCurrentExpression());
        Object value = null;

        int quote = getNotSpaceChar();
        if (quote != '"' && quote != '\'') {
            throw new IllegalArgumentException(
                "Syntax error: " + getCurrentExpression());
        }

        StringBuffer sb = new StringBuffer();
        int stat = 0;
        int ch;
        while ((ch = getChar()) != -1) {
            if (stat == 0) {
                if (ch == quote) {
                    value = sb.toString();
                    break;
                } else if (ch == '\\') {
                    stat = 1;
                } else {
                    sb.append((char)ch);
                }
            } else {
                sb.append((char)ch);
                stat = 0;
            }
        }
        if (value == null) {
            throw new IllegalArgumentException(
                "Syntax error: " + getCurrentExpression());
        }

        return value;
    }


    protected Object evaluateVariable()
        throws IllegalArgumentException
    {
//      System.out.println("variable: " + getCurrentExpression());
        Object value = null;

        int ch = getNotSpaceChar();
        if (ch != '$') {
            throw new IllegalArgumentException(
                "Syntax error: " + getCurrentExpression());
        }
        ch = getChar();
        if (ch == '_' || ch >= 'a' && ch <= 'z'
        || ch >= 'A' && ch <= 'Z') {
            StringBuffer sb = new StringBuffer();
            sb.append((char)ch);
            while ((ch = getChar()) != -1) {
                if (ch == '.' || ch == '_' || ch >= 'a' && ch <= 'z'
                || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9'
                || ch == '[' || ch == ']') {
                    sb.append((char)ch);
                } else {
                    back();
                    break;
                }
            }
            String key = sb.toString();
            if (!key.equals("null")) {
                if (param_ != null) {
                    if (ignoreCase_) {
                        key = key.toLowerCase();
                    }
                    value = param_.get(key);
                }
            }
        } else {
            throw new IllegalArgumentException(
                "Syntax error: " + getCurrentExpression());
        }

        return value;
    }


    protected Object evaluateFunction()
        throws IllegalArgumentException
    {
//      System.out.println("function: " + getCurrentExpression());
        String name = null;
        Object[] args = null;

        int ch = getNotSpaceChar();
        if (ch == '_' || ch >= 'a' && ch <= 'z'
        || ch >= 'A' && ch <= 'Z') {
            StringBuffer sb = new StringBuffer();
            sb.append((char)ch);
            while ((ch = getChar()) != -1) {
                if (ch == '_' || ch >= 'a' && ch <= 'z'
                || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9') {
                    sb.append((char)ch);
                } else {
                    back();
                    break;
                }
            }
            name = sb.toString();
            if (name.equals("null")) {
                return null;
            }

            ch = getNotSpaceChar();
            if (ch != '(') {
                throw new IllegalArgumentException(
                    "Syntax error: " + getCurrentExpression());
            }

            List list = new ArrayList();
            ch = getNotSpaceChar();
            if (ch != ')') {
                back();
                while (true) {
                    list.add(evaluateExpression());
                    ch = getNotSpaceChar();
                    if (ch != ',') {
                        break;
                    }
                }
            }
            if (ch != ')') {
                throw new IllegalArgumentException(
                    "Syntax error: " + getCurrentExpression());
            }
            args = list.toArray();
        } else {
            throw new IllegalArgumentException(
                "Syntax error: " + getCurrentExpression());
        }

        if (function_ != null) {
            try {
                return function_.execute(name, args);
            } catch (NoSuchMethodException ex) {
                ;
            }
        }
        try {
            return execute(name, args);
        } catch (NoSuchMethodException ex) {
            IllegalArgumentException e = new IllegalArgumentException();
            e.initCause(ex);
            throw e;
        } 
    }


    protected Object evaluateLiteral()
        throws IllegalArgumentException
    {
//      System.out.println("literal: " + getCurrentExpression());
        Object value = null;

        int ch = getNotSpaceChar();
        if (ch == -1) {
            throw new IllegalArgumentException(
                "Syntax error: " + getCurrentExpression());
        } else if (ch >= '0' && ch <= '9') {
            back();
            value =  evaluateNumber();
        } else if (ch == '"' || ch == '\'') {
            back();
            value = evaluateString();
        } else if (ch == '(') {
            value = evaluateExpression();
            if (getNotSpaceChar() != ')') {
                throw new IllegalArgumentException(
                    "Syntax error: " + getCurrentExpression());
            }
        } else if (ch == '$') {
            back();
            value = evaluateVariable();
        } else {
            back();
            value = evaluateFunction();
        }

        return value;
    }


    protected Object evaluateNot()
        throws IllegalArgumentException
    {
//      System.out.println("not: " + getCurrentExpression());
        Object value = null;

        int ch = getNotSpaceChar();
        if (ch == -1) {
            throw new IllegalArgumentException(
                "Syntax error: " + getCurrentExpression());
        } else if (ch == '!') {
            value = new Integer(toNumber(!toBoolean(evaluateLiteral())));
        } else {
            back();
            value = evaluateLiteral();
        }

        return value;
    }


    protected Object evaluateSekiKou()
        throws IllegalArgumentException
    {
//      System.out.println("seki kou: " + getCurrentExpression());
        Object value = evaluateNot();
        int ch;
        while ((ch = getNotSpaceChar()) != -1) {
            if (ch != '*' && ch != '/') {
                back();
                break;
            }
            Object value2 = evaluateNot();
            if (ch == '*') {
                value = new Integer(toNumber(value) * toNumber(value2));
            } else if (ch == '/') {
                value = new Integer(toNumber(value) / toNumber(value2));
            } else {
                throw new RuntimeException("Can't happen!");
            }
        }

        return value;
    }


    protected Object evaluateWaKou()
        throws IllegalArgumentException
    {
//      System.out.println("wa kou: " + getCurrentExpression());
        int ch = getNotSpaceChar();
        if (ch != '+' && ch != '-') {
            back();
            ch = '+';
        }

        Object value = evaluateSekiKou();
        if (ch == '-') {
            value = new Integer(-1 * toNumber(value));
        }
            
        while ((ch = getNotSpaceChar()) != -1) {
            if (ch != '+' && ch != '-') {
                back();
                break;
            }
            Object value2 = evaluateSekiKou();
            if (ch == '+') {
                value = new Integer(toNumber(value) + toNumber(value2));
            } else if (ch == '-') {
                value = new Integer(toNumber(value) - toNumber(value2));
            } else {
                throw new RuntimeException("Can't happen!");
            }
        }

        return value;
    }


    protected Object evaluateCondition()
        throws IllegalArgumentException
    {
//      System.out.println("condition: " + getCurrentExpression());
        Object value = evaluateWaKou();
        int ch = getNotSpaceChar();
        if (ch != -1) {
            back();
            int operator = getOperator();
            if (operator != OP_NONE) {
                Object value2 = evaluateWaKou();
                switch (operator) {
                case OP_EQ_N:
                    value = Boolean.valueOf(
                        toNumber(value) == toNumber(value2));
                    break;
    
                case OP_NE_N:
                    value = Boolean.valueOf(
                        toNumber(value) != toNumber(value2));
                    break;
    
                case OP_LT_N:
                    value = Boolean.valueOf(
                        toNumber(value) < toNumber(value2));
                    break;
    
                case OP_LE_N:
                    value = Boolean.valueOf(
                        toNumber(value) <= toNumber(value2));
                    break;
    
                case OP_GT_N:
                    value = Boolean.valueOf(
                        toNumber(value) > toNumber(value2));
                    break;
    
                case OP_GE_N:
                    value = Boolean.valueOf(
                        toNumber(value) >= toNumber(value2));
                    break;
    
                case OP_CMP_N:
                    {
                        int cmp = toNumber(value) - toNumber(value2);
                        cmp = (cmp > 0 ? 1 : (cmp < 0 ? -1 : 0));
                        value = new Integer(cmp);
                    }
                    break;
    
                case OP_EQ_S:
                    value = Boolean.valueOf(
                        toString(value).equals(toString(value2)));
                    break;
    
                case OP_NE_S:
                    value = Boolean.valueOf(
                        !toString(value).equals(toString(value2)));
                    break;
    
                case OP_LE_S:
                    value = Boolean.valueOf(
                        toString(value).compareTo(toString(value2)) <= 0);
                    break;
    
                case OP_LT_S:
                    value = Boolean.valueOf(
                        toString(value).compareTo(toString(value2)) < 0);
                    break;
    
                case OP_GE_S:
                    value = Boolean.valueOf(
                        toString(value).compareTo(toString(value2)) >= 0);
                    break;
    
                case OP_GT_S:
                    value = Boolean.valueOf(
                        toString(value).compareTo(toString(value2)) > 0);
                    break;
    
                case OP_CMP_S:
                    value = new Integer(
                        toString(value).compareTo(toString(value2)));
                    break;
    
                case OP_IS:
                    value = Boolean.valueOf(value == value2);
                    break;
    
                default:
                    throw new IllegalArgumentException(
                        "Syntax error: " + getCurrentExpression());
                }
            }
        }

        return value;
    }


    protected Object evaluateAnd()
        throws IllegalArgumentException
    {
//      System.out.println("and: " + getCurrentExpression());
        Object value = evaluateCondition();
        int ch;
        while ((ch = getNotSpaceChar()) != -1) {
            if (ch == '&') {
                ch = getNotSpaceChar();
                if (ch == '&') {
                    Object value2 = evaluateCondition();
                    value = Boolean.valueOf(
                        toBoolean(value) && toBoolean(value2));
                } else {
                    throw new IllegalArgumentException(
                        "Syntax error: " + getCurrentExpression());
                }
            } else {
                back();
                break;
            }
        }

        return value;
    }


    protected Object evaluateOr()
        throws IllegalArgumentException
    {
//      System.out.println("or: " + getCurrentExpression());
        Object value = evaluateAnd();
        int ch;
        while ((ch = getNotSpaceChar()) != -1) {
            if (ch == '|') {
                ch = getNotSpaceChar();
                if (ch == '|') {
                    Object value2 = evaluateAnd();
                    value = Boolean.valueOf(
                        toBoolean(value) || toBoolean(value2));
                } else {
                    throw new IllegalArgumentException(
                        "Syntax error: " + getCurrentExpression());
                }
            } else {
                back();
                break;
            }
        }

        return value;
    }


    protected Object evaluateThree()
        throws IllegalArgumentException
    {
//      System.out.println("three: " + getCurrentExpression());
        Object value = evaluateOr();
        int ch = getNotSpaceChar();
        if (ch != -1) {
            if (ch == '?') {
                Object value2 = evaluateOr();
                ch = getNotSpaceChar();
                if (ch != ':') {
                    throw new IllegalArgumentException(
                        "Syntax error: " + getCurrentExpression());
                }
                Object value3 = evaluateOr();
                value = toBoolean(value) ? value2 : value3;
            } else {
                back();
            }
        }

        return value;
    }


    protected Object evaluateExpression()
        throws IllegalArgumentException
    {
        return evaluateThree();
    }


    /*
     * private scope methods
     */

    private int getOperator()
        throws IllegalArgumentException
    {
        int operator = OP_NONE;
        int pos = pos_;

        int ch = getNotSpaceChar();
        if (ch == '=') {
            ch = getChar();
            if (ch == '=') {
                operator = OP_EQ_N;
            }
        } else if (ch == '!') {
            ch = getChar();
            if (ch == '=') {
                operator = OP_NE_N;
            }
        } else if (ch == '<') {
            ch = getChar();
            if (ch == '=') {
                ch = getChar();
                if (ch == '>') {
                    operator = OP_CMP_N;
                } else {
                    back();
                    operator = OP_LE_N;
                }
            } else {
                back();
                operator = OP_LT_N;
            }
        } else if (ch == '>') {
            ch = getChar();
            if (ch == '=') {
                operator = OP_GE_N;
            } else {
                back();
                operator = OP_GT_N;
            }
        } else if (ch == 'e') {
            ch = getChar();
            if (ch == 'q') {
                operator = OP_EQ_S;
            }
        } else if (ch == 'n') {
            ch = getChar();
            if (ch == 'e') {
                operator = OP_NE_S;
            }
        } else if (ch == 'l') {
            ch = getChar();
            if (ch == 'e') {
                operator = OP_LE_S;
            } else if (ch == 't') {
                operator = OP_LT_S;
            }
        } else if (ch == 'g') {
            ch = getChar();
            if (ch == 'e') {
                operator = OP_GE_S;
            } else if (ch == 't') {
                operator = OP_GT_S;
            }
        } else if (ch == 'c') {
            ch = getChar();
            if (ch == 'm') {
                ch = getChar();
                if (ch == 'p') {
                    operator = OP_CMP_S;
                }
            }
        } else if (ch == 'i') {
            ch = getChar();
            if (ch == 's') {
                operator = OP_IS;
            }
        }

        if (operator == OP_NONE) {
            pos_ = pos;
        }

        return operator;
    }


    private int getChar()
    {
        if (pos_ >= len_) {
            return -1;
        } else {
            return expression_.charAt(pos_++);
        }
    }


    private int getNotSpaceChar()
    {
        int ch = -1;
        while (pos_ < len_) {
            ch = expression_.charAt(pos_++);
            if (ch != ' ') {
                break;
            }
        }

        return ch;
    }


    private void back()
    {
        if (--pos_ < 0) {
            throw new RuntimeException();
        }
    }


    private String getCurrentExpression()
    {
        return expression_.substring(pos_);
    }


    /*
     * inner interfaces
     */

    public static interface Function
    {
        Object execute(String name, Object[] args)
            throws IllegalArgumentException, NoSuchMethodException;
    }


/*
    public static void main(String[] args)
        throws Exception
    {
        BufferedReader in
            = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(new Expression().evaluate(line, null));
        }
    }
*/
}
