package org.example.core.message.persistence.repository;


import org.example.core.message.persistence.entity.UserMessageEntity;
import org.example.core.message.persistence.entity.UserMessageId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserMessageRepository extends CrudRepository<UserMessageEntity, UserMessageId> {

    @Query(value = "SELECT um from UserMessageEntity um JOIN FETCH um.message" +
            " where ( um.id.senderId =:id1 and um.id.receiverId =:id2) or ( um.id.senderId =:id2 and um.id.receiverId =:id1)",
            countQuery="SELECT count(um) from UserMessageEntity um where ( um.id.senderId =:id1 and um.id.receiverId =:id2) or ( um.id.senderId =:id2 and um.id.receiverId =:id1)")
    Page<UserMessageEntity> findBySenderAndReceiverIds(@Param("id1") int id1, @Param("id2") int id2, Pageable pageable);

    Integer countDistinctByUnreadIsFalseAndMessage_Id(int messageId);

    boolean existsDistinctByReceiverUser_UsernameAndSenderUser_UsernameAndUnreadIsTrue(String receiverUsername,String senderUsername);

    Integer countUserMessageEntityByMessage_Id(int id);

    @Query("SELECT um.senderUser.username,um.receiverUser.username, um.sentTime" +
            " from UserMessageEntity um where um.receiverUser.username =:username or um.senderUser.username =:username order by um.sentTime desc ")
    List<String[]> getConversationPartnerNames(@Param("username") String username);

    Integer countByReceiverUserUsernameAndUnreadIsTrue(String username);

    Optional<UserMessageEntity> findByMessage_IdAndReceiverUser_UsernameAndAndSenderUser_Username(int id,String senderUsername,String receiverUsername);
}
