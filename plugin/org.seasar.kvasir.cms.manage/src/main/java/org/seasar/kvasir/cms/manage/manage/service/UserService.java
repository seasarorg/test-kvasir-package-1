package org.seasar.kvasir.cms.manage.manage.service;

import java.util.Map;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.page.type.UserMold;
import org.seasar.kvasir.util.PropertyUtils;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class UserService extends GenericPageService
{
    public static final String FIELD_PASSWORD = "password";

    public static final String FIELD_MAILADDRESSES = "mailAddresses";

    private AuthPlugin authPlugin_;


    /*
     * PageService
     */

    public Page createPage(Page parent, PageMold mold,
        Map<String, String[]> fieldMap)
        throws DuplicatePageException
    {
        String mailAddresses = getString(fieldMap, FIELD_MAILADDRESSES);
        if (mailAddresses != null) {
            ((UserMold)mold).setMailAddresses(PropertyUtils
                .toLines(mailAddresses));
        }

        User user = (User)super.createPage(parent, mold, fieldMap);

        String password = getString(fieldMap, FIELD_PASSWORD);
        if (password != null) {
            changePassword(user, password);
        }

        return user;
    }


    @Override
    public Notes updatePage(Page page, Map<String, String[]> fieldMap)
    {
        User user = (User)page;

        String mailAddresses = getString(fieldMap, FIELD_MAILADDRESSES);
        if (mailAddresses != null) {
            user.setMailAddresses(PropertyUtils.toLines(mailAddresses));
        }

        String password = getString(fieldMap, FIELD_PASSWORD);
        if (password != null) {
            if (password.length() == 0) {
                return new Notes().add(new Note(
                    "app.error.userService.passwordIsEmpty"));
            }
            changePassword(user, password);
        }

        return super.updatePage(page, fieldMap);
    }


    /*
     * private scope methods
     */

    private void changePassword(User user, String password)
    {
        authPlugin_.getDefaultAuthSystem().changePassword(user, password);
    }


    /*
     * for framework
     */

    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }
}
