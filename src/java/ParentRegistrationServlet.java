import dal.ManifestDAO;
import dal.RegistrationDAO;
import dal.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DailyTripRegistration;
import model.ManifestStudent;
import model.Student;
import model.UserAccount;
import util.DemoTimeUtil;
import util.SessionUtil;
import util.TimeRuleUtil;

public class ParentRegistrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        int studentId = Integer.parseInt(request.getParameter("studentId"));
        String sessionType = request.getParameter("sessionType");
        String attendanceChoice = request.getParameter("attendanceChoice");
        String note = request.getParameter("note");

        LocalTime demoTime = DemoTimeUtil.getDemoTime(request.getSession());
        boolean allow = "MORNING".equalsIgnoreCase(sessionType)
                ? TimeRuleUtil.canParentEditMorning(demoTime)
                : TimeRuleUtil.canParentEditAfternoon(demoTime);

        if (!allow) {
            SessionUtil.setError(request.getSession(), "Registration for " + sessionType + " cannot be modified at this time.");
            response.sendRedirect(request.getContextPath() + "/parent/registration");
            return;
        }

        RegistrationDAO registrationDAO = new RegistrationDAO();
        boolean ok = registrationDAO.saveOrUpdateRegistration(studentId,
                new Date(System.currentTimeMillis()),
                sessionType,
                attendanceChoice,
                "PARENT",
                note);

        if (ok) {
            SessionUtil.setSuccess(request.getSession(), "Registration saved for student.");
        } else {
            SessionUtil.setError(request.getSession(), "Failed to save registration.");
        }
        response.sendRedirect(request.getContextPath() + "/parent/registration");
    }

    private void loadPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        Date today = new Date(System.currentTimeMillis());
        LocalTime demoTime = DemoTimeUtil.getDemoTime(request.getSession());

        StudentDAO studentDAO = new StudentDAO();
        RegistrationDAO registrationDAO = new RegistrationDAO();
        ManifestDAO manifestDAO = new ManifestDAO();

        List<Student> students = studentDAO.getStudentsByParent(user.getUserId());
        Map<Integer, DailyTripRegistration> morningMap = registrationDAO.getRegistrationsByParent(user.getUserId(), today, "MORNING");
        Map<Integer, DailyTripRegistration> afternoonMap = registrationDAO.getRegistrationsByParent(user.getUserId(), today, "AFTERNOON");
        Map<Integer, ManifestStudent> statusMorningMap = new HashMap<>();
        Map<Integer, ManifestStudent> statusAfternoonMap = new HashMap<>();

        for (Student student : students) {
            statusMorningMap.put(student.getStudentId(), manifestDAO.getStudentTripStatus(student.getStudentId(), today, "MORNING"));
            statusAfternoonMap.put(student.getStudentId(), manifestDAO.getStudentTripStatus(student.getStudentId(), today, "AFTERNOON"));
        }

        request.setAttribute("pageTitle", "Parent Registration");
        request.setAttribute("students", students);
        request.setAttribute("morningMap", morningMap);
        request.setAttribute("afternoonMap", afternoonMap);
        request.setAttribute("statusMorningMap", statusMorningMap);
        request.setAttribute("statusAfternoonMap", statusAfternoonMap);
        request.setAttribute("canEditMorning", TimeRuleUtil.canParentEditMorning(demoTime));
        request.setAttribute("canEditAfternoon", TimeRuleUtil.canParentEditAfternoon(demoTime));
        request.setAttribute("timeHint", TimeRuleUtil.getParentHint(demoTime));
        request.getRequestDispatcher("/parent/registration.jsp").forward(request, response);
    }
}
