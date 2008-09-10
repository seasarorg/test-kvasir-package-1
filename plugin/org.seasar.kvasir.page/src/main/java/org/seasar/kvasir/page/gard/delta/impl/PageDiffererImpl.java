package org.seasar.kvasir.page.gard.delta.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.gard.delta.AddDelta;
import org.seasar.kvasir.page.gard.delta.ChangeDelta;
import org.seasar.kvasir.page.gard.delta.Delta;
import org.seasar.kvasir.page.gard.delta.PageDifference;
import org.seasar.kvasir.page.gard.delta.PageDifferer;
import org.seasar.kvasir.page.gard.delta.RemoveDelta;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageDiffererImpl
    implements PageDifferer
{
    private PagePlugin plugin_;

    private PageAlfr pageAlfr_;


    public PageDiffererImpl(PagePlugin plugin)
    {
        plugin_ = plugin;
        pageAlfr_ = plugin.getPageAlfr();
    }


    // toPage - fromPage
    public PageDifference diff(final Page toPage, final Page fromPage)
    {
        List<Page> list = new ArrayList<Page>(2);
        if (toPage != null) {
            list.add(toPage);
        }
        if (fromPage != null) {
            list.add(fromPage);
        }
        return pageAlfr_.runWithLocking(list.toArray(new Page[0]),
            new Processable<PageDifference>() {
                public PageDifference process()
                    throws ProcessableRuntimeException
                {
                    PageDifferenceImpl pd = new PageDifferenceImpl();
                    if (toPage == null) {
                        if (fromPage == null) {
                            return pd;
                        } else {
                            pd.setType(Delta.TYPE_REMOVE);
                        }
                    } else {
                        if (fromPage == null) {
                            pd.setType(Delta.TYPE_ADD);
                        }
                    }

                    // フィールドの差分を調べる。
                    diffForFields(pd, toPage, fromPage);

                    // ページフィーチャ毎の差分を調べる。
                    PageAbilityAlfr[] alfrs = plugin_.getPageAbilityAlfrs();
                    for (int i = 0; i < alfrs.length; i++) {
                        diffForAbility(pd, alfrs[i], toPage, fromPage);
                    }

                    return pd;
                }
            });
    }


    public void apply(final Page page, final PageDifference pd)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                // フィールドの差分を適用する。
                applyForFields(page, pd);

                // ページフィーチャ毎の差分を適用する。
                applyForAbilityAlfrs(page, pd);
                return null;
            }
        });
    }


    /*
     * private scope methods
     */

    private void diffForFields(PageDifferenceImpl pd, Page toPage, Page fromPage)
    {
        String toLordPath = null;
        Integer toOrderNumber = null;
        Date toCreateDate = null;
        Date toModifyDate = null;
        String toOwnerUserPath = null;
        Date toRevealDate = null;
        Date toConcealDate = null;
        Boolean toNode = null;
        Boolean toAsFile = null;
        Boolean toListing = null;
        if (toPage != null) {
            toLordPath = PageUtils.encodePathname(toPage, toPage
                .getLordPathname());
            toOrderNumber = new Integer(toPage.getOrderNumber());
            toCreateDate = toPage.getCreateDate();
            toModifyDate = toPage.getModifyDate();
            toOwnerUserPath = PageUtils.encodePathname(toPage, toPage
                .getOwnerUser().getPathname());
            toRevealDate = toPage.getRevealDate();
            toConcealDate = toPage.getConcealDate();
            toNode = Boolean.valueOf(toPage.isNode());
            toAsFile = Boolean.valueOf(toPage.isAsFile());
            toListing = Boolean.valueOf(toPage.isListing());
        }

        String fromLordPath = null;
        Integer fromOrderNumber = null;
        Date fromCreateDate = null;
        Date fromModifyDate = null;
        String fromOwnerUserPath = null;
        Date fromRevealDate = null;
        Date fromConcealDate = null;
        Boolean fromNode = null;
        Boolean fromAsFile = null;
        Boolean fromListing = null;
        if (fromPage != null) {
            fromLordPath = PageUtils.encodePathname(fromPage, fromPage
                .getLordPathname());
            fromOrderNumber = new Integer(fromPage.getOrderNumber());
            fromCreateDate = fromPage.getCreateDate();
            fromModifyDate = fromPage.getModifyDate();
            fromOwnerUserPath = PageUtils.encodePathname(fromPage, fromPage
                .getOwnerUser().getPathname());
            fromRevealDate = fromPage.getRevealDate();
            fromConcealDate = fromPage.getConcealDate();
            fromNode = Boolean.valueOf(fromPage.isNode());
            fromAsFile = Boolean.valueOf(fromPage.isAsFile());
            fromListing = Boolean.valueOf(fromPage.isListing());
        }

        pd.addFieldDelta(getDelta(Page.FIELD_LORD, toLordPath, fromLordPath));
        pd.addFieldDelta(getDelta(Page.FIELD_ORDERNUMBER, toOrderNumber,
            fromOrderNumber));
        pd.addFieldDelta(getDelta(Page.FIELD_CREATEDATE, toCreateDate,
            fromCreateDate));
        pd.addFieldDelta(getDelta(Page.FIELD_MODIFYDATE, toModifyDate,
            fromModifyDate));
        pd.addFieldDelta(getDelta(Page.FIELD_OWNERUSER, toOwnerUserPath,
            fromOwnerUserPath));
        pd.addFieldDelta(getDelta(Page.FIELD_REVEALDATE, toRevealDate,
            fromRevealDate));
        pd.addFieldDelta(getDelta(Page.FIELD_CONCEALDATE, toConcealDate,
            fromConcealDate));
        pd.addFieldDelta(getDelta(Page.FIELD_NODE, toNode, fromNode));
        pd.addFieldDelta(getDelta(Page.FIELD_ASFILE, toAsFile, fromAsFile));
        pd.addFieldDelta(getDelta(Page.FIELD_LISTING, toListing, fromListing));
    }


    private void diffForAbility(PageDifferenceImpl pd,
        PageAbilityAlfr abilityAlfr, Page toPage, Page fromPage)
    {
        Class<? extends PageAbilityAlfr> abilityAlfrClass = abilityAlfr
            .getClass();
        Set<String> set = new TreeSet<String>();
        String[] toVariants = abilityAlfr.getVariants(toPage);
        String[] fromVariants = abilityAlfr.getVariants(fromPage);
        for (int i = 0; i < toVariants.length; i++) {
            set.add(toVariants[i]);
        }
        for (int i = 0; i < fromVariants.length; i++) {
            set.add(fromVariants[i]);
        }
        String[] variants = set.toArray(new String[0]);
        for (int i = 0; i < variants.length; i++) {
            String variant = variants[i];

            for (Iterator<String> itr = abilityAlfr.attributeNames(toPage,
                variant); itr.hasNext();) {
                String name = itr.next();
                Attribute fromAttr = abilityAlfr.getAttribute(fromPage, name,
                    variant);
                Attribute toAttr = abilityAlfr.getAttribute(toPage, name,
                    variant);
                if (fromAttr == null) {
                    // fromになくてtoにある。
                    pd.addAbilityAlfrDelta(abilityAlfrClass, variant,
                        new AddDelta(name, toAttr));
                } else {
                    // fromにもtoにもある。
                    if (!toAttr.equals(fromAttr)) {
                        // 違いがあった。
                        pd.addAbilityAlfrDelta(abilityAlfrClass, variant,
                            new ChangeDelta(name, toAttr, fromAttr));
                    }
                }
            }

            for (Iterator<String> itr = abilityAlfr.attributeNames(fromPage,
                variant); itr.hasNext();) {
                String name = itr.next();
                Attribute fromAttr = abilityAlfr.getAttribute(fromPage, name,
                    variant);
                Attribute toAttr = abilityAlfr.getAttribute(toPage, name,
                    variant);
                if (toAttr == null) {
                    // toになくてfromにある。
                    pd.addAbilityAlfrDelta(abilityAlfrClass, variant,
                        new RemoveDelta(name, fromAttr));
                }
            }
        }
    }


    private Delta getDelta(String name, Object to, Object from)
    {
        if (to == null) {
            if (from == null) {
                return null;
            } else {
                return new RemoveDelta(name, from);
            }
        } else {
            if (from == null) {
                return new AddDelta(name, to);
            } else {
                if (to.equals(from)) {
                    return null;
                } else {
                    return new ChangeDelta(name, to, from);
                }
            }
        }
    }


    private void applyForFields(Page page, PageDifference pd)
    {
        Delta[] deltas = pd.getFieldDeltas(Delta.TYPE_CHANGE);
        for (int i = 0; i < deltas.length; i++) {
            Delta delta = deltas[i];
            String name = delta.getName();
            if (Page.FIELD_LORD.equals(name)) {
                String lordPath = PageUtils.encodePathname(page, page
                    .getLordPathname());
                String fromLordPath = (String)delta.getFrom();
                if ((".".equals(lordPath) && !".".equals(fromLordPath))
                    || (!".".equals(lordPath) && ".".equals(fromLordPath))) {
                    // この属性は変更されていないのでchangeして良い。
                    page.setAsLord(".".equals(delta.getTo()));
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            } else if (Page.FIELD_ORDERNUMBER.equals(name)) {
                Integer orderNumber = new Integer(page.getOrderNumber());
                Integer fromOrderNumber = (Integer)delta.getFrom();
                if (fromOrderNumber.equals(orderNumber)) {
                    // この属性は変更されていないのでchangeして良い。
                    page.setOrderNumber(((Integer)delta.getTo()).intValue());
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            } else if (Page.FIELD_CREATEDATE.equals(name)) {
                Date createDate = page.getCreateDate();
                Date fromCreateDate = (Date)delta.getFrom();
                if (fromCreateDate.equals(createDate)) {
                    // この属性は変更されていないのでchangeして良い。
                    page.setCreateDate((Date)delta.getTo());
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            } else if (Page.FIELD_MODIFYDATE.equals(name)) {
                Date modifyDate = page.getModifyDate();
                Date fromModifyDate = (Date)delta.getFrom();
                if (fromModifyDate.equals(modifyDate)) {
                    // この属性は変更されていないのでchangeして良い。
                    page.setModifyDate((Date)delta.getTo());
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            } else if (Page.FIELD_OWNERUSER.equals(name)) {
                String ownerUserPath = PageUtils.encodePathname(page, page
                    .getOwnerUser().getPathname());
                String fromOwnerUserPath = (String)delta.getFrom();
                if (fromOwnerUserPath.equals(ownerUserPath)) {
                    // この属性は変更されていないのでchangeして良い。
                    User ownerUser = (User)PageUtils.decodeToPage(User.class,
                        page, (String)delta.getTo());
                    if (ownerUser != null) {
                        page.setOwnerUser(ownerUser);
                    }
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            } else if (Page.FIELD_REVEALDATE.equals(name)) {
                Date revealDate = page.getRevealDate();
                Date fromRevealDate = (Date)delta.getFrom();
                if (fromRevealDate.equals(revealDate)) {
                    // この属性は変更されていないのでchangeして良い。
                    page.setRevealDate((Date)delta.getTo());
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            } else if (Page.FIELD_CONCEALDATE.equals(name)) {
                Date concealDate = page.getConcealDate();
                Date fromConcealDate = (Date)delta.getFrom();
                if (fromConcealDate.equals(concealDate)) {
                    // この属性は変更されていないのでchangeして良い。
                    page.setConcealDate((Date)delta.getTo());
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            } else if (Page.FIELD_NODE.equals(name)) {
                Boolean node = Boolean.valueOf(page.isNode());
                Boolean fromNode = (Boolean)delta.getFrom();
                if (fromNode.equals(node)) {
                    // この属性は変更されていないのでchangeして良い。
                    page.setNode(((Boolean)delta.getTo()).booleanValue());
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            } else if (Page.FIELD_ASFILE.equals(name)) {
                Boolean asFile = Boolean.valueOf(page.isAsFile());
                Boolean fromNode = (Boolean)delta.getFrom();
                if (fromNode.equals(asFile)) {
                    // この属性は変更されていないのでchangeして良い。
                    page.setAsFile(((Boolean)delta.getTo()).booleanValue());
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            } else if (Page.FIELD_LISTING.equals(name)) {
                Boolean listing = Boolean.valueOf(page.isListing());
                Boolean fromNode = (Boolean)delta.getFrom();
                if (fromNode.equals(listing)) {
                    // この属性は変更されていないのでchangeして良い。
                    page.setListing(((Boolean)delta.getTo()).booleanValue());
                } else {
                    // この属性は変更されているのでchangeしない。
                    ;
                }
            }
        }
    }


    private void applyForAbilityAlfrs(Page page, PageDifference pd)
    {
        PageAbilityAlfr[] alfrs = plugin_.getPageAbilityAlfrs();
        for (int a = 0; a < alfrs.length; a++) {
            PageAbilityAlfr alfr = alfrs[a];
            Class<? extends PageAbilityAlfr> alfrClass = alfr.getClass();

            String[] names = pd.getAbilityDeltaNames(alfrClass);
            Arrays.sort(names);
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                String[] variants = pd.getAbilityDeltaVariants(alfrClass, name);
                Arrays.sort(variants);
                for (int j = 0; j < variants.length; j++) {
                    String variant = variants[i];
                    Attribute attr = alfr.getAttribute(page, name, variant);
                    Delta delta = pd.getAbilityDelta(alfrClass, name, variant);
                    int type = delta.getType();
                    Attribute fromAttr = (Attribute)delta.getFrom();
                    Attribute toAttr = (Attribute)delta.getTo();
                    if (type == Delta.TYPE_REMOVE) {
                        // removeされた属性について処理を行なう。
                        if (fromAttr.equals(attr)) {
                            // この属性は変更されていないのでremoveして良い。
                            alfr.removeAttribute(page, name, variant);
                        } else {
                            // この属性は変更されているのでremoveしない。
                            ;
                        }
                    } else if (type == Delta.TYPE_CHANGE) {
                        // changeされた属性について処理を行なう。
                        if (fromAttr.equals(attr)) {
                            // この属性は変更されていないのでchangeして良い。
                            alfr.setAttribute(page, name, variant, toAttr);
                        } else {
                            // この属性は変更されているのでchangeしない。
                            ;
                        }
                    } else if (type == Delta.TYPE_ADD) {
                        // addされた属性について処理を行なう。
                        if (attr == null) {
                            // この属性は変更されていないのでaddして良い。
                            alfr.setAttribute(page, name, variant, toAttr);
                        } else {
                            // この属性は変更されているのでaddしない。
                            ;
                        }
                    }
                }
            }
        }
    }
}
