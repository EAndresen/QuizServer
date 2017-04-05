package code;

/**
 * Created by ${Eric} on 2017-04-03.
 */
public class Questions {
    private int answerId;
    private String returnQuestion;
    private String returnAnswer;

    //Get a question by the random number prompt when calling method.
    public String getQuestion(int number) {
        //Set answer id to same as question asked for.
        answerId = number;

        switch (number) {
            case 1: returnQuestion = "What color is the sky?";
                break;
            case 2: returnQuestion = "What is the solution for everything";
                break;
            case 3: returnQuestion = "Is it possible to solve everything by circles and lines?";
                break;
            case 4: returnQuestion = "Who directed the Star Wars movies?";
                break;
            case 5: returnQuestion = "How many Death Stars were built?";
                break;
        }
        return returnQuestion;
    }
    //Send answer according to last question send't
    public String getAnswer() {
        switch (answerId) {
            case 1: returnAnswer = "Blue";
                break;
            case 2: returnAnswer = "42";
                break;
            case 3: returnAnswer = "Yes";
                break;
            case 4: returnAnswer = "George Lucas";
                break;
            case 5: returnAnswer = "2";
                break;
        }
        return returnAnswer;
    }
}
