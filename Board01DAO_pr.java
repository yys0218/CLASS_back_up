package web.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
  연결 클래스를 상속
 */
public class Board01DAO_pr extends DBConnection { //데이터베이스와 직접 소통하는 객체
	//사용 객체의 변수 미리 선언
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql;

	//작성한 게시글을 데이터베이스에 등록하는 메서드
	public int insert(Board01DTO_pr dto) {
		int result=0;
		try {
			conn=getConn();//연결 객체 상속중이므로 객체생성필요x
			//insert into board01(컬럼,..) values(각 컬럼의 값,..) 시퀀스 사용: 시퀀스명.nextval(대소문자상관x)
			sql="insert into board01(num, writer, title,content,reg) values(board01_seq.NEXTVAL, ?,?,?,sysdate)";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,dto.getWriter());
			pstmt.setString(2, dto.getTitle());
			pstmt.setString(3,dto.getContent());
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn,pstmt,rs); //객체 연결 종료
		}
		return result;
	}//insert메서드 종료
	
	
	//글목록을 가져오는 메서드 - startRow,endRow로 보여지는 글 조절하도록 매개변수, sql문 수정
	public ArrayList<Board01DTO_pr> list(int start, int end){ //쿼리문 ? 채우는 데 사용
		ArrayList<Board01DTO_pr> list = new ArrayList<Board01DTO_pr>();
		try {
			conn=getConn();
			//모든 레코드(로우넘을 붙인(글번호 큰 순-최신순-으로 정렬한 레코드)로우넘이 ?-startRow-부터 ?-endRow까지인 
			sql="select * from ( select b.*, rownum r from (select * from board01 order by num desc) b) where r >= ? and r <= ?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, start); //startRow의 글부터 보여지기 시작
			pstmt.setInt(2, end); //endRow인 글까지 보여지기
			rs=pstmt.executeQuery(); //rs는 여러 개의 행(레코드
			while(rs.next()) { //rs에 값이 있다면
				Board01DTO_pr dto = new Board01DTO_pr(); //dto객체 생성
				dto.setNum(rs.getInt("num"));//dto의 변수에 rs의 컬럼값 추가
				dto.setTitle(rs.getString("title"));
				dto.setWriter(rs.getString("writer"));
				dto.setContent(rs.getString("content"));
				dto.setReg(rs.getTimestamp("reg"));
				list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn,pstmt,rs);
		}
		return list;
	} 
	
	
	//게시글 총 개수를 세는 메서드
	public int boardCount() {
		int result=0;//리턴값 담을 변수
		try {
			conn=getConn();
			sql="select count(*) from board01";
			pstmt=conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next()) { 
				result=rs.getInt(1); //쿼리 결과의 1번째 컬럼의 값 대입
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn,pstmt,rs);
		}
		return result;
	}
	
	
	//글 내용 확인하는 메서드
	public Board01DTO_pr content(int num) {
		Board01DTO_pr dto = new Board01DTO_pr();//쿼리 결과 레코드를 담을 객체 생성
		
		try {
			conn=getConn();
			sql="select * from board01 where num=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, num);//content페이지에서 파라미터로 받은 (인수)num
			rs=pstmt.executeQuery();
			
			if(rs.next()) { //rs에 값이 있다면 - 반복문일 필요x
				dto.setNum(rs.getInt("num")); //dto에 쿼리 결과 담기
				dto.setWriter(rs.getString("writer"));
				dto.setTitle(rs.getString("title"));
				dto.setContent(rs.getString("content"));
				dto.setReg(rs.getTimestamp("reg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn,pstmt,rs);
		}
		return dto;
	}
	
	//updateform에서 글 내용을 확인하는 메서드
	public Board01DTO_pr checkContent(int num) {
		Board01DTO_pr dto = new Board01DTO_pr(); //SQL의 결과를 담을 dto
		
		try {
			conn=getConn();//연결 객체 생성
			sql="select * from board01 where num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num); //인수로 받은 글번호
			rs = pstmt.executeQuery();
			if(rs.next()) {
				dto.setNum(rs.getInt("num"));//rs의 값을 dto에 대입
				dto.setTitle(rs.getString("title"));
				dto.setWriter(rs.getString("writer"));
				dto.setContent(rs.getString("content"));
				dto.setReg(rs.getTimestamp("reg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn,pstmt,rs); //객체 연결 종료
		}
		return dto;
	}
	
	
	//글 수정하는 메서드 - updatePro페이지에서 사용예정
	public int update(Board01DTO_pr dto) {
		int result=0; //메서드가 리턴할 result 미리 초기화
		try {
			conn=getConn();
			sql="update board01 set title=?, content=?, reg=sysdate where num=?"; //num을 조건으로 해, 수정할 레코드를 특정할 것.
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,dto.getTitle());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getNum());
			
			result = pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn,pstmt,rs);
		}
		return result;
	}
	
	//글 수정하는 메서드 update쿼리
	public int updatePro(Board01DTO_pr dto) { //폼파라미터를 담고있는 dto객체가 인수
		int result=0;//결과 담을 변수 초기화
		try {
			conn=getConn();
			sql="update board01 set title=?, content=? where num=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1,dto.getTitle()); //쿼리문 ?채우기
			pstmt.setString(2,dto.getContent());
			pstmt.setInt(3,dto.getNum());
			result=pstmt.executeUpdate(); //쿼리 실행결과를 변수에 담기
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			close(conn,pstmt,rs);
		}
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
