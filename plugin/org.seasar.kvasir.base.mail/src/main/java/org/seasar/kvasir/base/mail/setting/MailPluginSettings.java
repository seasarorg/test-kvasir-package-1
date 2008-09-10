package org.seasar.kvasir.base.mail.setting;

import net.skirnir.xom.annotation.Child;

import org.seasar.kvasir.base.util.ArrayUtils;

public class MailPluginSettings {
	private SendMail[] sendMails_ = new SendMail[0];

	public SendMail[] getSendMails() {
		return sendMails_;
	}

	@Child
	public void addSendMail(SendMail sendMail) {
		sendMails_ = ArrayUtils.add(sendMails_, sendMail);
	}
}
