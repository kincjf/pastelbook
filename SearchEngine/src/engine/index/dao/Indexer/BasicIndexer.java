package engine.index.dao.Indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import common.FileContainer;

import engine.index.vo.IndexDataBean;

/**
 * Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing. Run
 * it with no command-line arguments for usage information.
 */
public class BasicIndexer {
	String indexPath; // index된 파일을 저장할 폴더
	String docsPath; // index할 파일을 저장한 폴더

	HashMap<Integer, IndexDataBean> newIdxBeanMap;
	HashMap<Integer, Integer> updateIdxNHit;
	List<Integer> newIdxList;
	ArrayList<Integer> updateIdxList;

	String contributor;

	Iterator<Integer> newIdxListIter;

	public BasicIndexer(String indexPath, String docsPath, String contributor,
			HashMap<Integer, IndexDataBean> newIdxBeanMap,
			HashMap<Integer, Integer> updateIdxNMap, List<Integer> newIdxList,
			ArrayList<Integer> updateIdxList) {
		// indexPath, docsPath, contributor, newIdxBeanMap, updateIdxNHit,
		// newIdxList, updateIdxList
		this.indexPath = indexPath;
		this.docsPath = docsPath;
		this.newIdxBeanMap = new HashMap<Integer, IndexDataBean>(newIdxBeanMap);
		this.updateIdxNHit = new HashMap<Integer, Integer>(updateIdxNMap);
		this.newIdxList = new ArrayList<Integer>(newIdxList);
		this.updateIdxList = new ArrayList<Integer>(updateIdxList);

		// 차후에 간단한 배열로 써서 하면 메모리를 아낄 수 있을 것 같다..
		// 일단 테스트니깐 생각나는대로 쓰자..
	}

	/** Index all text files under a directory. */
	public int index() {
		String usage = "java org.apache.lucene.demo.IndexFiles"
				+ " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
				+ "This indexes the documents in DOCS_PATH, creating a Lucene index"
				+ "in INDEX_PATH that can be searched with SearchFiles";

		boolean create = false; // Index를 다 지우고 새로??
		int count = 0;

		if (docsPath == null) {
			System.err.println("Usage: " + usage);
			System.exit(1);
		}

		final File indexDir = new File(docsPath);
		if (!indexDir.exists() || !indexDir.canRead()) {
			System.out
					.println("BasicIndex : Document directory '"
							+ indexDir.getAbsolutePath()
							+ "' does not exist or is not readable, please check the path");
			System.exit(1);
		}

		Date start = new Date();
		try {
			System.out.println("BasicIndex : Indexing to directory '"
					+ indexPath + "'...");

			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_40,
					analyzer);

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			} // 새로 인덱스를 만들 것인가(CREATE), 아니면 추가 할 것인가(CREATE_OR_APPEND)?

			newIdxListIter = newIdxList.iterator();

			IndexWriter writer = new IndexWriter(dir, iwc);
			count = indexDocs(writer, indexDir);

			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here. This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			// writer.forceMerge(1);

			writer.close();

			Date end = new Date();
			System.out.println(end.getTime() - start.getTime()
					+ " total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass()
					+ "\n with message: " + e.getMessage());
		}

		return count;
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is
	 * given, recurses over files and directories found under the given
	 * directory.
	 * 
	 * NOTE: This method indexes one document per input file. This is slow. For
	 * good throughput, put multiple documents into your input file(s). An
	 * example of this is in the benchmark module, which can create "line doc"
	 * files, one document per line, using the <a href=
	 * "../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	 * >WriteLineDocTask</a>.
	 * 
	 * @param writer
	 *            Writer to the index where the given file/dir info will be
	 *            stored
	 * @param file
	 *            The file to index, or the directory to recurse into to find
	 *            files to index
	 * @param map2
	 * @throws IOException
	 *             If there is a low-level I/O error
	 */
	int indexDocs(IndexWriter writer, File file) {
		// do not try to index files that cannot be read
		int count = 0;

		/*
		 * target of create indexing list (new) url, title, path modified(date),
		 * contents, registered(date), members(source(name) of donate doc),
		 * hits(amount of who is been indexed doc)
		 */

		/*
		 * target of update indexing list (new) members, hits
		 */

		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) { // directory
					for (int i = 0; i < files.length; i++) {
						count += indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {
				// 중복되는 파일을 인덱싱 할 경우 pass함 (하지 않음)
				// map list에 존재 하지 않는 파일일 경우 인덱싱 하지 않음
				String filename = FileContainer.deleteExtension(file.getName());
				boolean isUpdateIdxList = false, isNewIdxList = false;
				Integer hit = 0;
				int idx = 0;
				
				Document doc = new Document();

				IndexDataBean indexDataBean = null;
				isUpdateIdxList = updateIdxList.contains(Integer
						.parseInt(filename));

				if (isUpdateIdxList == true) { // 찾은 파일 이름이 updateList에 있는 경우
					doc = new Document();

					System.out.println("BasicIndexer : " + file);
					hit = updateIdxNHit.get(Integer.parseInt(filename));		// * <Integer, Integer>

					Field hitField = new StringField("hit", hit.toString() + 1,
							Field.Store.NO);
					doc.add(hitField);

					try {
						writer.updateDocument(new Term("hit", hit.toString()), doc);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (isUpdateIdxList == false) { // 찾은 파일이 newList에 있는지 검사
					isNewIdxList = newIdxList.contains(Integer
							.parseInt(filename));

					if (isNewIdxList == true) { // 새로운 파일(newList에 있음)
						System.out.println("BasicIndexer : " + file);
						idx = newIdxListIter.next();
						indexDataBean = newIdxBeanMap.get(idx);

						hit = 1;

						FileInputStream fis = null;
						try {
							fis = new FileInputStream(file);

							Field urlField = new StringField("url",
									indexDataBean.getUrl(), Field.Store.YES);
							Field titleField = new StringField("title",
									indexDataBean.getTitle(), Field.Store.YES);
							//titleField.setBoost(1.5f);		// title에 대해서 1.5배의 가중치를 부여
							// err - 인덱싱이 안된 field에 대해서는 부여하지 못한다.....
							// 색인을 병합할 때 해야될 것 같다..
							Field modifiedField = new StringField(
									"modifiedDate",
									indexDataBean.getModifiedDate(),
									Field.Store.NO);
							Field registeredField = new StringField(
									"registeredDate",
									indexDataBean.getRegisteredDate(),
									Field.Store.NO);
							Field pathField = new StringField("path",
									file.getPath(), Field.Store.NO);
							Field hitField = new StringField("hit",
									hit.toString(), Field.Store.NO);

							doc.add(urlField);
							doc.add(titleField);
							doc.add(modifiedField);
							doc.add(registeredField);
							doc.add(pathField);
							doc.add(hitField);
							
							try {
								doc.add(new TextField("contents",
										new BufferedReader(
												new InputStreamReader(fis,
														"UTF-8"))));
								writer.addDocument(doc);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (FileNotFoundException fnfe) {
							// at least on windows, some temporary files raise
							// this
							// exception with an "access denied" message
							// checking if the file can be read doesn't help
							
							return 0;
						} finally {
							try {
								fis.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						++count;
					}
				} else { // 기존 있는 파일일 경우(현재 이미 색인이 되있지만 북마크상에는 없는 파일)
					// 아무것도 안한다.
					System.out.println("BasicIndexer - nothing to do");
				}
			}
		}

		return count;
	}
}