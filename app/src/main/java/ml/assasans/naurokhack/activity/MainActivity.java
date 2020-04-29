package ml.assasans.naurokhack.activity;

import ml.assasans.naurokhack.R;
import ml.assasans.naurokhack.RuntimeConfig;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.*;

import androidx.annotation.LayoutRes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.minidns.dnsmessage.DnsMessage;
import org.minidns.hla.DnssecResolverApi;
import org.minidns.hla.ResolverResult;
import org.minidns.record.TXT;

import java.io.IOException;
import java.util.Set;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";

	private EditText edit_code;
	private EditText edit_name;
	private EditText edit_session;

	private CheckBox check_use_session;

	private Button button_insert_shy;

	private Button button_proceed;

	private Button button_bot_menu;
	private Button button_brute_menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		edit_code = findViewById(R.id.edit_code);
		edit_name = findViewById(R.id.edit_name);
		edit_session = findViewById(R.id.edit_session);

		check_use_session = findViewById(R.id.check_use_session);

		button_insert_shy = findViewById(R.id.button_insert_shy);

		button_proceed = findViewById(R.id.button_proceed);

		button_bot_menu = findViewById(R.id.button_bot_menu);
		button_brute_menu = findViewById(R.id.button_brute_menu);

		button_insert_shy.setOnClickListener((view) -> {
			edit_name.append("\u00ad"); //SHY
		});

		button_proceed.setOnClickListener((view) -> {
			String _code = edit_code.getText().toString();
			String _session_id = edit_session.getText().toString();

			int code = 0;
			int sessionId = 0;

			if(_code.length() > 0) code = Integer.parseInt(_code);
			if(_session_id.length() > 0) sessionId = Integer.parseInt(_session_id);

			String name = edit_name.getText().toString();

			System.out.printf("[IN]: %d -> %s (%d)\n", code, name, sessionId);

			Intent intent = new Intent(this, AnswersActivity.class);
			Bundle bundle = new Bundle();

			bundle.putInt("code", code);
			bundle.putString("name", name);

			bundle.putInt("session_id", sessionId);

			intent.putExtras(bundle);
			startActivity(intent);
		});

		button_bot_menu.setOnClickListener((view) -> {
			Intent intent = new Intent(this, BotActivity.class);
			startActivity(intent);
		});

		button_brute_menu.setOnClickListener((view) -> {
			Intent intent = new Intent(this, BruteActivity.class);
			startActivity(intent);
		});

		check_use_session.setOnCheckedChangeListener((button, checked) -> {
			if(checked) {
				edit_code.setEnabled(false);
				edit_name.setEnabled(false);

				edit_session.setEnabled(true);
			} else {
				edit_code.setEnabled(true);
				edit_name.setEnabled(true);

				edit_session.setEnabled(false);
			}
		});
	}

	@Override
	@LayoutRes
	protected int getLayoutId() {
		return R.layout.activity_main;
	}
}
