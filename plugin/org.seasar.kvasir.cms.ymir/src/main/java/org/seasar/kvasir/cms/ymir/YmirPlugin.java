package org.seasar.kvasir.cms.ymir;

import org.seasar.cms.ymir.Ymir;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;


public interface YmirPlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.cms.ymir";

    String ID_PATH = ID.replace('.', '/');


    Ymir getYmir();


    YmirApplication getApplication(String gardId);


    YmirApplication[] getApplications();


    String[] getGardIds();


    boolean containsGardId(String gardId);


    /**
     * 指定されたIDを持つアプリケーションに関連付けられたApplicationMappingの配列を返します。
     * 
     * @param id アプリケーションのID。
     * @return ApplicationMappingの配列。
     * 関連付けられたApplicationMappingが存在しない場合は空の配列を返します。
     */
    ApplicationMapping[] getApplicationMappings(String id);
}
