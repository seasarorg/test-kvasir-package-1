package org.seasar.kvasir.page.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


/**
 * 詳細な検索条件式を表すクラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class Formula
    implements Cloneable
{
    private String base_;

    private Object[] params_;

    private boolean[] setParams_;

    private boolean freeze_;


    /*
     * constructors
     */

    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param base 条件式。パラメータの値に置き換える部分には「?」を埋め込んでおきます。
     */
    public Formula(String base)
    {
        base_ = base.trim();

        int pre = 0;
        int idx;
        int cnt = 0;
        while ((idx = base_.indexOf("?", pre)) >= 0) {
            cnt++;
            pre = idx + 1;
        }

        params_ = new Object[cnt];
        setParams_ = new boolean[cnt];
    }


    /*
     * static methods
     */

    /**
     * 指定された文字列をSQLに含めることのできる文字列リテラルの形式にクオートします。
     * <p>nullが指定された場合はnullを返します。
     * </p>
     * 
     * @param literal 文字列。
     * @return クオートした文字列。
     */
    public static String quoteString(String literal)
    {
        if (literal == null) {
            return null;
        }

        StringTokenizer st = new StringTokenizer(literal, "'", true);
        StringBuilder result = new StringBuilder("'");
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken();
            if (tkn.equals("'")) {
                result.append("''");
            } else {
                result.append(tkn);
            }
        }
        result.append("'");

        return result.toString();
    }


    /**
     * 指定された式文字列からFormulaオブジェクトを構築します。
     * <p>式中、SQLの文字列リテラルになっている部分は自動的にプレースホルダ「？」に置き換えられ、
     * 文字列リテラルの値がパラメータとして保持されます。
     * </p>
     * <p>式文字列としてnullが指定された場合はnullを返します。
     * </p>
     * 
     * @param formulaString 式文字列。
     * @return 構築したFormulaオブジェクト。
     */
    public static Formula newInstance(String formulaString)
    {
        if (formulaString == null) {
            return null;
        }

        List<String> list = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(formulaString, "'", true);
        StringBuilder sb = new StringBuilder();
        boolean inQuote = false;
        int quoteCount = 0;
        StringBuilder parameterSb = null;
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken();
            if (!inQuote) {
                if (tkn.equals("'")) {
                    sb.append("?");
                    inQuote = true;
                    quoteCount = 0;
                    parameterSb = new StringBuilder();
                } else {
                    sb.append(tkn);
                }
            } else if (quoteCount == 0) {
                if (tkn.equals("'")) {
                    quoteCount++;
                } else {
                    parameterSb.append(tkn);
                }
            } else {
                if (tkn.equals("'")) {
                    parameterSb.append("'");
                    quoteCount = 0;
                } else {
                    list.add(parameterSb.toString());
                    inQuote = false;
                    sb.append(tkn);
                }
            }
        }
        if (inQuote) {
            if (quoteCount == 0) {
                throw new IllegalArgumentException("Syntax error: "
                    + formulaString);
            }
            list.add(parameterSb.toString());
        }

        Formula formula = new Formula(sb.toString());
        int size = list.size();
        int idx = 1;
        for (int i = 0; i < size; i++) {
            formula.setString(idx++, list.get(i));
        }

        return formula;
    }


    /**
     * 指定された2つの式を連結します。
     * <p>2つの式は単にそのまま連結されます。例えば「A=?」と「B=?」という式は「A=?B=?」になります。
     * AND条件で連結したい場合は{@link #intersection(Formula, Formula)}を使って下さい。
     * </p>
     * 
     * @param o1 1つめの式。
     * @param o2 2つめの式。
     * @return 連結後の式。
     * @see #intersection(Formula, Formula)
     */
    public static Formula append(Formula o1, Formula o2)
    {
        return append(new Formula[] { o1, o2 });
    }


    /**
     * 指定された複数の式を連結します。
     * <p>式は単にそのまま連結されます。例えば「A=?」と「B=?」という式は「A=?B=?」になります。
     * AND条件で連結したい場合は{@link #intersection(Formula[])}を使って下さい。
     * </p>
     * 
     * @param formulas 式の配列。nullを指定してはいけません。配列の要素がnullであるものや
     * 式が空文字列であるものは無視されます。
     * @return 連結後の式。
     * @see #intersection(Formula[])
     */
    public static Formula append(Formula[] formulas)
    {
        List<Formula> list = new ArrayList<Formula>(formulas.length);
        int paramsCount = 0;
        for (int i = 0; i < formulas.length; i++) {
            if ((formulas[i] != null) && (formulas[i].length() > 0)) {
                list.add(formulas[i]);
                paramsCount += formulas[i].getParametersCount();
            }
        }
        int n = list.size();
        if (n == 0) {
            return new Formula("");
        } else if (n == 1) {
            return list.get(0);
        } else {
            StringBuilder sb = new StringBuilder();
            Object[] params = new Object[paramsCount];
            int idx = 0;
            for (int i = 0; i < n; i++) {
                Formula formula = list.get(i);
                sb.append(formula.getBase());
                int cnt = formula.getParametersCount();
                for (int j = 1; j <= cnt; j++) {
                    params[idx++] = formula.getParameter(j);
                }
            }
            Formula formula = new Formula(sb.toString());
            idx = 0;
            for (int i = 1; i <= paramsCount; i++) {
                formula.setObject(i, params[idx++]);
            }
            return formula;
        }
    }


    /**
     * 指定された2つの式をAND条件で連結します。
     * <p>例えば「A=?」と「B=?」という式は「(A=?) AND (B=?)」になります。
     * </p>
     * 
     * @param o1 1つめの式。
     * @param o2 2つめの式。
     * @return 連結後の式。
     */
    public static Formula intersection(Formula o1, Formula o2)
    {
        return intersection(new Formula[] { o1, o2 });
    }


    /**
     * 指定された複数の式をAND条件で連結します。
     * <p>例えば「A=?」と「B=?」という式は「(A=?) AND (B=?)」になります。
     * </p>
     * 
     * @param formulas 式の配列。nullを指定してはいけません。配列の要素がnullであるものや
     * 式が空文字列であるものは無視されます。
     * @return 連結後の式。
     */
    public static Formula intersection(Formula[] formulas)
    {
        List<Formula> list = new ArrayList<Formula>(formulas.length);
        int paramsCount = 0;
        for (int i = 0; i < formulas.length; i++) {
            if ((formulas[i] != null) && (formulas[i].length() > 0)) {
                list.add(formulas[i]);
                paramsCount += formulas[i].getParametersCount();
            }
        }
        int n = list.size();
        if (n == 0) {
            return new Formula("");
        } else if (n == 1) {
            return (Formula)list.get(0).clone();
        } else {
            StringBuilder sb = new StringBuilder();
            Object[] params = new Object[paramsCount];
            int idx = 0;
            for (int i = 0; i < n; i++) {
                Formula formula = list.get(i);
                if (i > 0) {
                    sb.append(" AND ");
                }
                sb.append("(");
                sb.append(formula.getBase());
                sb.append(")");
                int cnt = formula.getParametersCount();
                for (int j = 1; j <= cnt; j++) {
                    params[idx++] = formula.getParameter(j);
                }
            }
            Formula formula = new Formula(sb.toString());
            idx = 0;
            for (int i = 1; i <= paramsCount; i++) {
                formula.setObject(i, params[idx++]);
            }
            return formula;
        }
    }


    /*
     * public scope methods
     */

    @Override
    public Object clone()
    {
        try {
            Formula formula = (Formula)super.clone();

            if (params_ != null) {
                Object[] params = new Object[params_.length];
                for (int i = 0; i < params_.length; i++) {
                    params[i] = params_[i];
                }
                formula.params_ = params;
            }

            if (setParams_ != null) {
                boolean[] setParams = new boolean[setParams_.length];
                for (int i = 0; i < setParams_.length; i++) {
                    setParams[i] = setParams_[i];
                }
                formula.setParams_ = setParams;
            }

            formula.freeze_ = false;

            return formula;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }

        if ((obj != null) && (obj.getClass() == getClass())) {
            Formula formula = (Formula)obj;
            if (base_ == null) {
                if (formula.base_ != null) {
                    return false;
                }
            } else if (!base_.equals(formula.base_)) {
                return false;
            }

            if (params_ == null) {
                if (formula.params_ != null) {
                    return false;
                }
            } else if ((formula.params_ == null)
                || (params_.length != formula.params_.length)) {
                return false;
            } else {
                for (int i = 0; i < params_.length; i++) {
                    if (params_[i] == null) {
                        if (formula.params_[i] != null) {
                            return false;
                        }
                    } else if (!params_[i].equals(formula.params_[i])) {
                        return false;
                    }
                }
            }

            if (setParams_ == null) {
                if (formula.setParams_ != null) {
                    return false;
                }
            } else if ((formula.setParams_ == null)
                || (setParams_.length != formula.setParams_.length)) {
                return false;
            } else {
                for (int i = 0; i < setParams_.length; i++) {
                    if (setParams_[i] != formula.setParams_[i]) {
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        int pre = 0;
        int ptr = 0;
        int idx = 0;
        while ((ptr = base_.indexOf("?", pre)) >= 0) {
            if (pre < ptr) {
                sb.append(base_.substring(pre, ptr));
            }
            if ((idx >= setParams_.length) || !setParams_[idx]) {
                // エラーであることを埋め込む。
                sb.append("****UNSET PARAMETER****");
            } else {
                if (params_[idx] == null) {
                    sb.append("NULL");
                } else if (params_[idx] instanceof String) {
                    sb.append(quoteString((String)params_[idx]));
                } else {
                    sb.append(params_[idx]);
                }
            }
            idx++;
            pre = ptr + 1;
        }
        if (pre < base_.length()) {
            sb.append(base_.substring(pre));
        }

        return sb.toString();
    }


    /**
     * 式の長さを返します。
     * <p>式の長さは、式中のプレースホルダをそのままにした状態
     * （プレースホルダにパラメータを埋め込まない状態）での長さです。
     * 
     * @return 式の長さ。
     */
    public int length()
    {
        return base_.length();
    }


    /**
     * プレースホルダが埋め込まれた式文字列を返します。
     * 
     * @return プレースホルダが埋め込まれた式文字列。
     */
    public String getBase()
    {
        return base_;
    }


    /**
     * パラメータの個数を返します。
     * 
     * @return パラメータの個数。
     */
    public int getParametersCount()
    {
        return params_.length;
    }


    /**
     * パラメータを返します。
     * 
     * @return パラメータ。
     */
    public Object[] getParameters()
    {
        return params_;
    }


    /**
     * 指定されたインデックス番号に対応するパラメータを返します。
     * <p>インデックス番号は1オリジンです。
     * </p>
     * 
     * @param idx インデックス番号。インデックス番号は1オリジンです。
     * @return 指定されたインデックス番号に対応するパラメータ。
     */
    public Object getParameter(int idx)
    {
        if ((idx < 1) || (idx > params_.length)) {
            throw new IllegalArgumentException("Index out of range: " + idx
                + " (" + params_.length + ")");
        }

        return params_[idx - 1];
    }


    /**
     * 式中の全てのプレースホルダに対応するパラメータが設定済みかどうか検証します。
     * 
     * @return 式中の全てのプレースホルダに対応するパラメータが設定済みかどうか。
     */
    public boolean validateParameters()
    {
        for (int i = 0; i < params_.length; i++) {
            if (!setParams_[i]) {
                return false;
            }
        }

        return true;
    }


    /**
     * 指定されたインデックス番号に対応するパラメータの値を設定します。
     * 
     * @param idx インデックス番号。インデックス番号は1オリジンです。
     * @param value 値。
     * @return このオブジェクト。
     */
    public Formula setInt(int idx, int value)
    {
        return setObject(idx, Integer.valueOf(value));
    }


    /**
     * 指定されたインデックス番号に対応するパラメータの値を設定します。
     * 
     * @param idx インデックス番号。インデックス番号は1オリジンです。
     * @param value 値。
     * @return このオブジェクト。
     */
    public Formula setString(int idx, String value)
    {
        return setObject(idx, value);
    }


    /**
     * 指定されたインデックス番号に対応するパラメータの値を設定します。
     * 
     * @param idx インデックス番号。インデックス番号は1オリジンです。
     * @param value 値。
     * @return このオブジェクト。
     */
    public Formula setDouble(int idx, double value)
    {
        return setObject(idx, Double.valueOf(value));
    }


    /**
     * 指定されたインデックス番号に対応するパラメータの値としてnullを設定します。
     * 
     * @param idx インデックス番号。インデックス番号は1オリジンです。
     * @return このオブジェクト。
     */
    public Formula setNull(int idx)
    {
        return setObject(idx, null);
    }


    /**
     * 指定されたインデックス番号に対応するパラメータの値を設定します。
     * 
     * @param idx インデックス番号。インデックス番号は1オリジンです。
     * @param value 値。
     * @return このオブジェクト。
     */
    public Formula setObject(int idx, Object value)
    {
        if (freeze_) {
            throw new IllegalStateException("This object is freezed");
        }
        if ((idx < 1) || (idx > params_.length)) {
            throw new IllegalArgumentException("Index out of range: " + idx
                + " (" + params_.length + ")");
        }

        int i = idx - 1;
        params_[i] = value;
        setParams_[i] = true;

        return this;
    }


    /**
     * このオブジェクトを複数スレッドから利用できるよう、以降変更が行なえないようにします。
     * <p>このメソッドを呼び出した後は複数スレッドから安全に利用できるようになりますが、
     * 内容の変更はできなくなります。
     * </p>
     * 
     * @return このオブジェクト。
     */
    public Formula freeze()
    {
        freeze_ = true;

        return this;
    }


    /**
     * 変更が行なえない状態になっているかどうかを返します。
     * 
     * @return 変更が行なえない状態になっているかどうか。
     * @see #freeze()
     */
    public boolean isFreezed()
    {
        return freeze_;
    }
}
