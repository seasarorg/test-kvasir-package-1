package org.seasar.kvasir.cms.zpt;

/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface Globals
{
    String ID = "org.seasar.kvasir.cms.zpt";

    String ID_PATH = ID.replace('.', '/');
}
