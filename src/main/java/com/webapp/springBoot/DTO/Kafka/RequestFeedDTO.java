package com.webapp.springBoot.DTO.Kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestFeedDTO {


    private String namePost;

    // 0 - User
    // 1 - Community
    private Integer idAuthor;
}
