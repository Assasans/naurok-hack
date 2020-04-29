package ml.assasans.naurokhack.web;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class WebClient {
	public OkHttpClient client;

	public WebClient() {
		client = new OkHttpClient.Builder()
				.build();
	}

	public Response get(String url) throws IOException {
		return get(url, new Headers.Builder().build());
	}

	public Response get(String url, Headers headers) throws IOException {
		Request request = new Request.Builder()
				.url(url)
				.headers(headers)
				.build();

		return client.newCall(request).execute();
	}
}
