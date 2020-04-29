package ml.assasans.naurokhack.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SendAnswerRequest {
	@SerializedName("session_id")
	private Integer sessionId;

	@SerializedName("question_id")
	private String questionId;

	@SerializedName("answer")
	private List<String> answers;

	@SerializedName("point")
	private String point;

	@SerializedName("type")
	private String type;

	public SendAnswerRequest(Integer sessionId, Integer questionId, List<String> answers, Integer point, TestQuestion.QuestionType type) {
		this.sessionId = sessionId;
		this.questionId = String.valueOf(questionId);

		this.answers = answers;
		this.point = String.valueOf(point);

		this.type = type.getType();
	}
}
