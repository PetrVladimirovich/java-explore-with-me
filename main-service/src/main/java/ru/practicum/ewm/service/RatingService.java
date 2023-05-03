package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ReactionOnEventDto;
import ru.practicum.ewm.model.ReactionStatus;

import java.util.List;

public interface RatingService {

    ReactionOnEventDto createReaction(Long userId, Long eventId, ReactionStatus  react);

    ReactionOnEventDto updateReaction(Long userId, Long eventId, ReactionStatus react);

    void deleteReaction(Long userId, Long eventId);

    List<ReactionOnEventDto> getReactionsOnEvent(Long eventId, Integer from, Integer size);

}