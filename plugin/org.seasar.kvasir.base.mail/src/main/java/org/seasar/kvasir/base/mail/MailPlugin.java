package org.seasar.kvasir.base.mail;

import org.seasar.kvasir.base.mail.setting.MailPluginSettings;
import org.seasar.kvasir.base.plugin.Plugin;

import com.ozacc.mail.SendMail;


public interface MailPlugin
    extends Plugin<MailPluginSettings>
{
    String ID = "org.seasar.kvasir.base.mail";

    String ID_PATH = ID.replace('.', '/');

    String SENDMAIL_ID_DEFAULT = "default";


    /**
     * デフォルトの{@link SendMail}オブジェクトを返します。
     * <p>IDが"default"であるSendMailオブジェクトを返します。
     * </p>
     * 
     * @return デフォルトのSendMailオブジェクト。
     * nullが返されることがあります。
     */
    SendMail getDefaultSendMail();


    /**
     * 指定されたIDに対応する{@link SendMail}オブジェクトを返します。
     * 
     * @param id ID。
     * nullを指定してはいけません。
     * @return SendMailオブジェクト。
     * nullが返されることがあります。
     */
    SendMail getSendMail(String id);
}
