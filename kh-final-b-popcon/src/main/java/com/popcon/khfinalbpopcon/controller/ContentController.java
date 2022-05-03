package com.popcon.khfinalbpopcon.controller;

import com.popcon.khfinalbpopcon.model.Content;
import com.popcon.khfinalbpopcon.model.dto.UserContentListDto;
import com.popcon.khfinalbpopcon.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ContentController {
    @Autowired
    ContentService contentService;

    @GetMapping("/movies/searchTag")
    public List<Content> searchTag(@RequestParam(defaultValue = "", value = "tags") String tags) {
        List<String> splitTags = List.of(tags.split(","));

        return contentService.tagSearch(splitTags);
    }

    @GetMapping("/movies")
    public Page<Content> getAllContent(
            @PageableDefault(page = 0, size = 12,
                    sort = "contentCode", direction = Sort.Direction.ASC)
                    Pageable pageable,
            @RequestParam(name = "tagName", required = false) String tagName,
            @RequestParam(name = "title", required = false) String title) {

        String tag = tagName;
        String titleWrap = title;

        if(tag == null && titleWrap == null) {
            return contentService.contentList(pageable);
        } else if(titleWrap != null && tag == null) {
            return contentService.searchTitle(pageable, titleWrap);
        } else if(tag != null && titleWrap == null){
            return contentService.getContentByTagName(pageable, tagName);
        } else {
            return contentService.contentList(pageable);
        }
    }

    @GetMapping("/movies/{content_code}")
    public Content Detail(@PathVariable("content_code")Long content_code){
        Content content = contentService.getContentDetails(content_code);

        return content;
    }

    @PutMapping("/movies/{content_code}/like")
    public Boolean updateLike(
            @PathVariable("content_code")Long content_code,
            HttpServletRequest request
    ) {
        Boolean like = contentService.updateLike(request, content_code);
        return like;
    }

    @GetMapping("/me/like")
    public List<UserContentListDto> getLikeContents(HttpServletRequest request) {

        return contentService.getLikeContents(request);
    }
}
