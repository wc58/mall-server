package top.chao58.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class EventLog {

    // 请求
    private String ip;
    private Integer port;
    private String servletPath;
    private Map<String, String[]> params;
    private Integer status;


    // 客户端
    private String browser;
    private String operationSystem;

    // 其他
    private Long totalTime;
    private Date crateTime;

    @Override
    public String toString() {
        return "EventLog{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", servletPath='" + servletPath + '\'' +
                ", params=" + params.toString() +
                ", status=" + status +
                ", browser='" + browser + '\'' +
                ", operationSystem='" + operationSystem + '\'' +
                ", totalTime=" + totalTime +
                ", crateTime=" + crateTime +
                '}';
    }
}
