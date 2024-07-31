package com.newrelic.metrics.enhanced;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MetricConsumerEnhanced {
  
  private static final Integer ONE = 1;
  private Pattern pattern = Pattern.compile("(\\d+) (\\w+) (\\d+)");
  
  //hashmap concurrent
  private Map<Instant, Integer> cpuCounts = new ConcurrentHashMap<Instant, Integer>();
  private Map<Instant, Integer> cpuTotals = new ConcurrentHashMap<Instant, Integer>();

  //hashmap concurrent
  private Map<Instant, Integer> memCounts = new ConcurrentHashMap<Instant, Integer>();
  private Map<Instant, Integer> memTotals = new ConcurrentHashMap<Instant, Integer>();

  //TreeMap concurrent
  private Map<Instant, Double> cpu = new ConcurrentSkipListMap<Instant, Double>();
  private Map<Instant, Double> mem = new ConcurrentSkipListMap<Instant, Double>();

  //response.
  private  Map<String, Map<Instant, Double>> response = new Hashtable<String, Map<Instant, Double>>();

  //process
  private void processLine(String line) {
    var matcher = pattern.matcher(line);
    if (matcher.matches()) {
        var instant = Instant.ofEpochSecond(Long.parseLong(matcher.group(1)));
        var instantToMinutes = instant.truncatedTo(ChronoUnit.MINUTES);
        var metricName = matcher.group(2);
        var value = Integer.parseInt(matcher.group(3));
            
        if (metricName.equals("cpu")) {
            insertData(cpuCounts, cpuTotals, cpu, instantToMinutes, value);
        } else if (metricName.equals("mem")) {
            insertData(memCounts, memTotals, mem, instantToMinutes, value);
        }
        
    }
  }


  //insert data
  //synchronized function in theory is not necessary with concurrent containers. 
  //in tests i have to put it for avoiding race conditions. 
  private synchronized void insertData(Map<Instant, Integer> count, Map<Instant, Integer> total, Map<Instant, Double> avg, Instant index, Integer value) {
    if (count.containsKey(index)) {
        count.put(index, count.get(index) +  ONE);
        total.put(index, total.get(index) + value);
        avg.put(index, calculateAverage(total.get(index), count.get(index)));
    } else {
        count.put(index, ONE);
        total.put(index, value);
        avg.put(index, calculateAverage(total.get(index), count.get(index)));
    }
  }

  //calculate average
  private double calculateAverage(Integer total, Integer count) {
    if (total == null) {
      return -1;
    } else if (count == null) {
      return -1;
    } else {
      return Double.valueOf(total)/Double.valueOf(count);
    } 
  }


  //processor
  public Map<String, Map<Instant, Double>> consume(File file) throws IOException {
   //file digester
   try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8).parallel()) {
      stream.parallel().forEach(line -> { 
        processLine(line);
      });
   } catch (IOException e) {
      System.out.println("something happened " + e.getMessage());
   }
        
   response.put("cpu", cpu);
   response.put("mem", mem);
   return response;
  }
}
