package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.UserAccount;
import util.SessionUtil;

public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        UserAccount user = SessionUtil.getCurrentUser(req.getSession(false));
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String servletPath = req.getServletPath();
        String roleName = user.getRoleName() == null ? "" : user.getRoleName().toUpperCase();

        boolean allow = true;

        if (servletPath.startsWith("/parent/")) {
            allow = "PARENT".equals(roleName);
        } else if (servletPath.startsWith("/manager/")) {
            allow = "MANAGER".equals(roleName);
        } else if (servletPath.startsWith("/driver/")) {
            allow = "DRIVER".equals(roleName);
        } else if (servletPath.startsWith("/admin/")) {
            allow = "ADMIN".equals(roleName);
        }

        if (allow) {
            chain.doFilter(request, response);
        } else {
            resp.sendRedirect(req.getContextPath() + SessionUtil.redirectDashboard(user));
        }
    }

    @Override
    public void destroy() {
    }
}
