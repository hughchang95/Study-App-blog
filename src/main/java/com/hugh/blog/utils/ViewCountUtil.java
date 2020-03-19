package com.hugh.blog.utils;

import com.hugh.blog.po.Blog;
import com.hugh.blog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ViewCountUtil {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    BlogRepository blogRepository;

    /**
     * 更新访问量并返回
     *
     * @param blog
     * @return
     */
    @Transactional
    public Integer getAndincrement(Blog blog) {
        String viewKey = "viewKey-" + blog.getId().toString();
        //获取缓存
        if (redisUtil.get(viewKey) != null) {
            //成功，操作缓存
            redisUtil.incr(viewKey, 1);
        } else {
            //失败，使用库中数据，设置为缓存
            Integer views = blog.getViews();
            redisUtil.set(viewKey, ++views,86400);
        }
        return (Integer) redisUtil.get(viewKey);
    }

    /**
     * 每隔一段时间将缓存注入数据库（30分组）
     */
    @Transactional
    @Scheduled(cron = "* 0/30 * * * ?")
    public void refresh() {
        List<Blog> blogs = blogRepository.findAll();
        for (Blog blog : blogs) {
            String viewKey = "viewKey-" + blog.getId().toString();
            Integer views = (Integer) redisUtil.get(viewKey);
            if (views != null) {
                blog.setViews(views);
                blogRepository.save(blog);
            }
            redisUtil.set(viewKey,blog.getViews());
        }
    }

}
