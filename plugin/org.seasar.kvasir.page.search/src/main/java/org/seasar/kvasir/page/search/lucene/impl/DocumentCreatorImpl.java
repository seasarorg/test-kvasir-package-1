package org.seasar.kvasir.page.search.lucene.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.search.lucene.DocumentCreator;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.html.HTMLParser;
import org.seasar.kvasir.util.io.IOUtils;


public class DocumentCreatorImpl
    implements DocumentCreator
{
    protected KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public Document[] newDocuments(Page page)
    {
        PropertyAbility prop = page.getAbility(PropertyAbility.class);
        ContentAbility ability = page.getAbility(ContentAbility.class);

        Set<String> variantSet = new HashSet<String>();
        variantSet.addAll(Arrays.asList(prop.getVariants()));
        variantSet.addAll(Arrays.asList(ability.getVariants()));

        List<Document> docList = new ArrayList<Document>();
        for (Iterator<String> itr = variantSet.iterator(); itr.hasNext();) {
            String variant = itr.next();
            Content content = ability.getLatestContent(variant);

            String label = PropertyUtils.valueOf(prop.getProperty(
                PropertyAbility.PROP_LABEL, variant), "");
            String description = PropertyUtils.valueOf(prop.getProperty(
                PropertyAbility.PROP_DESCRIPTION, variant), "");
            String html;
            long size;
            if (content == null) {
                html = "";
                size = 0;
            } else {
                html = content.getBodyHTMLString(null);
                size = content.getBodyResource().getSize();
            }
            if (label.length() == 0 && description.length() == 0
                && html.length() == 0) {
                continue;
            }

            Document doc = new Document();
            doc.add(Field.Keyword(FIELD_ID, String.valueOf(page.getId())));
            doc.add(Field.UnIndexed(FIELD_VARIANT, variant));
            doc.add(Field.Keyword(FIELD_TYPE, String.valueOf(page.getType())));
            doc.add(Field.Keyword(FIELD_CREATEDATE, page.getCreateDate()));
            doc.add(Field.Keyword(FIELD_MODIFYDATE, page.getModifyDate()));
            //            doc.add(Field.Keyword("name", page.getName()));
            doc.add(Field.UnIndexed(FIELD_SIZE, String.valueOf(size)));
            if (label.length() > 0) {
                doc.add(Field.Text(FIELD_LABEL, label));
            }
            String summary = "";
            if (description.length() > 0) {
                doc.add(Field.Text(FIELD_DESCRIPTION, description));
                summary = description;
            }
            if (html.length() > 0) {
                HTMLParser parser = new HTMLParser(html);
                // JapaneseAnalyzerの不具合？のため
                // 先頭に大量の改行コードのある
                // テキストを正しく登録できないことと、
                // 少なくともSen1.0以前では
                // 改行があるとSenが正しく字句解析できないらしいので、
                // 改行を削除しておく。
                doc.add(Field.Text(FIELD_BODY, new StringReader(
                    removeCRLF(parser.getString()))));
                if (log_.isDebugEnabled()) {
                    log_.debug("INDEXED HTML:\n" + html);
                    if (html != null) {
                        parser = new HTMLParser(html);
                        log_.debug("INDEXED CONTENT:\n"
                            + IOUtils.readString(parser.getReader(), false));
                    }
                    log_.debug("END");
                }
                summary = parser.getSummary();
            }
            doc.add(Field.UnIndexed(FIELD_SUMMARY, summary));
            docList.add(doc);
        }

        return (Document[])docList.toArray(new Document[0]);
    }


    /*
     * private scope methods
     */

    private String removeCRLF(String str)
    {
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(str, "\r\n", true);
        boolean preAscii = true;
        boolean preCRLF = false;
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken();
            if (tkn.equals("\r") || tkn.equals("\n")) {
                preCRLF = true;
            } else {
                boolean ascii = (tkn.charAt(0) < 0x80);
                if (preCRLF) {
                    if (preAscii && ascii) {
                        sb.append(" ");
                    }
                }
                sb.append(tkn);
                preCRLF = false;
                preAscii = ascii;
            }
        }

        return sb.toString();
    }
}
