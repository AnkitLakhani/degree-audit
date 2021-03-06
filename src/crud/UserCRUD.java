package crud;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.HibernateException;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import pojo.UserCategoryPOJO;
import pojo.UserPOJO;

import util.GeneralUtility;
import util.UserRole;

public class UserCRUD extends CRUDCore {

	public UserCRUD() {
		response = GeneralUtility.generateUnauthorizedResponse();
	}

	@Override
	public Integer create(HttpServletRequest request) {
		Integer id = null;
		try {
			String username = request.getParameter("username");
			String password = BCrypt.hashpw(request.getParameter("password"), BCrypt.gensalt(12));
			UserCategoryPOJO category = session.get(UserCategoryPOJO.class,
					Integer.parseInt(request.getParameter("category")));
			short is_active = 1;
			UserPOJO user = new UserPOJO(username, password, category, is_active);
			id = (Integer) session.save(user);
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
		try {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<UserPOJO> criteria = builder.createQuery(UserPOJO.class);
			Root<UserPOJO> userPOJORoot = criteria.from(UserPOJO.class);
			criteria.select(userPOJORoot);
			criteria.where(builder.equal(userPOJORoot.get("username"), username));
			List<UserPOJO> users = session.createQuery(criteria).getResultList();
			if (!users.isEmpty()) {
				UserPOJO userPOJO = users.get(0);
				String hashed = userPOJO.getPassword();
				if (BCrypt.checkpw(password, hashed)) {
					UserCategoryPOJO userCategory = userPOJO.getUserCategory();
					String category = userCategory.getCategory();
					String authJSON = GeneralUtility.readAuthJSON();
					JsonParser parser = new JsonParser();
					JsonArray data = parser.parse(authJSON).getAsJsonArray();
					for (JsonElement element : data) {
						UserRole role = json.fromJson(element, UserRole.class);
						if (role.getRole().equalsIgnoreCase(category)) {
							response = GeneralUtility.generateSuccessResponse(role.getHome(), null);
							break;
						}
					}
					HttpSession session = request.getSession();
					session.setAttribute("userCategory", category);
				}				
			}
		} catch (HibernateException e) {
			if (tx != null) {
				tx.rollback();
			}
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
