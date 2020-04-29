package ml.assasans.naurokhack.async;

import ml.assasans.naurokhack.model.TestInfo;
import ml.assasans.naurokhack.web.WebAPI;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.*;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class GetTestInfoTask extends AsyncTask<Integer, Void, TestInfo> {
	private WebAPI api;
	private WeakReference<Context> context;

	private Callback callback;

	public GetTestInfoTask(WebAPI api, Context context, Callback callback) {
		this.api = api;
		this.context = new WeakReference<>(context);

		this.callback = callback;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected TestInfo doInBackground(Integer ...args) {
		try {
			JsonObject json = api.getTestJson(args[0]);

			return new TestInfo(json);
		} catch(IOException e) {
			e.printStackTrace();
		} catch(RuntimeException e) {
			e.printStackTrace();

			return null;
		}

		return null;
	}

	protected void onPostExecute(TestInfo result) {
		System.out.println(result);

		callback.done(result);
	}

	public interface Callback {
		void done(TestInfo result);
	}
}