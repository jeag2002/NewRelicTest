package com.newrelic.metrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MetricConsumer {

    private Pattern pattern = Pattern.compile("(\\d+) (\\w+) (\\d+)");
    private Map<Instant, List<Integer>> cpuValues = new TreeMap<>();
    private Map<Instant, List<Integer>> memValues = new TreeMap<>();

    public Map<String, Map<Instant, Double>> consume(InputStream is) throws IOException {

        var reader = new BufferedReader(new InputStreamReader(is));
        var line = "";
        while ((line = reader.readLine()) != null) {
            var matcher = pattern.matcher(line);
            if (!matcher.matches()) {
                continue;
            }

            var instant = Instant.ofEpochSecond(Long.parseLong(matcher.group(1)));
            var metricName = matcher.group(2);

            if (metricName.equals("cpu")) {
                var cpu = Integer.parseInt(matcher.group(3));
                var valuesCpu = cpuValues.computeIfAbsent(instant.truncatedTo(ChronoUnit.MINUTES), k -> new LinkedList<>());
                valuesCpu.add(cpu);
            } else if (metricName.equals("mem")) {
                var mem = Integer.parseInt(matcher.group(3));
                var valuesMem = memValues.computeIfAbsent(instant.truncatedTo(ChronoUnit.MINUTES), k -> new LinkedList<>());
                valuesMem.add(mem);
            }
        }

        var cpuAverages = cpuValues.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .mapToInt(i -> i)
                                .average()
                                .orElse(-1)
                ));

        var memAverages = memValues.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream()
                                .mapToInt(i -> i)
                                .average()
                                .orElse(-1)
                ));

        return Map.of(
                "cpu", new TreeMap<>(cpuAverages),
                "mem", new TreeMap<>(memAverages));
    }
}
