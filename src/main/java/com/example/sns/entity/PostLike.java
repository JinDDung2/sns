package com.example.sns.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "post_likes")
@Where(clause = "deleted_date IS NULL")
@SQLUpdate(sql = "UPDATE post_likes set delete_date = CURRENT_TIMESTAMP where like_id")
public class PostLike extends BaseTime{

    @Id @GeneratedValue
    @Column(name = "like_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public PostLike(Integer id, Post post, User user) {
        this.id = id;
        this.post = post;
        this.user = user;
    }
}
