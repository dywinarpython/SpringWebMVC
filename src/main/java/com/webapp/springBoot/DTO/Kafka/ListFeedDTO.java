package com.webapp.springBoot.DTO.Kafka;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ListFeedDTO {
    private List<FeedDTO> feedList;
}
