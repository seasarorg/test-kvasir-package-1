package org.seasar.kvasir.base.mail.setting;

import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;

public class SendMail {
	private String id_;

	private Integer connectionTimeout_;

	private String host_;

	private String messageId_;

	private String password_;

	private Integer port_;

	private String protocol_;

	private Integer readTimeout_;

	private String returnPath_;

	private String username_;

	public Integer getConnectionTimeout() {
		return connectionTimeout_;
	}

	public String getId() {
		return id_;
	}

	@Child
	@Required
	public void setId(String id) {
		id_ = id;
	}

	@Child
	public void setConnectionTimeout(Integer connectionTimeout) {
		connectionTimeout_ = connectionTimeout;
	}

	public String getHost() {
		return host_;
	}

	@Child
	@Required
	public void setHost(String host) {
		host_ = host;
	}

	public String getMessageId() {
		return messageId_;
	}

	@Child
	public void setMessageId(String messageId) {
		messageId_ = messageId;
	}

	public String getPassword() {
		return password_;
	}

	@Child
	public void setPassword(String password) {
		password_ = password;
	}

	public Integer getPort() {
		return port_;
	}

	@Child
	public void setPort(Integer port) {
		port_ = port;
	}

	public String getProtocol() {
		return protocol_;
	}

	@Child
	public void setProtocol(String protocol) {
		protocol_ = protocol;
	}

	public Integer getReadTimeout() {
		return readTimeout_;
	}

	@Child
	public void setReadTimeout(Integer readTimeout) {
		readTimeout_ = readTimeout;
	}

	public String getReturnPath() {
		return returnPath_;
	}

	@Child
	public void setReturnPath(String returnPath) {
		returnPath_ = returnPath;
	}

	public String getUsername() {
		return username_;
	}

	@Child
	public void setUsername(String username) {
		username_ = username;
	}
}
