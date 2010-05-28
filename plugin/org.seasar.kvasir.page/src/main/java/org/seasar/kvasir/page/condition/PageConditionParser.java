package org.seasar.kvasir.page.condition;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.cms.beantable.Pair;
import org.seasar.cms.database.identity.Identity;
import org.seasar.kvasir.base.dao.QueryHandler;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PrivilegeLevel;
import org.seasar.kvasir.page.dao.TableConstants;
import org.seasar.kvasir.page.dao.WhereTokenizer;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.PropertyUtils;


/**
 * {@link PageCondition}からSQLを構築するために{@link PageCondition}を解析するクラスです。
 * 
 * @author skirnir
 * @see PageCondition
 */
public class PageConditionParser
{
    static interface Function
    {
        Function FUNCTION_CURRENT_DATE_STRING = new Function() {
            public String get()
            {
                return "'"
                    + new SimpleDateFormat("yyyy-MM-dd").format(new Date())
                    + "'";
            }
        };

        Function FUNCTION_CURRENT_TIME_STRING = new Function() {
            public String get()
            {
                return "'"
                    + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "'";
            }
        };

        Function FUNCTION_CURRENT_DATETIME_STRING = new Function() {
            public String get()
            {
                return "'"
                    + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(new Date()) + "'";
            }
        };


        String get();
    }

    static class StringFunction
        implements Function
    {
        static StringFunction FUNCTION_TRUE = new StringFunction("1");

        static StringFunction FUNCTION_FALSE = new StringFunction("0");

        private String string;


        StringFunction(String string)
        {
            this.string = string;
        }


        public String get()
        {
            return string;
        }
    }


    private final Set<String> reservedSet_;

    private final Set<String> pageFieldSet_;

    private final Set<String> pageAliasSet_;

    private final Map<String, Function> constantMap_;

    private final PageType[] pageTypes_;

    private Identity identity_;


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param identity 使用しているデータベースを表わすIdentityオブジェクト。
     * nullを指定してはけません。
     * @param plugin PagePluginオブジェクト。
     * nullを指定してはけません。
     */
    public PageConditionParser(Identity identity, PagePlugin plugin)
    {
        Set<String> set = new HashSet<String>();
        set.add("and");
        set.add("or");
        set.add("not");
        set.add("like");
        set.add("in");
        set.add("exist");
        set.add("null");
        set.add("escape");
        reservedSet_ = Collections.unmodifiableSet(set);

        set = new HashSet<String>();
        for (int i = 0; i < PageCondition.FIELDS.length; i++) {
            set.add(PageCondition.FIELDS[i].toLowerCase());
        }
        pageFieldSet_ = Collections.unmodifiableSet(set);

        set = new HashSet<String>();
        for (int i = 0; i < PageCondition.ALIASES.length; i++) {
            set.add(PageCondition.ALIASES[i].toLowerCase());
        }
        pageAliasSet_ = Collections.unmodifiableSet(set);

        Map<String, Function> map = new HashMap<String, Function>();
        map.put("true", StringFunction.FUNCTION_TRUE);
        map.put("false", StringFunction.FUNCTION_FALSE);
        map.put("current_date_string", Function.FUNCTION_CURRENT_DATE_STRING);
        map.put("current_time_string", Function.FUNCTION_CURRENT_TIME_STRING);
        map.put("current_datetime_string",
            Function.FUNCTION_CURRENT_DATETIME_STRING);
        constantMap_ = Collections.unmodifiableMap(map);

        pageTypes_ = plugin.getPageTypes();

        identity_ = identity;
    }


    /**
     * PageConditionが持つ各情報を解析して、
     * SQL SELECT文を構築するための元となる{@link ParsedPageCondition}オブジェクトを作成します。
     * 
     * @param qh SQLを構築するためのQueryHandlerオブジェクト。
     * nullを指定してはいけません。
     * @param columns 取得するカラム名の配列。
     * nullを指定してはいけません。
     * @param type 取得するページのタイプ。
     * nullを指定することができます。
     * @param includeConcealed 不可視状態のページを取得するかどうか。
     * @param onlyListed  一覧表示に含めるページだけを取得するかどうか。
     * @param currentDate 可視状態を判定する基準となる日時。
     * nullを指定すると現在日時について判定されます。
     * @param heimId HeimのID。
     * @param user アクセスユーザ。この引数がnullでprivilegeもnullの場合、
     * 権限によらずページを取得するようなSQLが構築されます。
     * この引数が非nullの場合、指定したユーザについて指定した権限でアクセス可能なページだけを
     * 取得するようなSQLが構築されます。
     * @param privilege 権限。
     * userとともに使用されます。userが非nullの時はこの引数はnullであってはいけません。
     * userがnullの時はこの引数はnullである必要があります。
     * @param option オプション。nullを指定することもできます。
     * @param orders 取得の際の並び順。nullや空の配列を指定すると並び順は不定となります。
     * @return 構築した{@link ParsedPageCondition}オブジェクト。
     * nullが返されることはありません。
     * @throws IllegalArgumentException 引数が不正であった場合。
     */
    public ParsedPageCondition parse(QueryHandler qh, String[] columns,
        String type, boolean includeConcealed, boolean onlyListed,
        Date currentDate, int heimId, User user, Privilege privilege,
        Formula option, Order[] orders)
        throws IllegalArgumentException
    {
        StringBuilder sb = new StringBuilder();
        List<Object> list = new ArrayList<Object>();
        List<String> columnList = new ArrayList<String>(Arrays.asList(columns));
        Set<String> groupBySet = new LinkedHashSet<String>(
            toGroupByColumnList(columns));
        List<String> tableList = new ArrayList<String>();
        tableList.add(TableConstants.TABLE_PAGE);
        Map<String, String> tableByPropertyMap = new HashMap<String, String>();
        int propertyCount = 0;

        if (user == null && privilege != null || user != null
            && privilege == null) {
            throw new IllegalArgumentException(
                "Please specify both user and privilege, or specify neither user nor privilege: user="
                    + user + ", privilege=" + privilege);
        }

        boolean privilegeSpecified = (privilege != null && user != null && !user
            .isAdministrator());
        String maxClause = null;
        if (privilegeSpecified) {
            maxClause = "MAX(" + TableConstants.TABLE_PERMISSION + "."
                + TableConstants.COL_PERMISSION_LEVEL + ")";
            columnList.add(maxClause);
        }

        if (option != null && option.length() > 0) {
            if (!option.validateParameters()) {
                throw new IllegalArgumentException("option is not filled: "
                    + option.toString());
            }

            String base = option.getBase();
            WhereTokenizer wt = new WhereTokenizer(base);
            Object[] params = option.getParameters();
            String propTable = null;
            int idx = 0;
            int stat = 0;
            while (wt.hasMoreTokens()) {
                WhereTokenizer.Token tkn = wt.nextToken();
                int tp = tkn.getType();
                String value = tkn.getValue();

                if (stat == 0) {
                    switch (tp) {
                    case WhereTokenizer.TYPE_PARAM:
                        // '?'。
                        sb.append('?');
                        list.add(params[idx++]);
                        break;

                    case WhereTokenizer.TYPE_SPACE:
                        // ' '。
                    case WhereTokenizer.TYPE_NUMBER:
                        // 数値。
                    case WhereTokenizer.TYPE_OTHER:
                        // その他。
                        sb.append(value);
                        break;

                    case WhereTokenizer.TYPE_STRING:
                        // 文字列。
                        // XXX HSQLDBでESCAPE '|'等を
                        // ESACPE ?としてしまうとPreparedStatement
                        // をうまく構築できないのでこうしている。
                        // もっとうまくできないか…。
                        if ((value.indexOf('\'') < 0)
                            && (value.indexOf('\\') < 0)) {
                            sb.append('\'').append(value).append('\'');
                        } else {
                            sb.append('?');
                            list.add(value);
                        }
                        break;

                    case WhereTokenizer.TYPE_SYMBOL:
                        // シンボル。
                        if (value.equals("property")) {
                            propTable = TableConstants.TABLE_PROPERTY
                                + propertyCount;
                            tableList.add(TableConstants.TABLE_PROPERTY
                                + " AS " + propTable);
                            propertyCount++;

                            sb.append(propTable).append(".name=? AND ").append(
                                propTable).append(".value");
                            stat = 1;
                        } else {
                            String converted = convert(sb, null, value);
                            if (converted != null) {
                                // プロパティに変換された。
                                propTable = TableConstants.TABLE_PROPERTY
                                    + propertyCount;
                                tableList.add(TableConstants.TABLE_PROPERTY
                                    + " AS " + propTable);
                                tableByPropertyMap.put(value, propTable);
                                propertyCount++;

                                sb.append(propTable).append(".").append(
                                    TableConstants.COL_PROPERTY_NAME).append(
                                    "=? AND ").append(propTable).append(".")
                                    .append(TableConstants.COL_PROPERTY_VALUE);
                                list.add(converted);
                                stat = 4;
                            }
                        }
                        break;

                    default:
                        throw new RuntimeException("LOGIC ERROR");
                    }
                } else if (stat == 1) {
                    // property

                    if ((tp == WhereTokenizer.TYPE_OTHER) && value.equals("(")) {
                        // property(
                        stat = 2;
                    } else {
                        throw new IllegalArgumentException("Syntax error at: "
                            + value + " in option: " + base);
                    }
                } else if (stat == 2) {
                    // property(

                    if ((tp == WhereTokenizer.TYPE_STRING)
                        || (tp == WhereTokenizer.TYPE_PARAM)) {
                        // property('a.b.c'
                        String name = value;
                        if (tp == WhereTokenizer.TYPE_PARAM) {
                            name = PropertyUtils.toStringOrNull(params[idx++]);
                        }
                        list.add(name);
                        stat = 3;
                    } else {
                        throw new IllegalArgumentException("Syntax error at: "
                            + value + " in option: " + base);
                    }
                } else if (stat == 3) {
                    // property('a.b.c'

                    if ((tp == WhereTokenizer.TYPE_OTHER)
                        && value.startsWith(")")) {
                        // property('a.b.c')
                        sb.append(value.substring(1));
                        stat = 4;
                    } else {
                        throw new IllegalArgumentException("Syntax error at: "
                            + value + " in option: " + base);
                    }
                } else if (stat == 4) {
                    // property('a.b.c') または property('a.b.c')=

                    if (tp == WhereTokenizer.TYPE_SPACE
                        || tp == WhereTokenizer.TYPE_OTHER
                        || tp == WhereTokenizer.TYPE_SYMBOL) {
                        // property('a.b.c')=...
                        if (tp == WhereTokenizer.TYPE_SYMBOL) {
                            convert(sb, null, value);
                        } else {
                            sb.append(value);
                        }
                    } else if (tp == WhereTokenizer.TYPE_STRING
                        || tp == WhereTokenizer.TYPE_PARAM) {
                        // XXX 複雑になるので今のところinはサポートしていない。

                        sb.append("? AND ").append(TableConstants.TABLE_PAGE)
                            .append(".").append(TableConstants.COL_PAGE_ID)
                            .append("=").append(propTable).append(".").append(
                                TableConstants.COL_PROPERTY_PAGEID);
                        if (tp == WhereTokenizer.TYPE_PARAM) {
                            list.add(params[idx++]);
                        } else if (tp == WhereTokenizer.TYPE_STRING) {
                            // property('a.b.c')='value'
                            list.add(value);
                        } else {
                            throw new RuntimeException("LOGIC ERROR");
                        }
                        stat = 0;
                    } else {
                        throw new IllegalArgumentException("Syntax error at: "
                            + value + " in option: " + base);
                    }
                } else {
                    throw new RuntimeException("LOGIC ERROR");
                }
            }
        }

        if (type != null) {
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            convert(sb, null, PageCondition.FIELD_TYPE);
            sb.append("=?");
            list.add(type);
        }

        if (!includeConcealed) {
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            sb.append("(? BETWEEN ");
            convert(sb, null, PageCondition.FIELD_REVEALDATE);
            sb.append(" AND ");
            convert(sb, null, PageCondition.FIELD_CONCEALDATE);
            sb.append(")");
            list.add(new Timestamp((currentDate != null ? currentDate
                : new Date()).getTime()));
        }

        if (onlyListed) {
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            convert(sb, null, PageCondition.FIELD_LISTING);
            sb.append("=1");
        }

        if (sb.length() > 0) {
            sb.append(" AND ");
        }
        convert(sb, null, PageCondition.FIELD_HEIMID);
        sb.append("=?");
        list.add(heimId);

        if (privilegeSpecified) {
            tableList.add(TableConstants.TABLE_MEMBER);
            tableList.add(TableConstants.TABLE_CAST);
            tableList.add(TableConstants.TABLE_PERMISSION);
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("user_id", new Integer(user.getId()));
            paramMap.put("permission_type", new Integer(privilege.getType()
                .getValue()));
            paramMap.put("permission_level", new Integer(privilege.getLevel()
                .getValue()));
            Pair pair = qh.constructPair(
                "PageConditionParser.privilegeCondition", null, paramMap, null);
            sb.append(pair.getTemplate());
            list.addAll(Arrays.asList(pair.getParameters()));
        }
        if (sb.length() > 0) {
            sb.insert(0, "WHERE ");
        }

        boolean orderSpecified = (orders != null && orders.length > 0);
        StringBuilder orderBySb = null;
        if (orderSpecified) {
            orderBySb = new StringBuilder();
            for (int i = 0; i < orders.length; i++) {
                if (i > 0) {
                    orderBySb.append(",");
                }
                Order order = orders[i];
                String field = order.getFieldName();
                String converted = convert(orderBySb, groupBySet, field);
                if (converted != null) {
                    String table = tableByPropertyMap.get(converted);
                    if (table == null) {
                        table = TableConstants.TABLE_PROPERTY + propertyCount;
                        tableList.add(TableConstants.TABLE_PROPERTY + " AS "
                            + table);
                        tableByPropertyMap.put(converted, table);
                        propertyCount++;

                        if (sb.length() > 0) {
                            sb.append(" AND ");
                        }
                        sb.append(TableConstants.TABLE_PAGE).append(".")
                            .append(TableConstants.COL_PAGE_ID).append("=")
                            .append(table).append(".").append(
                                TableConstants.COL_PROPERTY_PAGEID).append(
                                " AND ").append(table).append(".").append(
                                TableConstants.COL_PROPERTY_NAME).append("=?");
                        list.add(converted);
                    }

                    String col = table + ".value";
                    if (isNumeric(field)) {
                        col = identity_.toNumericExpression(col);
                    }
                    orderBySb.append(col);
                    groupBySet.add(col);
                }
                if (!order.isAscending()) {
                    orderBySb.append(" DESC");
                }
            }
        }

        if (privilegeSpecified) {
            groupBySet.add(TableConstants.TABLE_PERMISSION + "."
                + TableConstants.COL_PERMISSION_TYPE);
            sb.append(" GROUP BY ");
            boolean first = true;
            for (Iterator<String> itr = groupBySet.iterator(); itr.hasNext();) {
                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }
                sb.append(itr.next());
            }
            sb.append(" HAVING ").append(maxClause).append("<>").append(
                PrivilegeLevel.NEVER.getValue());
        }

        if (orderSpecified) {
            sb.append(" ORDER BY ");
            sb.append(orderBySb);
        }

        return new ParsedPageCondition(columnList.toArray(new String[0]),
            tableList.toArray(new String[0]), sb.toString(), list.toArray());
    }


    List<String> toGroupByColumnList(String[] columns)
    {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < columns.length; i++) {
            // TODO もっと正確にエイリアス名を切り出す仕組みにしよう。
            int space = columns[i].lastIndexOf(' ');
            if (space < 0) {
                list.add(columns[i]);
            } else {
                list.add(columns[i].substring(0, space).trim());
            }
        }
        return list;
    }


    /*
     * private scope methods
     */

    private String convert(StringBuilder sb, Set<String> columnSet,
        String symbol)
    {
        String lsymbol = symbol.toLowerCase();
        if (reservedSet_.contains(lsymbol)) {
            sb.append(symbol);
            return null;
        } else if (pageFieldSet_.contains(lsymbol)) {
            String fieldName = TableConstants.TABLE_PAGE + "." + symbol;
            sb.append(fieldName);
            if (columnSet != null) {
                columnSet.add(fieldName);
            }
            return null;
        } else if (pageAliasSet_.contains(lsymbol)) {
            String aliasName = symbol;
            sb.append(aliasName);
            if (columnSet != null) {
                columnSet.add(aliasName);
            }
            return null;
        } else {
            Function constant = constantMap_.get(lsymbol);
            if (constant != null) {
                sb.append(constant.get());
                return null;
            } else {
                String converted = null;
                for (int i = 0; i < pageTypes_.length; i++) {
                    converted = pageTypes_[i]
                        .convertFieldToPropertyName(symbol);
                    if (converted != null) {
                        break;
                    }
                }
                if (converted != null) {
                    return converted;
                } else {
                    throw new IllegalArgumentException("Invalid symbol: "
                        + symbol);
                }
            }
        }
    }


    private boolean isNumeric(String field)
    {
        for (int i = 0; i < pageTypes_.length; i++) {
            if (pageTypes_[i].convertFieldToPropertyName(field) != null) {
                return pageTypes_[i].isNumericField(field);
            }
        }
        throw new IllegalArgumentException("Invalid field: " + field);
    }
}
