package ml.assasans.naurokhack.model;

import com.google.gson.*;

import java.util.*;

public class TestQuestion {
	public enum QuestionType { //TODO Add other types
		Unknown("unknown"),

		Quiz("quiz"),
		MultiQuiz("multiquiz");

		private String type;

		QuestionType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public static QuestionType get(String type) {
			for(QuestionType entry : values()) {
				if(entry.type.equals(type)) return entry;
			}
			return Unknown;
		}
	}

	public Integer id;
	public Integer document_id;

	public String content;
	public String image; //TODO Verify type

	public QuestionType type;

	public Integer point;
	public Integer hint;
	public Integer hint_penalty;
	public String hint_description;

	public Integer order;

	public List<QuestionOption> options;

	public Integer question_length;
	public Integer option_max_length;

	public TestQuestion(JsonObject json) {
		options = new ArrayList<>();

		id = json.get("id").getAsInt();
		document_id = json.get("document_id").getAsInt();

		content = json.get("content").getAsString();
		if(!json.get("image").isJsonNull()) image = json.get("image").getAsString();

		type = QuestionType.get(json.get("type").getAsString());

		point = json.get("point").getAsInt();
		hint = json.get("hint").getAsInt();
		hint_penalty = json.get("hint_penalty").getAsInt();
		hint_description = json.get("hint_description").getAsString();

		order = json.get("order").getAsInt();

		JsonArray optionsArray = json.get("options").getAsJsonArray();
		for(JsonElement entry : optionsArray) {
			QuestionOption option = new QuestionOption(entry.getAsJsonObject());

			options.add(option);
		}

		question_length = json.get("questionLength").getAsInt();
		option_max_length = json.get("optionMaxLength").getAsInt();
	}
}
