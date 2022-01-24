package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController

@RequestMapping("/project")
public class ProjectController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ProjectProvider projectProvider;
    @Autowired
    private final ProjectService projectService;
    @Autowired
    private final JwtService jwtService;

    public ProjectController(ProjectProvider projectProvider, ProjectService projectService, JwtService jwtService) {
        this.projectProvider = projectProvider;
        this.projectService = projectService;
        this.jwtService = jwtService;
    }


    //프로젝트 전체 조회
    @ResponseBody
    @GetMapping("/inquiry")
    public BaseResponse<List<GetProjectRes>> getProjects(@RequestParam(required = false) String search){
        try{
            if(search == null){
                List<GetProjectRes> getProjectRes = projectProvider.getProjects();
                return new BaseResponse<>(getProjectRes);
            }
            List<GetProjectRes> getProjectRes = projectProvider.getProjectsByKeyword(search);
            return new BaseResponse<>(getProjectRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //프로젝트 키워드 조회
    @ResponseBody
    @GetMapping("/keyword")
    public BaseResponse<List<GetPj_keywordRes>> getPj_keywords(@RequestParam(required = false) String search){
        try{
            if(search == null){
                List<GetPj_keywordRes> getPj_keywordRes = projectProvider.getPj_keywords();
                return new BaseResponse<>(getPj_keywordRes);
            }
            List<GetPj_keywordRes> getPj_keywordRes = projectProvider.getPj_keywordsBysearch(search);
            return new BaseResponse<>(getPj_keywordRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    //유저가 찜한 프로젝트 조회
    @ResponseBody
    @PostMapping("/likePj")
    public BaseResponse<List<PostPj_likeRes>> like(@RequestBody PostPj_likeReq postPj_likeReq){
        try{
            List<PostPj_likeRes> postPj_likeRes = projectProvider.like(postPj_likeReq);
            return new BaseResponse<>(postPj_likeRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
    //유저가 조회했던 프로젝트 조회
    @ResponseBody
    @PostMapping("/project-inquiry")
    public BaseResponse<List<PostPj_inquiryRes>> proInquiry(@RequestBody PostPj_inquiryReq postPj_inquiryReq){
        try{
            List<PostPj_inquiryRes> postPj_inquiryRes = projectProvider.proInquiry(postPj_inquiryReq);
            return new BaseResponse<>(postPj_inquiryRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //프로젝트에 참여한 팀원들 조회
    @ResponseBody
    @PostMapping("/team")
    public BaseResponse<List<PostPj_participateRes>> getTeam(@RequestBody PostPj_participateReq postPj_participateReq){
        try{
            List<PostPj_participateRes> postPj_participateRes = projectProvider.getTeam(postPj_participateReq);
            return new BaseResponse<>(postPj_participateRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //프로젝트 등록
    @ResponseBody
    @PostMapping("/registration")
    public BaseResponse<PostPjRegisterRes> pjRegistration(@RequestBody PostPjRegisterReq postPjRegisterReq){
        try{
            PjNullCheck(postPjRegisterReq.getPj_header(), postPjRegisterReq.getPj_field(), postPjRegisterReq.getPj_content(), postPjRegisterReq.getPj_name(), postPjRegisterReq.getPj_subField(), postPjRegisterReq.getPj_progress(), postPjRegisterReq.getPj_endTerm(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_totalPerson());
            PostPjRegisterRes postPjRegisterRes = projectService.registrationPj(postPjRegisterReq);
            return new BaseResponse<>(postPjRegisterRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //프로젝트 오류 값 확인
    private void PjNullCheck(String pj_header, String pj_field, String pj_content, String pj_name, String pj_subField, String pj_progress, LocalDate pj_endTerm, LocalDate pj_startTerm, LocalDate pj_deadline, int pj_totalPerson) throws BaseException{
        if(pj_header==null){
            throw new BaseException(POST_PROJECT_EMPTY_HEADER);
        }
        if(pj_field==null){
            throw new BaseException(POST_PROJECT_EMPTY_FIELD);
        }
        if(pj_content==null){
            throw new BaseException(POST_PROJECT_EMPTY_CONTENT);
        }
        if(pj_name==null){
            throw new BaseException(POST_PROJECT_EMPTY_NAME);
        }
        if(pj_subField==null){
            throw new BaseException(POST_PROJECT_EMPTY_SUBFIELD);
        }
        if(pj_progress==null){
            throw new BaseException(POST_PROJECT_EMPTY_PROGRESS);
        }
        if(pj_endTerm==null){
            throw new BaseException(POST_PROJECT_EMPTY_END_TERM);
        }
        if(pj_startTerm==null){
            throw new BaseException(POST_PROJECT_EMPTY_START_TERM);
        }
        if(pj_deadline==null){
            throw new BaseException(POST_PROJECT_EMPTY_DEADLINE);
        }
        if(pj_totalPerson==0){
            throw new BaseException(POST_PROJECT_EMPTY_TOTAL_PERSON);
        }
    }

    //프로젝트 수정
    @ResponseBody
    @PatchMapping("/modify")
    public BaseResponse<PatchPjModifyRes> pjModify(@RequestBody PatchPjModifyReq patchPjModifyReq){
        try {
            PjNullCheck(patchPjModifyReq.getPj_header(), patchPjModifyReq.getPj_field(), patchPjModifyReq.getPj_content(), patchPjModifyReq.getPj_name(), patchPjModifyReq.getPj_subField(), patchPjModifyReq.getPj_progress(), patchPjModifyReq.getPj_endTerm(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_totalPerson());
            PatchPjModifyRes patchPjModifyRes = projectService.pjModify(patchPjModifyReq);
            return new BaseResponse<>(patchPjModifyRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //프로젝트 삭제
    @ResponseBody
    @DeleteMapping("/del")
    public BaseResponse<DelPjDelRes> pjDel(@RequestBody DelPjDelReq delPjDelReq){
        try {
            DelPjDelRes delpjDelRes = projectService.pjDel(delPjDelReq);
            return new BaseResponse<>(delpjDelRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
