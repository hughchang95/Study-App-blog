package com.hugh.blog.service.impl;

import com.hugh.blog.exception.NotFoundException;
import com.hugh.blog.po.Tag;
import com.hugh.blog.repository.TagRepository;
import com.hugh.blog.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagRepository tagRepository;

    @Transactional
    @Override
    public Tag saveTag(Tag Tag) {
        return tagRepository.save(Tag);
    }

    @Transactional
    @Override
    public Tag getTag(Long id) {
        return tagRepository.findById(id).get();
    }

    @Override
    public Tag getTagByName(String name) {
        return tagRepository.findTagByName(name);
    }

    @Transactional
    @Override
    public Page<Tag> listTag(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public List<Tag> listTag() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> listTagTop(Integer size) {
        Sort sort=Sort.by(Sort.Direction.DESC,"blogs.size");
        Pageable pageable= PageRequest.of(0,size,sort);
        return tagRepository.findTop(pageable);
    }

    @Transactional
    @Override
    public List<Tag> listTag(String ids) {
        return tagRepository.findAllById(stringToList(ids));
    }

    private List<Long> stringToList(String ids){
        List<Long> list=new ArrayList<>();
        if (ids!=null&&!ids.equals("")){
            String[] split = ids.split(",");
            for (String s : split) {
                list.add(new Long(s));
            }
        }
        return list;
    }

    @Transactional
    @Override
    public Tag updateTag(Long id, Tag Tag) {
        Tag t = tagRepository.findById(id).get();
        if (t==null){
            throw new NotFoundException("不存在该类型");
        }
        BeanUtils.copyProperties(Tag,t);
        return tagRepository.save(t);
    }

    @Transactional
    @Override
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}
