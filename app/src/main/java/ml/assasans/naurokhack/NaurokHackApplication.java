package ml.assasans.naurokhack;

import ml.assasans.naurokhack.activity.UnhandledExceptionActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class NaurokHackApplication extends Application {
	private static final String TAG = "DanPriApplication";

	private Thread.UncaughtExceptionHandler previousHandler;

	@Override
	public void onCreate() {
		super.onCreate();

		previousHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
			Log.e(TAG, "Unhandled exception!");
			throwable.printStackTrace();

			Intent intent = new Intent(getApplicationContext(), UnhandledExceptionActivity.class);
			Bundle bundle = new Bundle();

			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

			bundle.putLong("thread_id", thread.getId());
			bundle.putString("thread_name", thread.getName());

			bundle.putString("message", throwable.getMessage());

			StringWriter stringWriter = new StringWriter();
			throwable.printStackTrace(new PrintWriter(stringWriter));
			String stacktrace = stringWriter.toString();

			bundle.putString("stacktrace", stacktrace);

			intent.putExtras(bundle);
			getApplicationContext().startActivity(intent);

			Process.killProcess(Process.myPid());
		});
	}
}
