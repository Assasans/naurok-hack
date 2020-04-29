package ml.assasans.naurokhack.adapter;

import ml.assasans.naurokhack.R;
import ml.assasans.naurokhack.model.BruteTestInfo;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BruteResultListAdapter extends RecyclerView.Adapter<BruteResultListAdapter.ViewHolder> {
	private Context context;

	private List<BruteTestInfo> data;

	private LayoutInflater inflater;
	private ItemClickListener clickListener;

	public BruteResultListAdapter(Context context, List<BruteTestInfo> data) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);

		this.data = data;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.brute_quiz_list_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		BruteTestInfo info = data.get(position);

		if(info.testInfo.settings.name == null) {
			holder.text_title.setText(context.getString(R.string.text_null));
		} else {
			holder.text_title.setText(info.testInfo.settings.name);
		}

		if(info.code == null) {
			holder.text_quiz_code.setText(context.getString(R.string.text_null));
		} else {
			Spannable spannableCode = new SpannableStringBuilder()
					.append(context.getString(R.string.text_quiz_code))
					.append(" ")
					.append(String.valueOf(info.code), new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.text_quiz_code.setText(spannableCode);
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView text_title;
		TextView text_quiz_code;

		ViewHolder(View itemView) {
			super(itemView);

			text_title = itemView.findViewById(R.id.text_quiz_title);
			text_quiz_code = itemView.findViewById(R.id.text_quiz_code);

			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			if(clickListener != null) {
				clickListener.onItemClick(view, getAdapterPosition());
			}
		}
	}

	public BruteTestInfo getItem(int id) {
		return data.get(id);
	}

	public void setClickListener(ItemClickListener itemClickListener) {
		this.clickListener = itemClickListener;
	}

	public interface ItemClickListener {
		void onItemClick(View view, int position);
	}
}
