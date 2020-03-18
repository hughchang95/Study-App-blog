package com.hugh.blog.web.front;

import com.hugh.blog.po.Tag;
import com.hugh.blog.po.Type;
import com.hugh.blog.service.BlogService;
import com.hugh.blog.service.TagService;
import com.hugh.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/front")
public class TagShowController {

    @Autowired
    TagService tagService;

    @Autowired
    BlogService blogService;

    @GetMapping("/tags/{id}")
    public String types(@PathVariable Long id,
                        @PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model){
        List<Tag> tags = tagService.listTagTop(100);
        if (id==-1){
            id=tags.get(0).getId();
        }
        model.addAttribute("tags",tags);
        model.addAttribute("page",blogService.listTagBlog(id,pageable));
        model.addAttribute("activeTagId",id);
        return "front/tags";
    }
}
