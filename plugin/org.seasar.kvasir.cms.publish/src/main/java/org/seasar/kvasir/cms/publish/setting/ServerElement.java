package org.seasar.kvasir.cms.publish.setting;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Required;

public class ServerElement {
    private String id;

    private String hostName;

    private String userName;

    private String password;

    private String passphrase;

    private String privateKey;

    public String getId() {
        return id;
    }

    @Attribute
    @Required
    public void setId(String id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    @Attribute
    @Required
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getUserName() {
        return userName;
    }

    @Attribute
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    @Attribute
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassphrase() {
        return passphrase;
    }

    @Attribute
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    @Attribute
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
