package org.seasar.kvasir.util.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.JavaResource;


/**
 * {@link I18NProperties}を構築します。
 * 複数のメッセージファイルを扱うようにできます。
 * 
 * @author manhole
 */
/*
 * NOTE: 多段構成のI18NPropertiesを構築する機能をymir-coreの
 * org.seasar.ymir.impl.MessagesImplが持っており、
 * その機能をYmir外でも利用したかったため、MessagesImplからロジックを持ってきた。
 */
public class I18NPropertiesBuilder
{

    private final List path_ = new ArrayList();

    /**
     * trueの場合はプロパティの${...}部分を変数として解釈します。
     */
    private boolean evaluateVariable_;


    /**
     * 先に登録したものがparentになる。
     * 
     * @param path クラスパス上のメッセージファイルのパス。
     */
    public void addPath(final String path)
    {
        path_.add(path);
    }


    public I18NProperties build()
    {
        if (path_.isEmpty()) {
            throw new IllegalStateException("no path");
        }
        I18NProperties messages = null;
        for (final Iterator it = path_.iterator(); it.hasNext();) {
            final String path = (String)it.next();

            final Resource resource = new JavaResource(path, Thread
                .currentThread().getContextClassLoader());

            String name;
            final int slash = path.lastIndexOf('/');
            if (slash >= 0) {
                name = path.substring(slash + 1);
            } else {
                name = path;
            }
            String baseName;
            String suffix;
            final int dot = name.lastIndexOf('.');
            if (dot >= 0) {
                baseName = name.substring(0, dot);
                suffix = name.substring(dot);
            } else {
                baseName = name;
                suffix = "";
            }

            messages = new I18NProperties(resource.getParentResource(),
                baseName, suffix, messages);
            messages.setEvaluateVariable(evaluateVariable_);
        }
        return messages;
    }


    public void setEvaluateVariable(final boolean evaluateVariable)
    {
        evaluateVariable_ = evaluateVariable;
    }

}
