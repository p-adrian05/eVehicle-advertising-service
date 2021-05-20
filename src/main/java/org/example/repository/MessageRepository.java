package org.example.repository;

import org.example.repository.entity.MessageEntity;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<MessageEntity,Integer> {
}
