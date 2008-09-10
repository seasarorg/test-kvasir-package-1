package org.seasar.kvasir.base.mail.impl;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.base.mail.MailPlugin;
import org.seasar.kvasir.base.mail.setting.MailPluginSettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.plugin.SettingsEvent;
import org.seasar.kvasir.base.plugin.SettingsListener;

import com.ozacc.mail.SendMail;
import com.ozacc.mail.impl.SendMailImpl;

public class MailPluginImpl extends AbstractPlugin<MailPluginSettings>
		implements MailPlugin {
	private Map<String, SendMail> sendMailMap_;

	@Override
	public Class<MailPluginSettings> getSettingsClass() {
		return MailPluginSettings.class;
	}

	protected boolean doStart() {
		addSettingsListener(new SettingsListener<MailPluginSettings>() {
			public void notifyUpdated(SettingsEvent<MailPluginSettings> event) {
				Map<String, SendMail> sendMailMap = new HashMap<String, SendMail>();
				MailPluginSettings settings = event.getNewSettings();
				org.seasar.kvasir.base.mail.setting.SendMail[] sendMails = settings
						.getSendMails();
				for (int i = 0; i < sendMails.length; i++) {
					sendMailMap.put(sendMails[i].getId(),
							createSendMailObject(sendMails[i]));
				}
				sendMailMap_ = sendMailMap;
			}
		});
		return true;
	}

	protected void doStop() {
		sendMailMap_ = null;
	}

	SendMail createSendMailObject(
			org.seasar.kvasir.base.mail.setting.SendMail sendMail) {
		SendMailImpl sendMailObject = getComponentContainer().getComponent(
				SendMailImpl.class);
		if (sendMail.getConnectionTimeout() != null) {
			sendMailObject.setConnectionTimeout(sendMail.getConnectionTimeout()
					.intValue());
		}
		sendMailObject.setHost(sendMail.getHost());
		if (sendMail.getMessageId() != null) {
			sendMailObject.setMessageId(sendMail.getMessageId());
		}
		if (sendMail.getPassword() != null) {
			sendMailObject.setPassword(sendMail.getPassword());
		}
		if (sendMail.getPort() != null) {
			sendMailObject.setPort(sendMail.getPort().intValue());
		}
		if (sendMail.getProtocol() != null) {
			sendMailObject.setProtocol(sendMail.getProtocol());
		}
		if (sendMail.getReadTimeout() != null) {
			sendMailObject.setReadTimeout(sendMail.getReadTimeout().intValue());
		}
		if (sendMail.getReturnPath() != null) {
			sendMailObject.setReturnPath(sendMail.getReturnPath());
		}
		if (sendMail.getUsername() != null) {
			sendMailObject.setUsername(sendMail.getUsername());
		}

		return sendMailObject;
	}

	public SendMail getDefaultSendMail() {
		return getSendMail(SENDMAIL_ID_DEFAULT);
	}

	public SendMail getSendMail(String id) {
		return sendMailMap_.get(id);
	}
}
