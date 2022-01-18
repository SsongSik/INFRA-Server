package com.example.demo.src.project;

import com.example.demo.src.project.model.GetProjectRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;
import com.example.demo.src.project.model.*;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.util.List;

@Repository
public class ProjectDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //프로젝트 조회
    public List<GetProjectRes> getProjects() {
        String getProjectQuery = "select Project.pj_num, User_id, pj_views, pj_header, pj_field, pj_content, pj_name, pj_subField, pj_progress, pj_end_term,pj_start_term, pj_deadline, pj_total_person,pj_recruit_person, pj_time from Project";
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_total_person"),
                        rs.getInt("pj_recruit_person")
                        )
        );

    }
    //검색 프로젝트 조회
    public List<GetProjectRes> getProjectsBySearch(String search) {
        String getProjectsBySearchQuery = "select distinct pj_header, pj_field, pj_name, pj_progress, pj_deadline, pj_total_person, pj_recruit_person from Project, Pj_keyword where pj_name like ? or pj_content like ? or keyword like ? or pj_subfield like ?";
        String getProjectsBySearchParams = '%'+search+'%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetProjectRes(
                        rs.getString("pj_header"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_total_person"),
                        rs.getInt("pj_recruit_person")),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }

    //키워드 조회
    public List<GetPj_keywordRes> getPj_keywords() {
        String getProjectQuery = "select Project.pj_num, keyword from Pj_keyword, Project where Project.pj_num = Pj_keyword.pj_num";
        System.out.println("gg");
        return this.jdbcTemplate.query(getProjectQuery,
                (rs, rowNum) -> new GetPj_keywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("keyword")
                )
        );
    }
    //버리는 카드
    public List<GetPj_keywordRes> getPj_keywordsBysearch(String search) {
        String getProjectsBySearchQuery = "select Project.pj_num, keyword from Project, Pj_keyword where Project.pj_num = Pj_keyword.pj_num and pj_name like ? or pj_content like ? or keyword like ? or pj_subfield like ?";

        String getProjectsBySearchParams = '%'+search+'%';

        return this.jdbcTemplate.query(getProjectsBySearchQuery,
                (rs, rowNum) -> new GetPj_keywordRes(
                        rs.getInt("pj_num"),
                        rs.getString("keyword")),
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams,
                getProjectsBySearchParams);
    }

    //유저가 찜한 프로젝트 조회
    public Project getPj_num(PostPj_likeReq postPj_likeReq) {
        String getPj_numQuery = "select Project.pj_num, Project.User_id, pj_views, pj_header, pj_field, pj_name, pj_subField, pj_progress, pj_deadline, pj_total_person, pj_recruit_person, pj_time from Project where pj_num in (select pj_num from Pj_like where User_id= ?)";
        String getParams = postPj_likeReq.getUser_id();
        return this.jdbcTemplate.queryForObject(getPj_numQuery,
                (rs, rowNum) -> new Project(
                        rs.getInt("pj_num"),
                        rs.getString("User_id"),
                        rs.getInt("pj_views"),
                        rs.getString("pj_header"),
                        rs.getString("pj_field"),
                        rs.getString("pj_name"),
                        rs.getString("pj_subField"),
                        rs.getString("pj_progress"),
                        rs.getString("pj_deadline"),
                        rs.getInt("pj_total_person"),
                        rs.getInt("pj_recruit_person"),
                        rs.getString("pj_time")),
                getParams
        );
    }
}
