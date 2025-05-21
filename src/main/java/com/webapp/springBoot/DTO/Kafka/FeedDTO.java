package com.webapp.springBoot.DTO.Kafka;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class FeedDTO {

    private Long userId;

    private String namePost;
}
