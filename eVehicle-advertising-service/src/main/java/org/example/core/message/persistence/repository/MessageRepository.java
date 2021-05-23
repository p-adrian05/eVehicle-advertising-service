package org.example.core.message.persistence.repository;


import org.example.core.message.persistence.entity.MessageEntity;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<MessageEntity,Integer> {
}
