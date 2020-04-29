package ml.assasans.naurokhack.web;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.*;

public class WebAPI {
	private static final String TAG = "WebAPI";

	public static final String HOST = "https://naurok.com.ua/";

	public static final String ENDPOINT_API = "api/";
	public static final String ENDPOINT_TEST_INFO = "test/sessions/";

	private WebClient client;
	private Gson gson;

	public WebAPI() {
		client = new WebClient();
		gson = new Gson();
	}

	public WebClient getClient() {
		return client;
	}

	public JsonObject getTestJson(int session) throws IOException {
		Response response = client.get(String.format(
				Locale.getDefault(),
				"%s%s%s%d",
				HOST,
				ENDPOINT_API,
				ENDPOINT_TEST_INFO,
				session
		));

		String data = response.body().string();

		//System.out.println(data);

		return gson.fromJson(data, JsonObject.class);
	}

	@Nullable
	public Integer generateSession(Integer code, String name) throws IOException {
		Response csrfResponse = client.get(new StringBuilder()
				.append(WebAPI.HOST)
				.append("test/join")
				.toString()
		);

		Matcher cookieMatcher = Pattern.compile("(_csrf=)(.+)(;)").matcher(TextUtils.join("; ", csrfResponse.headers("set-cookie")));
		Matcher metaMatcher = Pattern.compile("(<meta name=\"csrf-token\" content=\")(.+)(\">)").matcher(csrfResponse.body().string());

		if(!cookieMatcher.find() || !metaMatcher.find()) {
			return null;
		}

		String csrfCookie = cookieMatcher.group(2);
		//System.out.printf("CSRF Cookie: %s\n", csrfCookie);

		String csrfMeta = metaMatcher.group(2);
		//System.out.printf("CSRF Meta: %s\n", csrfMeta);

		Request request = new Request.Builder()
				.url(new StringBuilder()
						.append(WebAPI.HOST)
						.append("test/join")
						.toString()
				)
				.headers(new Headers.Builder()
						.add("Cookie", new StringBuilder()
								.append("_csrf=")
								.append(csrfCookie)
								.toString()
						)
						.build()
				)
				.post(new MultipartBody.Builder()
						.setType(MultipartBody.FORM)
						.addFormDataPart("_csrf", csrfMeta)
						.addFormDataPart("JoinForm[gamecode]", String.valueOf(code))
						.addFormDataPart("JoinForm[name]", name)
						.build()
				)
				.build();

		Response response = client.client.newCall(request).execute();

		Matcher sessionMatcher = Pattern.compile("(ng-init=\"init\\(.+,)(.+)(, .+\\)\")").matcher(response.body().string());
		if(sessionMatcher.find()) {
			Integer sessionId = Integer.parseInt(sessionMatcher.group(2));
			Log.i(TAG, String.format("Session ID: %d", sessionId));
			return sessionId;
		}
		return null;
	}
}
