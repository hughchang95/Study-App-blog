package com.hugh.blog.service.impl;

import com.hugh.blog.exception.NotFoundException;
import com.hugh.blog.po.Blog;
import com.hugh.blog.po.Type;
import com.hugh.blog.repository.BlogRepository;
import com.hugh.blog.service.BlogService;
import com.hugh.blog.utils.MarkdownUtils;
import com.hugh.blog.utils.MyBeanUtils;
import com.hugh.blog.utils.RedisUtil;
import com.hugh.blog.utils.ViewCountUtil;
import com.hugh.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    BlogRepository blogRepository;

    @Autowired
    ViewCountUtil viewCountUtil;

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findById(id).get();
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog=blogRepository.findById(id).get();
        if (blog==null){
            throw new NotFoundException("该博客不存在");
        }
        //避免操作数据库
        Blog b=new Blog();
        BeanUtils.copyProperties(blog,b);
        //获取内容并返回
        String content=b.getContent();
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));

        b.setViews(viewCountUtil.getAndincrement(b));

        return b;
    }

    /**
     * 管理端：动态查询
     * @param pageable
     * @param blog
     * @return
     */
    @Transactional
    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        //JpaSpecificationExecutor实现动态查询
        return blogRepository.findAll(new Specification<Blog>() {
            /**
             *
             * @param root 要查询的对象，用于获取表的字段
             * @param criteriaQuery 查询条件的容器
             * @param criteriaBuilder 设置查询条件表达式
             * @return
             */
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                //动态判断
                if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                    predicates.add(criteriaBuilder.like(root.<String>get("title"), "%" + blog.getTitle() + "%"));
                }
                if (blog.getTypeId() != null) {
                    predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"), blog.getTypeId() + ""));
                }
                if (blog.isRecommend()) {
                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }
                criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        }, pageable);
    }

    /**
     * 直接查询
     * @param pageable
     * @return
     */
    @Transactional
    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    /**
     * 搜索查询
     * @param pageable
     * @param query
     * @return
     */
    @Transactional
    @Override
    public Page<Blog> listBlog(String query,Pageable pageable) {
        return blogRepository.findByQuery("%"+query.trim()+"%",pageable);
    }

    /**
     * 查询前size条
     * @param size
     * @return
     */
    @Transactional
    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort=Sort.by(Sort.Direction.DESC,"updateTime");
        Pageable pageable= PageRequest.of(0,size,sort);
        return blogRepository.findTop(pageable);
    }

    @Transactional
    @Override
    public Page<Blog> listTagBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join join = root.join("tags");
                return cb.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    @Transactional
    @Override
    public Page<Blog> listTypeBlog(Long typeId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                if (typeId != null) {
                cq.where(cb.equal(root.<Type>get("type").get("id"), typeId));
                }
                return null;
            }
        },pageable);
    }


    @Override
    public Map<String, List<Blog>> listArchiveBlog() {
        List<String> years=blogRepository.findGroupByYear();
        Map<String, List<Blog>> map=new HashMap<>();
        for (String year : years) {
            map.put(year,blogRepository.findByYear(year));
        }
        return map;
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if (blog.getId() == null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
            blog.setFlag("原创");
        }else {
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogRepository.findById(id).get();
        if (b == null) {
            throw new NotFoundException("该博客不存在");
        }
        BeanUtils.copyProperties(blog, b,MyBeanUtils.getNullPropertyNames(blog));
        blog.setUpdateTime(new Date());
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }
}
