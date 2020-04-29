package ml.assasans.naurokhack.activity;

import ml.assasans.naurokhack.R;
import ml.assasans.naurokhack.async.GetTestInfoTask;
import ml.assasans.naurokhack.model.QuestionOption;
import ml.assasans.naurokhack.model.SendAnswerRequest;
import ml.assasans.naurokhack.model.TestQuestion;
import ml.assasans.naurokhack.web.WebAPI;

import androidx.annotation.LayoutRes;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.*;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BotActivity extends BaseActivity {
	private static final String TAG = "BotActivity";

	private EditText edit_code;
	private EditText edit_name;
	private EditText edit_score;

	private EditText edit_repeat_count;

	private CheckBox check_legit_mode;

	private Button button_start;
	private Button button_insert_shy;

	private TextView text_progress;
	private TextView text_quiz_progress;

	public BotActivity() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); //FIXME Network on main thread
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.title_bot_activity));

		edit_code = findViewById(R.id.edit_code);
		edit_name = findViewById(R.id.edit_name);
		edit_score = findViewById(R.id.edit_score);

		edit_repeat_count = findViewById(R.id.edit_repeat_count);

		check_legit_mode = findViewById(R.id.check_legit_mode);

		button_start = findViewById(R.id.button_start);
		button_insert_shy = findViewById(R.id.button_insert_shy);

		text_progress = findViewById(R.id.text_progress);
		text_quiz_progress = findViewById(R.id.text_quiz_progress);

		button_insert_shy.setOnClickListener((view) -> {
			edit_name.append("\u00ad"); //SHY
		});

		button_start.setOnClickListener((view) -> {
			AtomicInteger code = new AtomicInteger(0);
			AtomicInteger repeatCount = new AtomicInteger(0);
			AtomicInteger score = new AtomicInteger(0);

			AtomicBoolean legitMode = new AtomicBoolean(check_legit_mode.isChecked());

			String name = edit_name.getText().toString();

			{
				String _code = edit_code.getText().toString();
				String _repeatCount = edit_repeat_count.getText().toString();
				String _score = edit_score.getText().toString();

				if(_code.length() > 0) code.set(Integer.parseInt(_code));
				if(_repeatCount.length() > 0) repeatCount.set(Integer.parseInt(_repeatCount));
				if(_score.length() > 0) score.set(Integer.parseInt(_score));
			}

			System.out.printf("[IN]: %d -> %s (%d)\n", code.get(), name, repeatCount.get());

			Gson gson = new Gson();
			WebAPI api = new WebAPI();

			try {
				Integer sessionId = api.generateSession(code.get(), name);
				if(sessionId == null) {
					Toast.makeText(this, getString(R.string.text_csrf_error), Toast.LENGTH_LONG).show();
					return;
				}

				GetTestInfoTask infoTask = new GetTestInfoTask(api, this, (result) -> {
					if(result == null) {
						Toast.makeText(getApplicationContext(), getString(R.string.text_quiz_not_found), Toast.LENGTH_LONG).show();
						finish();
						return;
					}

					new Thread(() -> {
						for(AtomicInteger index = new AtomicInteger(0); index.get() < repeatCount.get(); index.getAndIncrement()) {
							runOnUiThread(() -> {
								Spannable spannableProgress = new SpannableStringBuilder()
										.append(getString(R.string.text_progress))
										.append(" ")
										.append(String.valueOf(index.get() + 1), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
										.append(" / ")
										.append(String.valueOf(repeatCount), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
								text_progress.setText(spannableProgress);
							});

							Integer answerSessionId = sessionId;
							if(index.get() > 0) {
								try {
									answerSessionId = api.generateSession(code.get(), name);
								} catch(IOException e) {
									e.printStackTrace();
								}
							}
							if(answerSessionId == null) {
								Toast.makeText(this, getString(R.string.text_csrf_error), Toast.LENGTH_LONG).show();
								return;
							}

							AtomicInteger questionIndex = new AtomicInteger(0);
							for(TestQuestion question : result.questions) {
								questionIndex.getAndIncrement();

								runOnUiThread(() -> {
									Spannable spannableQuizProgress = new SpannableStringBuilder()
											.append(getString(R.string.text_quiz_progress))
											.append(" ")
											.append(String.valueOf(questionIndex.get()), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
											.append(" / ")
											.append(String.valueOf(result.questions.size()), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
									text_quiz_progress.setText(spannableQuizProgress);
								});

								List<String> correctOptions = new ArrayList<>();
								for(QuestionOption option : question.options) {
									if(option.correct) {
										correctOptions.add(String.valueOf(option.id));
									}
								}

								Integer points = legitMode.get() ? question.point : score.get();

								SendAnswerRequest answerRequest = new SendAnswerRequest(
										answerSessionId,
										question.id,
										correctOptions,
										points,
										question.type
								);

								Request request = new Request.Builder()
										.url(new StringBuilder()
												.append(WebAPI.HOST)
												.append("api2/test/responses/answer")
												.toString()
										)
										.put(RequestBody.create(
												gson.toJson(answerRequest),
												MediaType.parse("application/json; charset=utf-8")
										))
										.build();

								try {
									Response response = api.getClient().client.newCall(request).execute();
									System.out.println(response.body().string());
								} catch(IOException e) {
									e.printStackTrace();
								}
							}

							Request request = new Request.Builder()
									.url(new StringBuilder()
											.append(WebAPI.HOST)
											.append("api2/test/sessions/end/")
											.append(answerSessionId)
											.toString()
									)
									.put(RequestBody.create("", null))
									.build();

							try {
								Response response = api.getClient().client.newCall(request).execute();
								System.out.println(response.body().string());
							} catch(IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				});

				infoTask.execute(sessionId);
			} catch(IOException e) {
				e.printStackTrace();
			}
		});

		check_legit_mode.setOnCheckedChangeListener((button, checked) -> {
			if(checked) {
				edit_score.setEnabled(false);
			} else {
				edit_score.setEnabled(true);
			}
		});
	}

	@Override
	@LayoutRes
	protected int getLayoutId() {
		return R.layout.activity_bot;
	}
}
