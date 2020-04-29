package ml.assasans.naurokhack.model;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

public class TestSettings {
	@Nullable
	public Integer id;
	@Nullable
	public Integer gamecode;

	@Nullable
	public String name;

	public TestSettings(JsonObject json) {
		if(!json.has("id") || json.get("id").isJsonNull()) {
			id = null;
		} else {
			id = json.get("id").getAsInt();
		}

		if(!json.has("gamecode") || json.get("gamecode").isJsonNull()) {
			gamecode = null;
		} else {
			gamecode = json.get("gamecode").getAsInt();
		}

		if(!json.has("name") || json.get("name").isJsonNull()) {
			name = null;
		} else {
			name = json.get("name").getAsString();
		}
	}
}
