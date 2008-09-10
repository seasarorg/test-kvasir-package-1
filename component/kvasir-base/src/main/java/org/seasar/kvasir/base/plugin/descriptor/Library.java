package org.seasar.kvasir.base.plugin.descriptor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.Resource;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Id;
import net.skirnir.xom.annotation.Parent;
import net.skirnir.xom.annotation.Required;


public class Library
{
    public static final String[] PATTERNS_ALL = new String[] { Export.NAME_ALL };

    private Runtime parent_;

    private String name_;

    private List<Export> exportList_ = new ArrayList<Export>();

    private boolean filter_ = false;

    private String encoding_ = null;

    private boolean expand_ = false;


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<library");
        if (name_ != null) {
            sb.append(" name=\"").append(name_).append("\"");
        }
        if (filter_ != false) {
            sb.append(" filter=\"").append(filter_).append("\"");
        }
        if (encoding_ != null) {
            sb.append(" encoding=\"").append(encoding_).append("\"");
        }
        if (expand_ != false) {
            sb.append(" expand=\"").append(expand_).append("\"");
        }
        sb.append(">");
        for (Iterator<Export> itr = exportList_.iterator(); itr.hasNext();) {
            sb.append(itr.next());
        }
        sb.append("</library>");
        return sb.toString();
    }


    public Runtime getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(Runtime parent)
    {
        parent_ = parent;
    }


    public String getEncoding()
    {
        return (encoding_ == null ? "UTF-8" : encoding_);
    }


    @Attribute
    @Default("UTF-8")
    public void setEncoding(String encoding)
    {
        encoding_ = encoding;
    }


    public boolean isExpand()
    {
        return expand_;
    }


    @Attribute
    @Default("false")
    public void setExpand(boolean expand)
    {
        expand_ = expand;
    }


    public Export[] getExports()
    {
        return exportList_.toArray(new Export[0]);
    }


    @Child
    public void addExport(Export export)
    {
        exportList_.add(export);
    }


    public void setExports(Export[] exports)
    {
        exportList_.clear();
        exportList_.addAll(Arrays.asList(exports));
    }


    public boolean isFilter()
    {
        return filter_;
    }


    @Attribute
    @Default("false")
    public void setFilter(boolean filter)
    {
        filter_ = filter;
    }


    public String getName()
    {
        return name_;
    }


    @Attribute
    @Required
    @Id
    public void setName(String name)
    {
        name_ = name;
    }


    public URL[] getURLsForURLClassLoader()
    {
        if (name_ != null) {
            Resource directory = getParent().getParent()
                .getRuntimeResourcesDirectory();
            Resource resource = directory.getChildResource(name_);
            List<URL> urlList = new ArrayList<URL>();
            if (expand_) {
                // ディレクトリ内のJARファイルを対象にする。
                Resource[] children = resource.listResources();
                if (children != null) {
                    for (int i = 0; i < children.length; i++) {
                        if (children[i].isDirectory()
                            || !children[i].getName().endsWith(".jar")) {
                            continue;
                        }
                        URL url = ClassUtils
                            .getURLForURLClassLoader(children[i].toFile());
                        if (url != null) {
                            urlList.add(url);
                        }
                    }
                }
            } else {
                URL url = ClassUtils.getURLForURLClassLoader(resource.toFile());
                if (url != null) {
                    urlList.add(url);
                }
            }
            return urlList.toArray(new URL[0]);
        }

        return new URL[0];
    }


    public String[] getExportClassPatterns()
    {
        return getExportPatterns(false);
    }


    public String[] getExportResourcePatterns()
    {
        return getExportPatterns(true);
    }


    String[] getExportPatterns(boolean resource)
    {
        String[] exportPatterns;
        boolean all = false;
        List<String> list = new ArrayList<String>();
        int size = exportList_.size();
        for (int i = 0; i < size; i++) {
            Export export = exportList_.get(i);
            if (export.isResource() != resource) {
                continue;
            }
            String name = export.getName();
            if (Export.NAME_ALL.equals(name)) {
                all = true;
                break;
            } else if (name != null) {
                list.add(name);
            }
        }
        if (all) {
            exportPatterns = PATTERNS_ALL;
        } else {
            exportPatterns = list.toArray(new String[0]);
        }
        return exportPatterns;
    }
}
