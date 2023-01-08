package com.example.sns.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.example.sns.entity.Role.ADMIN;
import static com.example.sns.entity.Role.USER;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Where(clause = "deleted_date IS NULL")
public class User extends BaseTime{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;
    @NotNull
    private String userName;
    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "commentUser")
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public User(Integer id, String userName, String password, Role role, List<Post> posts, List<PostLike> postLikes, List<Comment> comments) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.posts = posts;
        this.postLikes = postLikes;
        this.comments = comments;
    }

    public User upgradeAdmin(User user) {
        user.role = ADMIN;
        return user;
    }

    public User downgradeUser(User user) {
        user.role = USER;
        return user;
    }
}
