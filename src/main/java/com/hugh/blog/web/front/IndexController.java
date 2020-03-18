package com.hugh.blog.web.front;

import com.hugh.blog.service.BlogService;
import com.hugh.blog.service.TagService;
import com.hugh.blog.service.TypeService;
import com.hugh.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/front")
public class IndexController {

    @Autowired
    BlogService blogService;

    @Autowired
    TagService tagService;

    @Autowired
    TypeService typeService;

    @GetMapping("")
    public String index(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page", blogService.listBlog(pageable));
        model.addAttribute("types", typeService.listTypeTop(6));
        model.addAttribute("tags", tagService.listTagTop(6));
        model.addAttribute("recommendBlogs", blogService.listRecommendBlogTop(6));
        return "front/index";
    }

    @PostMapping("/search")
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         Model model,
                         @RequestParam String query) {
        model.addAttribute("page",blogService.listBlog(query,pageable));
        model.addAttribute("query",query);
        return "front/search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id,Model model){
        model.addAttribute("blog",blogService.getAndConvert(id));
        return "front/blog";
    }


}
