package com.hugh.blog.web.front;

import com.hugh.blog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/front")
public class ArchiveShowController {

    @Autowired
    BlogService blogService;

    @GetMapping("/archives")
    public String archives(Model model){
        model.addAttribute("archiveMap",blogService.listArchiveBlog());
        model.addAttribute("blogCount",blogService.countBlod());
        return "front/archives";
    }
}
