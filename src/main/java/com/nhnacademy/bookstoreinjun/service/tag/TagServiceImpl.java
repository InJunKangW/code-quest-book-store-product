package com.nhnacademy.bookstoreinjun.service.tag;

import com.nhnacademy.bookstoreinjun.dto.category.CategoryGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.entity.Tag;
import com.nhnacademy.bookstoreinjun.exception.DuplicateException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundNameException;
import com.nhnacademy.bookstoreinjun.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final String DUPLICATE_TYPE = "tag";


    public TagRegisterResponseDto saveTag(TagRegisterRequestDto tagRegisterRequestDto) {
        if (tagRepository.existsByTagName(tagRegisterRequestDto.tagName())) {
            throw new DuplicateException(DUPLICATE_TYPE);
        }else{
            Tag tag = tagRepository.save(Tag.builder()
                    .tagName(tagRegisterRequestDto.tagName())
                    .build());
            return new TagRegisterResponseDto(tag.getTagId(), tag.getTagName());
        }
    }

    public Tag updateTag(Tag tag) {
        if (!tagRepository.existsById(tag.getTagId())) {
            throw new NotFoundIdException(DUPLICATE_TYPE, tag.getTagId());
        }else{
            return tagRepository.save(tag);
        }
    }

    public List<TagGetResponseDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tag -> TagGetResponseDto.builder()
                        .tagName(tag.getTagName())
                        .build())
                .toList();
//        return categoryRepository.findAll().stream()
//                .map(category -> CategoryGetResponseDto.builder()
//                        .categoryName(category.getCategoryName())
//                        .parentCategory(category.getParentCategory())
//                        .build())
//                .collect(Collectors.toList());
    }

    public List<TagGetResponseDto> getTagsContaining(String tagName) {
        return tagRepository.findAllByTagNameContaining(tagName).stream()
                .map(tag -> TagGetResponseDto.builder()
                        .tagName(tag.getTagName())
                        .build())
                .toList();
    }

    public TagGetResponseDto getTagDtoByTagName(String tagName) {
        Tag tag = tagRepository.findByTagName(tagName);
        if (tag == null) {
            throw new NotFoundNameException(DUPLICATE_TYPE, tagName);
        }else{
            return TagGetResponseDto.builder()
                    .tagName(tag.getTagName())
                    .build();
        }
    }

    public Tag getTagByTagName(String tagName) {
        Tag tag = tagRepository.findByTagName(tagName);
        if (tag == null) {
            throw new NotFoundNameException(DUPLICATE_TYPE, tagName);
        }else{
            return tag;
        }
    }
}
