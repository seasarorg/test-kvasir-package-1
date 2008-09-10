package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.Version;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Id;
import net.skirnir.xom.annotation.Required;


abstract public class PluginDependency
{
    private String plugin_;

    private String versionString_;

    private Version version_;

    private String matchString_;

    private Match match_;


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<").append(getElementName());
        if (plugin_ != null) {
            sb.append(" plugin=\"").append(plugin_).append("\"");
        }
        if (versionString_ != null) {
            sb.append(" version=\"").append(versionString_).append("\"");
        }
        if (matchString_ != null) {
            sb.append(" match=\"").append(matchString_).append("\"");
        }
        sb.append(" />");
        return sb.toString();
    }


    String getElementName()
    {
        String name = getClass().getName();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            name = name.substring(dot + 1);
        }
        if (name.length() > 0) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    }


    public String getMatchString()
    {
        if (matchString_ == null) {
            return Match.COMPATIBLE.getName();
        } else {
            return matchString_;
        }
    }


    @Attribute("match")
    @Default("compatible")
    public void setMatchString(String matchString)
    {
        matchString_ = matchString;
        match_ = Match.getMatch(matchString);
    }


    public Match getMatch()
    {
        if (match_ == null) {
            return Match.COMPATIBLE;
        } else {
            return match_;
        }
    }


    public String getPlugin()
    {
        return plugin_;
    }


    @Attribute
    @Required
    @Id
    public void setPlugin(String plugin)
    {
        plugin_ = plugin;
    }


    public String getVersionString()
    {
        return versionString_;
    }


    @Attribute("version")
    public void setVersionString(String versionString)
    {
        versionString_ = versionString;
        if (versionString == null) {
            version_ = null;
        } else {
            try {
                version_ = new Version(versionString);
            } catch (IllegalArgumentException ex) {
                version_ = null;
            }
        }
    }


    public Version getVersion()
    {
        return version_;
    }


    public boolean isMatched(Version target)
    {
        Match match = getMatch();
        if (match == null) {
            return true;
        }

        Version version = getVersion();
        if (version == null) {
            return true;
        }

        switch (match) {
        case PERFECT:
            return target.equals(version);

        case EQUIVALENT:
            return target.isEquivalent(version);

        case COMPATIBLE:
            return target.isCompatible(version);

        case GREATER_OR_EQUAL:
            return (target.compareTo(version) >= 0);

        default:
            throw new IllegalArgumentException("Unknown match enumeration: "
                + match);
        }
    }
}
