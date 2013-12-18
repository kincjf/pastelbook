package engine.index.dao.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import engine.index.vo.IndexDataBean;

public class IndexDAO {

	// column name, table name은 대문자로 쓰기!!

	DataSource ds;
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	public IndexDAO() {
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env");
			ds = (DataSource) envCtx.lookup("jdbc/SearchEngine");
			// con = ds.getConnection();
		} catch (Exception ex) {
			System.out.println("IndexDAO - Connection Error");
			ex.printStackTrace();
			return;
		}
	}

	public void connection() {
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			System.err.println("IndexDAO - connection err");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		if (con != null)
			try {
				con.close();
			} catch (SQLException ex) {
				System.err.println("IndexDAO - close err");
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
	}

	public void setAutoCommit(boolean isCommit) {
		try {
			con.setAutoCommit(isCommit);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("IndexDAO - setAutoCommit err");
			e.printStackTrace();
		}
	}

	public int getURLListCount() {
		int x = 0;
		String sql = "select COUNT(IDX_NUM) from URL_DATA";

		try {
			// db access가 많으므로 접속관련은 직접 처리한다. //this.con = ds.getConnection();
			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				x = rs.getInt(1);
			}

		} catch (Exception ex) {
			System.out.println("IndexDAO getListCount - error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getListCount rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - getListCount pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return x;
	}

	public ArrayList<String> getURLList() {
		String sql = "select URL from URL_DATA";

		ArrayList<String> list = new ArrayList<String>();

		try {
			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(rs.getString("URL"));
			}

			return list;
		} catch (Exception ex) {
			System.out.println("IndexDAO - getURLList error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getURLList rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getURLList pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return list;		// 결과가 없더라도 일단 객체를 반환하고 본다.
	}

	
	public ArrayList<Integer> getIdxNumList() {
		String sql = "select IDX_NUM from URL_DATA";

		ArrayList<Integer> list = new ArrayList<Integer>();

		try {
			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(rs.getInt("IDX_NUM"));
			}

			return list;
		} catch (Exception ex) {
			System.out.println("IndexDAO - getIdxNumList error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getIdxNumList rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getIdxNumList pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return list;		// 결과가 없더라도 일단 객체를 반환하고 본다.
	}
	
	
	public int getHit(String url) {
		String sql = "select HIT from URL_DATA where URL = ?";

		int hit = 0;

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, url);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				hit = rs.getInt("HIT");
			}

			return hit;
		} catch (Exception ex) {
			System.out.println("IndexDAO - getHit error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getHit rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - getHit pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return hit;		// 실패 - 0 반환.
	}
	
	
	public boolean InsertIndexData(IndexDataBean indexDataBean) {
		String sql = null;

		int result = 0;

		try {
			sql = "insert into url_data "
					+ "(IDX_NUM, URL, TITLE, MODIFIED_DATE, REGISTERED_DATE, HIT) "
					+ "values(?, ?, ?, ?, ?, ?)";

			pstmt = con.prepareStatement(sql);
			
			pstmt.setInt(1, indexDataBean.getIdxNum()); // IDX_NUM
			pstmt.setString(2, indexDataBean.getUrl()); // URL
			pstmt.setString(3, indexDataBean.getTitle()); // TITLE
			pstmt.setString(4, indexDataBean.getModifiedDate());
			pstmt.setString(5, indexDataBean.getRegisteredDate()); // REGISTERED_DATE
			pstmt.setInt(6, indexDataBean.getHit()); // REGISTERED_DATE
			
			result = pstmt.executeUpdate();

			if (result == 0) {
				return false;
			}

			return true;

		} catch (Exception ex) {
			System.out.println("IndexDAO - IndexInsert() Error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexInsert rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexInsert rs close err");
					ex.printStackTrace();
				}
			}
		}

		return false;
	}

	public boolean IndexModify(IndexDataBean indexDataBean) throws Exception {
		String sql = "update URL_DATA set HIT = ? where URL = ?";

		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, indexDataBean.getHit());
			pstmt.setString(2, indexDataBean.getUrl());

			pstmt.executeUpdate();
			return true;
		} catch (Exception ex) {
			System.out.println("IndexDAO - IndexModify() Error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexModify rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - IndexModify pstmt close err");
					ex.printStackTrace();
				}
			}
		}
		return false;
	}

	public int IndexHitModify(String url) {		// 실패시 0, 성공시 갱신된 값 반환
		String sql = "update URL_DATA set HIT = ((SELECT HIT FROM URL_DATA WHERE URL = ? ) + 1) where URL = ?";
		// 기존에 비해 1만큼 증가시킨다.
		int updateHit = 0;
		
		try {
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, url);
			pstmt.setString(2, url);

			if(pstmt.executeUpdate() == 0) {		// 실패
				updateHit = 0;
			} else {		// 성공
				updateHit = this.getHit(url);
			}
			
			return updateHit;
		} catch (Exception ex) {
			System.out.println("IndexDAO - IndexHitModify() Error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - IndexHitModify rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - IndexHitModify pstmt close err");
					ex.printStackTrace();
				}
			}
		}
		return updateHit;
	}

	public boolean InsertContributor(String contributor) {
		String sql = null;
		boolean isExist = false;
		int result = 0;		// 0 - 실패, 1이상(DML을 수행한 수) - 성공

		try {
			isExist = this.isExistContributor(contributor);		// 등록이 되어 있는가(중복검사)

			if (isExist) { // 이미 등록되어 있음 OR 실패
				System.out.println("IndexDAO - InsertContributor \n "
						+ contributor + " is already exist.");
			} else { // 없음
				sql = "insert into CONTRIBUTOR (ID) values(?)";

				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, contributor); // contributor 추가

				result = pstmt.executeUpdate();

				if (result == 0) {
					return false;		// 실패
				}
			}

			return true;		// 성공

		} catch (Exception ex) {
			System.out.println("IndexDAO - IndexInsert() Error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexInsert rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - IndexInsert rs close err");
					ex.printStackTrace();
				}
			}
		}

		return false;
	}

	
	public boolean InsertRegisterMember(String url, String register) {
		String sql = null;
		boolean isExist = false;
		int result = 0;		// 0 - 실패, 1이상(DML을 수행한 수) - 성공

		try {
			isExist = this.isExistRegisterMember(url, register);		// 등록이 되어 있는가(중복검사)

			if (isExist) { // 이미 등록되어 있음 OR 실패
				System.out.println("IndexDAO - InsertRegisterMember \n "
						+ register + " is already exist.");
			} else { // 없음
				sql = "insert into REGISTER_MEMBER (URL, ID) values(?, ?)";

				pstmt = con.prepareStatement(sql);

				pstmt.setString(1, url); // register_member 추가
				pstmt.setString(2, register); // register_member 추가
				
				result = pstmt.executeUpdate();

				if (result == 0) {
					return false;		// 실패
				} else {
					return true;		// 성공
				}
			}

		} catch (Exception ex) {
			System.out.println("IndexDAO - InsertRegisterMember() Error");
			// ex.printStackTrace();		// 거슬려서 일단 지웠음..
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - InsertRegisterMember rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - InsertRegisterMember rs close err");
					ex.printStackTrace();
				}
			}
		}

		return false;		// 실패
	}
	
	
	public boolean isExistRegisterMember(String url, String register) {
		String sql = "select ID from REGISTER_MEMBER WHERE URL = ? AND ID = ?";
		boolean isExist = false;

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, url);
			pstmt.setString(2, register);
			
			rs = pstmt.executeQuery();

			if (rs.next()) { // 이미 등록되어 있음
				isExist = true;
			} else { // 없음
				isExist = false;
			}

			return isExist;
		} catch (Exception ex) {
			System.out.println("IndexDAO - isExistRegisterMember error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - isExistRegisterMember rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err.println("IndexDAO - isExistRegisterMember pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return false;
	}

	public boolean isExistContributor(String id) {
		String sql = "select ID from CONTRIBUTOR WHERE ID = ?";
		boolean isExist = false;

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id); // contributor 추가

			rs = pstmt.executeQuery();

			if (rs.next()) { // 이미 등록되어 있음
				isExist = true;
			} else { // 없음
				isExist = false;
			}

			return isExist;
		} catch (Exception ex) {
			System.out.println("IndexDAO - isExistContributor error");
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - isExistContributor rs close err");
					ex.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					System.err
							.println("IndexDAO - isExistContributor pstmt close err");
					ex.printStackTrace();
				}
			}
		}

		return false;
	}
}
