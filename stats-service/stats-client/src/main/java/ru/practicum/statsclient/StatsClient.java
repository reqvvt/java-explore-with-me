package ru.practicum.statsclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statsdto.HitDto;

import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {
    private static final String HIT_API_PREFIX = "/hit";
    private static final String STAT_API_PREFIX = "/stats";

    @Autowired
    public StatsClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveHit(HitDto hitDto) {
        return post(HIT_API_PREFIX, hitDto);
    }

    public ResponseEntity<Object> getStat(String start,
                                          String end,
                                          List<String> uris,
                                          Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris,
                "unique", unique);
        return post(STAT_API_PREFIX, parameters);
    }
}