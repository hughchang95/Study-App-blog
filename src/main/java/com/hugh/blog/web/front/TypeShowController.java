package com.hugh.blog.web.front;

import com.hugh.blog.po.Type;
import com.hugh.blog.service.BlogService;
import com.hugh.blog.service.TypeService;
import com.hugh.blog.vo.BlogQuery;
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
public class TypeShowController {

    @Autowired
    TypeService typeService;

    @Autowired
    BlogService blogService;

    @GetMapping("/types/{id}")
    public String types(@PathVariable Long id,
                        @PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model){
        List<Type> types = typeService.listTypeTop(100);
        if (id==-1){
            id=types.get(0).getId();
        }
        model.addAttribute("types",types);
        model.addAttribute("page",blogService.listTypeBlog(id,pageable));
        model.addAttribute("activeTypeId",id);
        return "front/types";
    }
}
