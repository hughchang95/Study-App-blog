package com.hugh.blog.web.admin;

import com.hugh.blog.po.Tag;
import com.hugh.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    TagService tagService;

    @GetMapping("/tags")
    public String list(@PageableDefault(size = 10, sort = {"id"}, direction = Sort.Direction.DESC)
                               Pageable pageable,
                       Model model) {
        model.addAttribute("page", tagService.listTag(pageable));
        return "admin/tags";
    }

    @GetMapping("/tags/input")
    public String input(Model model){
        model.addAttribute("tag",new Tag());
        return "admin/tags_input";
    }

    @PostMapping("/tags")
    public String post(@Valid Tag Tag, BindingResult result,
                       RedirectAttributes attributes){
        Tag TagByName = tagService.getTagByName(Tag.getName());
        if (TagByName!=null){
            result.rejectValue("name","nameError","该标签已存在");
        }
        if(result.hasErrors()){
            return "admin/tags_input";
        }
        Tag t=tagService.saveTag(Tag);
        if (t==null){
            attributes.addFlashAttribute("message","操作失败");
        }else {
            attributes.addFlashAttribute("message","操作成功");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/tags/{id}/input")
    public String editInput(@PathVariable Long id, Model model){
        model.addAttribute("tag",tagService.getTag(id));
        return "admin/tags_input";
    }

    @PostMapping("/tags/{id}")
    public String editPost(@Valid Tag Tag, BindingResult result,
                           @PathVariable Long id,
                           RedirectAttributes attributes){
        Tag TagByName = tagService.getTagByName(Tag.getName());
        if (TagByName!=null){
            result.rejectValue("name","nameError","该分类已存在");
        }
        if(result.hasErrors()){
            return "admin/tags_input";
        }
        Tag t=tagService.updateTag(id,Tag);
        if (t==null){
            attributes.addFlashAttribute("message","更新失败");
        }else {
            attributes.addFlashAttribute("message","更新成功");
        }
        return "redirect:/admin/tags";
    }

    @GetMapping("/tags/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes){
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message","删除成功");
        return "redirect:/admin/tags";
    }

}
