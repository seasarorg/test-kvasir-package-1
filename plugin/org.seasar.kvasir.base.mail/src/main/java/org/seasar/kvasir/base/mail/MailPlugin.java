package org.seasar.kvasir.base.mail;

import org.seasar.kvasir.base.mail.setting.MailPluginSettings;
import org.seasar.kvasir.base.plugin.Plugin;

import com.ozacc.mail.SendMail;

public interface MailPlugin extends Plugin<MailPluginSettings> {
	String ID = "org.seasar.kvasir.base.mail";

	String ID_PATH = ID.replace('.', '/');

	String SENDMAIL_ID_DEFAULT = "default";

	SendMail getDefaultSendMail();

	SendMail getSendMail(String id);
}
