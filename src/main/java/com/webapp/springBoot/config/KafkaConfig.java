package com.webapp.springBoot.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.example.DeleteFriendDTO;
import org.example.RequestFollowersFeedDTO;
import org.example.RequestFriendDTOFeed;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

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
    public NewTopic topicFriendFeedDel(){
        return createTopic("news-feed-topic-friend-del");
    }

    @Bean
    public NewTopic createTopicFollower(){
        return createTopic("news-feed-topic-community");
    }
    @Bean
    public NewTopic topicFollower(){
        return createTopic("news-feed-topic-follower");
    }

    @Bean
    public NewTopic topicFollowerDel(){
        return createTopic("news-feed-topic-follower-del");
    }

    @Bean
    public NewTopic topicDelFeedByNamePost(){
        return createTopic("news-feed-topic-namePost-del");
    }

    @Bean
    public NewTopic topicDelUserFeed(){
        return createTopic("news-feed-topic-user-del");
    }

    @Bean
    public NewTopic topicDelCommunityForFollower(){
        return createTopic("news-feed-topic-community-del");
    }

    @Bean
    @Qualifier("requestFriendKafkaTemplate")
    public KafkaTemplate<String, RequestFriendDTOFeed> requestFriendKafkaTemplate(
            ProducerFactory<String, RequestFriendDTOFeed> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier("deleteFriendKafkaTemplate")
    public KafkaTemplate<String, DeleteFriendDTO> deleteFriendKafkaTemplate(
            ProducerFactory<String, DeleteFriendDTO> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier("stringKafkaTemplate")
    public KafkaTemplate<String, String> stringKafkaTemplate(
            ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Qualifier("requestFollowerKafkaTemplate")
    public KafkaTemplate<String, RequestFollowersFeedDTO> requestFollowerKafkaTemplate(
            ProducerFactory<String, RequestFollowersFeedDTO> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}
