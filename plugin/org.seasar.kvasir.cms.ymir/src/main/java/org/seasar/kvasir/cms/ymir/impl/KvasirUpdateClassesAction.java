package org.seasar.kvasir.cms.ymir.impl;

import static org.seasar.cms.ymir.impl.DefaultRequestProcessor.PARAM_METHOD;
import static org.seasar.kvasir.cms.ymir.impl.KvasirTemplateUpdater.TEMPLATE_PREFIX;
import static org.seasar.kvasir.cms.ymir.impl.KvasirTemplateUpdater.TEMPLATE_SUFFIX;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.seasar.cms.ymir.Request;
import org.seasar.cms.ymir.Response;
import org.seasar.cms.ymir.extension.creator.ClassDesc;
import org.seasar.cms.ymir.extension.creator.ClassDescBag;
import org.seasar.cms.ymir.extension.creator.SourceCreator;
import org.seasar.cms.ymir.extension.creator.action.impl.UpdateClassesAction;
import org.seasar.cms.ymir.impl.DefaultRequestProcessor;
import org.seasar.kvasir.cms.ymir.ExternalTemplate.ResourceEntry;


public class KvasirUpdateClassesAction extends UpdateClassesAction
{
    public KvasirUpdateClassesAction(SourceCreator sourceCreator)
    {
        super(sourceCreator);
    }


    public Response actDefault(Request request, Response response,
        ResourceEntry[] entries, ClassDescBag bag)
    {
        if (bag.isEmpty()) {
            return response;
        }

        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("request", request);
        variableMap.put("entries", entries);
        variableMap.put("parameters", getParameters(request));
        variableMap.put("createdClassDescs", createClassDescDtos(bag
            .getCreatedClassDescs()));
        variableMap.put("updatedClassDescs", createClassDescDtos(bag
            .getUpdatedClassDescs()));
        return getSourceCreator().getResponseCreator().createResponse(
            getClass().getResource(
                TEMPLATE_PREFIX + "updateClasses" + TEMPLATE_SUFFIX),
            variableMap);
    }


    public Response actUpdate(Request request, Response response,
        ResourceEntry[] entries, ClassDescBag bag)
    {
        String method = request.getParameter(PARAM_METHOD);
        if (method == null) {
            return response;
        }

        String[] appliedClassNames = request.getParameterValues(PARAM_APPLY);
        Set<String> appliedClassNameSet = new HashSet<String>();
        if (appliedClassNames != null) {
            appliedClassNameSet.addAll(Arrays.asList(appliedClassNames));
        }
        Properties prop = getSourceCreator().getSourceCreatorProperties();
        ClassDesc[] classDescs = bag.getClassDescs();
        for (int i = 0; i < classDescs.length; i++) {
            String name = classDescs[i].getName();
            String checked;
            if (appliedClassNameSet.contains(name)) {
                checked = String.valueOf(true);
            } else {
                checked = String.valueOf(false);
                bag.remove(name);
            }
            prop.setProperty(PREFIX_CLASSCHECKED + name, checked);
        }
        getSourceCreator().saveSourceCreatorProperties();

        boolean mergeMethod = !"true".equals(request
            .getParameter(PARAM_REPLACE));

        getSourceCreator().updateClasses(bag, mergeMethod);

        Map<String, Object> variableMap = new HashMap<String, Object>();
        variableMap.put("request", request);
        variableMap.put("method", method);
        variableMap.put("parameters", getParameters(request));
        variableMap.put("entries", entries);
        variableMap.put("classDescBag", bag);
        variableMap.put("actionName", getSourceCreator().getActionName(
            request.getPath(), method));
        variableMap.put("suggestionExists", Boolean.valueOf(bag
            .getClassDescMap(ClassDesc.KIND_PAGE).size()
            + bag.getCreatedClassDescMap(ClassDesc.KIND_BEAN).size() > 0));
        variableMap.put("pageClassDescs", bag
            .getClassDescs(ClassDesc.KIND_PAGE));
        variableMap.put("renderActionName",
            DefaultRequestProcessor.ACTION_RENDER);
        variableMap.put("createdBeanClassDescs", bag
            .getCreatedClassDescs(ClassDesc.KIND_BEAN));
        return getSourceCreator().getResponseCreator().createResponse(
            getClass().getResource(
                TEMPLATE_PREFIX + "updateClasses_update" + TEMPLATE_SUFFIX),
            variableMap);
    }
}
