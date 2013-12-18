package engine.index.action;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexFrontController extends javax.servlet.http.HttpServlet
		implements javax.servlet.Servlet {
	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;

	protected void doProcess(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String RequestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String command = RequestURI.substring(contextPath.length());

		ActionForward forward = null;
		Action action = null;
		// Indexing
		if (command.equals("/indexAction.if")) {
			action = new IndexAction();
			try {
				forward = action.execute(request, response);
			} catch (Exception e) {
				System.err.println("IndexFrontController - IndexAction");
				e.printStackTrace();
			}
		} 
		// view
		if (command.equals("/indexStatus.if")) {
			forward = new ActionForward();
			forward.setPath("./index/index_status.jsp");
		}
		
		if (forward != null) {

			if (forward.isRedirect()) {
				response.sendRedirect(forward.getPath());
			} else {
				RequestDispatcher dispatcher = request
						.getRequestDispatcher(forward.getPath());
				dispatcher.forward(request, response);
			}

		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("IndexFrontController");
		doProcess(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("IndexFrontController");
		doProcess(request, response);
	}
}