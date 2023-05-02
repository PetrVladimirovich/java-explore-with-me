package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ReactionOnEventDto;
import ru.practicum.ewm.model.ReactionOnEvent;

import java.util.List;

public interface RatingService {

    ReactionOnEventDto createReaction(Long userId, Long eventId, ReactionOnEvent.ReactionStatus  react);

    ReactionOnEventDto updateReaction(Long userId, Long eventId, ReactionOnEvent.ReactionStatus  react);

    void deleteReaction(Long userId, Long eventId);

    List<ReactionOnEventDto> getReactionsOnEvent(Long eventId, Integer from, Integer size);

}