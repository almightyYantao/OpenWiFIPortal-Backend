package com.qunhe.its.networkportal.common;

public enum PortalAcErrorCode {
    NO_MEANING(-1),

    CHALLENGE_REQUEST(0x02, 0, "成功"),
    CHALLENGE_REQUEST_REJECTED(0x02, 0x01, "Challenge请求被拒绝"),
    CONNECTION_ESTABLISHED(0x02, 0x02, "链接已建立"),
    AUTH_IN_PROGRESS(0x02, 0x03, "有一个用户正在认证过程中，请稍后再试"),
    USER_CHALLENGE_FAILED(0x02, 0x04, "用户Challenge请求失败"),
    USER_NOT_FOUND(0x02, 0xfd, "未找到此用户（用户已漫游或下线）"),

    USER_AUTH_SUCCESSFUL(0x04, 0, "用户认证成功"),
    USER_AUTH_REJECTED(0x04, 0x01, "用户认证请求被拒绝"),
    USER_AUTH_CONNECTION_ESTABLISHED(0x04, 0x02, "链接已建立"),
    USER_AUTH_IN_PROGRESS(0x04, 0x03, "有一个用户正在认证过程中，请稍后再试"),
    USER_AUTH_FAILED(0x04, 0x04, "用户认证失败（发生错误，例如用户名错误）"),
    MAX_USERS_REACHED(0x04, 0x05, "用户认证失败（Portal在线用户数已达到最大值）"),
    OTHER_AUTH_IN_PROGRESS(0x04, 0x06, "用户认证失败（设备正在对该终端进行其他方式的认证）"),

    LOGOUT_REQUEST(0x05, 0, "Portal服务器发给接入设备的请求下线报文"),
    TIMEOUT_REQUEST(0x05, 0x01, "Portal服务器没有收到接入设备对各种请求的响应报文时，定时器超时后Portal服务器发给接入设备的报文"),

    USER_LOGOUT_SUCCESSFUL(0x06, 0, "用户下线成功"),
    USER_LOGOUT_REJECTED(0x06, 0x01, "用户下线被拒绝"),
    USER_LOGOUT_FAILED(0x06, 0x02, "用户下线失败"),

    FORCED_LOGOUT(0x08, 0x02, "用户被强制下线"),

    INQUIRY_MESSAGE_SUCCESS(0x0a, 0, "询问消息处理成功"),
    INQUIRY_MESSAGE_UNSUPPORTED(0x0a, 0x01, "询问消息处理失败（不支持该功能）"),
    INQUIRY_MESSAGE_ERROR(0x0a, 0x02, "询问消息处理失败（发生错误，例如询问消息格式错误）"),

    SMS_MESSAGE_ERROR(0x0a, 0x01, "短信验证码错误"),

    WXWORK_MESSAGE_ERROR(0x0a, 0x01, "企业微信认证错误，请联系内部员工进行认证");

    private final int type;
    private final int[] validErrCodeValues;
    private final String description;

    PortalAcErrorCode(int type, int... validErrCodeValues) {
        this.type = type;
        this.validErrCodeValues = validErrCodeValues;
        this.description = null;
    }

    PortalAcErrorCode(int type, int validErrCodeValue, String description) {
        this.type = type;
        this.validErrCodeValues = new int[]{validErrCodeValue};
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static PortalAcErrorCode getByTypeAndErrCode(int type, int errCode) {
        for (PortalAcErrorCode errorCode : values()) {
            if (errorCode.type == type && containsValue(errorCode.validErrCodeValues, errCode)) {
                return errorCode;
            }
        }
        return NO_MEANING;
    }

    public static PortalAcErrorCode parse(byte[] data) {
        if (data == null || data.length < 14) {
            return PortalAcErrorCode.NO_MEANING;
        }

        // 从字节数组中提取第2个和第14个字节
        byte typeByte = data[1];
        byte errCodeByte = data[14];

        // 将字节转换为无符号整数
        int type = typeByte & 0xFF;
        int errCode = errCodeByte & 0xFF;

        // 调用枚举的方法获取对应的错误码
        return PortalAcErrorCode.getByTypeAndErrCode(type, errCode);
    }

    private static boolean containsValue(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }
}
