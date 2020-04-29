package ml.assasans.naurokhack.model;

import com.google.gson.*;

import java.util.*;

public class TestInfo {
	public TestSession session;
	public TestSettings settings;
	public List<TestQuestion> questions;

	public TestInfo(JsonObject json) throws RuntimeException {
		questions = new ArrayList<>();

		if(!json.has("questions") || !json.has("settings")) {
			throw new RuntimeException("Test not found!");
		}

		session = new TestSession(json.get("session").getAsJsonObject());
		settings = new TestSettings(json.get("settings").getAsJsonObject());

		for(JsonElement element : json.get("questions").getAsJsonArray()) {
			TestQuestion question = new TestQuestion(element.getAsJsonObject());
			questions.add(question);
		}
	}
}
