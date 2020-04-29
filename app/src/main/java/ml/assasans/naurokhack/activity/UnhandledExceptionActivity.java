package ml.assasans.naurokhack.activity;

import ml.assasans.naurokhack.R;

import androidx.core.content.ContextCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Process;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.*;

public class UnhandledExceptionActivity extends BaseActivity {
	private static final String TAG = "UnhandledExceptionActivity";

	private TextView text_thread_id;
	private TextView text_thread_name;

	private TextView text_throwable_message;
	private TextView text_stacktrace;

	private ScrollView scroll_stacktrace;

	private Button button_close_app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.title_unhandled_exception));

		text_thread_id = findViewById(R.id.text_thread_id);
		text_thread_name = findViewById(R.id.text_thread_name);

		text_throwable_message = findViewById(R.id.text_throwable_message);
		text_stacktrace = findViewById(R.id.text_stacktrace);

		scroll_stacktrace = findViewById(R.id.scroll_stacktrace);

		button_close_app = findViewById(R.id.button_close_application);

		button_close_app.setOnClickListener((view) -> {
			Process.killProcess(Process.myPid());
		});

		Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			long threadId = bundle.getLong("thread_id");
			String threadName = bundle.getString("thread_name");

			String message = bundle.getString("message");
			String stacktrace = bundle.getString("stacktrace");

			Spannable spanThreadId = new SpannableStringBuilder()
					.append(getString(R.string.text_thread_id))
					.append(" ")
					.append(String.valueOf(threadId), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			text_thread_id.setText(spanThreadId);

			Spannable spanThreadName = new SpannableStringBuilder()
					.append(getString(R.string.text_thread_name))
					.append(" ")
					.append(threadName != null ? threadName : "[ null ]", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			text_thread_name.setText(spanThreadName);

			Spannable spanThrowableMessage = new SpannableStringBuilder()
					.append(getString(R.string.text_throwable_message))
					.append(" ")
					.append(message != null ? message : "[ null ]", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			text_throwable_message.setText(spanThrowableMessage);

			Spannable spanStacktrace = new SpannableStringBuilder()
					.append(stacktrace != null ? stacktrace : "[ null ]", new ForegroundColorSpan(ContextCompat.getColor(this, R.color.error)), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			text_stacktrace.setText(spanStacktrace);
		}
	}

	@Override
	protected boolean showBackButton() {
		return false;
	}

	@Override
	protected boolean showMoreAction() {
		return false;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_unhandled_exception;
	}
}
