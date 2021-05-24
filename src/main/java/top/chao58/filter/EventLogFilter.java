package top.chao58.filter;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.chao58.util.JacksonUtils;
import top.chao58.vo.EventLog;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class EventLogFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        int port = request.getRemotePort();
        String servletPath = request.getServletPath();
        Map<String, String[]> params = request.getParameterMap();
        int status = response.getStatus();

        UserAgent ua = UserAgentUtil.parse(request.getHeader("User-Agent"));

        EventLog eventLog = new EventLog();
        eventLog.setIp(ip);
        eventLog.setPort(port);
        eventLog.setServletPath(servletPath);
        eventLog.setParams(params);
        eventLog.setStatus(status);
        eventLog.setBrowser(ua.getBrowser() == null ? "" : ua.getBrowser().toString());
        eventLog.setOperationSystem(ua.getOs() == null ? "" : ua.getOs().toString());

        //todo-chao 2021/5/22 13:02：目前记录当前信息
        log.info("请求信息：{}", JacksonUtils.object2Json(eventLog));
        filterChain.doFilter(request, response);
    }
}
