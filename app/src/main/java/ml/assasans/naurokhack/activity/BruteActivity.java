package ml.assasans.naurokhack.activity;

import ml.assasans.naurokhack.R;
import ml.assasans.naurokhack.adapter.BruteResultListAdapter;
import ml.assasans.naurokhack.async.GetTestInfoTask;
import ml.assasans.naurokhack.model.BruteTestInfo;
import ml.assasans.naurokhack.web.WebAPI;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BruteActivity extends BaseActivity {
	private static final String TAG = "BruteActivity";

	private EditText edit_code_start;
	private EditText edit_code_end;

	private EditText edit_name;

	private Button button_insert_shy;

	private Button button_start;
	private Button button_stop;

	private TextView text_progress;
	private TextView text_current_progress;

	private RecyclerView recycler_brute_results;

	private boolean running;

	private AtomicInteger testedCodes;

	private List<BruteTestInfo> validCodes;

	private BruteResultListAdapter adapter;

	public BruteActivity() {
		running = false;

		testedCodes = new AtomicInteger(0);

		validCodes = new ArrayList<>();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); //FIXME Network on main thread
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.title_brute_activity));

		edit_code_start = findViewById(R.id.edit_code_start);
		edit_code_end = findViewById(R.id.edit_code_end);

		edit_name = findViewById(R.id.edit_name);

		button_insert_shy = findViewById(R.id.button_insert_shy);

		button_start = findViewById(R.id.button_start);
		button_stop = findViewById(R.id.button_stop);

		text_progress = findViewById(R.id.text_progress);
		text_current_progress = findViewById(R.id.text_current_progress);

		recycler_brute_results = findViewById(R.id.recycler_brute_results);

		adapter = new BruteResultListAdapter(activity, validCodes);
		adapter.setClickListener((view, position) -> {
			BruteTestInfo testInfo = adapter.getItem(position);

			Intent intent = new Intent(this, AnswersActivity.class);
			Bundle bundle = new Bundle();

			bundle.putInt("code", testInfo.code);
			bundle.putString("name", edit_name.getText().toString());

			bundle.putInt("session_id", testInfo.testInfo.session.id);

			intent.putExtras(bundle);
			startActivity(intent);
		});

		recycler_brute_results.setLayoutManager(new LinearLayoutManager(this));
		recycler_brute_results.setAdapter(adapter);

		button_insert_shy.setOnClickListener((view) -> {
			edit_name.append("\u00ad"); //SHY
		});

		button_start.setOnClickListener((view) -> {
			running = true;

			button_start.setEnabled(false);
			button_stop.setEnabled(true);

			validCodes.clear();
			adapter.notifyDataSetChanged();

			testedCodes = new AtomicInteger(0);

			AtomicInteger codeStart = new AtomicInteger(Integer.parseInt(edit_code_start.getText().toString()));
			AtomicInteger codeEnd = new AtomicInteger(Integer.parseInt(edit_code_end.getText().toString()));

			String name = edit_name.getText().toString();

			System.out.printf("[IN]: %d / %s (%s)\n", codeStart.get(), codeEnd.get(), name);

			WebAPI api = new WebAPI();

			new Thread(() -> {
				for(AtomicInteger code = new AtomicInteger(codeStart.get()); code.get() <= codeEnd.get(); code.getAndIncrement()) {
					if(!running) {
						break;
					}

					Log.i(TAG, String.format("Testing code: %d", code.get()));

					runOnUiThread(() -> {
						updateCurrentProgress(code.get());
					});

					try {
						Integer sessionId = api.generateSession(code.get(), name);
						testedCodes.getAndIncrement();

						if(sessionId == null) {
							runOnUiThread(() -> {
								updateTotalProgress();
							});
							continue;
						}

						GetTestInfoTask infoTask = new GetTestInfoTask(api, this, (result) -> {
							if(result == null) {
								return;
							}

							BruteTestInfo testInfo = new BruteTestInfo(result.settings.gamecode, result);
							validCodes.add(testInfo);
							adapter.notifyDataSetChanged();

							runOnUiThread(() -> {
								updateTotalProgress();
							});
						});

						infoTask.execute(sessionId);
					} catch(IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		});

		button_stop.setOnClickListener((view) -> {
			running = false;

			button_start.setEnabled(true);
			button_stop.setEnabled(false);
		});
	}

	private void updateTotalProgress() {
		Spannable spannableProgress = new SpannableStringBuilder()
				.append(getString(R.string.text_progress))
				.append(" ")
				.append(String.valueOf(validCodes.size()), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
				.append(" / ")
				.append(String.valueOf(testedCodes.get()), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		text_progress.setText(spannableProgress);
	}

	private void updateCurrentProgress(Integer code) {
		Spannable spannableCurrentProgress = new SpannableStringBuilder()
				.append(getString(R.string.text_current_brute_progress))
				.append(" ")
				.append(String.valueOf(code), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		text_current_progress.setText(spannableCurrentProgress);
	}

	@Override
	@LayoutRes
	protected int getLayoutId() {
		return R.layout.activity_brute;
	}
}
