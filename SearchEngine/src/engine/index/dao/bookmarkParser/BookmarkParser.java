package engine.index.dao.bookmarkParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.json.simple.parser.JSONParser;

public class BookmarkParser {
	public JSONArray Parser(String Filename) {

		JSONParser parser = new JSONParser();
	
		try {
			
			Object obj = parser.parse(new FileReader(Filename));
					//"/data/bookmark/chrome/Bookmarks_13.12.04"));
			
			String parsed = obj.toString();
			
			JSONObject jsonObject = (JSONObject)JSONSerializer.toJSON(parsed);
			// JSONObject (json-lib)형태로 변환
			JSONObject roots = (JSONObject) jsonObject.get("roots");
			
			JSONObject bookmark_bar = (JSONObject) roots.get("bookmark_bar");
			JSONObject other = (JSONObject) roots.get("other");
			JSONObject synced = (JSONObject) roots.get("synced");
			
			// jsonObject = null;

			JSONArray bookmarkArray, otherArray, syncedArray;
			JSONArray storeArray = new JSONArray();
			//bookmark_bar, other, synced를 JSONArray로 변환하기 위한 과정
			
			bookmarkArray = bookmark_bar.getJSONArray("children");
			otherArray = other.getJSONArray("children");
			syncedArray = synced.getJSONArray("children");
			
			FindChild(bookmarkArray, storeArray);
			FindChild(otherArray, storeArray);
			FindChild(syncedArray, storeArray);
			// 북마크를 찾아서 하나의 JSONArray(Object1, Object2, ....)의 형태로 쓰기 좋게 만들어준다.
			
			return storeArray;
			
		} catch (FileNotFoundException e) {
			System.err.println("BookmarkParser - FileNotFound");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("BookmarkParser - IO");
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("BookmarkParser - Parser Parse");
			e.printStackTrace();
		}
		return null;

	}

	// recursive Func
	public void FindChild(JSONArray children, JSONArray out) {
		JSONObject obj, tmpObj;
		Iterator<JSONObject> globalIterator = children.iterator();
		// array 안의 object에 대한 검사
		while (globalIterator.hasNext()) { // return JSONObject
			obj = globalIterator.next(); // JSONArray 안에 있는
														// JSONObject를 순회

			if (obj.has("children")) { // children(JSONArray)가 있으면
				FindChild(obj.getJSONArray("children"), out); // 부분 JSONArray에
																// 대한 순회
			} else if (obj.has("url")) {
				// 최하단 JSONObject에 대하여 URL을 가지고 있으면, 안가지고 있는 경우도 있으므로
				tmpObj = new JSONObject();
				tmpObj.put("date_added", obj.get("date_added"));
				tmpObj.put("name", obj.get("name"));
				tmpObj.put("url", obj.get("url"));

				out.add(tmpObj);		// 재귀적으로 수행하면서 정보들을 추가한다.
			}
		}
	}

}