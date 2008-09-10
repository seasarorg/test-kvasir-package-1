package org.seasar.kvasir.cms.processor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.util.io.IOUtils;


/**
 * 指定されたパスに対応するページが存在しない場合に静的ファイルを返す
 * PageProcessorです。
 * <p>対応する静的ファイルが存在しない場合は処理を次の
 * PageProcessorにフォワードします。</p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ResourcePageProcessor extends AbstractLocalPathPageProcessor
{
    private AuthPlugin authPlugin_;


    /*
     * AbstractLocalPathPageProcessor
     */

    protected boolean doProcessFile(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest, File file)
        throws ServletException, IOException
    {
        String mimeType = context_.getMimeType(pageRequest.getMy()
            .getLocalPathname());
        if (mimeType != null) {
            response.setContentType(mimeType);
        }
        response.setDateHeader("Last-Modified", file.lastModified());
        IOUtils.pipe(new FileInputStream(file), response.getOutputStream(),
            true, false);
        return true;
    }


    /*
     * PageProcessor
     */

    public void destroy()
    {
        super.destroy();

        authPlugin_ = null;
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        if (pageRequest.getMy().getPage() == null) {
            User actor = authPlugin_.getCurrentActor();
            PermissionAbility perm = (PermissionAbility)pageRequest.getMy()
                .getNearestPage().getAbility(PermissionAbility.class);
            if (perm.permits(actor, Privilege.ACCESS_VIEW)) {
                super.doProcess(request, response, pageRequest, chain);
                return;
            }
        }

        chain.doProcess(request, response, pageRequest);
    }


    /*
     * for framework
     */

    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }
}
