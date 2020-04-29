package ml.assasans.naurokhack.model;

import com.google.gson.*;

public class QuestionOption {
	public Integer id;
	public Integer question_id;

	public String content;
	public String image; //TODO Verify type

	public Boolean correct;

	public Integer order;

	public QuestionOption(JsonObject json) {
		id = json.get("id").getAsInt();
		question_id = json.get("question_id").getAsInt();

		content = json.get("value").getAsString();
		if(!json.get("image").isJsonNull()) image = json.get("image").getAsString();

		correct = json.get("correct").getAsInt() == 1;

		if(!json.get("order").isJsonNull()) order = json.get("order").getAsInt();
	}
}
