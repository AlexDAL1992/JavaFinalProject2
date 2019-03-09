import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class QuizApplication extends Application {
    private Scene welcomeScene, quizScene;
    private final TextField selectField = new TextField();
    private final TextArea quizQuestionArea = new TextArea();
    private final RadioButton choiceA = new RadioButton();
    private final RadioButton choiceB = new RadioButton();
    private final RadioButton choiceC = new RadioButton();
    private final ToggleGroup choice = new ToggleGroup();
    private final Button nextButton = new Button("NEXT");
    private final Text result = new Text("This is the result of user's answer.");
    private final Text quizProgress = new Text();
    private final String questionsFilePath = "questions.txt";
    private final ArrayList<Question> fullQuestionList = new ArrayList<>();
    private final ArrayList<Question> selectedQuestionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int countCorrect = 0;
    private int flag;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Read questions from text file
        try{
            readQuestions();
        }catch(IOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Problems opening file");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        //Welcome scene where user chooses number of quiz questions
        BorderPane welcomePanel = new BorderPane();

        GridPane welcomeGrid = new GridPane();
        welcomeGrid.setAlignment(Pos.CENTER);
        welcomeGrid.setHgap(10);
        welcomeGrid.setVgap(10);
        welcomeGrid.setPadding(new Insets(25, 25, 25, 25));

        Text welcomeText = new Text("Welcome to the Quiz!");
        Text selectText = new Text("Select the number of questions up to " + fullQuestionList.size() + " to start the game.");
        Button selectButton = new Button("START");

        welcomeGrid.add(welcomeText, 0, 0, 2, 1);
        welcomeGrid.add(selectText, 0, 1, 2, 1);
        welcomeGrid.add(selectField, 0, 2);
        welcomeGrid.add(selectButton, 1, 2);

        welcomePanel.setCenter(welcomeGrid);
        welcomeScene = new Scene(welcomePanel, 500, 500);


        //Quiz scene where user plays the quiz game
        BorderPane quizPanel = new BorderPane();
        
        quizQuestionArea.setEditable(false);
        quizQuestionArea.setFont(Font.font("Verdana", FontWeight.NORMAL, 18));
        quizQuestionArea.setWrapText(true);

        GridPane quizGrid = new GridPane();
        quizGrid.setAlignment(Pos.CENTER);
        quizGrid.setHgap(10);
        quizGrid.setVgap(10);
        quizGrid.setPadding(new Insets(5, 5, 5, 5));

        choiceA.setToggleGroup(choice);
        choiceB.setToggleGroup(choice);
        choiceC.setToggleGroup(choice);
        quizGrid.add(choiceA, 0, 0, 2, 1);
        quizGrid.add(choiceB, 0, 1, 2, 1);
        quizGrid.add(choiceC, 0, 2, 2, 1);
        quizGrid.add(nextButton, 1, 3, 1, 1);
        quizGrid.add(result, 0, 4, 2, 1);

        quizPanel.setTop(quizQuestionArea);
        quizPanel.setCenter(quizGrid);
        quizPanel.setBottom(quizProgress);
        quizScene = new Scene(quizPanel, 500, 500);

        //Set the welcome scene at first
        primaryStage.setTitle("Welcome to the game!");
        primaryStage.setScene(welcomeScene);
        primaryStage.show();


        //When pressing START button, questions are collected and quiz scene is displayed
        selectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                selectQuestions(event);
                primaryStage.setTitle("Let's play the quiz game!");
                primaryStage.setScene(quizScene);
                primaryStage.show();
                displayQuestion(event);
            }
        });
        
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {
        		if(currentQuestionIndex == selectedQuestionList.size()+1){
        	        selectedQuestionList.clear();
        	        nextButton.setText("NEXT");
        			currentQuestionIndex = 0;
        			countCorrect = 0;
        			selectField.setText(null);
        			choiceA.setDisable(false);
            		choiceB.setDisable(false);
            		choiceC.setDisable(false);
            		choice.selectToggle(null);
        			
        			primaryStage.setTitle("Welcome to the game!");
        	        primaryStage.setScene(welcomeScene);
        	        primaryStage.show();
        		}else
        			nextQuestion(event);
        		
        	}
        });
        
        
        //When clicking on answer radio button, the program checks if it's correct and gives notification
        choice.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            	Question currentQuestion = selectedQuestionList.get(currentQuestionIndex);
                RadioButton userChoice = (RadioButton)choice.getSelectedToggle();
                boolean isCorrect = userChoice.getText().substring(3).equals(currentQuestion.getAnswer());
                
                if(flag > 0){
                	if(isCorrect){
                		choiceA.setDisable(true);
                		choiceB.setDisable(true);
                		choiceC.setDisable(true);
                		nextButton.setDisable(false);
                		result.setText("Correct!");
                		countCorrect++;
                		currentQuestionIndex++;
                		
	                }else{
	                	result.setText("Wrong!");
	            		flag--;
	                }
                }
                else{
                	choiceA.setDisable(true);
            		choiceB.setDisable(true);
            		choiceC.setDisable(true);
            		nextButton.setDisable(false);
            		currentQuestionIndex++;
            		if(isCorrect){
            			result.setText("Correct!");
            			countCorrect++;
            		}
                }
            }
            
        });
        
        
    }

    public void readQuestions() throws IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource(questionsFilePath);

        try(InputStream in = url.openStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(in))){
            String line = input.readLine();
            while(line != null){
                if(line.equals("question")){
                    String question = input.readLine();
                    String choiceA = input.readLine();
                    String choiceB = input.readLine();
                    String choiceC = input.readLine();
                    String answer = input.readLine();
                    fullQuestionList.add(new Question(question, choiceA, choiceB, choiceC, answer));
                }
                line = input.readLine();
            }
        }
    }

    private void selectQuestions(ActionEvent event){
        int numberOfQuestions = Integer.parseInt(selectField.getText());
        if(numberOfQuestions > fullQuestionList.size())
            numberOfQuestions = fullQuestionList.size();
        else if(numberOfQuestions < 1)
            numberOfQuestions = 1;

        Random random = new Random();
        for(int i = 0; i < numberOfQuestions; i++){
            int index = random.nextInt(fullQuestionList.size());
            Question randomQuestion = fullQuestionList.get(index);
            selectedQuestionList.add(randomQuestion);
            fullQuestionList.remove(randomQuestion);
        }
        nextButton.setDisable(true);
        quizProgress.setText("You have got " + countCorrect + "/" + selectedQuestionList.size() + " correct answer(s).");
    }
    
    private void displayQuestion(ActionEvent event){
    	Question currentQuestion = selectedQuestionList.get(currentQuestionIndex);
    	quizQuestionArea.setText("" + (currentQuestionIndex+1) + ". " + currentQuestion.getQuestion());
    	choiceA.setText("" + "A. " + currentQuestion.getChoiceA());
    	choiceB.setText("" + "B. " + currentQuestion.getChoiceB());
    	choiceC.setText("" + "C. " + currentQuestion.getChoiceC());
    	result.setText(null);
    	flag = 1;
    }
    
    private void nextQuestion(ActionEvent event){
    	if(currentQuestionIndex == selectedQuestionList.size()){
    		quizQuestionArea.setText("You have finished the quiz!\nYou have got " + countCorrect + "/" + selectedQuestionList.size() + " correct answer(s).");
    		choiceA.setText(null);
        	choiceB.setText(null);
        	choiceC.setText(null);
        	nextButton.setText("PLAY AGAIN");
        	choiceA.setDisable(true);
        	choiceB.setDisable(true);
        	choiceC.setDisable(true);
        	currentQuestionIndex++;
    	}else{
    		displayQuestion(event);
    		choiceA.setDisable(false);
    		choiceB.setDisable(false);
    		choiceC.setDisable(false);
    		nextButton.setDisable(true);
    	}
    	choice.selectToggle(null);
    	result.setText(null);
    	quizProgress.setText("You have got " + countCorrect + "/" + selectedQuestionList.size() + " correct answer(s).");
    }
    
    public static void main(String[] args){
        launch(args);
    }
}