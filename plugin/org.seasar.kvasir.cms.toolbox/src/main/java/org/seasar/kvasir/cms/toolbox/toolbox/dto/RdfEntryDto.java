package org.seasar.kvasir.cms.toolbox.toolbox.dto;

import java.util.Locale;

import org.seasar.kvasir.cms.toolbox.toolbox.web.RssPage;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.html.HTMLParser;
import org.seasar.kvasir.util.html.HTMLUtils;


public class RdfEntryDto extends RdfEntryDtoBase
{
    public RdfEntryDto(Page page, String url, Locale locale)
    {
        setURL(url);
        setDate(page.getModifyDateString());
        setDescription(generateDescription(page, locale));
        setSubject("");
        setTitle(page.getAbility(PropertyAbility.class).getProperty(
            PropertyAbility.PROP_LABEL, locale));
    }


    String generateDescription(Page page, Locale locale)
    {
        if (!page.isAsFile()) {
            Content content = page.getAbility(ContentAbility.class)
                .getLatestContent(locale);
            if (content != null) {
                String bodyHTMLString = content
                    .getBodyHTMLString(VariableResolver.EMPTY);
                HTMLParser parser = new HTMLParser(bodyHTMLString);
                String body = parser.getSummary();
                if (body.length() <= RssPage.SUMMARY_LENGTH) {
                    return HTMLUtils.filter(body);
                } else {
                    return HTMLUtils.filter(body.substring(0,
                        RssPage.SUMMARY_LENGTH).concat(RssPage.SUFFIX_LEADING));
                }
            }
        }
        return "";
    }
}
