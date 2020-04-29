package ml.assasans.naurokhack.model;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

public class TestSession {
	@Nullable
	public Integer id;

	@Nullable
	public String uuid;

	public TestSession(JsonObject json) {
		if(!json.has("id") || json.get("id").isJsonNull()) {
			id = null;
		} else {
			id = json.get("id").getAsInt();
		}

		if(!json.has("uuid") || json.get("uuid").isJsonNull()) {
			uuid = null;
		} else {
			uuid = json.get("uuid").getAsString();
		}
	}
}
