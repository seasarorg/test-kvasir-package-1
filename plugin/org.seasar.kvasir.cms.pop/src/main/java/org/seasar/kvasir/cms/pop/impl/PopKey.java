package org.seasar.kvasir.cms.pop.impl;

import org.seasar.kvasir.cms.pop.Pop;


class PopKey
    implements Comparable<PopKey>
{
    private String id_;

    private int instanceId_ = Pop.INSTANCEID_DEFAULT;


    PopKey(String id, int instanceId)
    {
        id_ = id;
        instanceId_ = instanceId;
    }


    public String getId()
    {
        return id_;
    }


    public int getInstanceId()
    {
        return instanceId_;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        PopKey key = (PopKey)obj;
        if (!key.id_.equals(id_)) {
            return false;
        }
        if (key.instanceId_ != instanceId_) {
            return false;
        }
        return true;
    }


    @Override
    public int hashCode()
    {
        return id_.hashCode() + instanceId_;
    }


    public int compareTo(PopKey o)
    {
        int cmp = id_.compareTo(o.id_);
        if (cmp == 0) {
            cmp = instanceId_ - o.instanceId_;
        }
        return cmp;
    }
}
