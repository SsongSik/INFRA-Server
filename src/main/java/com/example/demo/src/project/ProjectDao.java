package com.example.demo.src.project;

import com.example.demo.src.project.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ProjectDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 프로젝트 전체, 검색 조회
     * @return List 제목, 분야, 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjects() {

        String getProjectQuery = "select Project.pj_num, user_id, pj_views, pj_header, pj_categoryNum, pj_content, pj_name, pj_subCategoryNum, pj_progress, pj_endTerm,pj_startTerm, pj_deadline, pj_totalPerson,pj_recruitPerson, pj_time, DATEDIFF(pj_deadline,now()) from Project where pj_status = '등록'";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_categoryNum"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())")
        ));
    }

    /**
     * 프로젝트 전체, 검색 조회
     * @param search
     * @return List 제목, 분야, 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    public List<GetProjectRes> getProjectsBySearch(String search) {
        String getProjectsBySearchQuery = "select distinct pj_header, Project.pj_categoryNum, pj_name, pj_name, pj_progress, pj_deadline, pj_totalPerson,pj_recruitPerson, DATEDIFF(pj_deadline,now()) " +
                "from Project, Pj_hashtag, Pj_category, Pj_subCategory " +

                "where pj_status = '등록' " +
                "and (Project.pj_num = Pj_hashtag.pj_num and hashtag like ?) " +
                "or (Project.pj_categoryNum = Pj_category.pj_categoryNum and pj_categoryName like ?) " +
                "or (Project.pj_subCategoryNum = Pj_subCategory.pj_subCategoryNum and pj_subCategoryName like ?) " +
                "or pj_name like ? or pj_content like ?";

        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_categoryNum"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        "모집중",
                        rs.getInt("DATEDIFF(pj_deadline,now())")),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }

    /**
     * 프로젝트 키워드 조회
     * @return List 프로젝트 번호, 키워드
     * @author 한규범, 윤성식
     */
    public List<GetPjKeywordRes> getPj_keywords() {
        String getProjectQuery = "select Project.pj_num, hashtag from Pj_hashtag, Project where Project.pj_num = Pj_hashtag.pj_num";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetPjKeywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("hashtag")
                )
        );
    }

    /**
     * 프로젝트 키워드 조회
     * @param search
     * @return List 프로젝트 번호, 키워드
     * @author 한규범, 윤성식
     */
    public List<GetPjKeywordRes> getPj_keywordsBysearch(String search) {
        String getProjectsBySearchQuery = "select Project.pj_num, hashtag from Project, Pj_hashtag " +
                "where Project.pj_num = Pj_hashtag.pj_num and pj_name like ? or pj_content like ? or hashtag like ?";

        String getProjectsBySearchParams = '%' + search + '%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetPjKeywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("hashtag")),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }

    /**
     * 유저가 찜한 프로젝트 조회
     * @param postPj_likeReq
     * @return List 프로젝트 번호, 제목, 조회수, 분야, 이름, 세부분야, 진행상황, 모집마감일, 총 모집인원, 현재 모집인원, 게시일
     * @author 한규범
     */
    public List<PostPjLikeRes> getPj_num(PostPjLikeReq postPj_likeReq) {
        String getPj_numQuery = "select Project.pj_num, pj_header, pj_views, pj_categoryNum, pj_name, pj_subCategoryNum, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time " +
                "from Project " +
                "where pj_num in (select pj_num from Pj_like where user_id= ?)";
        String getParams = postPj_likeReq.getUser_id();
        return this.jdbcTemplate.query(getPj_numQuery,
                (rs, rowNum) -> new PostPjLikeRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_categoryNum"),
                        rs.getString("pj_name"),
                        rs.getString("pj_subCategoryNum"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        rs.getString("pj_time")),
                getParams
        );
    }

    /**
     * 프로젝트에 참여한 팀원들 조회
     * @param postPj_participateReq
     * @return List 유저 닉네임, 유저 사진
     * @author 윤성식
     */
    public List<PostPjParticipateRes> getTeam(PostPjParticipateReq postPj_participateReq) {
        String getTeam_Query = "select user_nickname, user_prPhoto " +
                "from User " +
                "where user_id in (select user_id from Pj_request where pj_inviteStatus = '승인완료' and pj_num = ?)";
        Integer getParams = postPj_participateReq.getPj_num();
        return this.jdbcTemplate.query(getTeam_Query,
                (rs, rowNum) -> new PostPjParticipateRes(
                        rs.getString("user_nickname"),
                        rs.getString("user_prPhoto")),
                getParams
                );
    }

    /**
     * 유저가 조회했던 프로젝트 조회
     * @param postPj_inquiryReq
     * @return List 프로젝트 번호, 프로젝트 제목, 조회수, 프로젝트 분야, 이름, 세부분야, 진행, 마감일, 전체인원, 모집 중인 인원, 프로젝트 등록 시간
     * @author 한규범
     */
    public List<PostPjInquiryRes> proInquiry(PostPjInquiryReq postPj_inquiryReq) {
        String getPj_inquiryQuery = "select pj_num, pj_header, pj_views, pj_categoryNum, pj_name, pj_subCategoryNum, pj_progress, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time from Project where pj_num in (select pj_num from Pj_inquiry where user_id = ?)";
        String Pj_inquiryParams = postPj_inquiryReq.getUser_id();
        return this.jdbcTemplate.query(getPj_inquiryQuery,
                (rs, rowNum) -> new PostPjInquiryRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_header"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_categoryNum"),
                        rs.getString("pj_name"),
                        rs.getString("pj_subCategoryNum"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_totalPerson"),
                        rs.getInt("pj_recruitPerson"),
                        rs.getString("pj_time")),
                Pj_inquiryParams
                );
    }

    /**
     * 프로젝트 등록
     * @param postPjRegisterReq
     * @return PostPjRegisterRes 프로젝트 이름
     * @author 한규범
     */
    public String pjRegistration(PostPjRegisterReq postPjRegisterReq) {
        String Pj_numQuery = "SELECT pj_num FROM Project ORDER BY pj_num DESC LIMIT 1";
        postPjRegisterReq.setPj_num(this.jdbcTemplate.queryForObject(Pj_numQuery, int.class)+1);

        String Pj_timeQuery = "SELECT now()";
        postPjRegisterReq.setPj_time(this.jdbcTemplate.queryForObject(Pj_timeQuery, Timestamp.class));

        String registrationPjQuery = "insert into Project(pj_num, user_id, pj_views, pj_header, pj_categoryNum,    pj_content, pj_name, pj_subCategoryNum, pj_progress, pj_endTerm,      pj_startTerm, pj_deadline, pj_totalPerson, pj_recruitPerson, pj_time) VALUES (?,?,?,?,?  ,?,?,?,?,?   ,?,?,?,?,?)";
        Object[] registrationParms = new Object[]
                {postPjRegisterReq.getPj_num(),
                postPjRegisterReq.getUser_id(),
                postPjRegisterReq.getPj_views(),
                postPjRegisterReq.getPj_header(),
                postPjRegisterReq.getPj_categoryNum(),
                postPjRegisterReq.getPj_content(),
                postPjRegisterReq.getPj_name(),
                postPjRegisterReq.getPj_subCategoryNum(),
                postPjRegisterReq.getPj_progress(),
                postPjRegisterReq.getPj_endTerm(),
                postPjRegisterReq.getPj_startTerm(),
                postPjRegisterReq.getPj_deadline(),
                postPjRegisterReq.getPj_totalPerson(),
                postPjRegisterReq.getPj_recruitPerson(),
                postPjRegisterReq.getPj_time()};
        this.jdbcTemplate.update(registrationPjQuery, registrationParms);

        for(int i = 0; i<postPjRegisterReq.getHashtag().length; i++){
            String insertKeywordQuery = "INSERT INTO Pj_hashtag (pj_num, hashtag) VALUES(?,?)";
            this.jdbcTemplate.update(insertKeywordQuery, postPjRegisterReq.getPj_num(), postPjRegisterReq.getHashtag()[i]);
        }

        String lastInsertPjnameQuery = postPjRegisterReq.getPj_name();
        return lastInsertPjnameQuery;
    }

    /**
     *프로젝트 수정
     * @param patchPjModifyReq
     * @return PatchPjModifyRes 프로젝트 이름
     * @author 한규범
     */
    public String pjModify(PatchPjModifyReq patchPjModifyReq) {
        String pjModifyQuery = "update Project set pj_header = ?, pj_categoryNum = ?, pj_content = ?, pj_name = ?, pj_subCategoryNum = ?, pj_progress = ?, pj_startTerm = ?, pj_endTerm = ?, pj_deadline = ?, pj_totalPerson = ? where pj_num = ? ";
        Object[] pjModifyParms = new Object[]{
                patchPjModifyReq.getPj_header(),
                patchPjModifyReq.getPj_categoryNum(),
                patchPjModifyReq.getPj_content(),
                patchPjModifyReq.getPj_name(),
                patchPjModifyReq.getPj_subCategoryNum(),
                patchPjModifyReq.getPj_progress(),
                patchPjModifyReq.getPj_startTerm(),
                patchPjModifyReq.getPj_endTerm(),
                patchPjModifyReq.getPj_deadline(),
                patchPjModifyReq.getPj_totalPerson(),
                patchPjModifyReq.getPj_num()
        };
        this.jdbcTemplate.update(pjModifyQuery, pjModifyParms);

        String deleteKeywordQuery = "delete from Pj_hashtag where pj_num = ?";
        this.jdbcTemplate.update(deleteKeywordQuery, patchPjModifyReq.getPj_num());

        for(int i = 0; i<patchPjModifyReq.getHashtag().length; i++){
            String insertKeywordQuery = "INSERT into Pj_hashtag (pj_num, hashtag) VALUES (?,?)";
            this.jdbcTemplate.update(insertKeywordQuery,patchPjModifyReq.getPj_num(), patchPjModifyReq.getHashtag()[i]);
        }

        return patchPjModifyReq.getPj_name();
    }

    /**
     * 프로젝트 삭제
     * @param delPjDelReq
     * @return DelPjDelRes 결과 메시지
     * @author 한규범
     */
    public String pjDel(DelPjDelReq delPjDelReq) {

        String pjDelQuery = "update Project set pj_status = '삭제' where pj_num = ? ";
        this.jdbcTemplate.update(pjDelQuery, delPjDelReq.getPj_num());

        return "삭제가 완료되었습니다.";
    }

    /**
     * 프로젝트 지원
     * @param postPjApplyReq
     * @return PostPjApplyRes 완료 메시지
     * @author 한규범
     */
    public String pjApply(PostPjApplyReq postPjApplyReq) {
        String pjApplyCoincideCheckQuery = "Select Count(*) from Pj_request where pj_num = ? and user_id = ?";

        if(this.jdbcTemplate.queryForObject(pjApplyCoincideCheckQuery, int.class, postPjApplyReq.getPj_num(), postPjApplyReq.getUser_id()) == 1){
            return "중복";
        }else{
            String pjApplyQuery = "insert into Pj_request (user_id, pj_num, pj_inviteStatus) VALUES (?,?,'신청')";
            this.jdbcTemplate.update(pjApplyQuery, postPjApplyReq.getUser_id(), postPjApplyReq.getPj_num());
            return "신청이 완료되었습니다.";
        }
    }

    /**
     * 프로젝트신청한 유저 승인
     * @param patchPjApproveReq
     * @return PatchPjApproveRes 완료 메시지
     * @author 윤성식
     */
    public String pjApprove(PatchPjApproveReq patchPjApproveReq) {
        String pjApproveQuery = "update Pj_request set pj_inviteStatus = '승인완료' where user_id = ? and pj_num = ? and pj_inviteStatus = '신청'";
        Object[] pjApproveParams = new Object[]{
                patchPjApproveReq.getUser_id(),
                patchPjApproveReq.getPj_num()
        };
        this.jdbcTemplate.update(pjApproveQuery, pjApproveParams);

        return "승인완료";
    }

    /**
     * 본인이 지원한 프로젝트 신청 현황
     * @param postUserApplyReq
     * @return List 프로젝트 번호, 참여 상태, 프로젝트 이름, 조회수, 프로젝트 제목
     * @author 윤성식
     */
    public List<PostUserApplyRes> getUserApply(PostUserApplyReq postUserApplyReq) {
        String getApplyQuery = "select Pj_request.pj_num, pj_inviteStatus, pj_name, pj_views, pj_header from Pj_request, Project where Pj_request.pj_num = Project.pj_num and Pj_request.user_id = ?";
        String getApplyParams = postUserApplyReq.getUser_id();
        return this.jdbcTemplate.query(getApplyQuery,
                (rs, rowNum) -> new PostUserApplyRes(
                        rs.getInt("pj_num"),
                        rs.getString("pj_inviteStatus"),
                        rs.getString("pj_name"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_header")),
                getApplyParams
        );
    }


    /**
     * 프로젝트 신청 현황
     * @param pj_num
     * @return List 유저ID, 유저 평점, 유저 사진, 프로젝트 번호
     * @author 윤성식
     */
    public List<GetApplyListRes> pjApplyList(String pj_num) {
        String pjApplyListQuery = "select User.user_id, user_nickname, user_grade, user_prPhoto, pj_inviteStatus from User, Pj_request where User.user_id = Pj_request.user_id and pj_num = ?";
        return this.jdbcTemplate.query(pjApplyListQuery,
                (rs,rowNum) -> new GetApplyListRes(
                        rs.getString("user_id"),
                        rs.getString("user_nickname"),
                        rs.getString("user_grade"),
                        rs.getString("user_prPhoto"),
                        rs.getString("pj_inviteStatus")),
                pj_num);
    }

    //프로젝트 찜 등록
    public String likeRegister(PostLikeRegisterReq postLikeRegisterReq) {
        String likeRegisterQuery = "INSERT into Pj_like (user_id, pj_num) VALUES (?,?)";
        this.jdbcTemplate.update(likeRegisterQuery, postLikeRegisterReq.getUser_id(), postLikeRegisterReq.getPj_num());

        return "찜 등록완료";
    }

    //프로젝트 찜 삭제
    public String likeDel(PostLikeRegisterReq postLikeRegisterReq) {
        String likeDelQuery = "delete from Pj_like where user_id = ? and pj_num = ?";
        this.jdbcTemplate.update(likeDelQuery, postLikeRegisterReq.getUser_id(), postLikeRegisterReq.getPj_num());

        return "찜 삭제";
    }
}
