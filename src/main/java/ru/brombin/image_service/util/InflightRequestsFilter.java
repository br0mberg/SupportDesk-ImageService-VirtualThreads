package ru.brombin.image_service.util;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InflightRequestsFilter extends OncePerRequestFilter {

    private final AtomicInteger inflight = new AtomicInteger();

    public InflightRequestsFilter(MeterRegistry registry) {
        Gauge.builder("http.server.inflight", inflight, AtomicInteger::get)
                .description("In-flight HTTP requests")
                .register(registry);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        inflight.incrementAndGet();
        try {
            chain.doFilter(req, res);
        } finally {
            inflight.decrementAndGet();
        }
    }
}
