package org.seasar.kvasir.page.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.util.ArrayUtils;

import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;


/**
 * org.seasar.kvasir.page.nameRestrictions拡張ポイントにプラグインするための
 * 拡張を表すクラスです。
 * 
 * @author yokota
 */
@Bean("name-restriction")
public class NameRestrictionElement extends AbstractElement
{
    private String[] invalidChars_ = new String[0];

    private String[] invalidNames_ = new String[0];


    /**
     * Pageの名前として使用できないものの配列を返します。
     * 
     * @return Pageの名前として使用できないものの配列。
     * nullが返されることはありません。
     */
    public String[] getInvalidNames()
    {
        return invalidNames_;
    }


    /**
     * Pageの名前として使用できないものを追加します。
     * 
     * @param invalidName Pageの名前として使用できないもの。
     */
    @Child
    public void addInvalidName(String invalidName)
    {
        invalidNames_ = ArrayUtils.add(invalidNames_, invalidName);
    }


    /**
     * Pageの名前として使用できないものを設定します。
     * 
     * @param invalidNames Pageの名前として使用できないものの配列。
     */
    public void setInvalidNames(String[] invalidNames)
    {
        invalidNames_ = invalidNames;
    }


    /**
     * Pageの名前に含めることのできない文字の配列を返します。
     * 
     * @return Pageの名前に含めることのできない文字の配列。
     * nullが返されることはありません。
     */
    public String[] getInvalidChars()
    {
        return invalidChars_;
    }


    /**
     * Pageの名前に含めることのできない文字を追加します。
     * 
     * @param invalidChar Pageの名前に含めることのできない文字。
     */
    @Child
    public void addInvalidChar(String invalidChar)
    {
        invalidChars_ = ArrayUtils.add(invalidChars_, invalidChar);
    }


    /**
     * Pageの名前に含めることのできない文字を設定します。
     * 
     * @param invalidChars Pageの名前に含めることのできない文字の配列。
     */
    public void setInvalidChars(String[] invalidChars)
    {
        invalidChars_ = invalidChars;
    }
}
