package com.example.demo.src.help.report;

import com.example.demo.config.BaseException;
import com.example.demo.src.help.report.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReportDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // [POST] 새로운 신고 등록하기
    public void createReport(PostReportReq postReportReq) {
        String createReportQuery = "insert into dev_infraDB.User_Report (user_id, reportedUser_id, rp_category, rp_field, rp_opinion) " +
                "VALUES (?,?,?,?,?)";
        Object[] createReportParams = new Object[]
                {postReportReq.getUser_id(), postReportReq.getReportedUser_id(), postReportReq.getRp_category(),
                        postReportReq.getRp_field(), postReportReq.getRp_opinion()};
        this.jdbcTemplate.update(createReportQuery, createReportParams);
    }

    // [POST] 특정 사용자가 신고했던 목록 조회
    public List<PostReportUserRes> getReports(PostReportUserReq postReportUserReq) {
        String postReportUserQuery = "select * from dev_infraDB.User_Report where user_id = ?";
        String postReportUserParams = postReportUserReq.getUser_id();
        return this.jdbcTemplate.query(postReportUserQuery,
                (rs, rowNum) -> new PostReportUserRes(
                        rs.getString("reportedUser_id"),
                        rs.getString("rp_category"),
                        rs.getString("rp_field"),
                        rs.getString("rp_opinion")),
                postReportUserParams);
    }

    // [PATCH] 특정 사용자의 특정 신고글 삭제
    public int deleteReport(PostReportDelReq postReportDelReq) throws BaseException {
        String deleteReportQuery = "update User_Report set rp_status = '삭제' where user_id = ? and reportedUser_id = ?";
        Object[] deleteReportParams = new Object[]
                {postReportDelReq.getUser_id(), postReportDelReq.getReportedUser_id()};
        return this.jdbcTemplate.update(deleteReportQuery, deleteReportParams);
        }
    }


