package com.github.njuro.jboard.models.dto;

import com.github.njuro.jboard.models.Board;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardCatalog {

  private Board board;
  private List<ThreadCatalogEntry> threads;
}
