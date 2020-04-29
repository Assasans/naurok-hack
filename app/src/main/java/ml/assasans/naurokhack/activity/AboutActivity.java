package ml.assasans.naurokhack.activity;

import ml.assasans.naurokhack.R;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.*;

public class AboutActivity extends AppCompatActivity {
	private TextView text_app_author;
	private TextView text_exploit_author;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		text_app_author = findViewById(R.id.text_app_author);
		text_exploit_author = findViewById(R.id.text_exploit_author);

		Spannable spannableAppAuthor = new SpannableStringBuilder()
				.append(getString(R.string.text_app_author))
				.append(" ")
				.append("Assasans", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		text_app_author.setText(spannableAppAuthor);

		Spannable spannableExploitAuthor = new SpannableStringBuilder()
				.append(getString(R.string.text_exploit_author))
				.append(" ")
				.append("Assasans", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		text_exploit_author.setText(spannableExploitAuthor);

		new Handler().postDelayed(() -> {
			finish();
		}, 10000);
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
	}
}
