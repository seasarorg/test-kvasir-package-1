package org.seasar.kvasir.page.ability;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * ページに対して設定できる操作権限を表すクラスです。
 * <p>Kvasir/Sora
 * における操作権限は権限のタイプと権限のレベルによって表されますが、
 * 効率化のためにしばしばこの2つは単一のint値にエンコードされて扱われます。
 * （なお、このint値を単に「権限」と呼ぶことがあります。）
 * このクラスのメソッドを使うと権限タイプと権限レベルを単一の
 * int値にエンコードしたりその逆の操作を行なうことができます。</p>
 * <p>操作権限には次のものがあります。</p>
 * <dl>
 * <dt>アクセス（ACCESS）権限</dt>
 * <dd>ページを参照できるか、ページの内容を変更できるか、です。
 * アクセス権限にはNONE（全て不可）、PEEK（フィールド、プロパティのみ参照可）、
 * VIEW（全てを参照可）、COMMENT（参照可かつコメント可）、
 * ACCESS（全て可）の5レベルがあります。
 * ただし、パス名や名前等、
 * 一部のフィールドはアクセス権限がなくとも参照することができます。
 * </dd>
 * </dl>
 * <p>また、特別な権限レベルとしてNEVERレベルがあります。
 * ロールRがページIに対して権限タイプT、NEVERレベルの操作権限を持つ場合、
 * ロールRを持つユーザはページIに対して
 * （他にどのような権限を持っていたとしても）
 * 決して権限タイプTの権限を持たないようになります。</p>
 *
 * @author YOKOTA Takehiko
 * @see org.seasar.kvasir.page.InRole
 */
public enum Privilege
{
    ACCESS_NONE(PrivilegeType.ACCESS, PrivilegeLevel.NONE), ACCESS_PEEK(PrivilegeType.ACCESS, PrivilegeLevel.PEEK), ACCESS_VIEW(
        PrivilegeType.ACCESS, PrivilegeLevel.VIEW), ACCESS_COMMENT(PrivilegeType.ACCESS, PrivilegeLevel.COMMENT), ACCESS(
        PrivilegeType.ACCESS, PrivilegeLevel.ACCESS), ACCESS_NEVER(PrivilegeType.ACCESS, PrivilegeLevel.NEVER);

    private static final Map<String, Privilege> privilegeByNameMap_;

    private static final Map<Integer, Privilege> privilegeByCodeMap_;

    private PrivilegeType type_;

    private PrivilegeLevel level_;

    private int code_;

    private String name_;

    static {
        Map<String, Privilege> byNameMap = new HashMap<String, Privilege>();
        Map<Integer, Privilege> byCodeMap = new HashMap<Integer, Privilege>();
        Privilege[] privileges = values();
        for (int i = 0; i < privileges.length; i++) {
            byNameMap.put(privileges[i].getName(), privileges[i]);
            byCodeMap.put(privileges[i].getCode(), privileges[i]);
        }
        privilegeByNameMap_ = Collections.unmodifiableMap(byNameMap);
        privilegeByCodeMap_ = Collections.unmodifiableMap(byCodeMap);
    }


    /**
     * 権限を表す文字列を権限を表すオブジェクトに変換します。
     * <p>権限を表す文字列は、
     * "access.none"、"access.peek"、"access.view"、
     * "access.comment"、"access"、"access.never"、のいずれかです。
     * </p>
     *
     * @param name 権限を表す文字列。
     * @return 権限を表すオブジェクト。
     * 変換できない文字列が指定された場合はnullを返します。
     */
    public static Privilege getPrivilege(String name)
    {
        return privilegeByNameMap_.get(name);
    }


    /**
     * 権限を表す文字列を権限を表すオブジェクトに変換します。
     * <p>権限を表す文字列は、
     * "access.none"、"access.peek"、"access.view"、
     * "access.comment"、"access"、"access.never"、のいずれかです。</p>
     * <p>変換できない文字列が指定された場合はdefaultPrivを返します。</p>
     *
     * @param name 権限を表す文字列。
     * @param defaultPriv 変換できない文字列が指定された場合の返り値。
     * @return 権限を表すオブジェクト。
     */
    public static Privilege getPrivilege(String name, Privilege defaultPriv)
    {
        Privilege priv = getPrivilege(name);
        if (priv != null) {
            return priv;
        } else {
            return defaultPriv;
        }
    }


    public static Privilege getPrivilege(int type, int level)
    {
        return getPrivilege(PrivilegeType.getType(type), PrivilegeLevel.getLevel(level));
    }


    public static Privilege getPrivilege(PrivilegeType type, PrivilegeLevel level)
    {
        if (type == null || level == null) {
            return null;
        }
        return getPrivilege(toCode(type, level));
    }


    /**
     * 単一のint値にエンコードされた権限を権限を表すオブジェクトに変換します。
     *
     * @param code 権限を表すint値。
     * @return 権限を表すオブジェクト。
     * 変換できないint値が指定された場合はnullを返します。
     */
    public static Privilege getPrivilege(int code)
    {
        return privilegeByCodeMap_.get(code);
    }


    /**
     * 単一のint値にエンコードされた権限を権限を表すオブジェクトに変換します。
     * <p>変換できないint値が指定された場合はdefaultPrivを返します。</p>
     *
     * @param code 権限を表すint値。
     * @param defaultPriv 変換できないint値が指定された場合の返り値。
     * @return 権限を表すオブジェクト。
     */
    public static Privilege getPrivilege(int code, Privilege defaultPriv)
    {
        Privilege priv = getPrivilege(code);
        if (priv != null) {
            return priv;
        } else {
            return defaultPriv;
        }
    }


    private static int toCode(PrivilegeType type, PrivilegeLevel level)
    {
        return (type.getValue() + level.getValue());
    }


    Privilege(PrivilegeType type, PrivilegeLevel level)
    {
        type_ = type;
        level_ = level;
        code_ = toCode(type, level);
        String typeName = type.getName();
        String levelName = level.getName();
        if (typeName.equals(levelName)) {
            name_ = typeName;
        } else {
            name_ = typeName + "." + levelName;
        }
    }


    public PrivilegeType getType()
    {
        return type_;
    }


    public PrivilegeLevel getLevel()
    {
        return level_;
    }


    public int getCode()
    {
        return code_;
    }


    public String getName()
    {
        return name_;
    }


    public String toString()
    {
        return getName();
    }
}
