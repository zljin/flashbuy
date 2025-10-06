package com.zljin.flashbuy.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AuditLog {

    private String guid;

    private String httpMethod;

    private String requestPath;

    private String requestClass;

    private String requestMethod;

    private String requestParams;

    private String requestIp;

    private String userAgent;

    private String createTime;

    private long costTime;

    private String responseData;

    private boolean executeResult;

}
