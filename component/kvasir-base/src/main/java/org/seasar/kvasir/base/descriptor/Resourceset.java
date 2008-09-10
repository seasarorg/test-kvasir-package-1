package org.seasar.kvasir.base.descriptor;

import org.seasar.kvasir.base.util.ArrayUtils;
import org.seasar.kvasir.util.AntUtils;
import org.seasar.kvasir.util.io.Resource;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;


public class Resourceset
{
    private String dir_;

    private Include[] includes_ = new Include[0];

    private Exclude[] excludes_ = new Exclude[0];


    public String getDir()
    {
        return dir_;
    }


    @Attribute
    @Required
    public void setDir(String dir)
    {
        dir_ = dir;
    }


    public Include[] getIncludes()
    {
        return includes_;
    }


    @Child
    public void addInclude(Include include)
    {
        includes_ = ArrayUtils.add(includes_, include);
    }


    public void setIncludes(Include[] includes)
    {
        includes_ = includes;
    }


    public Exclude[] getExcludes()
    {
        return excludes_;
    }


    @Child
    public void addExclude(Exclude exclude)
    {
        excludes_ = ArrayUtils.add(excludes_, exclude);
    }


    public void setExcludes(Exclude[] excludes)
    {
        excludes_ = excludes;
    }


    public Resource[] getResources(Resource basedir)
    {
        return AntUtils.getResources(basedir.getChildResource(dir_),
            getIncludeStrings(), getExcludeStrings());

    }


    String[] getIncludeStrings()
    {
        String[] includeStrings = new String[includes_.length];
        for (int i = 0; i < includes_.length; i++) {
            includeStrings[i] = includes_[i].getName();
        }
        return includeStrings;
    }


    String[] getExcludeStrings()
    {
        String[] excludeStrings = new String[excludes_.length];
        for (int i = 0; i < excludes_.length; i++) {
            excludeStrings[i] = excludes_[i].getName();
        }
        return excludeStrings;
    }
}
