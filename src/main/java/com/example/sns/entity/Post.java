package com.example.sns.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "posts")
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE posts SET deleted_date = CURRENT_TIMESTAMP where post_id = ?")
public class Post extends BaseTime{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer id;

    @NotNull
    private String title;
    @NotNull
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(Integer id, String title, String body, User user, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
        this.comments = comments;
    }

    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
