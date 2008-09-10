package org.seasar.kvasir.util.html;

import java.io.Reader;
import java.io.StringReader;


public class HTMLParser
{
    private String      title_;
    private String      body_;
    private String      whole_;


    /*
     * constructors
     */

    public HTMLParser(String content)
    {
        StringBuffer whole = new StringBuffer();
        StringBuffer title = new StringBuffer();
        StringBuffer body = new StringBuffer();
        String wholeDelim = "";
        String titleDelim = "";
        String bodyDelim = "";

        int stat = 0;
        HTMLTokenizer ht = new HTMLTokenizer(content);
        while (ht.hasMoreTokens()) {
            HTMLTokenizer.Token tkn = ht.nextToken();
            int type = tkn.getType();
            String tag = tkn.getTagName();
            if (tag != null) {
                tag = tag.toLowerCase();
            }

            if (type == HTMLTokenizer.BODY) {
                whole.append(wholeDelim);
                whole.append(HTMLUtils.defilter(tkn.toString()));
//                wholeDelim = " ";
            }
            if (stat == 0) {
                if ("html".equals(tag)) {
                    stat = 1;
                } else if ("header".equals(tag)) {
                    stat = 2;
                } else if ("body".equals(tag)) {
                    stat = 3;
                }
            } else if (stat == 1) {
                if ("/html".equals(tag)) {
                    stat = -1;
                } else if ("header".equals(tag)) {
                    stat = 2;
                } else if ("body".equals(tag)) {
                    stat = 3;
                }
            } else if (stat == 2) {
                if ("/header".equals(tag)) {
                    stat = 1;
                } else if ("title".equals(tag)) {
                    stat = 4;
                }
            } else if (stat == 3) {
                if ("/body".equals(tag)) {
                    stat = -1;
                } else {
                    if (type == HTMLTokenizer.BODY) {
                        body.append(bodyDelim);
                        body.append(HTMLUtils.defilter(tkn.toString()));
//                        bodyDelim = " ";
                    }
                }
            } else if (stat == 4) {
                if ("/title".equals(tag)) {
                    stat = 2;
                } else {
                    if (type == HTMLTokenizer.BODY) {
                        title.append(titleDelim);
                        title.append(HTMLUtils.defilter(tkn.toString()));
//                        titleDelim = " ";
                    }
                }
            }
        }

        whole_ = whole.toString();
        if (title.length() > 0) {
            title_ = title.toString();
        } else {
            title_ = "";
        }
        if (body.length() > 0) {
            body_ = body.toString();
        } else {
            body_ = whole_;
        }
    }


    /*
     * public scope methods
     */

    public String getTitle()
    {
        return title_;
    }


    public Reader getReader()
    {
        return new StringReader(whole_);
    }


    public String getString()
    {
        return whole_;
    }


    public String getSummary()
    {
        return getSummary(160);
    }


    public String getSummary(int len)
    {
        if (len > body_.length()) {
            len = body_.length();
        }
        return body_.substring(0, len);
    }
}
