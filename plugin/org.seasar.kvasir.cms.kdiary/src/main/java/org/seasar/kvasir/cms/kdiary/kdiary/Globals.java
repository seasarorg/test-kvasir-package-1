package org.seasar.kvasir.cms.kdiary.kdiary;

import org.seasar.kvasir.page.ability.PropertyAbility;


public interface Globals
{
    String COMMENT_DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    int LIMIT_INFINITY = -1;

    int COMMENTBODY_SHORTLENGTH = 64;

    String SUFFIX_CONTINUING = "...";

    String DIR_ARTICLE = "article";

    String DIR_THEME = "theme";

    String PROP_PARAGRAPHANCHOR = "paragraphAnchor";

    String PROP_TITLE = PropertyAbility.PROP_LABEL;

    String PROP_HEADER = "header";

    String PROP_FOOTER = "footer";

    String PROP_AUTHORNAME = "authorName";

    String PROP_COMMENTANCHOR = "commentAnchor";

    String PROP_AUTHORMAILADDRESS = "authorMailAddress";

    String PROP_INDEXPAGE = "indexPage";

    String PROP_DATEFORMAT = "dateFormat";

    String PROP_JAVADATEFORMAT = "javaDateFormat";

    String PROP_LATESTLIMIT = "latestLimit";

    String PROP_SHOWCOMMENT = "showComment";

    String PROP_COMMENTLIMIT = "commentLimit";

    String PROP_THEME = "theme";

    String PROP_CSS = "css";

    String PROP_BODYMEDIATYPE = "bodyMediaType";
}
