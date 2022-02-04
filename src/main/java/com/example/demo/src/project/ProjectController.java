package com.example.demo.src.project;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.project.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;
import static com.example.demo.config.BaseResponseStatus.POST_PROJECT_COINCIDE_CHECK;

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

    /**
     * 프로젝트 전체, 검색 조회
     * @param search
     * @return List 제목, 분야, 이름, 진행, 모집마감일, 전체인원, 모집인원, (모집, 마감임박), 마감 남은 일수
     * @author 한규범, 윤성식
     */
    @ResponseBody
    @GetMapping("/inquiry")
    public BaseResponse<List<GetProjectRes>> getProjects(@RequestParam(required = false) String search){
        try{
            if(search == null){
                List<GetProjectRes> getProjectRes = projectProvider.getProjects();

                for(int i=0; i<getProjectRes.size(); i++){
                    if(getProjectRes.get(i).getPj_daysub() <= 2 && getProjectRes.get(i).getPj_daysub() >= 0){
                        getProjectRes.get(i).setPj_recruit("마감임박");
                    }
                }

            return new BaseResponse<>(getProjectRes);
            }
            List<GetProjectRes> getProjectRes = projectProvider.getProjectsByKeyword(search);
            for(int i=0; i<getProjectRes.size(); i++){
                if(getProjectRes.get(i).getPj_daysub() <= 2 && getProjectRes.get(i).getPj_daysub() >= 0){
                    getProjectRes.get(i).setPj_recruit("마감임박");
                }
            }
            return new BaseResponse<>(getProjectRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 프로젝트 키워드 조회
     * @param search
     * @return List 프로젝트 번호, 키워드
     * @author 한규범, 윤성식
     */
    @ResponseBody
    @GetMapping("/keyword")
    public BaseResponse<List<GetPjKeywordRes>> getPj_keywords(@RequestParam(required = false) String search){
        try{
            if(search == null){
                List<GetPjKeywordRes> getPj_keywordRes = projectProvider.getPj_keywords();
                return new BaseResponse<>(getPj_keywordRes);
            }
            List<GetPjKeywordRes> getPj_keywordRes = projectProvider.getPj_keywordsBysearch(search);
            return new BaseResponse<>(getPj_keywordRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저가 한 프로젝트 조회
     * @param postPj_likeReq
     * @return List 프로젝트 번호, 제목, 조회수, 분야, 이름, 세부분야, 진행상황, 모집마감일, 총 모집인원, 현재 모집인원, 게시일
     * @author 한규범
     */
    @ResponseBody
    @PostMapping("/likePj")
    public BaseResponse<List<PostPjLikeRes>> like(@RequestBody PostPjLikeReq postPj_likeReq){
        try{
            String userIdByJwt = jwtService.getUserId();
            if (!postPj_likeReq.getUser_id().equals(userIdByJwt)) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<PostPjLikeRes> postPj_likeRes = projectProvider.like(postPj_likeReq);
            return new BaseResponse<>(postPj_likeRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 유저가 조회했던 프로젝트 조회
     * @param postPj_inquiryReq
     * @return List 프로젝트 번호, 프로젝트 제목, 조회수, 프로젝트 분야, 이름, 세부분야, 진행, 마감일, 전체인원, 모집 중인 인원, 프로젝트 등록 시간
     * @author 한규범
     */
    @ResponseBody
    @PostMapping("/project-inquiry")
    public BaseResponse<List<PostPjInquiryRes>> proInquiry(@RequestBody PostPjInquiryReq postPj_inquiryReq){
        try{
            List<PostPjInquiryRes> postPj_inquiryRes = projectProvider.proInquiry(postPj_inquiryReq);
            return new BaseResponse<>(postPj_inquiryRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 프로젝트에 참여한 팀원들 조회
     * @param postPj_participateReq
     * @return List 유저 닉네임, 유저 사진
     * @author 윤성식
     */
    @ResponseBody
    @PostMapping("/team")
    public BaseResponse<List<PostPjParticipateRes>> getTeam(@RequestBody PostPjParticipateReq postPj_participateReq){
        try{
            List<PostPjParticipateRes> postPj_participateRes = projectProvider.getTeam(postPj_participateReq);
            return new BaseResponse<>(postPj_participateRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 프로젝트 등록
     * @param postPjRegisterReq
     * @return PostPjRegisterRes 프로젝트 이름
     * @author 한규범
     */
    @ResponseBody
    @PostMapping("/registration")
    public BaseResponse<PostPjRegisterRes> pjRegistration(@RequestBody PostPjRegisterReq postPjRegisterReq){
        try{
            projectService.PjDateCheck(postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_endTerm());
            projectService.PjNullCheck(postPjRegisterReq.getPj_header(), postPjRegisterReq.getPj_categoryNum(), postPjRegisterReq.getPj_content(), postPjRegisterReq.getPj_name(), postPjRegisterReq.getPj_subCategoryNum(), postPjRegisterReq.getPj_progress(), postPjRegisterReq.getPj_endTerm(), postPjRegisterReq.getPj_startTerm(), postPjRegisterReq.getPj_deadline(), postPjRegisterReq.getPj_totalPerson());
            projectService.PjKeywordCheck(postPjRegisterReq.getHashtag());
            PostPjRegisterRes postPjRegisterRes = projectService.registrationPj(postPjRegisterReq);
            return new BaseResponse<>(postPjRegisterRes);
        } catch (BaseException exception) {
            return new BaseResponse<
                    >((exception.getStatus()));
        }
    }

    /**
     *프로젝트 수정
     * @param patchPjModifyReq
     * @return PatchPjModifyRes 프로젝트 이름
     * @author 한규범
     */
    @ResponseBody
    @PatchMapping("/modify")
    public BaseResponse<PatchPjModifyRes> pjModify(@RequestBody PatchPjModifyReq patchPjModifyReq){
        try {
            projectService.PjDateCheck(patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_endTerm());
            projectService.PjNullCheck(patchPjModifyReq.getPj_header(), patchPjModifyReq.getPj_categoryNum(), patchPjModifyReq.getPj_content(), patchPjModifyReq.getPj_name(), patchPjModifyReq.getPj_subCategoryNum(), patchPjModifyReq.getPj_progress(), patchPjModifyReq.getPj_endTerm(), patchPjModifyReq.getPj_startTerm(), patchPjModifyReq.getPj_deadline(), patchPjModifyReq.getPj_totalPerson());
            projectService.PjKeywordCheck(patchPjModifyReq.getHashtag());
            PatchPjModifyRes patchPjModifyRes = projectService.pjModify(patchPjModifyReq);
            return new BaseResponse<>(patchPjModifyRes);
        }catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 프로젝트 삭제
     * @param delPjDelReq
     * @return DelPjDelRes 결과 메시지
     * @author 한규범
     */
    @ResponseBody
    @DeleteMapping("/del")
    public BaseResponse<DelPjDelRes> pjDel(@RequestBody DelPjDelReq delPjDelReq) {
        try {
            DelPjDelRes delpjDelRes = projectService.pjDel(delPjDelReq);
            return new BaseResponse<>(delpjDelRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 프로젝트 지원
     * @param postPjApplyReq
     * @return PostPjApplyRes 완료 메시지
     * @author 한규범
     */
    @ResponseBody
    @PostMapping("/apply")
    public BaseResponse<PostPjApplyRes> pjApply(@RequestBody PostPjApplyReq postPjApplyReq) {
        try {
            PostPjApplyRes postPjApplyRes = projectService.pjApply(postPjApplyReq);
            if (postPjApplyRes.getComment().equals("중복"))
                throw new BaseException(POST_PROJECT_COINCIDE_CHECK);
            else
                return new BaseResponse<>(postPjApplyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 프로젝트신청한 유저 승인
     * @param patchPjApproveReq
     * @return PatchPjApproveRes 완료 메시지
     * @author 윤성식
     */
    @ResponseBody
    @PatchMapping("/approve")
    public BaseResponse<PatchPjApproveRes> pjApprove(@RequestBody PatchPjApproveReq patchPjApproveReq) {
        try {
            PatchPjApproveRes patchPjApproveRes = projectService.pjApprove(patchPjApproveReq);
            return new BaseResponse<>(patchPjApproveRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 프로젝트 신청 현황
     * @param pj_num
     * @return List 유저ID, 유저 평점, 유저 사진, 프로젝트 번호
     * @author 윤성식
     */
    @ResponseBody
    @GetMapping("/apply-list")
    public BaseResponse<List<GetApplyListRes>> pjApplyList(@RequestParam(required = false) String pj_num) {
        try {
            List<GetApplyListRes> getApplyListRes = projectProvider.pjApplyList(pj_num);
            return new BaseResponse<>(getApplyListRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 본인이 지원한 프로젝트 신청 현황
     * @param postUserApplyReq
     * @return List 프로젝트 번호, 참여 상태, 프로젝트 이름, 조회수, 프로젝트 제목
     * @author 윤성식
     */
    @ResponseBody
    @PostMapping("/apply-mylist")
    public BaseResponse<List<PostUserApplyRes>> userApply(@RequestBody PostUserApplyReq postUserApplyReq) {
        try {
            List<PostUserApplyRes> postUserApplyRes = projectProvider.getUserApply(postUserApplyReq);
            return new BaseResponse<>(postUserApplyRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

        //프로젝트 찜 등록
        @ResponseBody
        @PostMapping("/like")
        public BaseResponse<PostLikeRegisterRes> likeRegister (@RequestBody PostLikeRegisterReq postLikeRegisterReq){
            try {
                PostLikeRegisterRes postLikeRegisterRes = projectService.likeRegister(postLikeRegisterReq);
                return new BaseResponse<>(postLikeRegisterRes);
            } catch (BaseException exception) {
                return new BaseResponse<>(exception.getStatus());
            }
        }

        //프로젝트 찜 삭제
        @ResponseBody
        @DeleteMapping("/like-del")
        public BaseResponse<PostLikeRegisterRes> likeDel (@RequestBody PostLikeRegisterReq postLikeRegisterReq){
            try {
                PostLikeRegisterRes postLikeRegisterRes = projectService.likeDel(postLikeRegisterReq);
                return new BaseResponse<>(postLikeRegisterRes);
            } catch (BaseException exception) {
                return new BaseResponse<>(exception.getStatus());
            }
        }
    }
