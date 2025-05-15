package com.webapp.springBoot.DTO.Feed;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FeedDTO {

    private Long userId;

    private String namePost;

    @Override
    public String toString() {
        return "FeedDTO{" +
                "userId=" + userId +
                ", namePost='" + namePost + '\'' +
                '}';
    }
}
