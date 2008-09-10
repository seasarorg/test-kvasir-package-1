package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.extension.PopElement;
import org.seasar.kvasir.page.Page;


public class NewPopsDto
{
    private static final int COUNT = 6;

    private PopDto[] pops_;

    private boolean ableToGoForward_;

    private String leftButtonId_;

    private String leftButtonName_;

    private String leftButtonTitle_;


    public NewPopsDto(PopPlugin popPlugin, String[] gardIds, int offset,
        Locale locale)
    {
        if (offset < 0) {
            leftButtonId_ = "showNewPops";
            leftButtonName_ = "blue_right";
            leftButtonTitle_ = "Show selection for new POPs";
            pops_ = new PopDto[0];
        } else {
            leftButtonId_ = "goBackward";
            if (offset == 0) {
                leftButtonName_ = "blue_left";
                leftButtonTitle_ = "Close this selection";
            } else {
                leftButtonName_ = "green_left";
                leftButtonTitle_ = "Previous POPs";
            }

            PopElement[] elements;
            if (gardIds.length == 0) {
                elements = popPlugin.getPopElements(Page.GARDID_ROOT);
            } else {
                Set<PopElement> elementSet = new LinkedHashSet<PopElement>();
                elementSet.addAll(Arrays.asList(popPlugin
                    .getPopElements(Page.GARDID_ROOT)));
                for (int i = 0; i < gardIds.length; i++) {
                    elementSet.addAll(Arrays.asList(popPlugin
                        .getPopElements(gardIds[i])));
                }
                elements = elementSet.toArray(new PopElement[0]);
            }
            for (int i = 0; i < elements.length / 2; i++) {
                PopElement element = elements[i];
                elements[i] = elements[elements.length - 1 - i];
                elements[elements.length - 1 - i] = element;
            }

            ableToGoForward_ = (offset * COUNT + COUNT < elements.length);

            elements = subArray(elements, offset * COUNT, offset * COUNT
                + COUNT);
            pops_ = new PopDto[elements.length];
            for (int i = 0; i < elements.length; i++) {
                if (elements[i] != null) {
                    pops_[i] = new PopDto(elements[i], locale);
                } else {
                    pops_[i] = new PopDto("pop.padding." + i, "", "", true);
                }
            }
        }
    }


    PopElement[] subArray(PopElement[] elements, int start, int end)
    {
        if (start >= elements.length) {
            return new PopElement[0];
        }
        if (end > elements.length) {
            end = elements.length;
        }
        PopElement[] newElements = new PopElement[COUNT];
        System.arraycopy(elements, start, newElements, 0, end - start);
        return newElements;
    }


    public PopDto[] getPops()
    {
        return pops_;
    }


    public boolean isAbleToGoForward()
    {
        return ableToGoForward_;
    }


    public String getLeftButtonId()
    {
        return leftButtonId_;
    }


    public String getLeftButtonName()
    {
        return leftButtonName_;
    }


    public String getLeftButtonTitle()
    {
        return leftButtonTitle_;
    }
}
