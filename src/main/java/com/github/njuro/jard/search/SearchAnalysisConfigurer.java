package com.github.njuro.jard.search;

import com.github.njuro.jard.common.Constants;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class SearchAnalysisConfigurer implements LuceneAnalysisConfigurer {

  @Override
  public void configure(LuceneAnalysisConfigurationContext context) {
    context
        .analyzer(Constants.POST_ANALYZER)
        .custom()
        .tokenizer(StandardTokenizerFactory.class)
        .charFilter(HTMLStripCharFilterFactory.class)
        .tokenFilter(ASCIIFoldingFilterFactory.class)
        .tokenFilter(LowerCaseFilterFactory.class)
        .tokenFilter(StopFilterFactory.class)
        .tokenFilter(SnowballPorterFilterFactory.class)
        .param("language", "English");
  }
}
