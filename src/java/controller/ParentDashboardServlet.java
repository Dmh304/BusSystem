package controller;
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

public class ParentDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

        request.setAttribute("pageTitle", "Parent Dashboard");
        request.setAttribute("students", students);
        request.setAttribute("morningMap", morningMap);
        request.setAttribute("afternoonMap", afternoonMap);
        request.setAttribute("statusMorningMap", statusMorningMap);
        request.setAttribute("statusAfternoonMap", statusAfternoonMap);
        request.setAttribute("canEditMorning", TimeRuleUtil.canParentEditMorning(demoTime));
        request.setAttribute("canEditAfternoon", TimeRuleUtil.canParentEditAfternoon(demoTime));
        request.setAttribute("timeHint", TimeRuleUtil.getParentHint(demoTime));
        request.getRequestDispatcher("/parent/dashboard.jsp").forward(request, response);
    }
}

