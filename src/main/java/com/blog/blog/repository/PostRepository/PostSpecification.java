package com.blog.blog.repository.PostRepository;

import com.blog.blog.DTO.PostRequest.PostSearchCriteria;
import com.blog.blog.constants.PostContants.PostCategory;
import com.blog.blog.entity.PostEntity.Post;
import com.blog.blog.repository.Cursor.Cursor;
import com.blog.blog.repository.Cursor.CursorCodec;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PostSpecification {


    public static Specification<Post> isNotDeleted(){
        return (root,query,cb) -> cb.equal(root.get("deleted"),false);
    }

    public static Specification<Post> getFilteredPosts(PostSearchCriteria criteria){
        return (root,query,cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            PostCategory category = criteria.getCategory();
            String content = criteria.getContent();
            Long userId = criteria.getUserId();
            if(category != null){
                predicateList.add(cb.equal(root.get("category"),criteria.getCategory()));
            }


            //filter out deleted posts
            predicateList.add(cb.equal(root.get("deleted"),false));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

    public static Specification<Post> getKeyWordSpecificPost(String content){
        return (root,query,cb) -> {
            String searchPattern = "%" + content.toLowerCase() + "%";
            Predicate titleMatch = cb.like(cb.lower(root.get("title")), searchPattern);
            Predicate contentMatch = cb.like(cb.lower(root.get("content")), searchPattern);
            return cb.or(titleMatch, contentMatch);
        };
    }

    public static Specification<Post> getCategorySpecificPost(PostCategory category){
        return (root,query,cb) -> cb.equal(root.get("category"),category);
    }

    public static Specification<Post> getUserSpecificPost(Long userId){
        return (root,query,cb) -> cb.equal(root.get("user").get("userId"),userId);
    }

    public static Specification<Post> afterCursor(Cursor cursor){
        return (root,query,cb) -> {
            Predicate createdAtCondition = cb.lessThan(root.get("createdAt"),cursor.getCreatedAt());
            Predicate sameTimeOlderId = cb.and(cb.equal(root.get("createdAt"),cursor.getCreatedAt()),cb.lessThan(root.get("postId"),cursor.getId()));
            return cb.or(createdAtCondition,sameTimeOlderId);

        };
    }

}
