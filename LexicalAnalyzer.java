import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class LexicalAnalyzer {

    private static final ArrayList<String> keys = new ArrayList<>();
    private static final ArrayList<String> token = new ArrayList<>();
    
    private static final ArrayList<Character> letters = new ArrayList<>();
    private static final ArrayList<Character> numbers = new ArrayList<>();
	
    private final ArrayList<CodeEntry> file = new ArrayList<>();
    
    private String location;
    
    private static class CodeEntry{
    	
        public final boolean isValid;
        public final String key;
        public final String value;

        CodeEntry(String key, String value, boolean isValid){
            this.isValid = isValid;
            this.key = key;
            this.value = value;
        }
    }
    
    public LexicalAnalyzer(String location){
    	setLocation(location);
    	keys.addAll(Arrays.asList("or", "div", "mod", "char", "integer", "boolean", "false", "true", "not", "while", "repeat", "until", "do", "loop", "end", "if", "else", "elsif", "procedure", "const", "type", "var", "module", "import"));
        token.addAll(Arrays.asList("*", "&", "+", "-", "=", "#", "<", "<=", ">", ">=", ":", ":=", ";", ":", ",", ".", "(", ")"));
        letters.addAll(elementsDisintegration("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"));
        numbers.addAll(elementsDisintegration("0123456789"));
    }
    
    public void setLocation(String location){
        this.location = location;
    }
    
    private static ArrayList<Character> elementsDisintegration(String str){
        char[] initialCharacters = str.toCharArray();
        List<Character> charsList = new ArrayList<>();
        for(char character: initialCharacters) charsList.add(character);
        return (ArrayList<Character>) charsList;
    }
    
    public void printTokens(){
    	
        for(CodeEntry element : file) {
        	
        	CodeEntry ce = element;
        	int index = 0;
        	
            if(!ce.isValid) {
                System.out.println(ce.value + ": " + ce.key);
                index++;
                continue;
            }
            
            if(!ce.key.equals("lexeme") && (index == 0 || !file.get(index - 1).value.equals(":="))) {
                System.out.println(ce.key + ": " + ce.value);
                index++;
                continue;
            }
            
            index++;
            System.out.println(ce.value);
        }
    }
    
    public void startPoint() throws FileNotFoundException, NoSuchFileException {
        Scanner scanner = new Scanner(new File(location));
        while(scanner.hasNextLine())
            analyse(scanner.nextLine());
    }
    
	private void analyse(String codeRow) {
		
		String[] ids = analyseHelper(codeRow);
		
		for(String id: ids) {
			
            if(id.length() == 0) continue;
            
            if(keys.contains(id) || token.contains(id)) {
                file.add(new CodeEntry("lexeme", id, true));
                continue;
            }
            
            char[] characters = id.toCharArray();
            
            if(numbers.contains(characters[0])) {
            	
                boolean key= true;
                
                for(int i = 1; i < characters.length; i++)
                    if(!numbers.contains(characters[i])) {
                        key= false;
                        break;
                    }
                
                if(key) file.add(new CodeEntry("number", id, true));
                else file.add(new CodeEntry("error, invalid lexeme", id, false));
                continue;
            }
            
            if(letters.contains(characters[0])) {
            	
                boolean key= true;
                
                for(int i = 1; i < characters.length; i++)
                    if(!letters.contains(characters[i]) && !numbers.contains(characters[i])) {
                        key= false;
                        break;
                    }

                if(key) file.add(new CodeEntry("lexeme", id, true));
                else file.add(new CodeEntry("error, invalid lexeme", id, false));
                continue;
            }
            
            if(characters[0] == '"' && characters[characters.length - 1] == '"') {
            	
                boolean key= true;
                
                for(int i = 1; i < characters.length - 1; i++)
                    if(characters[i] == '"'){
                        key= false;
                        break;
                    }

                if(key) file.add(new CodeEntry("string", id, true));
                else file.add(new CodeEntry("error, invalid lexeme", id, false));
                continue;
            }
            
            file.add(new CodeEntry("error, invalid lexeme", id, false));
        }
	}
	
	private String[] analyseHelper(String str){
		
        ArrayList<String> strs = new ArrayList<>();
        char[] characters = str.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        
        boolean key = false;
        
        for(int i = 0; i < characters.length; i++) {
            if(key){
            	
                if(characters[i] == '"') {
                    stringBuilder.append(characters[i]);
                    strs.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    key= false;
                    continue;
                }
                
            } else{
            	
                if(stringBuilder.length() == 0 && characters[i] == '"') {
                    stringBuilder.append(characters[i]);
                    key= true;
                    continue;
                }
                
                if(characters[i] == ' ') {
                    strs.add(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    continue;
                }
                
                if(i != characters.length - 1) {
                    String possibleToken = characters[i] + String.valueOf(characters[i + 1]);
                    if(token.contains(possibleToken)){
                        strs.add(stringBuilder.toString());
                        strs.add(possibleToken);
                        stringBuilder = new StringBuilder();
                        i += 1;
                        continue;
                    }
                }
                
                if(token.contains(String.valueOf(characters[i]))) {
                    strs.add(stringBuilder.toString());
                    strs.add(String.valueOf(characters[i]));
                    stringBuilder = new StringBuilder();
                    continue;
                }
            }
            
            stringBuilder.append(characters[i]);
        }
        
        strs.add(stringBuilder.toString());
        
        return strs.toArray(new String[]{});
    }

	public static void main(String[] args) {
		
		LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(args[0]);
        
		try{
        	lexicalAnalyzer.startPoint();
        } catch(Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        
        lexicalAnalyzer.printTokens();
	}

}
