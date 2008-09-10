package org.seasar.kvasir.cms.ymir.impl;

import static org.seasar.kvasir.cms.CmsPlugin.ATTR_CONTEXT_PATH;

import org.seasar.cms.ymir.RedirectionPathResolver;
import org.seasar.cms.ymir.Request;


public class KvasirRedirectionPathResolver
    implements RedirectionPathResolver
{
    private static final String PREFIX_ABSOLUTE = "!";

    private static final String PREFIX_GARDRELATIVE = "/";

    private static final String SELF = ".";


    public String resolve(String path, Request request)
    {
        if (path == null) {
            return null;
        }

        if (path.startsWith(PREFIX_ABSOLUTE)) {
            String trimmed = path.substring(PREFIX_ABSOLUTE.length());
            if (trimmed.startsWith("/")) {
                path = getOriginalContextPath(request) + trimmed;
            } else {
                // trimmedが「;a=b」などの場合。
                path = getOriginalContextPath(request) + "/" + trimmed;
            }
        } else if (path.startsWith(PREFIX_GARDRELATIVE)) {
            path = request.getContextPath() + path;
        } else if (path.length() == 0 || path.startsWith("?")
            || path.startsWith(";")) {
            // 空パスへのリダイレクトはYmirの世界ではルートパスへのリダイレクトとみなすのでこうしている。
            path = request.getContextPath() + "/" + path;
        } else if (path.startsWith(SELF)) {
            // 自分自身へのリダイレクト。
            path = request.getContextPath() + request.getPath()
                + path.substring(SELF.length());
        }

        return path;
    }


    String getOriginalContextPath(Request request)
    {
        return (String)request.getAttribute(ATTR_CONTEXT_PATH);
    }
}
