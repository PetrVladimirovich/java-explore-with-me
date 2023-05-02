package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.User;

import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u, sum(e.rating) as rate " +
            "from User u " +
            "inner join Event e on e.initiator = u and e.publishedOn > :eventDate " +
            "group by u"
    )
    Page<UserRating> getMostRateUser(@Param("eventDate") LocalDateTime eventPublishedDate, Pageable page);

    interface UserRating {
        User getUser();

        Long getRate();
    }
}