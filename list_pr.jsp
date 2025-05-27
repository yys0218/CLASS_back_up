<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="web.bean.Board01DTO" %>
<jsp:useBean class="web.bean.Board01DAO" id="dao"/>

<h2>글목록</h2>
<%
	//한 페이지에 보여질 글의 개수 처리
	//list.jsp진입 시, request에 pageNum이 있다면 꺼내서 변수에 대입
	String pageNum=request.getParameter("pageNum");
	if(pageNum==null){ pageNum="1"; } //pageNum이 없다면 1대입
	int currentPage=Integer.parseInt(pageNum);//pageNum이 있다면 int로 형변환

	int pageSize=10; //한 페이지에 보여질 글 개수
	int startRow=(currentPage-1)*pageSize+1; //보여지기 시작할 게시글의 rownum
	int endRow=currentPage*pageSize; //끝에 보여질 게시글의 rownum
%>

<table border="1" width="800" style="border-collapse:collapse">
	<tr>
	<th width="50">글번호</th>
	<th>글제목</th>
	<th width="100">작성자</th>
	<th width="200">작성일시</th>
	</tr>
    <%
	//테이블 전체를 보여주는-글목록 가져오는- dao.select 메서드
	ArrayList<Board01DTO> list = dao.list(); //매개변수 추가
	for( Board01DTO dto : list){ 
	%>	
	<tr>
	<td align=center><%=dto.getNum() %></td>
	<td><%--dto의num값을 get방식으로 같이 보냄 *게시판은 글번호(pk)&pageNum을 가지고 다녀야함--%>
	<a href="content.jsp?num=<%=dto.getNum()%>" > 
	<%--url에 값 같이 넘어가고 있는지 확인--%>
	<%=dto.getTitle() %>
	</a>
	</td>
	<td align=center><%=dto.getWriter() %></td>
	<td><%=dto.getReg() %></td>
	</tr>	
<%	} %>
</table>
<div align=left style="margin-left:745">
<button onclick="window.location='writeForm.jsp'">글쓰기</button>
</div>

<%
	//페이지 블록 처리
	int count=9;//dao.boardCount();
	if(count>0){ //게시글이 존재한다면,
		int pageBlock=5;//한 번에 보여질 페이지 블럭의 범위
		//페이지 블럭의 총 페이지 수 - 총 게시글을 pageBlock으로 나누고 남는 글이 있다면 페이지 +1
		int pageCount=count/pageSize + (count%pageSize==0? 0:1); 
		int startPage=(int)((currentPage-1)/pageBlock)*pageBlock+1; //한 번에 보여지는 페이지 블럭의 시작
		int endPage=startPage+pageBlock-1; //한 번에 보여지는 페이지 블럭의 끝
		if(endPage>pageCount){ endPage=pageCount; } //count가 pageSize보다 작다면 발생 가능
		
		if(startPage>pageBlock){ //두 번째 페이지 블럭이 보여지고 있다면 %> 
			<a href="list.jsp?pageNum=<%=startPage-pageBlock%>">*이전</a>
	<%	}
		for(int i=startPage; i<=endPage; i++){ //페이지 번호로 이동 시 해당 pageNum이 현재 페이지에 대입되어 게시글이 바뀜%>
			<a href="list.jsp?pageNum=<%=i%>">[<%=i %>]</a>
	<%	}
		if(endPage<pageCount){ //보여줘야할 페이지가 남아있다면%>
			<a href="list.jsp?pageNum=<%=startPage+pageBlock%>">다음*</a>
	<%	}
	}
%>