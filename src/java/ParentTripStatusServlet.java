import dal.ManifestDAO;
import dal.StudentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ManifestStudent;
import model.Student;
import model.UserAccount;
import util.SessionUtil;

public class ParentTripStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserAccount user = SessionUtil.getCurrentUser(request.getSession(false));
        Date today = new Date(System.currentTimeMillis());

        StudentDAO studentDAO = new StudentDAO();
        ManifestDAO manifestDAO = new ManifestDAO();

        List<Student> students = studentDAO.getStudentsByParent(user.getUserId());
        Map<Integer, ManifestStudent> statusMorningMap = new HashMap<>();
        Map<Integer, ManifestStudent> statusAfternoonMap = new HashMap<>();

        for (Student student : students) {
            statusMorningMap.put(student.getStudentId(), manifestDAO.getStudentTripStatus(student.getStudentId(), today, "MORNING"));
            statusAfternoonMap.put(student.getStudentId(), manifestDAO.getStudentTripStatus(student.getStudentId(), today, "AFTERNOON"));
        }

        request.setAttribute("pageTitle", "Parent Trip Status");
        request.setAttribute("students", students);
        request.setAttribute("statusMorningMap", statusMorningMap);
        request.setAttribute("statusAfternoonMap", statusAfternoonMap);
        request.getRequestDispatcher("/parent/trip-status.jsp").forward(request, response);
    }
}
