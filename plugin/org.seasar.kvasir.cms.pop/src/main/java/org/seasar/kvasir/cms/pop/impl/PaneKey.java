package org.seasar.kvasir.cms.pop.impl;

class PaneKey
{
    private int heimId_;

    private String paneId_;


    public PaneKey(int heimId, String paneId)
    {
        heimId_ = heimId;
        paneId_ = paneId;
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + heimId_;
        result = PRIME * result + ((paneId_ == null) ? 0 : paneId_.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final PaneKey other = (PaneKey)obj;
        if (heimId_ != other.heimId_) {
            return false;
        }
        if (paneId_ == null) {
            if (other.paneId_ != null)
                return false;
        } else if (!paneId_.equals(other.paneId_))
            return false;
        return true;
    }


    public int getHeimId()
    {
        return heimId_;
    }


    public String getPaneId()
    {
        return paneId_;
    }
}
