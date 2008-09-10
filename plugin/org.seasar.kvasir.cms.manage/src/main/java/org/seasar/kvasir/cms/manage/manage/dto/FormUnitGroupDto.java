package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.ArrayList;
import java.util.List;


public class FormUnitGroupDto
{
    private static final String CLASS_ERROR = "error";

    private FormUnitDto group_;

    private List<FormUnitDto> formUnits_ = new ArrayList<FormUnitDto>();

    private boolean error_;


    public FormUnitGroupDto(FormUnitDto group)
    {
        group_ = group;
    }


    public void add(FormUnitDto formUnit)
    {
        formUnits_.add(formUnit);
    }


    public List<FormUnitDto> getFormUnits()
    {
        return formUnits_;
    }


    public String getName()
    {
        return group_.getName();
    }


    public boolean isDefault()
    {
        return group_ instanceof DefaultGroupDto;
    }


    public String getHtmlId()
    {
        return group_.getHtmlId();
    }


    public void setError(boolean error)
    {
        error_ = error;
    }


    public String getStyleClass()
    {
        if (error_) {
            return CLASS_ERROR;
        } else {
            return null;
        }
    }
}
