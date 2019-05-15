package com.commonutils.util.properties;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QryPropertiesConfig {

    private static final Log log= LogFactory.getLog(QryPropertiesConfig.class );
    private static String configDIR = System.getProperty("user.dir");
    private static String separator = System.getProperty("file.separator");

    public static String  getPropertyById(String id) {
        try {
            String configFile = configDIR + separator + "config.properties";
            PropertiesConfiguration cfg = new PropertiesConfiguration(configFile);
            //���������ļ���ȡ��ʽ,�Զ�loadģʽ,�޸��ļ����Բ������²��������
            cfg.setReloadingStrategy( new FileChangedReloadingStrategy());
            String val = cfg.getString(id);
            log.info("get properties by name:"+id+" value:"+val);
            return val;
        }catch (Exception err) {
            err.printStackTrace();
        }
        return "";
    }

    public static String getWeblogicRoot(String id) {
        try {
            String configFile = configDIR + separator+id;
            log.info("getWeblogicRoot==== :"+id+"== value:"+configFile);
            return configFile;
        }catch (Exception err) {
            err.printStackTrace();
        }
        return "";
    }
}
