package com.hudson.velocityweb.editors.velocity.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.hudson.velocityweb.Plugin;

public class WordCompleteEngine {
	
	private static final int CLUSTER_SCORE_FACTOR = 5;

	private static final int CLUSTER_RANGE_FACTOR = 5;

	private static WordCompleteEngine INSTANCE;
	
	private static final int MAX_MATCH_SIZE = 30;

	private Directory directory = new RAMDirectory();
	
	private Analyzer analyzer = new IKAnalyzer();
	
	private IndexWriterConfig indexWriterConfig;
	
	private IndexWriter indexWriter;
	
	private static final String CONTENT_FILE_NAME = "text";

	private static final String FILE_NAME_FEILD = "name";
	
	private String lastFileName;
	
	private long lastIndexTime = 0;
	
//	private Map<String, String> fileContentsMap = new HashMap<String, String>();
	
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
	public List<ICompletionProposal> computeProposals(IFile file, IDocument doc, String prefix, int offset, int startPos) {
		try {
			long currentTime = System.currentTimeMillis();
			if (lastIndexTime + 1000*5 <= currentTime || !file.getFullPath().toPortableString().equals(lastFileName)) {
				parseDoc(file, doc);
				
				lastIndexTime = currentTime;
				lastFileName = file.getFullPath().toPortableString();
			}
			
			return computeProposals(prefix, file.getFullPath().toPortableString(), offset, startPos);
		} catch (Exception e) {
			return Collections.EMPTY_LIST;
		}
	}

	private List<ICompletionProposal> computeProposals(String prefix, String fileID, int offset, int startPos) throws Exception {
		Map<String, Integer> occurenceCountMap = new TreeMap<String, Integer>();
		Map<String, String> IndexPositionsMap = new HashMap<String, String>();
		
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		QueryParser queryParser = new QueryParser(Version.LUCENE_34, CONTENT_FILE_NAME, analyzer);
		queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = queryParser.parse(prefix + "*");
		
		Highlighter highlighter = new Highlighter(new QueryScorer(query, indexReader, CONTENT_FILE_NAME));
		
		ScoreDoc[] scoreDocs = indexSearcher.search(query, 20).scoreDocs;
		for (int scoreDocsIndex = 0; scoreDocsIndex < scoreDocs.length; scoreDocsIndex++) {
			String content = indexSearcher.doc(scoreDocs[scoreDocsIndex].doc).get(CONTENT_FILE_NAME);
			String[] fragments = highlighter.getBestFragments(analyzer, CONTENT_FILE_NAME, content, MAX_MATCH_SIZE);
			for (int fragIndex = 0; fragIndex < fragments.length; fragIndex++) {
				Matcher matcher = Pattern.compile("<B>[^(</B>)]*</B>").matcher(fragments[fragIndex]);
				
				int offsetInFragment = 0;
				while(matcher.find()) {
					String string = matcher.group();
					string = string.substring("<B>".length(), string.length() - "</B>".length());
					
					// hack, don't want xxx.getxxx come out
					if (string.indexOf(".") > 0) {
						string = string.substring(0, string.indexOf("."));
					}
					
					offsetInFragment = fragments[fragIndex].indexOf(string, offsetInFragment);
					if (IndexPositionsMap.get(string) == null) {
						IndexPositionsMap.put(string, "" + offsetInFragment);
					} else {
						IndexPositionsMap.put(string, IndexPositionsMap.get(string) + ":" + offsetInFragment);
					}
					offsetInFragment += string.length();

					// ignore the string itself
					if (string.equals(prefix)) {
						continue;
					}
					
					if (occurenceCountMap.containsKey(string)) {
						occurenceCountMap.put(string, occurenceCountMap.get(string) + 10);
					} else {
						occurenceCountMap.put(string, 0);
					}
				}
			}
		}
		
		computeClusteringScore(IndexPositionsMap, occurenceCountMap);
		
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		for (String string : occurenceCountMap.keySet()) {
			JavaCompletionProposal proposal = new JavaCompletionProposal(string, startPos, offset - startPos, Plugin
					.getDefault().getImage("wc"), string, 200 + occurenceCountMap.get(string));

			proposals.add(proposal);
		}
		
		return proposals;
	}

	private int clusterIntervalSpace = 30;
	
	private void computeClusteringScore(Map<String, String> stringIndexMap, Map<String, Integer> occurenceCountMap) {
		Map<String, Integer> clusterCountMap = new TreeMap<String, Integer>();
		int smallestClusterIntervalSpace = Integer.MAX_VALUE;
		for (String matchedString : occurenceCountMap.keySet()) {
			String[] indexes = stringIndexMap.get(matchedString).split(":");
			
			int clusterCount = 0;
			int lastIndex = 0;
			int largestCluster = 0;
			for (int i = 0; i < indexes.length; i++) {
				int index = NumberUtils.toInt(indexes[i], 0);
				int intervalSpace = index - lastIndex;
				if (intervalSpace < clusterIntervalSpace) {
					clusterCount++;
					
					if (smallestClusterIntervalSpace > intervalSpace) {
						smallestClusterIntervalSpace = intervalSpace;
					}
				} else {
					largestCluster = clusterCount;
					clusterCount = 0;
				} 

				lastIndex = index;
			}
			
			if (clusterCount > largestCluster) {
				largestCluster = clusterCount;
			}

			clusterIntervalSpace = smallestClusterIntervalSpace*CLUSTER_RANGE_FACTOR;
			
			clusterCountMap.put(matchedString, largestCluster);
		}

		for (String matchedString : clusterCountMap.keySet()) {
			if (occurenceCountMap.get(matchedString) < 3) {
				// Occurrence smaller than 3 need not to participate in cluster computing
				continue;
			}
			
			occurenceCountMap.put(matchedString, clusterCountMap.get(matchedString)*CLUSTER_SCORE_FACTOR);
		}
	}

	private void parseDoc(IFile file, IDocument doc) throws Exception {
//		QueryParser queryParser = new QueryParser(Version.LUCENE_34, FILE_NAME_FEILD, analyzer);
//		queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
//		Query query = queryParser.parse(file.getFullPath().toPortableString() + "*");
//		
//		indexWriter.deleteDocuments(query);
		indexWriter.deleteAll();
		
		Document document = new Document();
		document.add(new Field(CONTENT_FILE_NAME, doc.get(), Field.Store.YES, Field.Index.ANALYZED));
		document.add(new Field(FILE_NAME_FEILD , file.getFullPath().toPortableString(), Field.Store.YES, Field.Index.ANALYZED));

		indexWriter.addDocument(document);

		indexWriter.commit();
		
		lastFileName = file.getFullPath().toPortableString();
//		if (!fileContentsMap.containsKey(file.getFullPath().toPortableString())) {
//			fileContentsMap.put(file.getFullPath().toPortableString(), doc.get());
//		}
	}

}
