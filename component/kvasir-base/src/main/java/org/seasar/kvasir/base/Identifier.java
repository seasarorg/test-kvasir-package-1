package org.seasar.kvasir.base;

/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class Identifier
    implements Comparable<Identifier>
{
    private String id_;

    private Version version_;


    public Identifier(String name)
    {
        int hyphen = name.lastIndexOf('-');
        if (hyphen >= 0) {
            id_ = name.substring(0, hyphen);
            version_ = new Version(name.substring(hyphen + 1));
        } else {
            id_ = name;
            version_ = Version.NONE;
        }
    }


    public Identifier(String id, Version version)
    {
        id_ = id;
        version_ = (version != null ? version : Version.NONE);
    }


    @Override
    public String toString()
    {
        return getString();
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((id_ == null) ? 0 : id_.hashCode());
        result = PRIME * result
            + ((version_ == null) ? 0 : version_.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object o)
    {
        if ((o == null) || (o.getClass() != getClass())) {
            return false;
        }

        Identifier id = (Identifier)o;
        if (!id_.equals(id.id_)) {
            return false;
        }
        if (!version_.equals(id.version_)) {
            return false;
        }
        return true;
    }


    public int compareTo(Identifier id)
    {
        int cmp = id_.compareTo(id.id_);
        if (cmp != 0) {
            return cmp;
        }
        return version_.compareTo(id.version_);
    }


    public String getId()
    {
        return id_;
    }


    public Version getVersion()
    {
        return version_;
    }


    public String getString()
    {
        if (version_ != Version.NONE) {
            return id_ + "-" + version_.getString();
        } else {
            return id_;
        }
    }
}
