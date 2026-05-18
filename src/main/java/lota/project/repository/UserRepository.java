package lota.project.repository;


import lota.project.model.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ChatUser, String> {
    List<ChatUser> findByOnlineTrue();
}
