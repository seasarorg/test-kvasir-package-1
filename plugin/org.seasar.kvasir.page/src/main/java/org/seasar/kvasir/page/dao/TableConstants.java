package org.seasar.kvasir.page.dao;

/**
 * @author YOKOTA Takehiko
 */
public interface TableConstants
{
    public static final String TABLE_PAGE = "page";

    public static final String TABLE_MEMBER = "member";

    public static final String TABLE_CAST = "casto";

    public static final String TABLE_PROPERTY = "property";

    public static final String TABLE_PERMISSION = "permission";

    public static final String COL_PAGE_ID = "id";

    public static final String COL_PAGE_TYPE = "type";

    public static final String COL_PAGE_HEIMID = "heimid";

    public static final String COL_PAGE_LORDID = "lordid";

    public static final String COL_PAGE_PARENTPATHNAME = "parentpathname";

    public static final String COL_PAGE_NAME = "name";

    public static final String COL_PAGE_ORDERNUMBER = "ordernumber";

    public static final String COL_PAGE_CREATEDATE = "createdate";

    public static final String COL_PAGE_MODIFYDATE = "modifydate";

    public static final String COL_PAGE_OWNERUSERID = "owneruserid";

    public static final String COL_PAGE_CONCEALED = "concealed";

    public static final String COL_PAGE_NODE = "node";

    public static final String COL_PAGE_ASFILE = "asfile";

    public static final String COL_PAGE_LISTING = "listing";

    public static final String COL_PAGE_VERSION = "version";

    /*
     * ここにカラムを増やした場合はPage.FIELD_XXXを増やし、PageCondition.FIELDSにも
     * エントリを増やすこと。
     * また、PageDtoやPageMoldやPageにもgetter/setterを追加し、以下のメソッドも変更すること。
     *   PageGardExporterImpl#exportsFields()
     *   PageGardImporterImpl#importsFields()
     *   PageGardInstallerImpl#populateFields()
     *   PageDiffererImpl#applyForFields()
     *   PageDiffererImpl#diffForFields()
     *   PageProvider#newPageDto()
     *   PageProvider#newRootPageDto()
     */
    public static final String[] COLS_PAGE = new String[] {
        TABLE_PAGE + "." + COL_PAGE_ID, TABLE_PAGE + "." + COL_PAGE_TYPE,
        TABLE_PAGE + "." + COL_PAGE_HEIMID, TABLE_PAGE + "." + COL_PAGE_LORDID,
        TABLE_PAGE + "." + COL_PAGE_PARENTPATHNAME,
        TABLE_PAGE + "." + COL_PAGE_NAME,
        TABLE_PAGE + "." + COL_PAGE_ORDERNUMBER,
        TABLE_PAGE + "." + COL_PAGE_CREATEDATE,
        TABLE_PAGE + "." + COL_PAGE_MODIFYDATE,
        TABLE_PAGE + "." + COL_PAGE_OWNERUSERID,
        TABLE_PAGE + "." + COL_PAGE_CONCEALED,
        TABLE_PAGE + "." + COL_PAGE_NODE, TABLE_PAGE + "." + COL_PAGE_ASFILE,
        TABLE_PAGE + "." + COL_PAGE_LISTING,
        TABLE_PAGE + "." + COL_PAGE_VERSION, };

    public static final String ALIAS_PAGE_PATHNAME = "pathname";

    public static final String COL_PROPERTY_PAGEID = "pageid";

    public static final String COL_PROPERTY_NAME = "name";

    public static final String COL_PROPERTY_VALUE = "value";

    public static final String COL_PERMISSION_TYPE = "type";

    public static final String COL_PERMISSION_LEVEL = "level";
}
