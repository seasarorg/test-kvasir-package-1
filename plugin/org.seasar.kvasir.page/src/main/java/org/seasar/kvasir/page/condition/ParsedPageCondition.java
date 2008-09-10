package org.seasar.kvasir.page.condition;

/**
 * {@link PageConditionParser}によってパースされた後の検索条件オブジェクトです。
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 *
 * @author YOKOTA Takehiko
 * @see PageConditionParser
 */
public class ParsedPageCondition
{
    private String[] columns_;

    private String[] tables_;

    private String base_;

    private Object[] parameters_;


    public ParsedPageCondition(String[] columns, String[] tables, String base,
        Object[] parameters)
    {
        columns_ = columns;
        tables_ = tables;
        base_ = base;
        parameters_ = parameters;
    }


    public String[] getColumns()
    {
        return columns_;
    }


    public String[] getTables()
    {
        return tables_;
    }


    public String getBase()
    {
        return base_;
    }


    public Object[] getParameters()
    {
        return parameters_;
    }
}
