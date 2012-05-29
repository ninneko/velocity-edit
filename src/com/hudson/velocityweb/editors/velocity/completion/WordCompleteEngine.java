package com.hudson.velocityweb.editors.velocity.completion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.eclipse.jdt.internal.ui.text.template.contentassist.PositionBasedCompletionProposal;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.hudson.velocityweb.Plugin;

public class WordCompleteEngine {
	
	private static WordCompleteEngine INSTANCE;
	
	private static final int MAX_MATCH_SIZE = 20;

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
			parseDoc(file, doc);
			
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
		
		Map<String, Integer> stringMap = new TreeMap<String, Integer>();
		
		TopDocs hitDocs = indexSearcher.search(query, 20);
		ScoreDoc[] scoreDocs = hitDocs.scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			QueryScorer scorer = new QueryScorer(query, indexReader, CONTENT_FILE_NAME);
			Highlighter highlighter = new Highlighter(scorer);

			String content = indexSearcher.doc(scoreDocs[i].doc).get(CONTENT_FILE_NAME);
//			String content = fileContentsMap.get(fileID);
			String[] fragments = highlighter.getBestFragments(analyzer, CONTENT_FILE_NAME, content, MAX_MATCH_SIZE);
			
			for (int j = 0; j < fragments.length; j++) {
				Matcher matcher = Pattern.compile("<B>\\w*</B>").matcher(fragments[j]);
				
				while(matcher.find()) {
					String string = matcher.group();
					string = string.substring(3, string.length() - 4);
					if (string.equals(prefix)) {
						continue;
					}
					
					if (stringMap.containsKey(string)) {
						stringMap.put(string, stringMap.get(string) + 1);
						continue;
					}
					
					stringMap.put(string, 0);
				}
			}
		}
		
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		for (String string : stringMap.keySet()) {
//			proposals.add(new CompletionProposal(
//					string,
//					startPos,
//					offset - startPos,
//					string.length(),
//					Plugin.getDefault().getImage("wc"), string, null, null));
			
			Position position = new Position(startPos, offset - startPos);
			WordCompletionProposal proposal = new WordCompletionProposal(string, position, string.length(), Plugin
					.getDefault().getImage("wc"), string, null, null, null);

			proposal.setRelevance(200 + stringMap.get(string));
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

	private void parseDoc(IFile file, IDocument doc) throws Exception {
//		QueryParser queryParser = new QueryParser(Version.LUCENE_34, FILE_NAME_FEILD, analyzer);
//		queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
//		Query query = queryParser.parse(file.getFullPath().toPortableString() + "*");
//		
//		indexWriter.deleteDocuments(query);
		long currentTime = System.currentTimeMillis();
		if (lastIndexTime + 1000*5 > currentTime && file.getFullPath().toPortableString().equals(lastFileName)) {
			return;
		}
		
		indexWriter.deleteAll();
		
		Document document = new Document();
		document.add(new Field(CONTENT_FILE_NAME, doc.get(), Field.Store.YES, Field.Index.ANALYZED));
		document.add(new Field(FILE_NAME_FEILD , file.getFullPath().toPortableString(), Field.Store.YES, Field.Index.ANALYZED));

		indexWriter.addDocument(document);

		indexWriter.commit();
		
		lastFileName = file.getFullPath().toPortableString();
		lastIndexTime = currentTime;
//		if (!fileContentsMap.containsKey(file.getFullPath().toPortableString())) {
//			fileContentsMap.put(file.getFullPath().toPortableString(), doc.get());
//		}
	}

}
