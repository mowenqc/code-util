package com.ytx.util.domain.enums;

/**
 * Created by chutao on 2017/12/13.
 */
public enum OperateSystem {
    WINDOWS(1,"windows","script/checkout.cmd","script/build.cmd"),
    LINUX(2,"linux","script/checkout.sh","script/build.sh");

    private int value;
    private String descript;
    private String checkoutScript;
    private String buildScript;

    OperateSystem(int value, String descript, String checkoutScript,String buildScript) {
        this.value = value;
        this.descript = descript;
        this.checkoutScript = checkoutScript;
        this.buildScript = buildScript;
    }

    public int getValue() {
        return value;
    }

    public String getDescript() {
        return descript;
    }

    public String getCheckoutScript() {
        return checkoutScript;
    }

    public String getBuildScript() {
        return buildScript;
    }

    //如果找不到，默认配置linux
    public static OperateSystem parseOperateSystem(String descript){
        for(OperateSystem operateSystem : OperateSystem.values()){
            if(operateSystem.getDescript().equals(descript)){
                return operateSystem;
            }
        }
        return null;
    }
}
