package com.example.secondhandlibrary.Member.Entity;


import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="Member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long MemberId;

    @Column
    private Long authID ;

    @Column
    private String nickName;

    @Column
    private String profile_img;

}
