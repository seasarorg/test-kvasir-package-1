package org.seasar.kvasir.base.mail.setting;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;

public class MailPluginSettingsTest extends XOMBeanTestCase {
	public void testToXML() throws Exception {
		assertBeanEquals("<mail-plugin-settings />", new MailPluginSettings());
	}

	public void testToXML2() throws Exception {
		MailPluginSettings target = new MailPluginSettings();
		SendMail sendMail = new SendMail();
		sendMail.setConnectionTimeout(100);
		sendMail.setHost("localhost");
		sendMail.setId("default");
		sendMail.setMessageId("messageId");
		sendMail.setPassword("password");
		sendMail.setPort(200);
		sendMail.setProtocol("smtp");
		sendMail.setReadTimeout(300);
		sendMail.setReturnPath("returnPath");
		sendMail.setUsername("username");
		target.addSendMail(sendMail);
		sendMail = new SendMail();
		sendMail.setHost("localhost");
		sendMail.setId("custom");
		target.addSendMail(sendMail);

		assertBeanEquals("<mail-plugin-settings>" + "<send-mail>"
				+ "<connection-timeout>100</connection-timeout>"
				+ "<host>localhost</host>" + "<id>default</id>"
				+ "<message-id>messageId</message-id>"
				+ "<password>password</password>" + "<port>200</port>"
				+ "<protocol>smtp</protocol>"
				+ "<read-timeout>300</read-timeout>"
				+ "<return-path>returnPath</return-path>"
				+ "<username>username</username>" + "</send-mail>"
				+ "<send-mail>" + "<host>localhost</host>" + "<id>custom</id>"
				+ "</send-mail>" + "</mail-plugin-settings>", target);
	}
}
