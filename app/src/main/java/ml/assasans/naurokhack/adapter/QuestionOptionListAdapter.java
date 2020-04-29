package ml.assasans.naurokhack.adapter;

import ml.assasans.naurokhack.R;
import ml.assasans.naurokhack.model.QuestionOption;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;
import android.view.*;

import java.util.List;

public class QuestionOptionListAdapter extends RecyclerView.Adapter<QuestionOptionListAdapter.ViewHolder> {
	private Context context;

	private List<QuestionOption> data;

	private LayoutInflater inflater;
	private ItemClickListener clickListener;

	public QuestionOptionListAdapter(Context context, List<QuestionOption> data) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.data = data;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.question_option_list_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		QuestionOption option = data.get(position);

		holder.text_content.setText(Html.fromHtml(option.content));

		if(option.correct) {
			holder.text_is_correct.setText(context.getString(R.string.answer_option_correct));
			holder.text_is_correct.setTextColor(ContextCompat.getColor(context, R.color.green600));
		} else {
			holder.text_is_correct.setText(context.getString(R.string.answer_option_incorrect));
			holder.text_is_correct.setTextColor(ContextCompat.getColor(context, R.color.red600));
		}
	}

	@Override
	public int getItemCount() {
		return data.size();
	}

	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		TextView text_content;
		TextView text_is_correct;

		ViewHolder(View itemView) {
			super(itemView);

			text_content = itemView.findViewById(R.id.text_option_content);
			text_is_correct = itemView.findViewById(R.id.text_option_correct);

			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			if(clickListener != null) {
				clickListener.onItemClick(view, getAdapterPosition());
			}
		}
	}

	public QuestionOption getItem(int id) {
		return data.get(id);
	}

	public void setClickListener(ItemClickListener itemClickListener) {
		this.clickListener = itemClickListener;
	}

	public interface ItemClickListener {
		void onItemClick(View view, int position);
	}
}
