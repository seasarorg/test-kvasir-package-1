package org.seasar.kvasir.base;

/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 * @author manhole
 */
public class Version
    implements Comparable<Version>
{
    public static final Version NONE = new Version(0, 0, 0, "");

    public static final String SUFFIX_SNAPSHOT = "-SNAPSHOT";

    private final String string_;

    private final int major_;

    private final int minor_;

    private final int service_;

    private final String qualifier_;


    public Version(String string)
    {
        string_ = string;

        try {
            int dot1 = string.indexOf('.');
            if (dot1 >= 0) {
                major_ = Integer.parseInt(string.substring(0, dot1));
                int dot2 = string.indexOf('.', dot1 + 1);
                if (dot2 >= 0) {
                    minor_ = Integer.parseInt(string.substring(dot1 + 1, dot2));
                    int nonDigit = indexOfNonDigit(string, dot2 + 1);
                    service_ = Integer.parseInt(string.substring(dot2 + 1,
                        nonDigit));
                    qualifier_ = string.substring(nonDigit);
                } else {
                    int nonDigit = indexOfNonDigit(string, dot1 + 1);
                    minor_ = Integer.parseInt(string.substring(dot1 + 1,
                        nonDigit));
                    service_ = 0;
                    qualifier_ = string.substring(nonDigit);
                }
            } else {
                major_ = 0;
                minor_ = 0;
                service_ = 0;
                qualifier_ = string;
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid version string: "
                + string);
        }
    }


    public Version(int major, int minor, int service, String qualifier)
    {
        major_ = major;
        minor_ = minor;
        service_ = service;
        qualifier_ = qualifier;
        string_ = major + "." + minor + "." + service + qualifier;
    }


    @Override
    public String toString()
    {
        return string_;
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + major_;
        result = PRIME * result + minor_;
        result = PRIME * result
            + ((qualifier_ == null) ? 0 : qualifier_.hashCode());
        result = PRIME * result + service_;
        return result;
    }


    @Override
    public boolean equals(Object o)
    {
        if ((o == null) || (o.getClass() != getClass())) {
            return false;
        }

        Version v = (Version)o;
        if (major_ != v.major_) {
            return false;
        }
        if (minor_ != v.minor_) {
            return false;
        }
        if (service_ != v.service_) {
            return false;
        }
        if (!qualifier_.equals(v.qualifier_)) {
            return false;
        }
        return true;
    }


    public int compareTo(final Version v)
    {
        int cmp = major_ - v.major_;
        if (cmp != 0) {
            return cmp;
        }
        cmp = minor_ - v.minor_;
        if (cmp != 0) {
            return cmp;
        }
        cmp = service_ - v.service_;
        if (cmp != 0) {
            return cmp;
        }

        String q = stripSnapshotSuffix(qualifier_);
        String vq = stripSnapshotSuffix(v.qualifier_);
        boolean snapshot = (q != qualifier_);
        boolean vsnapshot = (vq != v.qualifier_);
        if (q.equals(vq)) {
            return compareSnapshot(snapshot, vsnapshot);
        } else {
            if (q.equals("")) {
                return 1;
            } else {
                if (vq.equals("")) {
                    return -1;
                } else {
                    return q.compareTo(vq);
                }
            }
        }
    }


    public int getMajor()
    {
        return major_;
    }


    public int getMinor()
    {
        return minor_;
    }


    public int getService()
    {
        return service_;
    }


    public String getString()
    {
        return string_;
    }


    public String getQualifier()
    {
        return qualifier_;
    }


    public boolean isEquivalent(Version version)
    {
        // XXX equivalentの定義ってこれで合ってる？
        if (!isCompatible(version)) {
            return false;
        } else {
            return (minor_ == version.minor_);
        }
    }


    public boolean isCompatible(Version version)
    {
        return (major_ == version.major_);
    }


    /*
     * private scope methods
     */

    private int indexOfNonDigit(String str, int offset)
    {
        for (int i = offset; i < str.length(); i++) {
            char ch = str.charAt(i);
            if ((ch < '0') || (ch > '9')) {
                return i;
            }
        }
        return str.length();
    }


    private String stripSnapshotSuffix(String qualifier)
    {
        if (qualifier.endsWith(SUFFIX_SNAPSHOT)) {
            return qualifier.substring(0, qualifier.length()
                - SUFFIX_SNAPSHOT.length());
        } else {
            return qualifier;
        }
    }


    private int compareSnapshot(boolean snapshot1, boolean snapshot2)
    {
        if (snapshot1) {
            if (snapshot2) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (snapshot2) {
                return 1;
            } else {
                return 0;
            }
        }
    }


    public boolean isSnapshot()
    {
        if (qualifier_.endsWith(SUFFIX_SNAPSHOT)) {
            return true;
        }
        return false;
    }

}
