package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.StatCountDto;
import ru.practicum.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<Stat, Long> {
    @Query(" select new ru.practicum.dto.StatCountDto(a.app, a.uri, count(distinct a.ip)) from Stat a " +
            "where (a.timestamp between :start and :end) and (:#{#uris == null} = true or a.uri in :uris) " +
            "group by a.app, a.uri " +
            "order by count(distinct a.ip) desc")
    List<StatCountDto> getStatsUniqueIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query(" select new ru.practicum.dto.StatCountDto(a.app, a.uri, count(a.ip)) from Stat a " +
            "where (a.timestamp between :start and :end) and (:#{#uris == null} = true or a.uri in :uris) " +
            "group by a.app, a.uri " +
            "order by count(a.ip) desc")
    List<StatCountDto> getStatsAllIp(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);
}