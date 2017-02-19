package com.city.safe.data.investigation;

/**
 * Created by user on 16-12-17.
 */

public class Answer {
    //答案id
    private String answerId;
    //答案主体
    private String answer_content;
    //答案是否被解答
    private int ans_state;

    public int getAns_state() {
        return ans_state;
    }

    public void setAns_state(int ans_state) {
        this.ans_state = ans_state;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getAnswer_content() {
        return answer_content;
    }

    public void setAnswer_content(String answer_content) {
        this.answer_content = answer_content;
    }
}