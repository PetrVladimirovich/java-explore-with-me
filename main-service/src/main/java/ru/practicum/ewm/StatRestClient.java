package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import ru.practicum.RestClient;

@Component
public class StatRestClient extends RestClient {

    @Autowired
    public StatRestClient(@Value("${stat-server.url}") String url, RestTemplateBuilder builder) {
        super(url, builder);
    }
}