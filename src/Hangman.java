import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Hangman {
    private static HashSet<String> dict;
    private HashSet<Character> charactersGuessed;
    private  String fileName;
    private String currentGuess;
    private int triesLeft;


    Hangman(String fileName){
        this.fileName = fileName;
        dict = readFile(this.fileName);
        charactersGuessed = new HashSet<>();
        initialSetup();
    }

    private void initialSetup() {
        Scanner scan = new Scanner(System.in);

        try{
            System.out.print("Enter the size of the word you want to try and guess: ");
            int size = Integer.parseInt(scan.nextLine());
            System.out.println();

            System.out.print("Enter how many tries you want to have: ");
            triesLeft = Integer.parseInt(scan.nextLine());
            System.out.println();

            if(triesLeft <= 0 || size > 29 || size < 1)   throw new Exception(); // out of bounds input

            this.setDictSize(size); // set dictionary to word length
            this.currentGuess = setGuesses(size); // set current guess to all dashes
            System.out.println("Welcome to Hangman!\n\n");
        } catch(Exception e){
            System.out.println("Error, you entered in the wrong format, please try again...\n\n");
            dict = readFile(this.fileName);
            initialSetup();
        }

    }


    private void setDictSize(int size){
        dict.removeIf(temp -> temp.length() != size);
    }

    private String getRandomWord(){
        return dict.toArray()[(int)(Math.random() * dict.size())].toString();
    }

    private String setGuesses(int size){
        StringBuilder temp = new StringBuilder();

        for(int i = 0; i < size; i++){
            temp.append('-');
        }
        return temp.toString();
    }

    void startGame(){
        while(this.currentGuess.contains(String.valueOf('-')) && this.triesLeft > 0) {
            System.out.println("**********************************************************************************************************************************************");
            System.out.printf("Current guess: %-20s Attempts left: %-20d Guessed letters: %-20s size: %-20d\n", this.currentGuess, this.triesLeft, charactersGuessed.toString(), dict.size());
            Scanner scan = new Scanner(System.in);

            System.out.print("Enter your next guess: ");
            String input = scan.nextLine();
            System.out.println("**********************************************************************************************************************************************");
            System.out.println();
            System.out.println();
            System.out.println();
            System.out.println();


            if (validGuess(input)) {
                Character ch = input.charAt(0);
                if(charactersGuessed.contains(ch)){
                    System.out.println("You already guessed that letter, try again\n\n");
                    continue;
                }
                updateWordFamilies(ch);
                if (!this.currentGuess.contains(String.valueOf('-'))) {
                    System.out.printf("Congrats you won!!! The word was: %s\n", getRandomWord());
                    break;
                } else if (triesLeft == 0) {
                    System.out.printf("Game Over, you lose. The word was: %s\n", getRandomWord());
                    break;
                }
                this.charactersGuessed.add(ch);
            } else {
                System.out.println("You entered an invalid guess, try again...\n");
            }
        }
    }

    private boolean validGuess(String input){
        return input.replaceAll("[^a-zA-Z]", "").length() == 1;

    }

    private HashMap<ArrayList<Integer>, Integer> createFam(Character ch){
        // This creates the families of every possible grouping given the input ch
        // Key: indices of ch, Value: occurrences

        HashMap<ArrayList<Integer>, Integer> families = new HashMap<>();
        for(String str : dict){
            ArrayList<Integer> indices = new ArrayList<>();
            if(!str.contains(String.valueOf(ch))){ // if it doesn't exist, add -1 list
                indices.add(-1);
            } else{  // otherwise add the index of every occurrence to the list
                for(int i = 0; i < str.length(); i++){
                    if(str.charAt(i) == ch){
                        indices.add(i);
                    }
                }
            }
            families.put(indices, families.getOrDefault(indices, 0) + 1);  // increment frequency occurred
        }
        return families;
    }

    private ArrayList<Integer> findBiggestFam(HashMap<ArrayList<Integer>, Integer> families){
        // Find the biggest family, determined by frequency (Value)
        ArrayList<Integer> best = families.keySet().iterator().next();

        for(ArrayList<Integer> x : families.keySet()){
            if(families.get(x) > families.get(best)){
                best = x;
            }
        }
        return best;
    }

    private void updateCurrentGuess(ArrayList<Integer> best, Character ch){
        if(best.get(0) == -1){
            removeAllWith(ch);
            return;
        }

        StringBuilder ret = new StringBuilder(this.currentGuess);

        for(Integer i : best){
            ret.setCharAt(i, ch);
        }

        this.currentGuess = ret.toString();

        fitDict(this.currentGuess, best);
    }

    private void updateWordFamilies(Character ch){
        String before = this.currentGuess;
        HashMap<ArrayList<Integer>, Integer> families = createFam(ch);
        ArrayList<Integer> best = findBiggestFam(families);

        updateCurrentGuess(best, ch);

        if(before.equals(this.currentGuess)){
            this.triesLeft--;
        }
    }

    private void fitDict(String currentGuess, ArrayList<Integer> best){
        Iterator<String> iter = dict.iterator();

        while(iter.hasNext()){
            String temp = iter.next();
            Boolean flag = false;
            for(int i = 0; i < temp.length(); i++){
                if(temp.charAt(i) != currentGuess.charAt(i) && currentGuess.charAt(i) != '-'){
                    flag = true;
                    break;
                } else {
                    for(Integer x : best){
                        if(temp.charAt(x) != currentGuess.charAt(x)){
                            flag = true;
                            break;
                        }
                    }
                }

            }
            if(flag){
                iter.remove();
            }
        }
    }

    private void removeAllWith(Character ch){
        dict.removeIf(temp -> temp.contains(String.valueOf(ch)));
    }

    private HashSet<String> readFile(String fileName){
        HashSet<String> ret = new HashSet<>();

        try {
            Scanner scanner = new Scanner(new File(fileName));
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] words = line.split("\\s+");
                for(String word : words){
                    word = word.replaceAll("\\W", "").toLowerCase();
                    ret.add(word);
                }
            }
            scanner.close();

        } catch (FileNotFoundException e1) {
            System.out.println("Error, file wasn't found");
        }
        return ret;
    }
}
