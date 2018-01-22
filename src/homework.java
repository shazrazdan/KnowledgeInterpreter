import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

@SuppressWarnings("Duplicates")

/**
 * Created by Shashwat on 16/11/17.
 */
public class homework {
    static int nOfQueries;
    static int nOfSentences;
    static ArrayList<String> inputQueries = new ArrayList<>();
    static ArrayList<String> knowledgeBase = new ArrayList<>();
    static HashMap<String,Predicate> predicates = new HashMap<String, Predicate>();
    static ArrayList<Boolean> results = new ArrayList<Boolean>();
    static int maxDepth = 10;

    static class Predicate{
        String predicateName;
        ArrayList<Integer> partOfSentencesPositive = new ArrayList<>();
        ArrayList<Integer> partOfSentencesNegative = new ArrayList<>();
        int noOfParameters;
        ArrayList<ArrayList<String>> constants = new ArrayList<ArrayList<String>>(noOfParameters);

        Predicate(String predicateName, ArrayList<Integer> partOfSentencesPositive, ArrayList<Integer> partOfSentencesNegative, int noOfParameters, ArrayList<ArrayList<String>> constants){
            this.noOfParameters = noOfParameters;
            this.partOfSentencesNegative = partOfSentencesNegative;
            this.partOfSentencesPositive = partOfSentencesPositive;
            this.constants = constants;
            this.predicateName = predicateName;
        }

        void addTopartOfSentencesPositive(int sentence){
            partOfSentencesPositive.add(sentence);
        }

        void addTopartOfSentencesNegative(int sentence){
            partOfSentencesNegative.add(sentence);
        }

        void addToConstantsList(int constantIndex, String constant){
            constants.get(constantIndex).add(constant);
        }

        void displayValues() {
            printToScreen(predicateName + ", " + noOfParameters);
            printToScreen("Positive Sentences: ");
            for (int i :
                    partOfSentencesPositive) {
                System.out.print((i) + ", ");
            }
            printToScreen("\nNegative Sentences: ");
            for (int i :
                    partOfSentencesNegative) {
                System.out.print((i) + ", ");
            }
            printToScreen("");
            for (int i = 0; i < constants.size(); i++) {
                ArrayList<String> tempStr = constants.get(i);
                System.out.print("Parameter num:" + i + ", values: ");
                for (String str : tempStr) {
                    System.out.print(str + ", ");
                }
                printToScreen("");
            }

        }
    }

    public static void printToScreen(String message){
        //System.out.println(message);
    }

    static void addToPredicatesList(String predicate, int sentenceNumber){
        boolean negated = false;
        String predicateName = predicate.split("[(]")[0];
        String parameters = predicate.split("[(]")[1].replaceAll("[)]", "");

        String[] parametersList = parameters.split("[,]");

        if(predicateName.toCharArray()[0] == '~'){
            negated = true;
            predicateName = predicateName.substring(1,predicateName.length());
        }

        if(predicates.containsKey(predicateName)){
            //Found this predicate already in the list.
            //printToScreen("Found " + predicateName);
            Predicate temp = predicates.get(predicateName);
            if(negated){
                temp.addTopartOfSentencesNegative(sentenceNumber);
            }else{
                temp.addTopartOfSentencesPositive(sentenceNumber);
            }
            for(int j=0; j<parametersList.length; j++){
                temp.addToConstantsList(j, parametersList[j]);
            }
            predicates.put(predicateName, temp);

        }else{
            //Seeing this predicate for the first time. Will create a new entry for this.
            ArrayList<Integer> positive = new ArrayList<>();
            ArrayList<Integer> negative = new ArrayList<>();
            if(negated){
                negative.add(sentenceNumber);
            }else{
                positive.add(sentenceNumber);
            }
            ArrayList<ArrayList<String>> constants = new ArrayList<>();
            for(int j=0; j<parametersList.length; j++){
                ArrayList<String> tempConsString = new ArrayList<>(1);
                tempConsString.add(parametersList[j]);
                constants.add(j, tempConsString);
            }
            Predicate newPredicate = new Predicate(predicateName, positive, negative, parametersList.length, constants);
            predicates.put(predicateName, newPredicate);
            //printToScreen("Created " + predicateName);
        }
    }
    static void readFile(){
        try {
            File input = new File("input.txt");
            Scanner in = new Scanner(input);
            nOfQueries = Integer.parseInt(in.nextLine());
            for(int i=0; i<nOfQueries; i++){
                inputQueries.add(in.nextLine().replaceAll("[ ]", ""));
            }
            nOfSentences = Integer.parseInt(in.nextLine());
            for(int i=0; i<nOfSentences; i++){
                String nextLine = in.nextLine();
                addToKnowledgeBase(i, nextLine);
            }
            maxDepth = Integer.min(nOfSentences*30, maxDepth);
            processSentences();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private static void addToKnowledgeBase(int num, String nextLine) {
        printToScreen(nextLine);
        String standard = "";
        String[] predicates = nextLine.replaceAll(" ", "").split("[|]");
        for (int i1 = 0; i1 < predicates.length; i1++) {
            //printToScreen(predicates[i1]);
            String i = predicates[i1];
            String[] pieces = i.split("[(]");
            standard += pieces[0] + "(";
            String[] parameters = pieces[1].substring(0, pieces[1].length() - 1).split("[,]");
            for (int i2 = 0; i2 < parameters.length; i2++) {
                String j = parameters[i2];
                if (Character.isUpperCase(j.toCharArray()[0])) {
                    standard += j;
                } else {
                    standard += j + num;
                }
                if(i2 < parameters.length-1){
                    standard += ",";
                }
            }
            if (i1 < predicates.length - 1) {
                standard += ") | ";
            }else{
                standard += ")";
            }
        }
        knowledgeBase.add(standard);
    }
    private static void processSentences() {
        for (int i1 = 0; i1 < knowledgeBase.size(); i1++) {
            String i = knowledgeBase.get(i1);
            //printToScreen("Processing: " + i);
            String predicates[] = i.split("[ ][|][ ]");
            for (String j : predicates) {
                addToPredicatesList(j, i1);
            }
            //printToScreen();
        }
    }


    private static String[] getPredicates(String sentence){
        return sentence.split("[ ][|][ ]");
    }
    private static boolean isConstant(String var){
        return Character.isUpperCase(var.toCharArray()[0]);
    }
    private static String[] getPredicateVars(String predicate){
        String temp = predicate.split("[(]")[1];
        return temp.substring(0,temp.length()-1).split("[,]");
    }
    private static String getPredicateName(String predicate){
        if(predicate.toCharArray()[0] == '~') {
            return predicate.substring(1, predicate.length()).split("[(]")[0];
        }
        else{
            return predicate.split("[(]")[0];
        }
    }
    private static String negatePredicate(String predicate){
        if(predicate.toCharArray()[0]=='~'){
            return predicate.substring(1,predicate.length());
        }else{
            return "~" + predicate;
        }
    }
    private static boolean isNegativePredicate(String predicate){
        return predicate.toCharArray()[0]=='~';
    }


    private static HashMap<String, String> shazUnify(String x, String y, HashMap<String, String> theta){
        theta = new HashMap<>();
        String[] predKbVars = getPredicateVars(x);
        String[] predCurrVars = getPredicateVars(y);
        for(int i=0; i<predCurrVars.length; i++){
            if(!isConstant(predCurrVars[i]) && !isConstant(predKbVars[i])){
                //BOTH VARIABLES
                //WILL HAVE TO CHECK WHICH WAY TO PUT
                printToScreen("{" + predCurrVars[i] + "/" + predKbVars[i] + "}");
                if (!theta.containsKey(predKbVars[i])) {
                    theta.put(predCurrVars[i], predKbVars[i]);
                } else if(!theta.get(predKbVars[i]).equals(predCurrVars[i])){
                    printToScreen("Already found assignment. Not unified.");
                    return null;
                }
            }
            else if(!isConstant(predCurrVars[i]) && isConstant(predKbVars[i])){
                printToScreen("{" + predCurrVars[i] + "/" + predKbVars[i] + "}");
                if (!theta.containsKey(predCurrVars[i])) {
                    theta.put(predCurrVars[i], predKbVars[i]);
                } else if(!theta.get(predCurrVars[i]).equals(predKbVars[i])){
                    printToScreen("Already found assignment. Not unified.");
                    return null;
                }
            }
            else if(isConstant(predCurrVars[i]) && !isConstant(predKbVars[i])){
                printToScreen("{" + predKbVars[i] + "/" + predCurrVars[i] + "}");
                if (!theta.containsKey(predKbVars[i])) {
                    theta.put(predKbVars[i], predCurrVars[i]);
                } else if(!theta.get(predKbVars[i]).equals(predCurrVars[i])){
                    printToScreen("Already found assignment. Not unified.");
                    return null;
                }
            }
            else if(predCurrVars[i].equals(predKbVars[i])){
                printToScreen("{" + predCurrVars[i] + " and " + predKbVars[i] + "}");
            }else{
                printToScreen("Cannot unify {" + predCurrVars[i] + " and " + predKbVars[i] + "}");
                return null;
            }
        }
        return theta;
    }

    private static String unify(String kbSentence, String currentSentence, int step) {
        printToScreen(kbSentence + " :: " + currentSentence);
        HashMap<String, String> theta = null;
        String commonPred="", commonPredicateKb="", commonPredicateCurr="";
        boolean commonPredFound = false;
        boolean kbNegative = false;
        int positionPredicate1=-1, positionPredicate2=-1;
        String[] kbSenPredicates = getPredicates(kbSentence);
        String[] curSenPredicates = getPredicates(currentSentence);
        for(int i=0; i<curSenPredicates.length; i++){
            for(int j=0; j<kbSenPredicates.length; j++){
                if(getPredicateName(kbSenPredicates[j]).equals(getPredicateName(curSenPredicates[i]))
                        && (isNegativePredicate(kbSenPredicates[j]) && !isNegativePredicate(curSenPredicates[i])
                            || !isNegativePredicate(kbSenPredicates[j]) && isNegativePredicate(curSenPredicates[i]))){

                    theta = shazUnify(kbSenPredicates[j],curSenPredicates[i], theta);
                    if(theta!=null){
                        //unification found. Will proceed with unifying sentences.
                        positionPredicate1 = j;
                        positionPredicate2 = i;
                        break;
                    }
                }
            }
            if(theta!=null){
                break;
            }
        }

        if(theta==null){
            return null;
        }

        ArrayList<String> returnExp = new ArrayList<>();

        for(int i=0; i<kbSenPredicates.length; i++){
            if(i!=positionPredicate1){
                //Only add those predicates to the list which are not unified.
                // No care for the skipped predicate.
                if (true || !getPredicateName(kbSenPredicates[i]).equals(commonPred)) {
                    //The predicates are not same name. Will proceed with normal unification.
                    String currPred = kbSenPredicates[i].split("[(]")[0] + "(";
                    String[] currPredKbVars = getPredicateVars(kbSenPredicates[i]);
                    for(int j=0; j<currPredKbVars.length; j++){
                        if(theta.containsKey(currPredKbVars[j])){
                            currPred += theta.get(currPredKbVars[j]);
                        }else{
                            currPred += currPredKbVars[j];
                        }
                        if(j<currPredKbVars.length-1){
                            currPred += ",";
                        }
                    }
                    currPred += ")";
                    returnExp.add(currPred);
                } else {
                    //The predicates are with the same predName. Will have to substitute accordingly.
                    //Take care of the constants
                    String currPred = kbSenPredicates[i].split("[(]")[0] + "(";
                    String[] currPredKbVars = getPredicateVars(kbSenPredicates[i]);
                    String[] commPredKbVars = getPredicateVars(commonPredicateKb);
                    for(int j=0; j<currPredKbVars.length; j++){
                        if(isConstant(currPredKbVars[j]) && !isConstant(commPredKbVars[j])) {
                            //if curr is constant and comm is not constant. We have 2 cases here
                            //1. comm is unified to a constant. Easy peezy to check
                            //2. comm is unified to a variable. Will have to see which way substitution is done.
                            //I think in var-var unification, I'll create a new var to unify them. It'll fix the problem.

                            if(theta.containsKey(commPredKbVars[j])){
                                if(theta.get(commPredKbVars[j]).equals(currPredKbVars[j])){
                                    //Case 1. comm is unified to a constant
                                    currPred += currPredKbVars[j];
                                }else if(!isConstant(theta.get(commPredKbVars[j]))){
                                    //Case 2. comm is unified to a var. We have new var substitution here.
                                    currPred += currPredKbVars[j];
                                }else{
                                    return null;
                                }
                            }else{
                                return null;
                            }

                        }else if(isConstant(currPredKbVars[j]) && isConstant(commPredKbVars[j])) {
                            if(currPredKbVars[j].equals(commPredKbVars[j])){
                                currPred += currPredKbVars[j];
                            }else{
                                return null;
                            }
                        }else{
                            if(theta.containsKey(currPredKbVars[j])){
                                currPred += theta.get(currPredKbVars[j]);
                            }else{
                                currPred += currPredKbVars[j];
                            }
                        }

                        if(j<currPredKbVars.length-1){
                            currPred += ",";
                        }
                    }
                    currPred += ")";
                    returnExp.add(currPred);
                }
            }
        }

        for(int i=0; i<curSenPredicates.length; i++){
            if(i!=positionPredicate2){
                //Only add those predicates to the list which are not unified.
                // No care for the skipped predicate.
                if (true || !getPredicateName(curSenPredicates[i]).equals(commonPred)) {
                    //The predicates are not same name. Will proceed with normal unification.
                    String currPred = curSenPredicates[i].split("[(]")[0] + "(";
                    String[] currPredSenVars = getPredicateVars(curSenPredicates[i]);
                    for(int j=0; j<currPredSenVars.length; j++){
                        if(theta.containsKey(currPredSenVars[j])){
                            currPred += theta.get(currPredSenVars[j]);
                        }else{
                            currPred += currPredSenVars[j];
                        }
                        if(j<currPredSenVars.length-1){
                            currPred += ",";
                        }
                    }
                    currPred += ")";
                    returnExp.add(currPred);
                } else {
                    //The predicates are with the same predName. Will have to substitute accordingly.
                    //Take care of the constants
                    String currPred = curSenPredicates[i].split("[(]")[0] + "(";
                    String[] currPredSenVars = getPredicateVars(curSenPredicates[i]);
                    String[] commPredSenVars = getPredicateVars(commonPredicateKb);
                    for(int j=0; j<currPredSenVars.length; j++){
                        if(isConstant(currPredSenVars[j]) && !isConstant(commPredSenVars[j])) {
                            //if curr is constant and comm is not constant. We have 2 cases here
                            //1. comm is unified to a constant. Easy peezy to check
                            //2. comm is unified to a variable. Will have to see which way substitution is done.
                            //I think in var-var unification, I'll create a new var to unify them. It'll fix the problem.

                            if(theta.containsKey(commPredSenVars[j])){
                                if(theta.get(commPredSenVars[j]).equals(currPredSenVars[j])){
                                    //Case 1. comm is unified to a constant
                                    currPred += currPredSenVars[j];
                                }else if(!isConstant(theta.get(commPredSenVars[j]))){
                                    //Case 2. comm is unified to a var. We have new var substitution here.
                                    currPred += currPredSenVars[j];
                                }else{
                                    return null;
                                }
                            }else{
                                return null;
                            }

                        }else if(isConstant(currPredSenVars[j]) && isConstant(commPredSenVars[j])) {
                            if(currPredSenVars[j].equals(commPredSenVars[j])){
                                currPred += currPredSenVars[j];
                            }else{
                                return null;
                            }
                        }else{
                            if(theta.containsKey(currPredSenVars[j])){
                                currPred += theta.get(currPredSenVars[j]);
                            }else{
                                currPred += currPredSenVars[j];
                            }
                        }

                        if(j<currPredSenVars.length-1){
                            currPred += ",";
                        }
                    }
                    currPred += ")";
                    returnExp.add(currPred);
                }
            }
        }


        String returnExpStr = "";
        for(int i=0; i<returnExp.size(); i++) {

            if (i != 0) {
                returnExpStr += " | ";
            }
            returnExpStr += returnExp.get(i);
        }

        return returnExpStr;
    }



    private static void resolveQueries() {
        for(int i=0; i<nOfQueries; i++){
            System.out.println("Query " + (i+1) + ": " + inputQueries.get(i));
            String predicate = negatePredicate(inputQueries.get(i));
            knowledgeBase.add(predicate);

            results.add(resolve(predicate, 0));
            knowledgeBase.remove(knowledgeBase.size()-1);
        }
    }

    private static boolean resolve(String currentSentence, int depth) {

        printToScreen("\n\nResolving: " + currentSentence + " depth: " + depth);

        if(depth>=maxDepth) {
            printToScreen("\n---cut:" + depth + "---");
            return false;
        }
        if(knowledgeBase.contains(negatePredicate(currentSentence))){
            return true;
        }

        if(sentenceAlreadyTrue(currentSentence)){
            printToScreen("Already true");
            return true;
        }


        printToScreen("---srt:" + depth + "---");
        boolean negated;
        String firstPred = getPredicates(currentSentence)[0];
        if (currentSentence.toCharArray()[0] != '~') {
            negated = false;
        } else {
            negated = true;
        }

        ArrayList<Integer> sentences;

        try {
            if(negated){
                sentences = predicates.get(getPredicateName(firstPred)).partOfSentencesPositive;
            }else{
                sentences = predicates.get(getPredicateName(firstPred)).partOfSentencesNegative;
            }
        } catch (Exception e) {
            return false;
        }

        boolean success = false;
        for (int i=0; i<sentences.size(); i++) {
            printToScreen("Unifying with Sentence: " + sentences.get(i));
            String unifiedSentence = unify(knowledgeBase.get(sentences.get(i)), currentSentence,1);

            printToScreen("Unified Sentence: " + unifiedSentence);
            if(unifiedSentence==null){

            }else if(unifiedSentence.equals("")){
                success = true;
                break;
            }else{
                if(resolve(unifiedSentence,depth+1)) {
                    success = true;
                    break;
                }
            }
        }
        return success;

        //printToScreen("---end:" + depth + "---");
        //return false;
    }

    private static boolean sentenceAlreadyTrue(String currentSentence) {
        String[] predicates = getPredicates(currentSentence);
        boolean returnState = false;
        for(int i=0; i<predicates.length; i++){
            for(int j=0; j<predicates.length; j++){
                if(getPredicateName(predicates[i]).equals(getPredicateName(predicates[j]))){
                    if(isNegativePredicate(predicates[i]) && !isNegativePredicate(predicates[j])){
                        if(predicates[i].equals("~" + predicates[j])){
                            returnState = true;
                            break;
                        }
                    }
                    else if(!isNegativePredicate(predicates[i]) && isNegativePredicate(predicates[j])){
                        if(predicates[j].equals("~" + predicates[i])){
                            returnState = true;
                            break;
                        }
                    }
                }
            }
        }

        return returnState;
    }

    private static void printToFile(){
        try {
            PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
            for (Boolean result : results) {
                if (result) {
                    writer.println("TRUE");
                } else {

                    writer.println("FALSE");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String... args){

        long start = System.currentTimeMillis();
        readFile();

//        HashMap<String, String> uni = null;
//        uni = shazUnify("~FA(y5,x4,z)","FA(z,w,x4)", uni);
//        if(uni!=null){
//            printToScreen("UNIFIED");
//        }else{
//            printToScreen("NOT UNIFIED");
//        }





        resolveQueries();
        System.out.println();
        printToScreen("\n\n\n\n");
        for (Boolean result : results) {
            System.out.println("" + result);
        }
        printToFile();

        System.out.println("\nTime:" + (System.currentTimeMillis()-start));


//        printToScreen("PredName: " + getPredicateName("Nono(x,d,f,g,h)"));
//        String[] temp = getPredicateVars("Nono(x,d,f,g,h)");
//

//        printToScreen();
//
//        Iterator it = predicates.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry)it.next();
//            Predicate temp = (Predicate) pair.getValue();
//            temp.displayValues();
//            it.remove(); // avoids a ConcurrentModificationException
//        }
    }


}
