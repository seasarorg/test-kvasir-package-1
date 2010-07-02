package org.seasar.kvasir.cms.java.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.annotation.ForTest;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.java.Base;
import org.seasar.kvasir.cms.java.JavaPlugin;
import org.seasar.kvasir.cms.java.util.CompilerUtils;
import org.seasar.kvasir.cms.processor.impl.AbstractLocalPathPageProcessor;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;


/**
 * 指定されたパスに対応するページの内容をJavaのソースコードとみなし、
 * ソースコードからビルドされたクラスのメソッドを呼び出すPageProcessorです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class JavaPageProcessor extends AbstractLocalPathPageProcessor
{
    public static final String PARAM_ENCODING = "encoding";

    private JavaPlugin plugin_;

    private String encoding_;

    private Map<Integer, Map<String, Reference<Entry>>> entryMap_ = Collections
        .synchronizedMap(new HashMap<Integer, Map<String, Reference<Entry>>>());


    public void setPlugin(JavaPlugin plugin)
    {
        plugin_ = plugin;
    }


    @ForTest
    void setEncoding(String encoding)
    {
        encoding_ = encoding;
    }


    public void init(ServletConfig config)
    {
        super.init(config);
        encoding_ = config.getInitParameter(PARAM_ENCODING);
        if (encoding_ == null) {
            encoding_ = "UTF-8";
        }
    }


    public void destroy()
    {
        super.destroy();

        entryMap_.clear();
        plugin_ = null;
    }


    protected boolean doProcessFile(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest, File file)
        throws ServletException, IOException
    {
        try {
            return newInstance(pageRequest.getMy().getGardRootPage(),
                pageRequest.getMy().getLocalPathname(), file).process(request,
                response, pageRequest);
        } catch (Exception ex) {
            ServletException exeption = (ServletException)new ServletException(
                "Can't process java file: file=" + file + ", pathname="
                    + pageRequest.getMy().getPathname(), ex);
            // ServletAPIによってはコンストラクタの第二引数で与えたExceptionをinitCause
            // として設定することがあるみたいだが、その場合にさらにinitCauseしてしまうとThrowableが
            // 「Can't overwrite cause」と怒ることがある。
            // それを避けるためにこうしている。
            if (exeption.getCause() != ex) {
                exeption.initCause(ex);
            }
            throw exeption;
        }
    }


    /*
     * private scope methods
     */

    private Base newInstance(Page gardRootPage, String localPathname, File file)
    {
        Integer key = Integer.valueOf(gardRootPage.getId());
        Map<String, Reference<Entry>> map = entryMap_.get(key);
        if (map == null) {
            map = new HashMap<String, Reference<Entry>>();
            entryMap_.put(key, map);
        }
        synchronized (map) {
            Entry entry = null;
            Reference<Entry> ref = map.get(localPathname);
            if (ref != null) {
                entry = ref.get();
            }
            if (entry != null) {
                entry.update(file);
            } else {
                entry = new Entry(localPathname, file);
                map.put(localPathname, new SoftReference<Entry>(entry));
            }
            return entry.newInstance();
        }
    }


    /*
     * inner classes
     */

    class Entry
    {
        private static final String CLASSNAMESUFFIX = "Page";

        private String localPathname_;

        private File file_;

        private long lastModified_;

        private Class<Base> clazz_;


        public Entry(String localPathname, File file)
        {
            localPathname_ = localPathname;
            file_ = file;
            compile();
        }


        public void update(File file)
        {
            if (file.equals(file_)) {
                if (file_.lastModified() <= lastModified_) {
                    return;
                }
            } else {
                file_ = file;
            }
            compile();
        }


        public Base newInstance()
        {
            try {
                return clazz_.newInstance();
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }


        void compile()
        {
            InputStream in = null;
            try {
                in = new FileInputStream(file_);
                String source = IOUtils.readString(in, encoding_, false);

                clazz_ = CompilerUtils.compile(plugin_, localPathname_,
                    CLASSNAMESUFFIX, Base.class, source);
                lastModified_ = System.currentTimeMillis();
            } catch (IOException ex) {
                throw new IORuntimeException(ex);
            } finally {
                IOUtils.closeQuietly(in);
            }
        }
    }
}
