package com.newrelic.metrics.refactor;

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

public class MetricConsumerRefactor {

    private Pattern pattern = Pattern.compile("(\\d+) (\\w+) (\\d+)");
    private Map<Instant, List<Integer>> cpuValues = new TreeMap<>();
    private Map<Instant, List<Integer>> memValues = new TreeMap<>();
    
    
    //process new metric
    private void processMetrics(Map<Instant, List<Integer>> metricContent, Instant instant, Integer metric) {
      var valuesCpu = metricContent.computeIfAbsent(instant.truncatedTo(ChronoUnit.MINUTES), k -> new LinkedList<>());
      valuesCpu.add(metric);
    }
    
    //process line
    private void processLine(String line) {
      var matcher = pattern.matcher(line);
      if (matcher.matches()) {
         var instant = Instant.ofEpochSecond(Long.parseLong(matcher.group(1)));
         var metricName = matcher.group(2);
         var metric = Integer.parseInt(matcher.group(3));

         if (metricName.equals("cpu")) {
            processMetrics(cpuValues, instant, metric);		
         } else if (metricName.equals("mem")) {
            processMetrics(memValues, instant, metric);
         }
      }
    }
    
    
    //calculate average
    private Map<Instant, Double> processAverage(Map<Instant, List<Integer>> metric) {
       return metric.entrySet()
               .stream()
               .collect(Collectors.toMap(
                       Map.Entry::getKey,
                       e -> e.getValue().stream()
                               .mapToInt(i -> i)
                               .average()
                               .orElse(-1) ));
    }
    
   
    public Map<String, Map<Instant, Double>> consume(InputStream is) throws IOException {

        var reader = new BufferedReader(new InputStreamReader(is));
        var line = "";
        while ((line = reader.readLine()) != null) {
        	processLine(line);
        }
        var cpuAverages = processAverage(cpuValues);
        var memAverages = processAverage(memValues);
        return Map.of(
                "cpu", new TreeMap<>(cpuAverages),
                "mem", new TreeMap<>(memAverages));
    }
}
