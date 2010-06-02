package org.seasar.kvasir.page.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.cms.beantable.Pair;
import org.seasar.kvasir.page.condition.Replacement;


public class CachedPair
{
    private Pair pair_;


    public CachedPair(Pair pair)
    {
        pair_ = pair;
    }


    public Pair getPair(Map<String, Object> paramMap)
    {
        if (paramMap == null) {
            return pair_;
        }

        List<Object> params = new ArrayList<Object>();
        for (Object param : pair_.getParameters()) {
            if (param instanceof Replacement) {
                String id = ((Replacement)param).getId();
                if (!paramMap.containsKey(id)) {
                    throw new IllegalArgumentException(
                        "parameter corresponding id (" + id
                            + ") does not exists in map");
                }
                params.add(paramMap.get(id));
            } else {
                params.add(param);
            }
        }

        return new Pair(pair_.getTemplate(), params.toArray());
    }
}
