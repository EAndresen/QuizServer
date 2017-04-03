package code;

/**
 * Created by ${Eric} on 2017-04-03.
 */
public class Questions {
    private int answerId;
    private String returnQuestion;
    private String returnAnswer;

    public String getQuestion(int number) {
        answerId = number;

        switch (number) {
            case 1: returnQuestion = "1What color is the sky?";
                break;
            case 2: returnQuestion = "2What color is the sky?";
                break;
            case 3: returnQuestion = "3What color is the sky?";
                break;
            case 4: returnQuestion = "4What color is the sky?";
                break;
            case 5: returnQuestion = "5What color is the sky?";
                break;
        }
        return returnQuestion;
    }
    public String getAnswer() {
        switch (answerId) {
            case 1: returnAnswer = "1";
                break;
            case 2: returnAnswer = "2";
                break;
            case 3: returnAnswer = "3";
                break;
            case 4: returnAnswer = "4";
                break;
            case 5: returnAnswer = "5";
                break;
        }
        return returnAnswer;
    }
}
