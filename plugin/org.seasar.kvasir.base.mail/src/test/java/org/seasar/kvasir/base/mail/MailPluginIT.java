package org.seasar.kvasir.base.mail;

import junit.framework.Test;

import org.seasar.kvasir.test.KvasirPluginTestCase;

public class MailPluginIT extends KvasirPluginTestCase<MailPlugin> {
	protected String getTargetPluginId() {
		return MailPlugin.ID;
	}

	public static Test suite() throws Exception {
		return createTestSuite(MailPluginIT.class);
	}

	public void testGetDefaultSendMail() throws Exception {
		assertNotNull(getPlugin().getDefaultSendMail());
	}
}
