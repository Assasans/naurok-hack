package ml.assasans.naurokhack.activity;

import ml.assasans.naurokhack.adapter.QuestionOptionListAdapter;
import ml.assasans.naurokhack.async.GetTestInfoTask;
import ml.assasans.naurokhack.model.QuestionOption;
import ml.assasans.naurokhack.model.TestQuestion;
import ml.assasans.naurokhack.web.WebAPI;
import ml.assasans.naurokhack.R;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnswersActivity extends BaseActivity {
	private AnswersActivity activity;

	private WebAPI api;

	private Spinner spinner;

	private RecyclerView recycler_options;

	private QuestionOptionListAdapter adapter;
	private List<QuestionOption> options;

	private List<TestQuestion> questions;
	private List<Spanned> questionsStrings;

	public AnswersActivity() {
		activity = this;

		api = new WebAPI();

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); //FIXME Network on main thread

		options = new ArrayList<>();

		questions = new ArrayList<>();
		questionsStrings = new ArrayList<>();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getString(R.string.title_answers_activity));

		spinner = findViewById(R.id.spinner_questions);

		recycler_options = findViewById(R.id.recycler_question_options);

		adapter = new QuestionOptionListAdapter(activity, options);
		adapter.setClickListener((view, position) -> {

		});

		recycler_options.setLayoutManager(new LinearLayoutManager(this));
		recycler_options.setAdapter(adapter);

		Bundle bundle = getIntent().getExtras();

		if(bundle == null) {
			finish();
			return;
		}

		AtomicBoolean extractSessionId = new AtomicBoolean(false);

		int code = bundle.getInt("code");
		Integer sessionId = bundle.getInt("session_id");
		String name = bundle.getString("name");

		System.out.printf("[OUT]: %d -> %s (%d)\n", code, name, sessionId);

		if(sessionId == 0) {
			extractSessionId.set(true);

			try {
				sessionId = new WebAPI().generateSession(code, name);
				if(sessionId == null) {
					Toast.makeText(this, getString(R.string.text_csrf_error), Toast.LENGTH_LONG).show();
					return;
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		new GetTestInfoTask(api, this, (result) -> {
			if(result == null) {
				Toast.makeText(getApplicationContext(), getString(R.string.text_quiz_not_found), Toast.LENGTH_LONG).show();
				finish();
				return;
			}

			if(extractSessionId.get()) {
				Toast.makeText(this, getString(R.string.text_extract_session_author), Toast.LENGTH_LONG).show();
			}

			questions = result.questions;

			for(TestQuestion question : questions) {
				questionsStrings.add(Html.fromHtml(question.content)); //TODO HTML render (custom adapter)
			}

			ArrayAdapter<Spanned> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, questionsStrings);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
					System.out.println("Selected: " + selectedItemPosition);

					TestQuestion question = questions.get(selectedItemPosition);

					options.clear();
					options.addAll(question.options);
					activity.adapter.notifyDataSetChanged();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					System.out.println("Nothing selected!");
				}
			});
		}).execute(sessionId);
	}

	@Override
	@LayoutRes
	protected int getLayoutId() {
		return R.layout.activity_answers;
	}
}
