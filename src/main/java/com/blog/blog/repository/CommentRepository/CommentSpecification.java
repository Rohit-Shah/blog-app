package com.blog.blog.repository.CommentRepository;


import com.blog.blog.entity.CommentEntity.Comment;
import org.springframework.data.jpa.domain.Specification;

public class CommentSpecification {

    public static Specification<Comment> belongsToPost(Long postId){
        return (root,query,cb) -> cb.equal(root.get("post").get("postId"),postId);
    }

    public static Specification<Comment> isNotDeleted(){
        return (root,query,cb) -> cb.equal(root.get("deleted"),false);
    }

    public static Specification<Comment> isTopLevel(){
        return (root,query,cb) -> cb.isNull(root.get("parentCommentId"));
    }

    public static Specification<Comment> belongsToParentCommentId(Long parentCommentId){
        return (root,query,cb) -> cb.equal(root.get("parentCommentId"),parentCommentId);
    }

}
