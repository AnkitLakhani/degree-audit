package crud;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import pojo.ProgramPOJO;
import util.GeneralUtility;
import util.HibernateSessionFactory;
import util.Response;

public class ProgramCRUD extends CRUDCore {

	@Override
	public Integer create(HttpServletRequest request) {
		Session session = HibernateSessionFactory.getSession();
		Transaction tx = null;
		Integer id = null;
		try {
			tx = session.beginTransaction();
			String name = request.getParameter("name");
			Integer min_duration = Integer.parseInt(request.getParameter("min_duration"));
			Integer max_duration = Integer.parseInt(request.getParameter("max_duration"));
			Integer min_credits = Integer.parseInt(request.getParameter("min_credits"));
			Integer max_credits = Integer.parseInt(request.getParameter("max_credits"));
			Integer min_grade_points = Integer.parseInt(request.getParameter("min_grade_points"));
			Float min_cpi = Float.parseFloat(request.getParameter("min_cpi"));
			Integer min_foundation_course = Integer.parseInt(request.getParameter("min_foundation_course"));
			Integer min_courses = Integer.parseInt(request.getParameter("min_courses"));
			Integer max_courses = Integer.parseInt(request.getParameter("max_courses"));
			ProgramPOJO program = new ProgramPOJO(name, min_duration, max_duration, min_credits, max_credits,
					min_grade_points, min_cpi, min_foundation_course, min_courses, max_courses);
			id = (Integer) session.save(program);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return id;
	}

	@Override
	public Object retrive(HttpServletRequest request) {
		Response response = null;
		try {
			List<ProgramPOJO> programs = session.createQuery("FROM ProgramPOJO").list();
			response = GeneralUtility.generateSuccessResponse(null, programs);
		} catch (HibernateException e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return response.toString();
	}

	@Override
	public Integer update(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
}
