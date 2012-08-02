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
import org.apache.lucene.search.TopDocs;
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
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		
		QueryParser queryParser = new QueryParser(Version.LUCENE_34, CONTENT_FILE_NAME, analyzer);
		queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		Query query = queryParser.parse(prefix + "*");
		
		Map<String, Integer> occurenceCountMap = new TreeMap<String, Integer>();
		Map<String, String> stringIndexMap = new HashMap<String, String>();
		
		TopDocs hitDocs = indexSearcher.search(query, 20);
		ScoreDoc[] scoreDocs = hitDocs.scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			QueryScorer scorer = new QueryScorer(query, indexReader, CONTENT_FILE_NAME);
			Highlighter highlighter = new Highlighter(scorer);

			String content = indexSearcher.doc(scoreDocs[i].doc).get(CONTENT_FILE_NAME);
			
//			content = content.replace("\r", "");
//			content = content.replace("\n", "");
			
			String[] fragments = highlighter.getBestFragments(analyzer, CONTENT_FILE_NAME, content, MAX_MATCH_SIZE);
			for (int j = 0; j < fragments.length; j++) {
				Matcher matcher = Pattern.compile("<B>[^(</B>)]*</B>").matcher(fragments[j]);
				
				int offsetInDoc = 0;
				while(matcher.find()) {
					String string = matcher.group();
					
					string = string.substring(3, string.length() - 4);
					
					// hack
					if (string.indexOf(".") > 0) {
						string = string.substring(0, string.indexOf("."));
					}
					
					offsetInDoc = fragments[j].indexOf(string, offsetInDoc);
					if (stringIndexMap.get(string) != null) {
						stringIndexMap.put(string, stringIndexMap.get(string) + ":" + offsetInDoc);
					} else {
						stringIndexMap.put(string, String.valueOf(offsetInDoc));
					}
					offsetInDoc += string.length();
					
					if (string.equals(prefix)) {
						continue;
					}
					
					if (occurenceCountMap.containsKey(string)) {
						occurenceCountMap.put(string, occurenceCountMap.get(string) + 10);
						continue;
					}
					
					occurenceCountMap.put(string, 0);
				}
			}
		}
		
		computeClusteringScore(stringIndexMap, occurenceCountMap);
		
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		for (String string : occurenceCountMap.keySet()) {
//			proposals.add(new CompletionProposal(
//					string,
//					startPos,
//					offset - startPos,
//					string.length(),
//					Plugin.getDefault().getImage("wc"), string, null, null));
			
//			Position position = new Position(startPos, offset - startPos);
//			JavaCompletionProposal proposal = new JavaCompletionProposal(string, position, string.length(), Plugin
//					.getDefault().getImage("wc"), string, null, null, null);
//			Position position = new Position(startPos, offset - startPos);
			JavaCompletionProposal proposal = new JavaCompletionProposal(string, startPos, offset - startPos, Plugin
					.getDefault().getImage("wc"), string, 200 + occurenceCountMap.get(string));

//			proposal.setRelevance(200 + stringMap.get(string));
			proposals.add(proposal);
		}
		
		return proposals;
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

	private int clusterIntervalSpace = 30;
	
	private void computeClusteringScore(Map<String, String> stringIndexMap, Map<String, Integer> occurenceCountMap) {
		Map<String, Integer> clusterCountMap = new TreeMap<String, Integer>();
		int smallestClusterIntervalSpace = Integer.MAX_VALUE;
		for (String matchedString : occurenceCountMap.keySet()) {
//			int largestIndex = 0;
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
			
//			
//			// the first one is alway 0, so minus 1
//			avg /= indexes.length;
//			
//			int variance = 0;
//			for (int i = 0; i < indexes.length; i++) {
//				int currentIndex = NumberUtils.toInt(indexes[i], 0);
////				if ((currentIndex > largestIndex)) {
////					largestIndex = currentIndex;
////				}
//				
//				variance += Math.pow((currentIndex - avg), 2); 
//			}
//			
//			variance /= indexes.length;
//
			clusterIntervalSpace = smallestClusterIntervalSpace*5;
			
			clusterCountMap.put(matchedString, largestCluster);
		}

//		int count = 0;
		for (String matchedString : clusterCountMap.keySet()) {
			if (occurenceCountMap.get(matchedString) < 3) {
				// Occurrence smaller than 3 need not to participate in cluster computing
				continue;
			}
			
			occurenceCountMap.put(matchedString, clusterCountMap.get(matchedString)*5);
//			
//			if (++count > 10) {
//				break;
//			}
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
