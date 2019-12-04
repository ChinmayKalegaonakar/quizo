package com.stackroute.employeeservice.service;

import com.mongodb.MongoClient;
import com.stackroute.employeeservice.domain.Attempt;
import com.stackroute.employeeservice.domain.Question;
import com.stackroute.employeeservice.domain.Result;
import com.stackroute.employeeservice.exception.QuestionNotFoundException;
import com.stackroute.employeeservice.repository.QuestionRepository;
import com.stackroute.employeeservice.repository.ResultRepository;
import com.stackroute.employeeservice.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FetchQuestionsServiceImpl implements FetchQuestionsService {

    @Value("${spring.data.mongodb.database}")
    public static final String DB_NAME = "AQE";
    @Value("${spring.data.mongodb.host}")
    public static final String MONGO_HOST = "localhost";
    @Value("${spring.data.mongodb.port}")
    public static final int MONGO_PORT = 27017;

    MongoClient mongoClient = new MongoClient(MONGO_HOST,MONGO_PORT);
    MongoOperations mongoOperations = new MongoTemplate(mongoClient, DB_NAME);

    private ResultRepository resultRepository;
    Result result;

    private TopicRepository topicRepository;

    private QuestionRepository questionRepository;

    String collectionName;

    List<Question> questions;
    List<Question> easyQuestions;
    List<Question> mediumQuestions;
    List<Question> hardQuestions;

    List<Attempt> attempts;

    int easyIndex;
    int mediumIndex;
    int hardIndex;

    int correctEasy;
    int correctMedium;
    int correctHard;

    boolean inEasy;
    boolean inMedium;
    boolean inHard;

    int totalAskedInSections = 10;
    int needPassedInEasy = 6;
    int needPassedInMedium = 5;
    int needPassedInHard = 4;

    float totalMarksInEasy = 30;
    float totalMarksInMedium = 30;
    float totalMarksInHard = 40;

    @Autowired
    public FetchQuestionsServiceImpl(QuestionRepository questionRepository, TopicRepository topicRepository, ResultRepository resultRepository) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
        this.resultRepository = resultRepository;
    }

    @Override
    public Question getFirstQuestion(String testId, String collectionName, String empId, String empName) throws QuestionNotFoundException {
        this.collectionName = collectionName;

        questions = mongoOperations.findAll(Question.class, this.collectionName);
        if (questions.size() == 0){
            throw new QuestionNotFoundException("No questions were found");
        }

        easyQuestions = new ArrayList<>();
        mediumQuestions = new ArrayList<>();
        hardQuestions = new ArrayList<>();

        attempts = new ArrayList<>();
        result = new Result();

        result.setEmpId(empId);
        result.setEmpName(empName);
        result.setTestId(testId);

        result.setTopicId(topicRepository.findByName(collectionName).get(0).getId());
        result.setTopicName(collectionName);



        for(Question question: questions){
            if(question.getDifficulty().equals("E"))
                easyQuestions.add(question);
            else if(question.getDifficulty().equals("M"))
                mediumQuestions.add(question);
            else if(question.getDifficulty().equals("H"))
                hardQuestions.add(question);
        }

        easyIndex = 0;
        correctEasy = 0;
        inEasy = true;
        inMedium = false;
        inHard = false;
        return easyQuestions.get(easyIndex);
    }

    @Override
    public Question getNextQuestion(int response) throws QuestionNotFoundException{
        if(inEasy && !inMedium && !inHard)
            return easyQuestion(response);
        else if(!inEasy && inMedium && !inHard)
            return mediumQuestion(response);
        else if(!inEasy && !inMedium && inHard)
            return hardQuestion(response);
        return null;
    }

    public Question easyQuestion(int response) throws QuestionNotFoundException{
        Question question = easyQuestions.get(easyIndex);
        question.setTotalOccurrences(question.getTotalOccurrences()+1);

        easyIndex = easyIndex + 1;
        if(easyQuestions.get(easyIndex-1).getAnswer().equals(easyQuestions.get(easyIndex-1).getChoices()[response])) {
            correctEasy = correctEasy + 1;
            question.setCorrectAttempts(question.getCorrectAttempts()+1);
        }

        if(question.getTotalOccurrences() >= 2){
            if(question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) < 0.3){
                question.setDifficulty("H");
            }
            else if(question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) >= 0.3 && question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) < 0.7){
                question.setDifficulty("M");
            }
            else if(question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) >= 0.7){
                question.setDifficulty("E");
            }
        }

        mongoOperations.save(question, collectionName);

        Attempt attempt = new Attempt();
        attempt.setQuestionId(easyQuestions.get(easyIndex-1).getId());
        attempt.setQuestion(easyQuestions.get(easyIndex-1).getQuestion());
        attempt.setResponse(easyQuestions.get(easyIndex-1).getChoices()[response]);
        attempt.setAnswer(easyQuestions.get(easyIndex-1).getAnswer());
        attempt.setChoices(easyQuestions.get(easyIndex-1).getChoices());
        attempts.add(attempt);

        if(easyIndex < totalAskedInSections && correctEasy < needPassedInEasy)
            return easyQuestions.get(easyIndex);
        else if(easyIndex >= totalAskedInSections && correctEasy < needPassedInEasy){
            result.setScore((correctEasy/Double.valueOf(easyIndex))*totalMarksInEasy);
            result.setCorrect(correctEasy);
            result.setWrong(easyIndex-correctEasy);
            result.setAttempts(attempts);
            resultRepository.save(result);

            System.out.println("Your test is completed in easy section");
        }
        else if(easyIndex < totalAskedInSections && correctEasy >= needPassedInEasy){
            mediumIndex = 0;
            correctMedium = 0;
            inEasy = false;
            inMedium = true;
            inHard = false;
            return mediumQuestions.get(mediumIndex);
        }
        return null;

    }

    public Question mediumQuestion(int response) throws QuestionNotFoundException{
        Question question = mediumQuestions.get(mediumIndex);
        question.setTotalOccurrences(question.getTotalOccurrences()+1);

        mediumIndex = mediumIndex + 1;
        if(mediumQuestions.get(mediumIndex-1).getAnswer().equals(mediumQuestions.get(mediumIndex-1).getChoices()[response])) {
            correctMedium = correctMedium + 1;
            question.setCorrectAttempts(question.getCorrectAttempts()+1);
        }

        if(question.getTotalOccurrences() >= 2){
            if(question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) < 0.3){
                question.setDifficulty("H");
            }
            else if(question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) >= 0.3 && question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) < 0.7){
                question.setDifficulty("M");
            }
            else if(question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) >= 0.7){
                question.setDifficulty("E");
            }
        }

        mongoOperations.save(question, collectionName);

        Attempt attempt = new Attempt();
        attempt.setQuestionId(mediumQuestions.get(mediumIndex-1).getId());
        attempt.setQuestion(mediumQuestions.get(mediumIndex-1).getQuestion());
        attempt.setResponse(mediumQuestions.get(mediumIndex-1).getChoices()[response]);
        attempt.setAnswer(mediumQuestions.get(mediumIndex-1).getAnswer());
        attempt.setChoices(mediumQuestions.get(mediumIndex-1).getChoices());
        attempts.add(attempt);

        if(mediumIndex < totalAskedInSections && correctMedium < needPassedInMedium)
            return mediumQuestions.get(mediumIndex);
        else if(mediumIndex >= totalAskedInSections && correctMedium < needPassedInMedium){
            result.setScore(((correctMedium/Double.valueOf(mediumIndex))*totalMarksInMedium) + ((correctEasy/Double.valueOf(easyIndex))*totalMarksInEasy));
            result.setCorrect(correctMedium + correctEasy);
            result.setWrong((mediumIndex-correctMedium) + (easyIndex-correctEasy));
            result.setAttempts(attempts);
            resultRepository.save(result);

            System.out.println("Your test is completed in medium section");
        }
        else if(mediumIndex < totalAskedInSections && correctMedium >= needPassedInMedium){
            hardIndex = 0;
            correctHard = 0;
            inEasy = false;
            inMedium = false;
            inHard = true;
            return hardQuestions.get(hardIndex);
        }
        return null;
    }

    public Question hardQuestion(int response) throws QuestionNotFoundException{
        Question question = hardQuestions.get(hardIndex);
        question.setTotalOccurrences(question.getTotalOccurrences()+1);

        hardIndex = hardIndex + 1;
        if(hardQuestions.get(hardIndex-1).getAnswer().equals(hardQuestions.get(hardIndex-1).getChoices()[response])) {
            correctHard = correctHard + 1;
            question.setCorrectAttempts(question.getCorrectAttempts()+1);
        }

        if(question.getTotalOccurrences() >= 2){
            if(question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) < 0.3){
                question.setDifficulty("H");
            }
            else if(question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) >= 0.3 && question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) < 0.7){
                question.setDifficulty("M");
            }
            else if(question.getCorrectAttempts()/Double.valueOf(question.getTotalOccurrences()) >= 0.7){
                question.setDifficulty("E");
            }
        }

        mongoOperations.save(question, collectionName);

        Attempt attempt = new Attempt();
        attempt.setQuestionId(hardQuestions.get(hardIndex-1).getId());
        attempt.setQuestion(hardQuestions.get(hardIndex-1).getQuestion());
        attempt.setResponse(hardQuestions.get(hardIndex-1).getChoices()[response]);
        attempt.setAnswer(hardQuestions.get(hardIndex-1).getAnswer());
        attempt.setChoices(hardQuestions.get(hardIndex-1).getChoices());
        attempts.add(attempt);

        if(hardIndex < totalAskedInSections && correctHard < needPassedInHard)
            return hardQuestions.get(hardIndex);
        else if(hardIndex >= totalAskedInSections && correctHard < needPassedInHard){
            result.setScore(((correctHard/Double.valueOf(hardIndex))*totalMarksInHard) + ((correctMedium/Double.valueOf(mediumIndex))*totalMarksInMedium) + ((correctEasy/Double.valueOf(easyIndex))*totalMarksInEasy));
            result.setCorrect(correctHard + correctMedium + correctEasy);
            result.setWrong((hardIndex-correctHard) + (mediumIndex-correctMedium) + (easyIndex-correctEasy));
            result.setAttempts(attempts);
            resultRepository.save(result);

            System.out.println("Your test is completed in hard section");
        }
        else if(hardIndex < totalAskedInSections && correctHard >= needPassedInHard){
            result.setScore(((correctHard/Double.valueOf(hardIndex))*totalMarksInHard) + ((correctMedium/Double.valueOf(mediumIndex))*totalMarksInMedium) + ((correctEasy/Double.valueOf(easyIndex))*totalMarksInEasy));
            result.setCorrect(correctHard + correctMedium + correctEasy);
            result.setWrong((hardIndex-correctHard) + (mediumIndex-correctMedium) + (easyIndex-correctEasy));
            result.setAttempts(attempts);
            resultRepository.save(result);

            System.out.println("You have passed all the three sections");
        }
        return null;
    }
}
