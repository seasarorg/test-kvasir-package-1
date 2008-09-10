package org.seasar.kvasir.cms.impl;

import java.util.regex.Pattern;

import org.seasar.kvasir.cms.GardIdProvider;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.page.ability.template.TemplateAbility;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class AbstractPageMatcher
{
    private static final int MATCH_WHOLE = 0;

    private static final int MATCH_SUFFIX = 1;

    private static final int MATCH_SUBPATH = 2;

    protected String what_;

    protected String how_;

    protected String except_;

    private boolean not_;

    protected boolean regex_;

    private Pattern howPattern_;

    private Pair howPair_;

    private Pattern exceptPattern_;

    private Pair exceptPair_;

    private GardIdProvider gardIdProvider_;


    public AbstractPageMatcher(String what, String how, String except,
        boolean not, boolean regex, GardIdProvider gardIdProvider)
    {
        what_ = what;
        how_ = how;
        except_ = except;
        not_ = not;
        regex_ = regex;
        gardIdProvider_ = gardIdProvider;
        if (how_ != null) {
            if (regex) {
                howPattern_ = Pattern.compile(how_);
            } else {
                howPair_ = createPair(how_);
            }
        }
        if (except_ != null) {
            if (regex) {
                exceptPattern_ = Pattern.compile(except_);
            } else {
                exceptPair_ = createPair(except_);
            }
        }
    }


    public String toString()
    {
        return "what=" + what_ + ", how=" + how_ + ",except=" + except_
            + ", not=" + not_ + ", regex=" + regex_ + ", gardIdProvider="
            + gardIdProvider_;
    }


    Pair createPair(String pattern)
    {
        Pair pair = new Pair();
        if (pattern.startsWith("*.")) {
            pair.matchMethod = MATCH_SUFFIX;
            pair.suffix = pattern.substring(2/*= "*.".length() */);
        } else if (pattern.endsWith("/*")) {
            pair.matchMethod = MATCH_SUBPATH;
            pair.suffix = pattern
                .substring(0, pattern.length() - 2/*= "/*".length() */);
        } else {
            pair.matchMethod = MATCH_WHOLE;
        }
        return pair;
    }


    protected boolean isMatched(PageRequest pageRequest)
    {
        // gardIdProviderについては、notは見ない。
        if (gardIdProvider_ != null) {
            if (!gardIdProvider_.containsGardId(pageRequest.getMy()
                .getGardRootPage().getGardId())) {
                return false;
            }
        }

        boolean matched = false;
        if (what_ == null) {
            // whatが無指定の時は必ずマッチする。
            matched = true;
        } else if (PageProcessorElement.WHAT_TEMPLATETYPE.equals(what_)) {
            if (pageRequest.getMy().getPage() != null) {
                TemplateAbility template = pageRequest.getMy().getPage()
                    .getAbility(TemplateAbility.class);
                if (template.getTemplate() != null) {
                    matched = isMatched(template.getType());
                }
            }
        } else if (PageProcessorElement.WHAT_PATH.equals(what_)) {
            matched = isMatched(pageRequest.getMy().getLocalPathname());
        } else if (PageProcessorElement.WHAT_PAGETYPE.equals(what_)) {
            if (pageRequest.getMy().getPage() != null) {
                matched = isMatched(pageRequest.getMy().getPage().getType());
            }
        }

        if (not_) {
            return !matched;
        } else {
            return matched;
        }
    }


    boolean isMatched(String how)
    {
        if (how == null) {
            return false;
        }

        if (!isMatched(howPattern_, howPair_, how)) {
            return false;
        }
        if (isMatched(exceptPattern_, exceptPair_, how)) {
            return false;
        }
        return true;
    }


    boolean isMatched(Pattern pattern, Pair pair, String how)
    {
        if (regex_) {
            if (pattern == null) {
                return false;
            } else {
                return pattern.matcher(how).find();
            }
        } else {
            if (pair == null) {
                return false;
            } else {
                return isMatchedToPathPattern(pair, how);
            }
        }
    }


    boolean isMatchedToPathPattern(Pair pair, String localPathname)
    {
        switch (pair.matchMethod) {
        case MATCH_WHOLE:
            return how_.equals(localPathname);

        case MATCH_SUFFIX:
            int dot = localPathname.lastIndexOf('.');
            return (dot >= 0 && localPathname.substring(dot + 1).equals(
                pair.suffix));

        case MATCH_SUBPATH:
            return (localPathname.equals(pair.suffix) || localPathname
                .startsWith(pair.suffix)
                && localPathname.charAt(pair.suffix.length()) == '/');

        default:
            throw new RuntimeException("Unknown matchMehtod: "
                + pair.matchMethod);
        }
    }


    /*
     * inner classes
     */

    private static class Pair
    {
        public int matchMethod;

        public String suffix;
    }
}
