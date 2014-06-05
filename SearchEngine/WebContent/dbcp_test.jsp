<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*"%>
<%@ page import="javax.naming.*"%>
<%
	//1
%>
<%@ page errorPage="error.jsp"%>

<%
	Connection conn = null;

	PreparedStatement pstmt = null;

	ResultSet rs = null;

	try {

		// 큐브리드 DB에 Connect하기 위해서 JNDI를 통해 DataSourse를 가져온다.

		Context initContext = new InitialContext();//2

		DataSource ds = (DataSource) initContext
				.lookup("java:comp/env/jdbc/SearchEngine");//3

		conn = ds.getConnection();//4

		String sql = "select * from url_data";

		pstmt = conn.prepareStatement(sql);

		rs = pstmt.executeQuery();

		while (rs.next()) {

			out.println("board_book_professor ==> " + rs.getString(2));
			// index는 1부터 시작한다.

			out.println("<br>");

		}

	} catch (SQLException e) {

		System.err.println(e.getMessage());

	} catch (Exception e) {

		System.err.println(e.getMessage());

	} finally {

		try {

			if (rs != null)
				rs.close();

		} catch (Exception e) {
		}

		try {

			if (pstmt != null)
				pstmt.close();

		} catch (Exception e) {
		}

		try {

			if (conn != null)
				conn.close();

		} catch (Exception e) {
		}

	}
%>