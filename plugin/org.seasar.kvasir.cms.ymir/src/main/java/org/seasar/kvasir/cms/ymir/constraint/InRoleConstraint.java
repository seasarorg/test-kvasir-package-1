package org.seasar.kvasir.cms.ymir.constraint;

import java.util.ArrayList;
import java.util.List;

import org.seasar.cms.ymir.ConstraintViolatedException;
import org.seasar.cms.ymir.PermissionDeniedException;
import org.seasar.cms.ymir.Request;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


public class InRoleConstraint extends AbstractPermissionConstraint
{
    private String[] absolutePaths_;

    private String[] relativePaths_;

    private PageAlfr pageAlfr_;


    public InRoleConstraint(String[] paths, PageAlfr pageAlfr,
        AuthPlugin pageAuthPlugin)
    {
        super(pageAuthPlugin);

        pageAlfr_ = pageAlfr;

        List<String> absolutePathList = new ArrayList<String>();
        List<String> relativePathList = new ArrayList<String>();
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            if (path.length() == 0) {
                throw new IllegalArgumentException("Illegal path: " + path);
            }
            char ch = path.charAt(0);
            if (ch == '/') {
                absolutePathList.add(path);
            } else if (ch == '@') {
                relativePathList.add(path.substring(1));
            } else {
                throw new IllegalArgumentException("Illegal path: " + path);
            }
        }
        absolutePaths_ = absolutePathList.toArray(new String[0]);
        relativePaths_ = relativePathList.toArray(new String[0]);
    }


    public void confirm(Object component, Request request)
        throws ConstraintViolatedException
    {
        User actor = getCurrentActor();

        PageRequest pageRequest = (PageRequest)request
            .getAttribute(CmsPlugin.ATTR_PAGEREQUEST);
        int heimId = pageRequest.getRootPage().getHeimId();
        for (int i = 0; i < absolutePaths_.length; i++) {
            if (confirm(absolutePaths_[i], actor, heimId)) {
                return;
            }
        }
        if (relativePaths_.length > 0) {
            Page lord = pageRequest.getMy().getNearestPage().getLord();
            for (int i = 0; i < relativePaths_.length; i++) {
                if (confirm(relativePaths_[i], actor, lord)) {
                    return;
                }
            }
        }
        throw new PermissionDeniedException();
    }


    boolean confirm(String relativePath, User actor, Page lord)
    {
        Role role = lord.getChild(Role.class, relativePath);
        if (role == null) {
            throw new IllegalArgumentException(
                "Specified path is not a role: gard-root-path="
                    + lord.getPathname() + ", relative-path=" + relativePath);
        }
        return role.isUserInRole(actor);
    }


    boolean confirm(String absolutePath, User actor, int heimId)
    {
        Role role = pageAlfr_.getPage(Role.class, heimId, absolutePath);
        if (role == null && heimId != PathId.HEIM_MIDGARD) {
            role = pageAlfr_.getPage(Role.class, PathId.HEIM_MIDGARD,
                absolutePath);
        }
        if (role == null) {
            throw new IllegalArgumentException("Specified path is not a role: "
                + absolutePath);
        }
        return role.isUserInRole(actor);
    }
}
