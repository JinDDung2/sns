package com.example.sns.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE comments SET deleted_date = CURRENT_TIMESTAMP where comment_id = ?")
public class Comment extends BaseTime{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer id;
    @NotNull
    private String comment;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User commentUser;

    @Builder
    public Comment(Integer id, String comment, Post post, User commentUser) {
        this.id = id;
        this.comment = comment;
        this.post = post;
        this.commentUser = commentUser;
    }
}
