package com.popcon.khfinalbpopcon.service;

import com.popcon.khfinalbpopcon.model.Content;
import com.popcon.khfinalbpopcon.model.UserContentList;
import com.popcon.khfinalbpopcon.model.dto.UserContentListDto;
import com.popcon.khfinalbpopcon.repository.ContentRepository;
import com.popcon.khfinalbpopcon.repository.UserContentListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ContentService {

    @Autowired
    UserService userService;
    @Autowired
    ContentRepository contentRepository;
    @Autowired
    UserContentListRepository userContentListRepository;

    // List 를 Page 로 바꿔주는 메소드
    private Page<Content> listToPage(Pageable pageable, List<Content> list) {
        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), list.size());
        final Page<Content> pagingList = new PageImpl<>(list.subList(start, end), pageable, list.size());
        return pagingList;
    }

    public Page<Content> contentList(Pageable pageable) {
        return contentRepository.findAll(pageable);
    }

    public Page<Content> getContentByTagName(Pageable pageable, String tagName) {

        List<Content> list = contentRepository.findByTagName(tagName);

        return listToPage(pageable, list);
    }



    public Content getContentDetails(Long contentCode) {
        Content content = contentRepository.findByContentCode(contentCode);
        Integer detailsViewCount = content.getDetailsViewCount() + 1;
        content.setDetailsViewCount(detailsViewCount);

        contentRepository.saveAndFlush(content);

        return content;
    }

    public Boolean updateLike(HttpServletRequest request, Long contentCode) {

        Long userCode = userService.getUser(request).getUserCode();

        UserContentList userContentList = userContentListRepository.findByUserCodeAndContentCode(userCode, contentCode);
        if(userContentList == null) {
            userContentList = UserContentList.builder()
                    .userCode(userCode)
                    .contentCode(contentCode).build();
            userContentListRepository.save(userContentList);
            return true;
        } else {
            userContentListRepository.deleteById(userContentList.getUserContentIndex());
            return false;
        }
    }

    public List<UserContentListDto> getLikeContents(HttpServletRequest request) {

        Long userCode = userService.getUser(request).getUserCode();

        // 최신순 정렬 추가
        Sort sort = Sort.by(Sort.Direction.DESC, "userContentIndex");

        // userCode 로 userContentList 목록 담기
        List<UserContentList> userContentLists = userContentListRepository.findByUserCode(userCode, sort);

        if(userContentLists != null) {
            List<UserContentListDto> userContentListDtos = new ArrayList<UserContentListDto>();

            Iterator<UserContentList> iter = userContentLists.iterator();
            while (iter.hasNext()) {
                UserContentList userContentList = (UserContentList)iter.next();
                UserContentListDto userContentListDto = UserContentListDto.builder()
                        .userContentIndex(userContentList.getUserContentIndex())
                        .userCode(userContentList.getUserCode())
                        .contentCode(userContentList.getContentCode())
                        .content(contentRepository.findByContentCode(userContentList.getContentCode())).build();
                userContentListDtos.add(userContentListDto);
            }
            return userContentListDtos;
        }
        return null;
    }

    public List<Content> tagSearch(List<String> names){
        return contentRepository.findByTagNameIn(names);
    }

    public Page<Content> searchTitle(Pageable pageable, String title) {

        List<Content> contentList = contentRepository.findByContentNameContains(title);

        return listToPage(pageable, contentList);
    }
}
