package com.hugh.blog.controller.front;

import com.hugh.blog.po.Comment;
import com.hugh.blog.po.User;
import com.hugh.blog.service.BlogService;
import com.hugh.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/front")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @Value("${comment.avatar}")
    private String avatar;

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));
        return "front/blog :: commentList";
    }

    @PostMapping("/comments")
    public String post(Comment comment, HttpSession session) {
        Long blogId=comment.getBlog().getId();
        comment.setBlog(blogService.getBlog(blogId));
        User user = (User) session.getAttribute("user");
        if (user!=null){
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
            comment.setNickname(user.getNickname());
        }else {
            comment.setAvatar(avatar);
        }
        commentService.saveComment(comment);
        return "redirect:/front/comments/" + blogId;
    }
}
