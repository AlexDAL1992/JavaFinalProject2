public class Question {

    private String question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String answer;

    public Question(String question, String choiceA, String choiceB, String choiceC, String answer){
        this.question = question;
        this.choiceA = choiceA;
        this.choiceB = choiceB;
        this.choiceC = choiceC;
        this.answer = answer;
    }

    public String getQuestion() { return this.question; }
    public String getChoiceA() { return this.choiceA; }
    public String getChoiceB() { return this.choiceB; }
    public String getChoiceC() { return this.choiceC; }
    public String getAnswer() { return this.answer; }

    public boolean isCorrect(String answer){
        return this.answer.equalsIgnoreCase(answer);
    }

    public String toString(){
        return this.question + "\n"
			    + "\tA. " + this.choiceA + "\n"
			    + "\tB. " + this.choiceB + "\n"
			    + "\tC. " + this.choiceC + "\n";
    }
}
