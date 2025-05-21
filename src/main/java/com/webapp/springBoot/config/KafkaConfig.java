package com.webapp.springBoot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.partitions}")
    private int partitions;


    private NewTopic createTopic(String nameTopic){
        return TopicBuilder.name(nameTopic)
                .partitions(partitions)
//                .replicas(2)
//                .configs(Map.of("min.insync.replicas", "1")) // мин кол-во серверов которыйе должны быть в синхроне с нашим главным
                .build();
    }


    @Bean
    public NewTopic createTopicFriend(){
        return createTopic("news-feed-topic-friend");
    }

    @Bean
    public NewTopic createTopicUser(){
        return createTopic("news-feed-topic-user");
    }

    @Bean
    public NewTopic TopicFriendFeedDel(){
        return createTopic("news-feed-topic-friend-del");
    }

}
