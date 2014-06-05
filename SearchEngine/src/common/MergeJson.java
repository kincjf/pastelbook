package common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class MergeJson<T1> {
	//객체 + 숫자에 사용
	//다른 객체를 넣고 싶으면 잘 튜닝해서 쓰세요..
	private Gson gson1;
	private JsonElement jsonElement;
	
	public void setMergeJson(T1 input1, Number input2, String propertyName) {
		this.gson1 = new Gson();
		this.jsonElement = this.gson1.toJsonTree(input1);
		this.jsonElement.getAsJsonObject().addProperty(propertyName, input2);
    }
	
    public JsonElement getMergeJson() {
        return this.jsonElement;
    }
    
    public String printMergeJson() {
        String json = null;
        json = gson1.toJson(this.jsonElement);
    	
        return json;
    }
}
