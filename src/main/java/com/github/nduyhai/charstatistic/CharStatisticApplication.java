package com.github.nduyhai.charstatistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
public class CharStatisticApplication {

  public static void main(String[] args) {
    SpringApplication.run(CharStatisticApplication.class, args);
  }

}

@RestController
class ContentController {

  @Autowired
  private ContentRepository repository;

  @Autowired
  private Statistic statistic;

  @GetMapping("/show")
  public List<String> show(@RequestParam("column") String column,
      @RequestParam("table") String table) {
    return this.repository.all(column, table);
  }

    @GetMapping("/statistic")
    public List<Map<Character, Long>> all(@RequestParam("column") String column,
                                          @RequestParam("table") String table) {
        return  this.statistic.all(this.repository.all(column, table));
    }
}

@Component
class Statistic {
    public List<Map<Character, Long>> all(List<String> list) {
        return list.stream().map(i -> mono(i)).collect(Collectors.toList());
    }

    private Map<Character, Long> mono(String i) {
        return i.chars().mapToObj( c -> (char)c)
                .collect(Collectors.groupingBy( Function.identity(), Collectors.counting()));
    }
}

@Repository
class ContentRepository {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<String> all(String column, String table) {
    String sql = "SELECT %s FROM %s";

    return this.jdbcTemplate
        .query(String.format(sql, column, table), (resultSet, i) -> resultSet.getString(1));
  }
}