package ml.assasans.naurokhack.activity;

import ml.assasans.naurokhack.R;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;

public abstract class BaseActivity extends AppCompatActivity {
	protected BaseActivity activity;

	protected Toolbar toolbar;

	protected ConstraintLayout layout;
	protected ConstraintLayout layout_content;

	public BaseActivity() {
		activity = this;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());

		toolbar = findViewById(R.id.toolbar);

		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		if(actionBar != null) {
			if(showBackButton()) {
				actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setDisplayShowHomeEnabled(true);

				if(toolbar.getNavigationIcon() != null) {
					Drawable icon = toolbar.getNavigationIcon();
					if(icon != null) {
						icon.mutate();
						icon.setColorFilter(ContextCompat.getColor(this, android.R.color.background_light), PorterDuff.Mode.SRC_ATOP);

						toolbar.setNavigationIcon(icon);
					}
				}
			} else {
				actionBar.setDisplayHomeAsUpEnabled(false);
				actionBar.setDisplayShowHomeEnabled(false);
			}
		}
	}

	@Override
	protected void onPostCreate(@Nullable Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.toolbar, menu);

		MenuItem itemMore = menu.findItem(R.id.action_more);
		Drawable iconMore = itemMore.getIcon();
		if(iconMore != null) {
			iconMore.mutate();
			iconMore.setColorFilter(ContextCompat.getColor(this, android.R.color.background_light), PorterDuff.Mode.SRC_ATOP);

			itemMore.setIcon(iconMore);
		}

		if(showMoreAction()) {
			itemMore.setVisible(true);
		} else {
			itemMore.setVisible(false);
		}

		SubMenu subMenu = itemMore.getSubMenu();
		for(int index = 0; index < subMenu.size(); index++) {
			MenuItem item = subMenu.getItem(index);
			Drawable icon = item.getIcon();
			if(icon != null) {
				Drawable wrapped = DrawableCompat.wrap(icon.mutate());

				DrawableCompat.setTint(wrapped, ContextCompat.getColor(this, R.color.primary));

				item.setIcon(icon);
			}
		}

		MenuItem aboutItem = subMenu.findItem(R.id.action_about);

		aboutItem.setVisible(showAboutAction());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			case R.id.action_about:
				Intent intentAbout = new Intent(this, AboutActivity.class);
				startActivity(intentAbout);

				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
		//return true;
	}

	@SuppressLint("InflateParams")
	@Override
	public void setContentView(int resId) {
		layout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null, false);

		layout_content = layout.findViewById(R.id.layout_content);

		getLayoutInflater().inflate(resId, layout_content, true);

		super.setContentView(layout);
	}

	protected void setTitle(String title) {
		toolbar.setTitle(title);
	}

	@LayoutRes
	protected abstract int getLayoutId();

	protected boolean showBackButton() {
		return true;
	}

	protected boolean showMoreAction() {
		return true;
	}

	protected boolean showAboutAction() {
		return true;
	}
}
