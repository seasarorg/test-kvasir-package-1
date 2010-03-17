package org.seasar.kvasir.base.webapp.impl;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.mime.MimeMappings;
import org.seasar.kvasir.base.mime.MimePlugin;
import org.seasar.kvasir.base.webapp.Content;
import org.seasar.kvasir.base.webapp.WebappPlugin;
import org.seasar.kvasir.base.webapp.extension.StaticContentElement;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.webapp.processor.RequestProcessor;
import org.seasar.kvasir.webapp.processor.RequestProcessorChain;
import org.seasar.kvasir.webapp.util.ServletUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class StaticContentProcessor
    implements RequestProcessor
{
    private WebappPlugin plugin_;

    private MimePlugin mimePlugin_;

    private ServletContext context_;

    private Map<String, Content> contentByPathMap_ = Collections
        .synchronizedMap(new HashMap<String, Content>());


    public void setPlugin(WebappPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setMimePlugin(MimePlugin mimePlugin)
    {
        mimePlugin_ = mimePlugin;
    }


    public void registerStaticContent(String path, Content content)
    {
        contentByPathMap_.put(path, content);
    }


    /*
     * RequestProcessor
     */

    public void init(ServletConfig config)
    {
        context_ = config.getServletContext();
        MimeMappings mappings = mimePlugin_.getMimeMappings();

        StaticContentElement[] staticContents = plugin_
            .getExtensionElements(StaticContentElement.class);
        for (int i = 0; i < staticContents.length; i++) {
            String[] paths = staticContents[i].getExpandedPaths();
            for (int j = 0; j < paths.length; j++) {
                registerStaticContent(paths[j], staticContents[i].getContent(
                    paths[j], mappings));
            }
        }
    }


    public void destroy()
    {
        plugin_ = null;

        context_ = null;
        contentByPathMap_.clear();
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, RequestProcessorChain chain)
        throws ServletException, IOException
    {
        String path = ServletUtils.getPath(request);
        Content content = contentByPathMap_.get(path);
        if (content != null) {
            String contentType = content.getContentType();
            if (contentType == null) {
                contentType = context_.getMimeType(path);
            }
            if (contentType != null) {
                response.setContentType(contentType);
            }
            response.setDateHeader("Last-Modified", content
                .getLastModifiedTime());
            // responseのoutputStreamのクローズ処理はコンテナに任せるのが吉なので
            // 閉じないように指定している。
            IOUtils.pipe(content.getInputStream(), response.getOutputStream(),
                true, false);
            return;
        }

        chain.doProcess(request, response);
    }
}
