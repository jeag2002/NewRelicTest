package com.newrelic.metrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import com.newrelic.metrics.enhanced.MetricConsumerEnhanced;
import com.newrelic.metrics.refactor.MetricConsumerRefactor;

public class Main {
	
    public static final String inputFile = "input.txt";
    public static final String outputFileNormal = "output-normal.txt";
    public static final String outputFileRefactor = "output-refactor.txt";
    public static final String outputFileEnhanced = "output-enhanced.txt";
    
    
    public void processNormal() throws IOException {
      var consumer = new MetricConsumer();
      System.out.println("\nBEGIN. process normal   \n" );
      var start = Instant.now();
      Map<String, Map<Instant, Double>> result;
      try(var is = new FileInputStream(inputFile)) {
          result = consumer.consume(is);
      }
      var end = Instant.now();
      var output = new StringBuilder();
      result.get("cpu").forEach((k, v) -> output.append(k).append(" cpu ").append(v).append("\n"));
      result.get("mem").forEach((k, v) -> output.append(k).append(" mem ").append(v).append("\n"));
      System.out.println(output);
      try(var os = new FileWriter(outputFileNormal)) {
          os.write(output.toString());
      }
      System.out.println("END. process time normal: " + Duration.between(start, end));
    }
    
    public void processRefactor() throws IOException {
    
      var consumer = new MetricConsumerRefactor();
      System.out.println("\nBEGIN. process refactor \n" );
      var start = Instant.now();
      Map<String, Map<Instant, Double>> result;
      try(var is = new FileInputStream(inputFile)) {
          result = consumer.consume(is);
      }
      var end = Instant.now();
      var output = new StringBuilder();
      result.get("cpu").forEach((k, v) -> output.append(k).append(" cpu ").append(v).append("\n"));
      result.get("mem").forEach((k, v) -> output.append(k).append(" mem ").append(v).append("\n"));
      System.out.println(output);
      try(var os = new FileWriter(outputFileRefactor)) {
          os.write(output.toString());
      }
      System.out.println("END. process time refactor: " + Duration.between(start, end));
    
    }
    
    public void processEnhanced() throws IOException {
      var consumer = new MetricConsumerEnhanced();
      System.out.println("\nBEGIN. process enhanced \n" );
      var start = Instant.now();
      Map<String, Map<Instant, Double>> result;
      File fil = new File(inputFile);
      result = consumer.consume(fil);
      var end = Instant.now();
      var output = new StringBuilder();
      result.get("cpu").forEach((k, v) -> output.append(k).append(" cpu ").append(v).append("\n"));
      result.get("mem").forEach((k, v) -> output.append(k).append(" mem ").append(v).append("\n"));
      System.out.println(output);
      try(var os = new FileWriter(outputFileEnhanced)) {
          os.write(output.toString());
      }
      System.out.println("END. process time enhanced: " + Duration.between(start, end));
    
    }
     

    public static void main(String[] args) throws IOException {
      Main main = new Main();
      main.processNormal();
      main.processRefactor();
      main.processEnhanced();

    }
}
