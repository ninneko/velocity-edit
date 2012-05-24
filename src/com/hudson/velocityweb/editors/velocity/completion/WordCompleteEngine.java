package com.hudson.velocityweb.editors.velocity.completion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class WordCompleteEngine {
	
	private static WordCompleteEngine INSTANCE;
	
	private static final int MAX_MATCH_SIZE = 20;

	private Directory directory = new RAMDirectory();
	
	private Analyzer analyzer = new IKAnalyzer();
	
	private IndexWriterConfig indexWriterConfig;
	
	private String contentFieldName = "text";

	private IndexWriter indexWriter;

	private String fileNameField = "name";
	
	private WordCompleteEngine() throws Exception {
		indexWriterConfig = new IndexWriterConfig(Version.LUCENE_34, analyzer);
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		
		indexWriter = new IndexWriter(directory, indexWriterConfig);
	}
	
	public static synchronized WordCompleteEngine getInstance() {
		if (INSTANCE == null) {
			try {
				INSTANCE = new WordCompleteEngine();
			} catch (Exception e) {
				return null;
			}
		}
		
		return INSTANCE;
	}

	@SuppressWarnings("unchecked")
	public List<String> computeSuggestions(IFile file, IDocument doc, String prefix) {
		try {
			parseDoc(file, doc);
			
			return searchDoc(prefix);
		} catch (Exception e) {
			return Collections.EMPTY_LIST;
		}
	}

	private List<String> searchDoc(String prefix) throws Exception {
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		QueryParser queryParser = new QueryParser(Version.LUCENE_34, contentFieldName, analyzer);
		queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = queryParser.parse(prefix + "*");
		
		Set<String> matchedStrings = new HashSet<String>();
		matchedStrings.add(prefix);
		
		TopDocs hitDocs = indexSearcher.search(query, 20);
		ScoreDoc[] scoreDocs = hitDocs.scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			QueryScorer scorer = new QueryScorer(query, indexReader, contentFieldName);
			Highlighter highlighter = new Highlighter(scorer);

			String content = indexSearcher.doc(scoreDocs[i].doc).get(contentFieldName);
			String[] fragments = highlighter.getBestFragments(analyzer, contentFieldName, content, MAX_MATCH_SIZE);
			
			for (int j = 0; j < fragments.length; j++) {
				Matcher matcher = Pattern.compile("<B>\\w*</B>").matcher(fragments[i]);
				
				while(matcher.find()) {
					String string = matcher.group();
					matchedStrings.add(string.substring(3, string.length()-4));
				}
			}
		}
		
		matchedStrings.remove(prefix);
		return Arrays.asList(matchedStrings.toArray(new String[matchedStrings.size()]));
		
//		Term t = new Term(fieldName, prefix);
//		TermEnum termEnum = new PrefixTermEnum(indexReader, t);
//		List<String> matchedStrings = new ArrayList<String>();
//		while (termEnum.next()) {
//			Term term = termEnum.term();
//			matchedStrings.add(term.text());
//			
//			if (matchedStrings.size() > MAX_MATCH_SIZE) {
//				break;
//			}
//		}
//		
//		termEnum.close();
	}

	private void parseDoc(IFile file, IDocument doc) throws Exception {
		QueryParser queryParser = new QueryParser(Version.LUCENE_34, fileNameField, analyzer);
		queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = queryParser.parse(file.getFullPath().toOSString());
		
		indexWriter.deleteDocuments(query);
		
		Document document = new Document();
		document.add(new Field(contentFieldName, doc.get(), Field.Store.YES, Field.Index.ANALYZED));
		document.add(new Field(fileNameField , file.getFullPath().toOSString(), Field.Store.YES, Field.Index.ANALYZED));

		indexWriter.addDocument(document);

		indexWriter.commit();
	}

}
